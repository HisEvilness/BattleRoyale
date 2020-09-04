package com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.BattleRoyale.game.BRPlayer;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.LocUtils;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Movable;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.StandBlockFace;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Vehicle;
import com.hotmail.adriansr.core.util.entity.UUIDEntity;
import com.hotmail.adriansr.core.util.entity.UUIDPlayer;
import com.hotmail.adriansr.core.util.math.DirectionUtil;
import com.hotmail.adriansr.core.util.math.LocationUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;
import com.hotmail.adriansr.core.util.sound.UniversalSound;

/**
 * Represents the Battle Royale Parachute.
 * <p>
 * @author AdrianSR
 */
public final class Parachute extends Movable implements Listener {

	/**
	 * Use leashs (ropes).
	 */
	public static final boolean USE_LEASH = true;

	/**
	 * Global class vals.
	 */
	private static final long   SECONDS_TO_DISMOUNT = 2l;
	private static final double FALL_VELOCITY       = 0.5F;

	/**
	 * Class values.
	 */
//	private final World                w;
	private final UUIDPlayer player;
	private final ParachuteColor   color;
	private       boolean     registered;
	private       boolean       finished;
	private       boolean         moving;
	private       long         openMills;
	private       List<BukkitTask> tasks;
	
	/**
	 * Construct a parachute.
	 * 
	 * @param owner the parachute owner.
	 */
	public Parachute(final Player owner) {
		// super.
		super(LocationUtil.roundUpYaw(owner.getLocation()), false, false, BattleRoyale.getInstance());
		
		// get data.
//		this.w      = owner.getWorld();
		this.player = new UUIDPlayer(owner);
		this.tasks  = new ArrayList<BukkitTask>();
		this.color  = BRPlayer.getBRPlayer(owner).getDatabasePlayer().getUsingParchuteColor();
		
		/* clean movables */
		for (Movable mov : new ArrayList<Movable>(Movable.MOVABLES)) {
			if (!(mov instanceof Parachute)) {
				continue;
			}
			
			Parachute par = (Parachute) mov;
			if (par.player != null && par.player.getUniqueId().equals(this.player.getUniqueId())) {
				Movable.MOVABLES.remove(mov);
			}
		}
		Movable.MOVABLES.add(this);
		
		// model.
		model();

		// register.
		register();
	}

	@Override
	protected void model() {
		// save open millis.
		openMills = System.currentTimeMillis();

		// change main stand visibility.
		this.setVisibleMainStand(false);

		// set destroy on ground.
		this.setDestroyOnGround(true);

		// get facind directions.
		final BlockFace faceF      = DirectionUtil.getBlockFace(getYaw()); // get facing direction.
		final BlockFace leftF      = DirectionUtil.getLeftFace(faceF);
		final StandBlockFace face  = StandBlockFace.fromBlockFace(faceF);
		final StandBlockFace left  = StandBlockFace.fromBlockFace(leftF);
		final StandBlockFace right = left.getOppositeFace();

		// get leashs holder.
//		ArmorStand leashHolder = null;

		// add part.
		for (int x = 0; x < 7; x++) {
			// get spawn. (cleaning picth)
			Location spawn = getSpawn ( ).clone ( );
			spawn.setPitch ( 0F );

			// get part data.
			boolean gravity = false;
			boolean visible = false;

			// modify spawn, dependig of part number.
			switch (x) {
			// ALAS
			case 0:
				// set up.
				LocUtils.add(spawn, BlockFace.UP, 2.8);

				// add rotation.
				spawn.setYaw(spawn.getYaw() + -90);
				break;

			case 1:
				// set up.
				LocUtils.add(spawn, BlockFace.UP, 2.8);

				// add rotation.
				spawn.setYaw(spawn.getYaw() + 90);
				break;

			// CENTER
			case 2:
				// set up.
				LocUtils.add(spawn, BlockFace.UP, 2.56);

				// add at left.
				LocUtils.add(spawn, left, 0.2);
				break;

			case 3:
				// set up.
				LocUtils.add(spawn, BlockFace.UP, 2.56);

				// add at right.
				LocUtils.add(spawn, right, 0.2);
				break;

			// LEASHS HOLDER
			case 4:
				// add to up.
				LocUtils.add(spawn, StandBlockFace.UP, 2);

				// add at left.
				LocUtils.add(spawn, left, 1.2);

				// add to back.
				LocUtils.add(spawn, face.getOppositeFace(), 1);
				break;

			// LEASHS
//			case 5:
//				// set up.
//				LocUtils.add(spawn, BlockFace.UP, 2.8);
//
//				// add at left.
//				LocUtils.add(spawn, left, 3);
//
//				// add to back.
//				LocUtils.add(spawn, face.getOppositeFace(), 0.6);
//
//				// change entity class.
//				clazz = Chicken.class;
//				break;
//
//			case 6:
//				// set up.
//				LocUtils.add(spawn, BlockFace.UP, 2.8);
//
//				// add at right.
//				LocUtils.add(spawn, right, 3);
//
//				// add to back.
//				LocUtils.add(spawn, face.getOppositeFace(), 0.6);
//
//				// change entity class.
//				clazz = Chicken.class;
//				break;
			}

			// add part.
			final UUIDEntity < ArmorStand > part = addPart ( ArmorStand.class , spawn, visible, gravity);

			// modify part.
			switch (x) {
			case 0:
			case 1: {
				// get entity.
				ArmorStand stand = part.get();

				// set helmet.
//				stand.setHelmet(new ItemStack(Material.BANNER, 1, GameUtils.getParachuteColor((Player)player.get()).getValue()));
//				stand.setHelmet(new ItemStack(Material.BANNER, 1, PARACHUTE_COLOR.getValue()));
				stand.setHelmet(new ItemStack(Material.BANNER, 1, color.getValue()));

				// rotate head.
				stand.setHeadPose(new EulerAngle(4.64, 0, 0));
				break;
			}

			case 2:
			case 3: {
				// get entity.
				ArmorStand stand = part.get();

				// set helmet.
				stand.setHelmet(new ItemStack(Material.WOOD_PLATE, 1));
				break;
			}

//			// LEASH HOLDER
//			case 4: {
//				// get entity
//				ArmorStand stand = part.get();
//				leashHolder = stand;
//				break;
//			}
//
//			// CHICKENS
//			case 5:
//			case 6: {
//				// get entity.
//				Chicken ch = part.get();
//
//				// set leash holder.
//				ch.setLeashHolder(leashHolder);
//				break;
//			}
			}
		}
	}

	/**
	 * Custom Movement.
	 */
	@Override
	public void startMovement() {
		// check is not already moving.
		if (moving) {
			return;
		}

		// set moving.
		moving = true;

		// start task.
		tasks.add(SchedulerUtil.runTaskTimer ( ( ) -> {
			// get player.
			final Player p = player.get();
			if (p == null || !p.isOnline()) { // check.
				destroy();
				return;
			}
			
			// destroy if is on ground.
			if (isOnGround()) {
				destroy();
				return;
			}

			// set player look direction.
			final Location toSee = getLocation();
			
			// get yaws diference.
//			float dif = DirectionUtils.resolveTo360Raw(DirectionUtils.resolveTo360Raw(p.getLocation().getYaw())
//					- DirectionUtils.resolveTo360Raw(toSee.getYaw()));
			float dif = DirectionUtil.normalize ( DirectionUtil.normalize ( p.getLocation ( ).getYaw ( ) ) 
					- DirectionUtil.normalize ( toSee.getYaw ( ) ) );
			
			// check dif.
			if (dif < 0) {
				dif = Math.max(dif, -10.0F);
			} else {
				dif = Math.min(dif, 10.0F);
			}
			
			// get yaw to.
//			float yawTo = DirectionUtils.resolveTo360(toSee.getYaw() + dif);

			// add rotation.
			toSee.setYaw(p.getLocation().getYaw()); // yawTo

			// set to down.
			toSee.setPitch(30.0F);

			// get direction.
			final Vector direction = toSee.getDirection();

			// decrease velocity.
			direction.multiply(FALL_VELOCITY);

			// set location.
			Location to = toSee.add ( direction );
			if ( to.getY ( ) >= 0 ) {
				setLocation ( toSee.add ( direction ) );
			} else {
				// parachute is into the void
				destroy ( );
			}
		}, 0, 0, BattleRoyale.getInstance()));
	}

	/**
	 * Mount the desired {@code player} on this parachute.
	 * <p>
	 * Note that spectator players will be ignored.
	 */
	@Override
	public void mount ( final Player player ) {
		if ( player.getGameMode ( ) == GameMode.SPECTATOR 
				|| BRPlayer.getBRPlayer ( player ).isSpectator ( ) ) {
			return;
		}
		
		// mount from super.
		super.mount(player);

		// play open sound.
		for (int x = 0; x < 4; x++) {
			player.playSound(player.getLocation(), UniversalSound.ENDERDRAGON_WINGS.asBukkit(), 4.0F, 2.5F); // ENDERDRAGON_WINGS
		}
		
		Vehicle.setCheckFly ( false );
		player.setAllowFlight ( true );
		player.setFlying ( true );

		// start movement.
		startMovement();
	}
	
	public UUIDPlayer getOwner() {
		return this.player;
	}
	
	public boolean isOwnerInside() {
		if (!(this.player.get() instanceof Player)
				|| !((Player)this.player.get()).isOnline()
				|| isFinished()) {
			return false;
		}
		
		if (this.getMainEntity() == null || this.getMainEntity().get() == null
				|| !(this.getMainEntity().get().getPassenger() instanceof Player)
				|| !this.getMainEntity().get().getPassenger().getUniqueId().equals(this.player.getUniqueId())) {
			return false;
		}
		return true;
	}

	/**
	 * @return true if the player can dismount the parachute.
	 */
	public boolean canDismount() {
		final long current = System.currentTimeMillis();
		final long open    = (current - openMills) / 1000;
		return open       >= SECONDS_TO_DISMOUNT;
	}
	
	/**
	 * @return true if already landed.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Register listeners.
	 */
	public void register() {
		// check is not already registered.
		if (registered) {
			return;
		}

		// register
		Bukkit.getPluginManager().registerEvents(this, BattleRoyale.getInstance());
		registered = true;
		return;
	}

	/**
	 * Unregister listeners.
	 * 
	 * @return this.
	 */
	public Parachute unregister() {
		if (registered) {
			HandlerList.unregisterAll(this);
			registered = false;
		}
		return this;
	}
	
	@EventHandler ( priority = EventPriority.LOWEST )
	public void onBreak ( EntityDamageEvent event ) {
		UUID entity_uuid = event.getEntity ( ).getUniqueId ( );
		
		if ( event.getCause ( ) != DamageCause.VOID ) {
			if ( getCustomParts ( ).stream ( ).filter ( entity -> entity.getUniqueId ( ).equals ( entity_uuid ) ).findAny ( ).isPresent ( ) 
					|| getParts ( ).stream ( ).filter ( entity -> entity.getUniqueId ( ).equals ( entity_uuid ) ).findAny ( ).isPresent ( ) ) {
				event.setCancelled ( true );
			}
		} else {
			destroy ( );
		}
		
		
		
//		final Entity ent = eve.getEntity();
//		for (Entity part : this.getCustomParts()) {
//			if (part != null && ent.getUniqueId().equals(part.getUniqueId())) {
//				eve.setCancelled(true);
//				break;
//			}
//		}
//		
//		for (UUIDEntity < ? > part : this.getParts()) {
//			if (part != null && ent.getUniqueId().equals(part.getUniqueId())) {
//				eve.setCancelled(true);
//				break;
//			}
//		}
	}

//	/**
//	 * Detect if the player is leaving the vehicle.
//	 */
//	@EventHandler(priority = EventPriority.HIGH)
//	public void onDismount(final EntityDismountEvent eve) {
//		final org.bukkit.entity.Entity ent = eve.getEntity();
//		if (!(ent instanceof Player)) {
//			return;
//		}
//
//		// check dismounted entity is the parachute chicken.
//		final org.bukkit.entity.Entity dism = eve.getDismounted();
//		if (!(dism instanceof ArmorStand) || !dism.getUniqueId().equals(getMainEntity().getUniqueId())) {
//			return;
//		}
//
//		/* remount */
//		SchedulerUtil.runTaskLater ( new Runnable() {
//			@Override
//			public void run() {
//				// mount again.
//				((ArmorStand) dism).setPassenger((Player) ent);
//			}
//		}, 5, BattleRoyale.getInstance());
//	}

	/**
	 * Detect if the player is leaving the server.
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(final PlayerQuitEvent eve) {
		// check is not fisnished.
		if (finished) {
			return;
		}

		// get player and check is this.
		final Player p = eve.getPlayer();
		if (!p.getUniqueId().equals(player.getUniqueId())) {
			return;
		}

		// leave the vehicle to avoid bugs.
		destroy();
	}

	@Override
	public void destroy() {
		if (finished) { /* check is not already finished */
			return;
		}

		for (BukkitTask task : tasks) { /* stop tasks */
			task.cancel(); // !!!
		}
		
		Player player = this.player.get();
		if (player != null && player.isOnline()) {
			player.setNoDamageTicks(20 * 2);
			player.setFlying(false);
			player.setAllowFlight(false);
			Vehicle.setCheckFly ( true );
		}
		
		/* finish */
		finished = true;
		unregister();
		super.destroy();
	}
}