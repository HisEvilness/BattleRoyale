package com.hotmail.AdrianSR.BattleRoyale.util;

import org.bukkit.Bukkit;

/**
 * An enum for the various bukkit versions.
 * <p>
 * @author AdrianSR
 */
public enum BukkitVersion {

    /**
     * 1.8 versions.
     */
    v1_8_R1,
    v1_8_R2,
    v1_8_R3,
    
    /**
     * 1.9 versions.
     */
    v1_9_R1,
    v1_9_R2,
    
    /**
     * latest versions.
     */
    v1_10_R1,
    v1_11_R1,
    v1_12_R1,
    v1_13_R1,
    v1_13_R2,
    v1_14_R1;

    /**
     * Version id.
     */
    private final int id;

    /**
     * Construct the bukkit version.
     */
    BukkitVersion() {
        this.id = Integer.valueOf(name().substring(1).replace("_", "").replace("R", "0"));
    }

    /**
     * Get server version id.
     * <p>
     * @return version id.
     */
    public int getID() {
        return id;
    }
    
    /**
     * @return true if there is Material enum
     * constants with the prefix 'LEGACY_'.
     */
    public boolean isLegacy() { /* The 'LEGACY_' started from the v1_13_R1 */
        return isNewerEqualsThan(BukkitVersion.v1_13_R1);
    }

    /**
     * Check this version is newer than other.
     * <p>
     * @param version the version to compare.
     * @return true if is newer.
     */
    public boolean isNewerThan(BukkitVersion version) {
        return version != null && getID() > version.getID();
    }

    /**
     * Check this version is newer or equals than other.
     * <p>
     * @param version the version to compare.
     * @return true if is newer or equals.
     */
    public boolean isNewerEqualsThan(BukkitVersion version) {
        return version != null && getID() >= version.getID();
    }

    /**
     * Check this version is older than other.
     * <p>
     * @param version the version to compare.
     * @return true if is older.
     */
    public boolean isOlderThan(BukkitVersion version) {
        return version != null && getID() < version.getID();
    }

    /**
     * Check this version is older or equals than other.
     * <p>
     * @param version the version to compare.
     * @return true if is older or equals.
     */
    public boolean isOlderEqualsThan(BukkitVersion version) {
        return version != null && getID() <= version.getID();
    }

    /**
     * Example: ( 1_7_R1 == 1_7_R3 ) = true ( 1_8_R1 == 1_7_R1 ) = false
     */
    public boolean sameVersion(BukkitVersion other) {
        return name().substring(0, name().indexOf("_R"))
                .equals(other.name().substring(0, other.name().indexOf("_R")));
    }

    /**
     * Example: ( 1_8_R3 == 1_7_R3 ) = true ( 1_8_R1 == 1_8_R3 ) = false
     */
    public boolean sameSubVersion(BukkitVersion other) {
        return name().substring((name().indexOf("R") + 1))
                .equals(other.name().substring((other.name().indexOf("R") + 1)));
    }
    
    /**
     * Examples:
     * - If the current version is 'v1_8_R3', this will return 'v1_8_R1';
     * - If the current version is 'v1_9_R2', this will return 'v1_9_R1';
     */
    public BukkitVersion getFirstOfThisVersion() {
        BukkitVersion first = this;
        for (BukkitVersion other : values()) {
            if (!other.sameVersion(this)) {
                continue;
            }
            
            if (other.isOlderThan(first)) {
                first = other;
            }
        }
        return first;
    }
    
    /**
	 * Get the version of this server.
	 * <p>
	 * @return the version of this server.
	 */
	public static BukkitVersion getServerVersion() {
		final String packageName = Bukkit.getServer().getClass().getPackage().getName();
		final String version     = packageName.substring(packageName.lastIndexOf(".") + 1);
		return valueOf(version);
	}
}
