package com.hotmail.AdrianSR.BattleRoyale.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import com.hotmail.AdrianSR.BattleRoyale.database.DTBPlayer;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.GameManager;
import com.hotmail.AdrianSR.BattleRoyale.game.managers.MembersLoader;
import com.hotmail.AdrianSR.BattleRoyale.main.BattleRoyale;
import com.hotmail.AdrianSR.BattleRoyale.util.entity.UUIDArmorStand;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.Movable;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.Parachute;
import com.hotmail.adriansr.core.util.packet.PacketAdapter;
import com.hotmail.adriansr.core.util.packet.PacketChannelHandler;
import com.hotmail.adriansr.core.util.packet.PacketEvent;
import com.hotmail.adriansr.core.util.packet.PacketListener;

/**
 * Represents a Battle Royale Player.
 * <p>
 * @author AdrianSR.
 */
public class BRPlayer implements Member {
	
	/**
	 * Global members map.
	 */
	private static final Map<UUID, Member> MEMBERS = new HashMap<UUID, Member>();
	
	/**
	 * Members Loader.
	 */
	private static MembersLoader LOADER;
	
	/**
	 * Initialize the members loader.
	 * 
	 * @param plugin the initializer.
	 */
	public static void initLoader(final BattleRoyale plugin) {
		// check is not already initialized.
		if (LOADER != null) {
			throw new UnsupportedOperationException("The Members loader is already initialized!");
		}
		
		// create instance.
		LOADER = new MembersLoader(plugin, MEMBERS);
		
		// register.
		LOADER.register();
	}
	
	/**
	 * Stop the members loader.
	 * 
	 * @param plugin the stoper.
	 */
	public static void stopLoader(final BattleRoyale plugin) {
		if (LOADER != null) { // check loader.
			LOADER.unregister();
			
			// set null instance.
			LOADER = null;
		}
	}
	
	/**
	 * Get BRPlayer from a bukkit player.
	 * 
	 * @param p the bukkit player.
	 * @return the BR player from the bukkit player.
	 */
	public static BRPlayer getBRPlayer(final Player p) {
		if (p == null) {
			return null;
		}
		
		/* could not be found in the cache ? load! */
		if (!MEMBERS.containsKey(p.getUniqueId())) {
			LOADER.load(p);
		}
		return (BRPlayer) MEMBERS.get(p.getUniqueId());
	}
	
	/**
	 * Get BRPlayer from a bukkit player Unique {@link UUID}.
	 * 
	 * @param id the bukkit player {@link UUID}
	 * @return the BR player from the bukkit player Unique {@link UUID} owner. 
	 */
	public static BRPlayer getBRPlayer(final UUID id) {
		return getBRPlayer(Bukkit.getPlayer(id));
	}
	
	/**
	 * Get BRPlayers.
	 * 
	 * @return a Unmodifiable Battle Royale Player list.
	 */
	public static List<BRPlayer> getBRPlayers() {
		return Collections.unmodifiableList(new ArrayList<BRPlayer>((Collection<? extends BRPlayer>) MEMBERS.values()));
	}
	
	/**
	 * Class values.
	 */
	private final String               name;
	private final UUID                   id;
	private Team                       team;
	private BRPlayerMode               mode;
	private boolean            hasParachute;
	private boolean                 knocked;
	private Member                  knocker;
	private boolean             reanimating;
	private Scoreboard                board;
	private final Map<String, Object>  data;
	private final DTBPlayer database_player;
	
	protected final PacketListener packet_listener;
	
	/* knock data */
	protected UUIDArmorStand knock_seat;
	
	/**
	 * Costruct a new Battle Royale player.
	 * 
	 * @param name the name.
	 * @param id the Unique {@link UUID}.
	 */
	public BRPlayer(final String name, final UUID id) {
		this.name			 = name;
		this.id  			 = id;
		this.data            = new HashMap<String, Object>();
		this.database_player = new DTBPlayer(this);
		this.hasParachute    = true;
		this.mode            = BRPlayerMode.WAITING;
		
		// this listener avoid the player to leave the knock seat or the parachute.
		this.packet_listener = new PacketAdapter ( ) {
			@Override public void onReceiving ( PacketEvent event ) {
				BRPlayer player = BRPlayer.getBRPlayer ( event.getPlayer ( ) );
				if ( !player.getUUID ( ).equals ( getUUID ( ) ) ) {
					return;
				}
				
				if ( player.isKnocked ( ) || Movable.MOVABLES.stream ( ).filter ( movable -> ( movable instanceof Parachute ) )
						.filter ( parachute -> ((Parachute) parachute).isOwnerInside ( ) 
								&& ((Parachute) parachute).getOwner ( ).getUniqueId ( ).equals ( getUUID ( ) ) ).findAny ( ).isPresent ( ) ) {
					event.setCancelled ( true );
				}
			}
		};
		PacketChannelHandler.addPacketListener ( "PacketPlayInSteerVehicle" , 
				PacketListener.Priority.LOWEST , packet_listener );
	}
	
	/**
	 * Save a data for
	 * the player.
	 * 
	 * @param key the key.
	 * @param obj the Object.
	 */
	public void setData(String key, Object obj) {
		// put data with his key.
		data.put(key, obj);
	}
	
	/**
	 * Get saved object in
	 * data.
	 * 
	 * @param key the key.
	 * @return saved object or null.
	 */
	public Object getData(String key) {
		return data.get(key);
	}
	
	/**
	 * Get data from
	 * class type.
	 * 
	 * @param key the key.
	 * @param clazz the assignable classe.
	 * @return a assinated object with the class, 
	 * or null if not.
	 */
	public <T> T getAssignableData(String key, Class<T> clazz) {
		// get object.
		final Object obj = data.get(key);
		if (obj != null 
				&& clazz.isAssignableFrom(obj.getClass())) { // check is assignable.
			return (T) obj;
		}
		return null;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUUID() {
		return id;
	}

	@Override
	public Team getTeam() {
		return team;
	}
	
	@Override
	public BRPlayerMode getPlayerMode() {
		return mode;
	}
	
	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(id);
	}
	
	/**
	 * Get database player.
	 * <p>
	 * @return the database player.
	 */
	public DTBPlayer getDatabasePlayer() {
		return database_player;
	}
	
	@Override
	public Member setTeam(final Team team) {
		this.team = team;
		return this;
	}
	
	@Override
	public Member setPlayerMode(BRPlayerMode mode) {
		Player player = getPlayer();
		if (player == null || !player.isOnline()) {
			return this;
		}
		
		setKnocked ( false );
		
		/* current gamemode if giving mode is null */
		if (mode == null) {
			mode = ( GameManager.isRunning() ? ( isLiving ( ) ? BRPlayerMode.PLAYING : BRPlayerMode.SPECTATOR ) : ( BRPlayerMode.WAITING ) );
		}
		
		boolean       visible = true;
		boolean          fly = false;
//		boolean invulnerable = false;
		switch (mode) {
		case PLAYING:
		case WAITING:
			visible = true;
			fly     = false;
//			invulnerable = false;
			break;
		case SPECTATOR:
			visible      = false;
			fly          = true;
//			invulnerable = true;
			break;
		}
		
		player.setAllowFlight(fly);
		player.setFlying(fly);
//		ReflectionUtils.setInvulnerable(player, invulnerable);
		
		for ( Player other : Bukkit.getOnlinePlayers ( ) ) {
			if ( other.getUniqueId ( ).equals ( getUUID ( ) ) ) {
				continue;
			}
			
			if ( visible ) {
				other.showPlayer ( player );
			} else {
				other.hidePlayer ( player );
			}
		}
		this.mode = mode;
		return this;
	}
	
	/**
	 * Set player has parachute.
	 * 
	 * @param has = has parachute?
	 */
	public void setHasParachute ( boolean has ) {
		hasParachute = has && isLiving ( );
	}
	
	@Override
	public boolean isOnline() {
		return getPlayer() != null && getPlayer().isOnline();
	}

	@Override
	public boolean hasTeam() {
		return team != null;
	}

	@Override
	public boolean isDead() {
		return !isLiving();
	}

	@Override
	public boolean isLiving() {
		return getPlayer() != null 
				&& !getPlayer().isDead() && isOnline()
				&& getPlayerMode() != BRPlayerMode.SPECTATOR;
	}
	
	@Override
	public boolean isKnocked ( ) {
		return knocked;
	}
	
	@Override
	public Member getKnocker ( ) {
		return knocker;
	}
	
	public UUIDArmorStand getKnockSeat ( ) {
		return knock_seat;
	}
	
	@Override
	public boolean isBeingReanimated() {
		return reanimating;
	}
	
	@Override
	public Scoreboard getScoreboard() {
		return board;
	}

	@Override
	public void setKnocked ( boolean knocked ) {
		final boolean was_knocked = this.knocked;
		if ( isLiving ( ) ) {
			this.knocked = knocked;
		} else {
			this.knocked = false;
		}
		
		if ( ( was_knocked != knocked ) && this.knocked ) {
			Player                  bukkit = getPlayer ( );
			ArmorStand knock_seat_instance = bukkit.getWorld ( ).spawn ( bukkit.getLocation ( ) , ArmorStand.class );
			
			knock_seat_instance.setVisible ( false );
			knock_seat_instance.setInvulnerable ( true );
			knock_seat_instance.setSmall ( true );
			knock_seat_instance.setPassenger ( bukkit );
			
			knock_seat = new UUIDArmorStand ( knock_seat_instance );
		} else {
			if ( knock_seat != null ) {
				ArmorStand knock_seat_instance = knock_seat.get ( );
				if ( knock_seat_instance != null ) {
					knock_seat_instance.eject ( );
					knock_seat_instance.remove ( );
				}
				knock_seat = null;
			}
		}
	}
	
	@Override
	public void setKnocker ( Member knocker ) {
		this.knocker = knocker;
	}
	
	@Override
	public void setReanimating(boolean reanimating) {
		this.reanimating = reanimating;
	}
	
	@Override
	public boolean isSpectator() {
		return getPlayerMode() == BRPlayerMode.SPECTATOR;
	}
	
	@Override
	public void setScoreboard(Scoreboard scoreboard) {
		// change member scoreboard.
		if (isOnline()) {
			getPlayer().setScoreboard(scoreboard);
		}
		
		// set scoreboard.
		this.board = scoreboard;
	}

	/**
	 * Check if the player has parachute.
	 * 
	 * @return true if has.
	 */
	public boolean hasParachute ( ) {
		return hasParachute || GameManager.getBattleMode ( ).isRedeployEnabled ( );
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}

		if (!(obj instanceof BRPlayer)) {
			return false;
		}
		return getUUID().equals(((BRPlayer) obj).getUUID());
	}
}