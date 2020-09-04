package com.hotmail.AdrianSR.BattleRoyale.events;

import org.bukkit.event.HandlerList;

import com.hotmail.AdrianSR.BattleRoyale.config.main.Config;
import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicle;
import com.hotmail.adriansr.core.events.CustomEvent;

/**
 * Thrown whenever a {@link Member} jumps from
 * his {@link BRVehicle}.
 * <p>
 * @author AdrianSR
 */
public class MemberJumpFromFlyingVehicle extends CustomEvent {
	
	private static final HandlerList HANDLER_LIST = new HandlerList ( );
	
	@Override
	public HandlerList getHandlers ( ) {
		return HANDLER_LIST;
	}
	
	public static HandlerList getHandlerList ( ) {
		return HANDLER_LIST;
	}

	/**
	 * Class values.
	 */
	private final Member          member;
	private final BRVehicle      vehicle;
	private       boolean auto_parachute;

	public MemberJumpFromFlyingVehicle(final Member member, final BRVehicle vehicle) {
		/* load member and his vehicle */
		this.member  = member;
		this.vehicle = vehicle;
		
//		/* load auto-parachute flag from config */
		this.auto_parachute = Config.VEHICLE_AUTO_PARACHUTE_ON_JUMP.getAsBoolean ( );
	}

	/**
	 * Returns jumping {@link Member}.
	 * <p>
	 * @return jumping member.
	 */
	public Member getMember ( ) {
		return member;
	}
	
	/**
	 * Returns {@link BRVehicle} the
	 * from which the {@link Member} jumps.
	 * <p>
	 * @return vehicle.
	 */
	public BRVehicle getVehicle ( ) {
		return vehicle;
	}
	
	/**
	 * @return true if the parachute
	 * will open automatically.
	 */
	public boolean isAutoParachute ( ) {
		return auto_parachute;
	}
	
	/**
	 * Set open parachute automatically.
	 * <p>
	 * @param auto_parachute open parachute automatically?
	 */
	public void setAutoParachute ( boolean auto_parachute ) {
		this.auto_parachute = auto_parachute; 
	}
}