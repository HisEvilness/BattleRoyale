package com.hotmail.AdrianSR.BattleRoyale.vehicles;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.util.Vector;

import com.hotmail.AdrianSR.BattleRoyale.config.money.Money;

import de.slikey.effectlib.util.ParticleEffect;

public enum BRVehicleParticle {
	
	RAINBOW("Rainbow", Material.APPLE, 0),
	FLAMES("Flames", Material.BLAZE_POWDER, 30, ParticleEffect.FLAME),
	RAIN("Rain", Material.WATER_BUCKET, 60, ParticleEffect.WATER_DROP),
	LAVA("Lava", Material.LAVA_BUCKET, 7, ParticleEffect.LAVA),
	UNDER_WATER("Under Water", Material.RAW_FISH, 60, ParticleEffect.SUSPENDED_DEPTH),
	FIREWORK("Firework", Material.FIREWORK, 30, ParticleEffect.FIREWORKS_SPARK),
	SMOKE("Smoke", Material.TORCH, 30, ParticleEffect.SMOKE_NORMAL),
	CRIT("Crit", Material.WOOD_SWORD, 30, ParticleEffect.CRIT),
	MAGIC_GREEN("Magic Green", Material.EMERALD, 30, ParticleEffect.VILLAGER_HAPPY),
	HATER("Hater", Material.REDSTONE, 6, ParticleEffect.VILLAGER_ANGRY),
	ENCHANTMENT("Enchantment", Material.ENCHANTMENT_TABLE, 60, ParticleEffect.ENCHANTMENT_TABLE),
	HEARTS("Hearts", Material.COOKIE, 8, ParticleEffect.HEART);
	
	/**
	 * Global enum values.
	 */
	private static final List<Color> RAINBOW_COLORS = Arrays.asList(Color.AQUA, Color.BLUE, Color.RED, Color.LIME,
			Color.YELLOW, Color.GREEN, Color.WHITE);
	
	/**
	 * Enum values.
	 */
	private int                    amount;
	private ParticleEffect[]      effects;
	private String            screen_name;
	private Material          screen_icon;
	private Permission         permission;
	private boolean permission_registered;
	
	BRVehicleParticle(String screen_name, Material icon, int amount, ParticleEffect... effects) {
		this.effects     = effects;
		this.screen_name = screen_name;
		this.screen_icon = icon;
		this.amount      = amount;
		this.permission  = new Permission("br.vehicle.particle." + super.name());
	}
	
	public String getScreenName() {
		// return Lang.valueOf("VEHIClE_PARTICLE_" + super.name() + "_NAME").toString();
		return screen_name;
	}
	
	public ItemStack getScreenIcon() {
		return new ItemStack(screen_icon, 1);
	}
	
	public Material getScreenIconMaterial() {
		return screen_icon;
	}
	
	public int getCost() {
		return Money.valueOf(super.name() + "_PARTICLE_COST").getAsNotNullInteger();
	}
	
	public void sendTo(final Location location, final List<Player> viewers) {
		// send rainbow.
		if (this == RAINBOW) {
			displayRainbow(location, viewers);
			return;
		}
		
		for (ParticleEffect effect : effects) {
			effect.display(0.5F, 0.5F, 0.5F, 0.1F, amount, location, viewers);
		}
	}
	
	public void sendTo(final Location location) {
		// send depending effect.
		if (this == RAINBOW) {
			displayRainbow(location, null);
			return;
		}
		
		for (ParticleEffect effect : effects) {
			effect.display(0.5F, 0.5F, 0.5F, 0.1F, amount, location, 300);
		}
	}
	
	private static final Random RANDOM = new Random(System.nanoTime());
	
	private static void displayRainbow(final Location where, List<Player> viewers) {
		final Location to = where.clone(); /* protect where by cloning it */
		for (int x = 0; x < 15; x++) {
			for (Color color : RAINBOW_COLORS) {
				// randomize to.
				to.add(getRandomCircleVector().multiply(RANDOM.nextDouble() * 0.3D));
				to.add(0, Math.min(RANDOM.nextFloat(), 0.1) * 0.25, 0);
				
				// display.
				if (viewers == null || viewers.isEmpty()) {
					ParticleEffect.REDSTONE.display(to, color, 300);
				} else {
					ParticleEffect.REDSTONE.display(to, color, 300, viewers);
				}
			}
		}
	}
	
	public static Vector getRandomCircleVector() {
		double rnd, x, z;
		rnd = RANDOM.nextDouble() * 2 * Math.PI;
		x = Math.cos(rnd);
		z = Math.sin(rnd);

		return new Vector(x, 0, z);
	}
	
	private void registerPermission() {
		// check is not registered.
		if (!permission_registered) {
			// register permission.
			Bukkit.getPluginManager().addPermission(permission);
			
			// recalculate permissibles.
			permission.recalculatePermissibles();
			
			// set registered.
			permission_registered = true;
		}
	}
	
	public boolean hasPermission(final Player player) {
		// check permission registered.
		registerPermission();
		
		// check has permission.
		return player.hasPermission(permission);
	}
}
