package com.hotmail.AdrianSR.BattleRoyale.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.hotmail.AdrianSR.BattleRoyale.game.Member;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleType;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.BRVehicleParticle;
import com.hotmail.AdrianSR.BattleRoyale.vehicles.parachute.ParachuteColor;

/**
 * Represents the players
 * stats container.
 * <p> 
 * @author AdrianSR
 */
public final class DTBPlayer {
	
	/**
	 * Member owner.
	 */
	private final Member member;
	
	/**
	 * Database data.
	 */
	private int 										kills;
	private int 									won_games;
	private int 								   lost_games;
	private BRVehicleType 						 vehicle_type;
	private BRVehicleParticle 				 vehicle_particle;
	private ParachuteColor 					  parachute_color;
	private List<BRVehicleType> 		   purchased_vehicles;
	private List<BRVehicleParticle> purchased_vehicle_particles;
	private List<ParachuteColor>   purchased_parachute_colors;
	
	/**
	 * Construct a new stats
	 * container for a {@link Member}.
	 * <p>
	 * @param member the {@link Member} owner
	 * of this container.
	 */
	public DTBPlayer(final Member member) {
		// load member.
		this.member = member;
		
		// make lists of purchases.
		this.purchased_vehicles			 = new ArrayList<BRVehicleType>();
		this.purchased_vehicle_particles = new ArrayList<BRVehicleParticle>();
		this.purchased_parachute_colors  = new ArrayList<ParachuteColor>();
		
		// load defaults.
		this.vehicle_type     = BRVehicleType.HORSE;
		this.vehicle_particle = BRVehicleParticle.MAGIC_GREEN;
		this.parachute_color  = ParachuteColor.BLACK;
		
		// add defaults to purchased list.
		this.purchased_vehicles.add(vehicle_type);
		this.purchased_vehicle_particles.add(vehicle_particle);
		this.purchased_parachute_colors.add(parachute_color);
	}
	
	public BRVehicleType getUsingVehicleType() {
		return vehicle_type;
	}

	public void setUsingVehicleType(BRVehicleType vehicle_type) {
		if (vehicle_type != null) {
			this.vehicle_type = vehicle_type;
		}
	}

	public void addPurchasedVehicle(BRVehicleType vehicle_type) {
		if (!purchased_vehicles.contains(vehicle_type)) {
			purchased_vehicles.add(vehicle_type);
		}
	}

	public List<BRVehicleType> getPurchasedVehicles() {
		return Collections.unmodifiableList(purchased_vehicles);
	}

	public BRVehicleParticle getUsingVehicleParticle() {
		return vehicle_particle;
	}

	public void setUsingVehicleParticle(BRVehicleParticle vehicle_particle) {
		if (vehicle_particle != null) {
			this.vehicle_particle = vehicle_particle;
		}
	}

	public void addPurchasedVehicleParticle(BRVehicleParticle vehicle_particle) {
		if (!purchased_vehicle_particles.contains(vehicle_particle)) {
			purchased_vehicle_particles.add(vehicle_particle);
		}
	}

	public List<BRVehicleParticle> getPurchasedVehicleParticles() {
		return Collections.unmodifiableList(purchased_vehicle_particles);
	}

	public ParachuteColor getUsingParchuteColor() {
		return parachute_color;
	}

	public void setUsingParachuteColor(ParachuteColor parachute_color) {
		if (parachute_color != null) {
			this.parachute_color = parachute_color;
		}
	}

	public void addPurchasedParachuteColor(ParachuteColor parachute_color) {
		if (!purchased_parachute_colors.contains(parachute_color)) {
			purchased_parachute_colors.add(parachute_color);
		}
	}

	public List<ParachuteColor> getPurchasedParachuteColors() {
		return Collections.unmodifiableList(purchased_parachute_colors);
	}

	/**
	 * Get member kills.
	 * <p>
	 * @return total member kills.
	 */
	public int getKills() {
		return kills;
	}

	/**
	 * Set member kills.
	 * <p>
	 * @param kills total member kills.
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Register kill.
	 * <p>
	 * @param amount the kills amount.
	 */
	public void addKill(int amount) {
		this.kills += amount;
	}
	
	/**
	 * Get member won games.
	 * <p>
	 * @return total member won games.
	 */
	public int getWonGames() {
		return won_games;
	}

	/**
	 * Set member won games.
	 * <p>
	 * @param won_games the won games amount.
	 */
	public void setWonGames(int won_games) {
		this.won_games = won_games;
	}
	
	/**
	 * Register won game.
	 * <p>
	 * @param amount the amount to register.
	 */
	public void addWonGames(int amount) {
		this.won_games += amount;
	}

	/**
	 * Get member lost games.
	 * <p>
	 * @return total member lost games.
	 */
	public int getLostGames() {
		return lost_games;
	}

	/**
	 * Set member lost games.
	 * <p>
	 * @param lost_games the lost games amount.
	 */
	public void setLostGames(int lost_games) {
		this.lost_games = lost_games;
	}

	/**
	 * Register lost game.
	 * <p>
	 * @param amount the amount to register.
	 */
	public void addLostGames(int amount) {
		this.lost_games += amount;
	}
	
	/**
	 * Register stat from type.
	 * <p>
	 * @param stat the stat type to register..
	 * @param amount the amount to register.
	 */
	public void addStat(StatType stat, int amount) {
		switch(stat) {
		case KILLS:
			addKill(amount);
			break;
		case LOST_GAMES:
			addLostGames(amount);
			break;
		case WON_GAMES:
			addWonGames(amount);
			break;
		}
	}
	
	/**
	 * Get stats member owner.
	 *<p>
	 * @return member owner of this.
	 */
	public Member getMember() {
		return member;
	}
	
	/**
	 * Save this to database.
	 */
	public void save(boolean save_stats, boolean save_settings, boolean save_cosmetics) {
		// update stats in database.
		DatabaseManager.saveData(this, save_stats, save_settings, save_cosmetics);
	}
	
	/**
	 * Print data to console.
	 */
	public void printData() {
		System.out.println(member.getName() + " Stats: ");
		System.out.println("- kills: " + this.getKills());
		System.out.println("- won games: " + this.getWonGames());
		System.out.println("- lost games: " + this.getLostGames());
		System.out.println("- vehicle type: " + this.getUsingVehicleType().name());
		System.out.println("- vehicle particles: " + this.getUsingVehicleParticle().name());
		System.out.println("- parachute color: " + this.getUsingParchuteColor().name());
	}
}
