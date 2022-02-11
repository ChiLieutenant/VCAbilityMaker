package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.vantahub.chilieutenant.abilitymaker.GeneralMethods;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.events.AbilityDamageEvent;
import com.vantahub.chilieutenant.stats.StatPlayer;

public class JaafarL implements Listener{

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		StatPlayer sp = new StatPlayer(event.getPlayer());
		if(!sp.getCurrSkill().equalsIgnoreCase("DarkBall")) {
			return;
		}
		new DarkBall(event.getPlayer());
	}
	
	@EventHandler
	public void onClick2(PlayerInteractEvent event) {
		if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		StatPlayer sp = new StatPlayer(event.getPlayer());
		if(!sp.getCurrSkill().equalsIgnoreCase("PowerfulPull")) {
			return;
		}
		new PowerfulPull(event.getPlayer());
	}
	
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		if(event.getPlayer().isSneaking()) {
			return;
		}
		StatPlayer sp = new StatPlayer(event.getPlayer());
		if(!sp.getCurrSkill().equalsIgnoreCase("Explode")) {
			return;
		}
		new Explode(event.getPlayer());
	}
	
	@EventHandler
	public void onShift2(PlayerToggleSneakEvent event) {
		if(event.getPlayer().isSneaking()) {
			return;
		}
		StatPlayer sp = new StatPlayer(event.getPlayer());
		if(!sp.getCurrSkill().equalsIgnoreCase("UncontrolledPower")) {
			return;
		}
		new UncontrolledPower(event.getPlayer());
	}
	
	@EventHandler
	public void onHit(AbilityDamageEvent event) {
		if(event.getAbility().getName().equalsIgnoreCase("UncontrolledPower")) {
			return;
		}
		if(!MainAbility.hasAbility(event.getSource(), UncontrolledPower.class)) {
			return;
		}
		UncontrolledPower kg = MainAbility.getAbility(event.getSource(), UncontrolledPower.class);
		if(!kg.getMode().equalsIgnoreCase("hit")) {
			return;
		}
		kg.hit(event.getEntity());
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if(event.getAction() != Action.LEFT_CLICK_AIR) {
			return;
		}
		StatPlayer sp = new StatPlayer(event.getPlayer());
		if(sp.getCurrSkill().equalsIgnoreCase("")) {
			return;
		}
		if(GeneralMethods.getTargetedEntity(event.getPlayer(), 15) == null){
			return;
		}
		new AutoAttack(event.getPlayer(), GeneralMethods.getTargetedEntity(event.getPlayer(), 15));
	}
	
}
