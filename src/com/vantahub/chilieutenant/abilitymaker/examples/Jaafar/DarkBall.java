package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;

import com.vantahub.chilieutenant.abilitymaker.DamageHandler;
import com.vantahub.chilieutenant.abilitymaker.GeneralMethods;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.ParticleEffect;

public class DarkBall extends MainAbility{

	private Location loc;
	private long time;
	
	public DarkBall(Player player) {
		super(player);

		if(aPlayer.isOnCooldown(this)) {
			return;
		}
		if(aPlayer.getdarkBalls().size() >= 5) {
			return;
		}
		if(GeneralMethods.getTargetedLocation(player, 15).getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
			return;
		}
		aPlayer.addCooldown(this);
		loc = GeneralMethods.getTargetedLocation(player, 15).getBlock().getLocation().add(0.5, 0.5, 0.5);
		aPlayer.addBall(loc);
		time = System.currentTimeMillis();
		for(Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 1)) {
			if(e.getUniqueId() != player.getUniqueId() && e instanceof LivingEntity) {
				DamageHandler.damageEntity(e, player, 200, this, "AP");
			}
		}
		ParticleEffect.REDSTONE.display(loc, 50, 0.5, 0.5, 0.5, 0.005, new Particle.DustOptions(Color.fromRGB(75, 0, 75), 1));
		start();
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
	public String getChampion() {
		// TODO Auto-generated method stub
		return "Jaafar";
	}

	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 3000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return loc;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "KaranlıkTop";
	}

	int i = 0;
	
	@Override
	public void progress() {
		i++;
		// TODO Auto-generated method stub
		if(System.currentTimeMillis() > time + 20000 || !aPlayer.getdarkBalls().contains(loc)) {
			aPlayer.removeBall(loc);
			remove();
			return;
		}
		if(i % 2 == 0) {
			circleParticle(loc);
		}
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}
	
}
