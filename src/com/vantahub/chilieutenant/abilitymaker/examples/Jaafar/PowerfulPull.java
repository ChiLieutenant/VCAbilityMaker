package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.vantahub.chilieutenant.abilitymaker.DamageHandler;
import com.vantahub.chilieutenant.abilitymaker.GeneralMethods;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.ParticleEffect;

public class PowerfulPull extends MainAbility{

	private List<Location> balls = new ArrayList<Location>();
	
	public PowerfulPull(Player player) {
		super(player);

		if(aPlayer.isOnCooldown(this)) {
			return;
		}
		if(hasAbility(player, PowerfulPull.class)) {
			return;
		}
		for(Location loc : aPlayer.getdarkBalls()) {
			if(loc.distance(GeneralMethods.getTargetedLocation(player, 20)) < 7) {
				balls.add(loc);
			}
		}
		if(balls.size() <= 0) {
			return;
		}
		for(Location loc : balls) {
			aPlayer.removeBall(loc);
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
		return 15000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return GeneralMethods.getTargetedLocation(player, 20);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "GüçÇekimi";
	}

	public void circleParticle(Location loc) {
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i);
            double y = Math.cos(i)*0.2;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
               double x = Math.cos(a) * radius * 0.2;
               double z = Math.sin(a) * radius * 0.2;
               loc.add(x, y, z);
               ParticleEffect.REDSTONE.display(loc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.fromRGB(75, 0, 75), 1));
               loc.subtract(x, y, z);
            }
         }
	}
	
	@Override
	public void progress() {
		// TODO Auto-generated method stub
		if(balls.isEmpty()) {
			remove();
			return;
		}
		for(Location loc : balls) {
			circleParticle(loc);
			Vector vec = player.getLocation().toVector().subtract(loc.toVector()).normalize();
			if(loc.distance(player.getLocation()) > 2) {
				loc.add(vec.multiply(1));
			}else {
				balls.remove(loc);
			}
			for(Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 1)) {
				if(controlEntity(e)) {
					DamageHandler.damageEntity(e, player, 150, this, "AP");
					vec.setY(0.1);
					e.setVelocity(vec.multiply(0.6));
				}
			}
		}
	}
	
	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

}
