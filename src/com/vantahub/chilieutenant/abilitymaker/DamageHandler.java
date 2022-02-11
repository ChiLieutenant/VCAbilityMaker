package com.vantahub.chilieutenant.abilitymaker;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import com.vantahub.chilieutenant.abilitymaker.events.AbilityDamageEvent;
import com.vantahub.chilieutenant.abilitymaker.events.EntityAbilityDeathEvent;
import com.vantahub.chilieutenant.health.health;

import net.minecraft.server.v1_16_R3.DamageSource;

public class DamageHandler {

	/**
	 * Damages an Entity by amount of damage specified. Starts a
	 * {@link EntityDamageByEntityEvent}.
	 *
	 * @param ability The ability that is used to damage the entity
	 * @param entity The entity that is receiving the damage
	 * @param damage The amount of damage to deal
	 */
	public static void damageEntity(final Entity entity, Player source, double damage, final MainAbility ability, String damageType) {
		if (ability == null) {
			return;
		}
		if (source == null) {
			source = ability.getPlayer();
		}

		final AbilityDamageEvent damageEvent = new AbilityDamageEvent(entity, ability, damage, damageType);
		Bukkit.getServer().getPluginManager().callEvent(damageEvent);
		if (entity instanceof LivingEntity) {
			if (!damageEvent.isCancelled()) {
				damage = damageEvent.getDamage();
				final EntityDamageByEntityEvent finalEvent = new EntityDamageByEntityEvent(source, entity, DamageCause.CUSTOM, damage);
				if(entity instanceof Player) {
					//calculate armor & mr
					//damage = calculatedDamage
					if(health.isPlayerHaveShield((Player) entity)) {
						if(damage > health.getShield((Player) entity)){
							damage = damage - health.getShield((Player) entity);
							health.setShield((Player) entity, 0);
						}else {
							health.removeShield((Player) entity, damage);
							damage = 0;
						}
					}
					if(health.getHealth((Player) entity) - damage <= 0) {
						final EntityAbilityDeathEvent event = new EntityAbilityDeathEvent(entity, damage, ability);
						Bukkit.getServer().getPluginManager().callEvent(event);
						((LivingEntity) entity).damage(((Damageable) entity).getHealth());
						Bukkit.getServer().getPluginManager().callEvent(finalEvent);
						entity.setLastDamageCause(finalEvent);
						return;
					}
					health.removeHealth((Player) entity, damage);
					((LivingEntity) entity).damage(0.0005);
				}else {
					//nonplayer (mobs, minions, etc.)
					((LivingEntity) entity).damage(damage);
					//((CraftEntity) entity).getHandle().damageEntity(DamageSource.MAGIC, (float) damage);
				}
				Bukkit.getServer().getPluginManager().callEvent(finalEvent);
				entity.setLastDamageCause(finalEvent);
			}
		}

	}
}
