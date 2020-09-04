package com.hotmail.AdrianSR.BattleRoyale.vehicles;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.BattleRoyale.config.lang.Lang;
import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.events.MemberJumpFromFlyingVehicle;
import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.map.managers.MapsManager;
import com.hotmail.adriansr.core.util.actionbar.ActionBarUtil;
import com.hotmail.adriansr.core.util.entity.UUIDPlayer;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.packet.PacketAdapter;
import com.hotmail.adriansr.core.util.packet.PacketChannelHandler;
import com.hotmail.adriansr.core.util.packet.PacketEvent;
import com.hotmail.adriansr.core.util.packet.PacketListener;
import com.hotmail.adriansr.core.util.reflection.bukkit.BukkitReflection;
import com.hotmail.adriansr.core.util.reflection.bukkit.EntityReflection;
import com.hotmail.adriansr.core.util.reflection.general.ClassReflection;
import com.hotmail.adriansr.core.util.reflection.general.ConstructorReflection;
import com.hotmail.adriansr.core.util.reflection.general.FieldReflection;
import com.hotmail.adriansr.core.util.reflection.general.MethodReflection;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.titles.TitlesUtil;

import de.slikey.effectlib.util.ParticleEffect;

/**
 * TODO: Description
 * <p>
 * @author AdrianSR / Tuesday 18 August, 2020 / 11:16 AM
 */
public final class BRVehicle implements Vehicle , Runnable {
	
	/**
	 * We're saving the time players close their vehicles.
	 */
	protected static final Map < UUID , Long > DISMOUNT_MILLIS = new HashMap < > ( );
	
	/**
	 * Gets the time in milliseconds that the provided {@code player} closed this
	 * vehicle.
	 * <p>
	 * @param player the player to get.
	 * @return the time in milliseconds.
	 */
	public static long getDismountMillis ( Player player ) {
		return DISMOUNT_MILLIS.containsKey ( player.getUniqueId ( ) ) ? DISMOUNT_MILLIS.get ( player.getUniqueId ( ) ) : -1L;
	}
	
	/**
	 * Gets whether players are able to close their vehicles.
	 * <p>
	 * Note that this is determined by an option of the configuration
	 * ({@link Config#VEHICLE_SECONDS_TO_DISMOUNT} specifically).
	 * <p>
	 * @return whether players can close their vehicles.
	 */
	public static boolean playersCanClose ( ) {
		if ( GameManager.isRunning ( ) && MapsManager.BATTLE_MAP != null ) {
			return GameManager.getTimeInGame ( TimeUnit.SECONDS ) >= Config.VEHICLE_SECONDS_TO_DISMOUNT.getAsInteger ( );
		} else {
			return false;
		}
	}
	
	final PacketListener packet_listener = new PacketAdapter ( ) {
		@Override public void onReceiving ( final PacketEvent event ) {
			if ( !hasPassenger ( ) || base == null || handle == null ) {
				// oh!, it seems this vehicle has never been spawned or it was already
				// destroyed.
				return;
			}
			
			Player player = event.getPlayer ( );
			Object packet = event.getPacket ( );
			if ( Objects.equals ( player.getUniqueId ( ) , BRVehicle.this.player.get ( ).getUniqueId ( ) ) ) {
				try {
					if ( (boolean) FieldReflection.getValue ( packet , "d" /* this field name is version dependent */ ) ) {
						if ( playersCanClose ( ) ) {
							close ( );
						} else {
							event.setCancelled ( true );
						}
					}
				} catch ( SecurityException | NoSuchFieldException | IllegalArgumentException
						| IllegalAccessException ex ) {
					ex.printStackTrace ( );
				}
			}
		}
	};
	
	private final UUIDPlayer player;
	private final Location location;
	
	private BRVehicleType         type = BRVehicleType.HORSE;
	private BRVehicleParticle particle = BRVehicleParticle.MAGIC_GREEN;
	
	private Object   nms_handle;
	private int       handle_id;
	private LivingEntity handle;
	private ArmorStand     base;
	
	private final double travel_velocity;
	
	private boolean passenger_inside;
	
	public BRVehicle ( Player player , BRVehicleType type , BRVehicleParticle particle , Location location ) {
		Validate.notNull ( player , "player cannot be null!" );
		
		this.player   = new UUIDPlayer ( player );
		this.location = location;
		
		if ( type     != null ) { setType ( type );         }
		if ( particle != null ) { setParticle ( particle ); }
		
		// travel velocity, from configuration
		Double travel_velocity_config = Config.VEHICLE_TRAVEL_VELOCITY.getAsDouble ( );
		this.travel_velocity          = Math.max ( travel_velocity_config != null ? travel_velocity_config 
				: Config.VEHICLE_TRAVEL_VELOCITY.getAsDouble ( true ) , 0.2D );
		
		// register this instance
		Vehicle.VEHICLES.add ( this );
		
		// registering packet listener
		PacketChannelHandler.addPacketListener ( "PacketPlayInSteerVehicle" , PacketListener.Priority.LOWEST , packet_listener );
	}
	
	public BRVehicle ( Player player , BRVehicleType type , Location location ) {
		this ( player , type , null , location );
	}
	
	public BRVehicle ( Player player , Location location ) {
		this ( player , null , null , location );
	}
	
	@Override
	public Player getPassenger ( ) {
		return passenger_inside ? player.get ( ) : null;
	}
	
	@Override
	public boolean hasPassenger ( ) {
		return getPassenger ( ) != null;
	}
	
	public BRVehicleType getType ( ) {
		return type;
	}
	
	public BRVehicleParticle getParticle ( ) {
		return particle;
	}
	
	public void setType ( BRVehicleType type ) {
		this.type = type;
	}
	
	public void setParticle ( BRVehicleParticle particle ) {
		this.particle = particle;
	}
	
	private BukkitTask task;
	
	public void start ( ) {
		Validate.isTrue ( handle != null && base != null , "call 'join()' first!" );
		Validate.isTrue ( task == null , "call 'stop()' first!" );
		task = SchedulerUtil.runTaskTimer ( this , 0L , 0L , BattleRoyale.getInstance ( ) );
	}
	
	public void stop ( ) {
		Validate.isTrue ( task != null , "call 'start()' first!" );
		task.cancel ( );
		task = null;
	}
	
	@Override
	public void run ( ) {
		if ( player.get ( ) == null ) {
			// player disconnected.
			stop ( ); return;
		}
		
		Location current = base.getLocation ( );
		Vector direction = DirectionUtil.getDirection ( current.getYaw ( ) , 0F ).multiply ( travel_velocity );
		
		Location   base_to = current.clone ( ).add ( direction );
		Location handle_to = base_to.clone ( ).add ( 0.0D , ( 1.8D * 0.75D ) , 0.0D );
		
		EntityReflection.setLocation ( base , base_to );
		EntityReflection.setLocation ( handle , handle_to );
		
		try {
			Object teleport_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutEntityTeleport" ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "Entity" ) } , nms_handle );
			Object rotation_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutEntityHeadRotation" ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "Entity" ) , byte.class } , 
					nms_handle , (byte) ( ( current.getYaw ( ) * 256.0F ) / 360.0F ) );
			
			BukkitReflection.sendPacket ( player.get ( ) , teleport_packet );
			BukkitReflection.sendPacket ( player.get ( ) , rotation_packet );
			
			getParticle ( ).sendTo ( handle_to.clone ( ).add ( 0.0D , 0.5D , 0.0D ) , Arrays.asList ( player.get ( ) ) );
		} catch ( NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex ) {
			ex.printStackTrace ( );
		}
		
		if ( playersCanClose ( ) ) {
			ActionBarUtil.send ( player.get ( ) , Lang.BAR_DISMOUNT_VEHICLE.getValue ( true ) );
			TitlesUtil.send ( player.get ( ) , Lang.TITLE_DISMOUNT_VEHICLE.getValue ( true ) , "" , 0 , 20 , 0 );
		} else {
			ActionBarUtil.send ( player.get ( ) , 
					Lang.BAR_SECONDS_TO_DISMOUNT.getValueReplacingNumber ( String.valueOf ( (int) ( Config.VEHICLE_SECONDS_TO_DISMOUNT.getAsInteger ( ) 
							- GameManager.getTimeInGame ( TimeUnit.SECONDS ) ) ) , true ) );
		}
	}
	
	@Override
	public void join ( ) {
		Validate.isTrue ( handle == null && base == null , "this vehicle has already been spawned!" );
		Validate.isTrue ( player != null && player.get ( ) != null , "invalid passenger!" );
		
		World world = location.getWorld ( );
		
		try {
			// handle initialization
			nms_handle = ConstructorReflection.newInstance ( type.getNmsClass ( ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "World" ) } , BukkitReflection.getHandle ( world ) );
			
			handle_id = (int) MethodReflection.invoke ( nms_handle , "getId" );
			handle    = (LivingEntity) MethodReflection.invoke ( nms_handle , "getBukkitEntity" );
			
			EntityReflection.setLocation ( nms_handle , location );
			
			base = world.spawn ( location , ArmorStand.class );
			base.setPassenger ( handle );
			base.setGravity ( false );
			base.setVisible ( false );
			base.setInvulnerable ( true );
			
			// handle spawning
			Object spawn_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutSpawnEntityLiving" ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "EntityLiving" ) } , nms_handle );
			
			BukkitReflection.sendPacket ( player.get ( ) , spawn_packet );
			
			if ( handle instanceof Ageable ) {
				((Ageable) handle).setAdult ( );
			}
			handle.setAI ( false );
			handle.setInvulnerable ( true );
			handle.setPassenger ( player.get ( ) );
			
			// mount packet
			Object mount_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutMount" ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "Entity" ) } , nms_handle );
			
			BukkitReflection.sendPacket ( player.get ( ) , mount_packet );
			
			// hiding player
			final Object hide_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutEntityDestroy" ) , 
					new Class < ? > [ ] { int [ ].class } , new int [ ] { (int) MethodReflection.invoke ( BukkitReflection.getHandle ( player.get ( ) ) , "getId" ) } );
			Bukkit.getOnlinePlayers ( ).forEach ( player -> {
				if ( !Objects.equals ( player.getUniqueId ( ) , BRVehicle.this.player.get ( ).getUniqueId ( ) ) ) {
					BukkitReflection.sendPacket ( player , hide_packet );
				}
			});
			
			// oh oh oh, here we go!
			passenger_inside = true;
		} catch ( ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException 
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
			ex.printStackTrace ( );
		}
	}
	
	@Override
	public void close ( ) {
		destroy ( );
		
		// mapping the time.
		DISMOUNT_MILLIS.put ( player.getUniqueId ( ) , System.currentTimeMillis ( ) );
	}

	@Override
	public void destroy ( ) {
		if ( handle == null || base == null ) {
			// we're returning instead of throwing an exception due there is no way for
			// developers to check whether this vehicle has already been spawned, then it is
			// recommended to call this method first.
			return;
		}
		
		if ( task != null ) {
			stop ( );
		}
		
		// showing player
		try {
			Class < ? >        add_packet_class = ClassReflection.getNmsClass ( "PacketPlayOutPlayerInfo" );
			Class < ? > add_packet_action_class = ClassReflection.getSubClass ( add_packet_class , "EnumPlayerInfoAction" );
			
			// add packet arguments
			Object add_argument_0 = MethodReflection.get ( add_packet_action_class , "valueOf" , String.class ).invoke ( null , "ADD_PLAYER" );
			Object add_argument_1 = Array.newInstance ( ClassReflection.getNmsClass ( "EntityPlayer" ) , 1 );
			Array.set ( add_argument_1 , 0 , BukkitReflection.getHandle ( player.get ( ) ) );
			// add packet
			final Object add_packet = ConstructorReflection.newInstance ( add_packet_class ,  
					new Class < ? > [ ] { add_packet_action_class , add_argument_1.getClass ( ) } , add_argument_0 , add_argument_1 );
			
			// spawn named entity packet
			final Object spawn_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutNamedEntitySpawn" ) , 
					new Class < ? > [ ] { ClassReflection.getNmsClass ( "EntityHuman" ) } , BukkitReflection.getHandle ( player.get ( ) ) );

			Bukkit.getOnlinePlayers ( ).forEach ( player -> {
				if ( !Objects.equals ( player.getUniqueId ( ) , BRVehicle.this.player.get ( ).getUniqueId ( ) ) ) {
					BukkitReflection.sendPacket ( player , add_packet );
					BukkitReflection.sendPacket ( player , spawn_packet );
				}
			});
			
			// now we can close
			handle.eject ( );
			base.eject ( );
			
			Object destroy_packet = ConstructorReflection.newInstance ( ClassReflection.getNmsClass ( "PacketPlayOutEntityDestroy" ) , 
					new Class < ? > [ ] { int [ ].class } , new int [ ] { handle_id } );
			BukkitReflection.sendPacket ( player.get ( ) , destroy_packet );
			
			handle.remove ( );
			base.remove ( );
			handle = null;
			base   = null;
		} catch ( ClassNotFoundException | NoSuchMethodException | SecurityException 
				| InstantiationException | IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NegativeArraySizeException ex ) {
			ex.printStackTrace ( );
		}
		
		// player ejecting
		player.get ( ).setVelocity ( player.get ( ).getLocation ( ).getDirection ().setY ( 1 ).multiply ( 1.8D ) );
		ParticleEffect.FIREWORKS_SPARK.display ( 0.0F , 0.0F , 0.0F , 0.1F , 40 , player.get ( ).getLocation ( ) , 9999 );
		Bukkit.getPluginManager ( ).callEvent ( new MemberJumpFromFlyingVehicle ( BRPlayer.getBRPlayer ( player.get ( ) ) , this ) );
		
		// mapping the time.
		if ( !DISMOUNT_MILLIS.containsKey ( player.getUniqueId ( ) ) ) {
			DISMOUNT_MILLIS.put ( player.getUniqueId ( ) , System.currentTimeMillis ( ) );
		}
		
		// goodbye
		passenger_inside = false;
	}
}