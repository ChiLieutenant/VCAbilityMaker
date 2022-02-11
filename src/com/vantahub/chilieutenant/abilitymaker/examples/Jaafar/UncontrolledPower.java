package com.vantahub.chilieutenant.abilitymaker.examples.Jaafar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.vantahub.chilieutenant.abilitymaker.DamageHandler;
import com.vantahub.chilieutenant.abilitymaker.MainAbility;
import com.vantahub.chilieutenant.abilitymaker.ParticleEffect;
import com.vantahub.chilieutenant.abilitymaker.main;

public class UncontrolledPower extends MainAbility{

	private List<Location> ballsAround = new ArrayList<Location>();
	private List<Location> groundBalls = new ArrayList<Location>();
	private List<Location> goingToRemove = new ArrayList<Location>();
	private String mod;
	private int balls = 0;
	
	public UncontrolledPower(Player player) {
		super(player);
		// TODO Auto-generated constructor stub
		if(aPlayer.isOnCooldown(this)) {
			return;
		}
		if(hasAbility(player, UncontrolledPower.class)) {
			return;
		}
		if(aPlayer.getdarkBalls().isEmpty()) {
			return;
		}
		for(Location loc : aPlayer.getdarkBalls()) {
			if(loc.distance(player.getLocation()) < 20) {
				ballsAround.add(loc);
			}
		}
		if(ballsAround.isEmpty()) {
			return;
		}
		for(Location loc : ballsAround) {
			aPlayer.removeBall(loc);
		}
		mod = "collect";
		start();
		
	}


	public List<Location> getCirclePoints(Location location, int points, double size) {
		return getCirclePoints(location, points, size, 0);
	}

	/**
	 * Gets points in a circle.
	 * @param location
	 * @param points
	 * @param size
	 * @return
	 */
	public List<Location> getCirclePoints(Location location, int points, double size, double startangle){
		List<Location> locations = new ArrayList<Location>();
		for(int i = 0; i < 360; i += 360/points){
			double angle = (i * Math.PI / 180);
			double x = size * Math.cos(angle + startangle);
			double z = size * Math.sin(angle + startangle);
			Location loc = location.clone();
			loc.add(x, 0, z);
			locations.add(loc);
		}
		return locations;
	}

	
	public void setMode(String s) {
		mod = s;
	}
	
	public String getMode() {
		return mod;
	}
	
	@Override
	public String getChampion() {
		// TODO Auto-generated method stub
		return "Jaafar";
	}

	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 30000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "KontrolsüzGüç";
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
	
	public void circleParticle2(Location loc) {
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i);
            double y = Math.cos(i)*0.2;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
               double x = Math.cos(a) * radius * 0.2;
               double z = Math.sin(a) * radius * 0.2;
               loc.add(x, y, z);
               ParticleEffect.REDSTONE.display(loc, 1, 0, 0, 0, 0.005, new Particle.DustOptions(Color.fromRGB(75, 0, 75), 0.4F));
               loc.subtract(x, y, z);
            }
         }
	}
	
	public void hit(Entity target) {
		balls--;
		Location loc = player.getLocation().add(0, 1, 0);
		new BukkitRunnable() {
			public void run() {
				Vector vec = target.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(0.6);
				loc.add(vec);
				circleParticle(loc);
				if(loc.distance(target.getLocation()) < 1.5) {
					DamageHandler.damageEntity(target, player, 200, UncontrolledPower.this, "AP");
					aPlayer.addBall(loc);
					groundBalls.add(loc);
					this.cancel();
				}
			}
		}.runTaskTimer(main.getInstance(), 0, 1);
	}
	
	@Override
	public void progress() {
		// TODO Auto-generated method stub
		if(mod.equalsIgnoreCase("collect")) {
			if(!player.isSneaking() || ballsAround.isEmpty()) {
				mod = "hit";
				return;
			}
			if(!ballsAround.isEmpty()) {
				for(Location loc : ballsAround) {
					Vector vec = player.getLocation().add(0, 1, 0).toVector().subtract(loc.toVector()).normalize().multiply(0.8);
					loc.add(vec);
					circleParticle(loc);
					if(loc.distance(player.getLocation().add(0, 1, 0)) < 1.5) {
						goingToRemove.add(loc);
						balls++;
					}
				}
				ballsAround.removeAll(goingToRemove);
			}
		}
		if(balls > 0) {
			for(Location loc : getCirclePoints(player.getLocation().add(0, 1, 0), balls, 1)) {
				circleParticle2(loc);
			}
		}
		if(mod.equalsIgnoreCase("hit")) {
			if(balls <= 0 || player.isDead() || !player.isOnline()) {
				aPlayer.addCooldown(this);
				for(Location loc : groundBalls) {
					aPlayer.removeBall(loc); 
				}
				remove();
				return;
			}
			for(Location loc : groundBalls) {
				if(aPlayer.getdarkBalls().contains(loc)) {
					circleParticle(loc);
				}
			}
		}
	}
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		
	}

}
