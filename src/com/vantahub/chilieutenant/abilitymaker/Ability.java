package com.vantahub.chilieutenant.abilitymaker;

import org.bukkit.Location;

public interface Ability {

	public String getName();
	public long getCooldown();
	public void progress();
	public String getChampion();
	public Location getLocation();
	public void load();
}
