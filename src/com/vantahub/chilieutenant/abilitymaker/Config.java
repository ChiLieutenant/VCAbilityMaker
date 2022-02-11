package com.vantahub.chilieutenant.abilitymaker;

import de.leonhard.storage.Yaml;

public class Config {

	private MainAbility ability;
	private Yaml data;
	
	public Config(MainAbility ability) {
		this.ability = ability;
		this.data = new Yaml("config", "plugins/Champions");
	}
	
	public void set(String key, Object value) {
		data.set(ability.getChampion() + "." + ability.getName() + "." + key, value);
	}
	
	public int getInt(String key) {
		return data.getInt(ability.getChampion() + "." + ability.getName() + "." + key);
	}
	
	public double getDouble(String key) {
		return data.getDouble(ability.getChampion() + "." + ability.getName() + "." + key);
	}
	
	public String getString(String key) {
		return data.getString(ability.getChampion() + "." + ability.getName() + "." + key);
	}
	
	public boolean getBoolean(String key) {
		return data.getBoolean(ability.getChampion() + "." + ability.getName() + "." + key);
	}
	
	public long getLong(String key) {
		return data.getLong(ability.getChampion() + "." + ability.getName() + "." + key);
	}
	
	public Yaml getData() {
		return data;
	}
}
