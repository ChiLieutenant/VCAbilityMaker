package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.vantahub.chilieutenant.abilitymaker.DamageHandler;
import com.vantahub.chilieutenant.abilitymaker.GeneralMethods;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.ParticleEffect;

public class Explode extends MainAbility{

	private List<Location> balls = new ArrayList<Location>();
	
	public Explode(Player player) {
		super(player);

		if(aPlayer.isOnCooldown(this)) {
			return;
		}
		if(hasAbility(player, Explode.class)) {
			return;
		}
		if(aPlayer.getdarkBalls().size() <= 0) {
			return;
		}
		for(Location loc : aPlayer.getdarkBalls()) {
			if(loc.distance(player.getLocation()) < 20) {
				balls.add(loc);
			}
		}
		if(balls.size() <= 0) {
			return;
		}
		aPlayer.addCooldown(this);
		start();
		
	}

	@Override
	public String getChampion() {
		// TODO Auto-generated method stub
		return "Jaafar";
	}

	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 6000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Patlat";
	}

	@Override
	public void progress() {
		// TODO Auto-generated method stub
		for(Location loc : balls) {
			ParticleEffect.REDSTONE.display(loc, 500, 1, 0.5, 1, 0.005, new Particle.DustOptions(Color.fromRGB(75, 0, 75), 1));
			for(Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 2)) {
				if(e.getUniqueId() != player.getUniqueId() && e instanceof LivingEntity) {
					DamageHandler.damageEntity(e, player, 400, this, "AP");
				}
			}
			aPlayer.removeBall(loc);
		}
		remove();
	}

	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}
}
