package com.vantahub.chilieutenant.abilitymaker;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class main extends JavaPlugin implements Listener{
	
    private static main instance;
    public static main getInstance() {return instance;}
	
    @Override
    public void onEnable() {
    	instance = this;
		Bukkit.getLogger().info(ChatColor.AQUA + "AbilityMaker has started!");
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getOnlinePlayers().forEach(player -> new AbilityPlayer(player.getUniqueId(), player.getName()));
		new BukkitRunnable() {
			public void run() {
				if(!MainAbility.getAbilities().isEmpty()) {
					final Iterator<MainAbility> iterator = MainAbility.getAbilities().iterator();
					while(iterator.hasNext()) {
						final MainAbility abil = iterator.next();
						if(abil.isRemoved()) {
							MainAbility.getAbilities().remove(abil);
							return;
						}else {
							abil.progress();
						}
					}
				}
			}
		}.runTaskTimer(this, 0, 1);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		new AbilityPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getName());
		Bukkit.getLogger().info("Created an AbilityPlayer for " + event.getPlayer().getName());
	}
	
	public void onDisable(){
        Bukkit.getLogger().info(ChatColor.AQUA + "AbilityMaker plugin has disabled!");
		for(TempBlock tbl : TempBlock.instances.values()) {
			tbl.revertBlock();
		}
		TempBlock.instances.clear();
    }
}
