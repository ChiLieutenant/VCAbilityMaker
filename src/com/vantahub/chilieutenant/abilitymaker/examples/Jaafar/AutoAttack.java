package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.vantahub.chilieutenant.abilitymaker.DamageHandler;
import com.vantahub.chilieutenant.abilitymaker.GeneralMethods;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.ParticleEffect;

public class AutoAttack extends MainAbility{

	private Entity target;
	private Location loc;
	
	public AutoAttack(Player player, Entity target) {
		super(player);
		// TODO Auto-generated constructor stub
		if(aPlayer.isOnCooldown(this)) {
			return;
		}
		this.target = target;
		Location offset1 = GeneralMethods.getRightSide(player.getLocation(), .55).add(0, 1.2, 0);
		Vector dir1 = player.getEyeLocation().getDirection();
		loc = offset1.toVector().add(dir1.clone().multiply(.8D)).toLocation(player.getWorld());
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
		return 1000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return loc;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "JaafarAutoAttack";
	}

	@Override
	public void progress() {
		// TODO Auto-generated method stub
		if(target.isDead()) {
			remove();
			return;
		}
		for(int i = 0; i < 5; i++) {
			Vector vec = target.getLocation().add(0, 1, 0).toVector().subtract(loc.toVector()).normalize().multiply(0.1);
			loc.add(vec);
			ParticleEffect.REDSTONE.display(loc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.fromRGB(75, 0, 75), 1.5f));
			if(target.getLocation().add(0, 1, 0).distance(loc) < 1) {
				DamageHandler.damageEntity(target, player, 20, this, "AD");
				remove();
				return;
			}
		}
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
