package com.vantahub.chilieutenant.abilitymaker;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.vantahub.chilieutenant.abilitymaker.events.AbilityEndEvent;
import com.vantahub.chilieutenant.abilitymaker.events.AbilityStartEvent;
import com.vantahub.chilieutenant.arenacontroller.ArenaMethods;

import sun.reflect.ReflectionFactory;

public abstract class MainAbility implements Ability{

	private static final List<MainAbility> abilities = new ArrayList<MainAbility>();
	private static final Map<Class<? extends MainAbility>, Map<UUID, Map<Integer, MainAbility>>> INSTANCES_BY_PLAYER = new ConcurrentHashMap<>();
	private static final Map<String, MainAbility> ABILITIES_BY_NAME = new ConcurrentSkipListMap<>(); // preserves ordering.
	
	protected Player player;
	protected AbilityPlayer aPlayer;
	private static int idCounter;
	private long starttime;
	private int id;
	private boolean started;
	private boolean removed;
	
	static {
		idCounter = Integer.MIN_VALUE;
	}
	
	public MainAbility() {
		
	}
	
	public MainAbility(final Player player) {
		if(player == null) {
			return;
		}
		this.player = player;
		this.id = MainAbility.idCounter;
		this.aPlayer = AbilityPlayer.getAbilityPlayer(player);
		if (idCounter == Integer.MAX_VALUE) {
			idCounter = Integer.MIN_VALUE;
		} else {
			idCounter++;
		}
	}
	
	public static List<MainAbility> getAbilities(){
		return abilities;
	}
	
	public static <T extends MainAbility> Collection<T> getAbilities(final Player player, final Class<T> clazz) {
		if (player == null || clazz == null || INSTANCES_BY_PLAYER.get(clazz) == null || INSTANCES_BY_PLAYER.get(clazz).get(player.getUniqueId()) == null) {
			return Collections.emptySet();
		}
		return (Collection<T>) INSTANCES_BY_PLAYER.get(clazz).get(player.getUniqueId()).values();
	}
	
	public static <T extends MainAbility> T getAbility(final Player player, final Class<T> clazz) {
		final Collection<T> abils = getAbilities(player, clazz);
		if (abils.iterator().hasNext()) {
			return abils.iterator().next();
		}
		return null;
	}
	
	public static <T extends MainAbility> boolean hasAbility(final Player player, final Class<T> clazz) {
		return getAbility(player, clazz) != null;
	}
	
	public static boolean hasAbility(Player player, MainAbility ability) {
		if(!abilities.isEmpty()) {
			for(MainAbility abil : abilities) {
				if(abil.getName().equalsIgnoreCase(ability.getName())) {
					if(ability.getPlayer().equals(player)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void start() {
		if(player == null) {
			return;
		}
		AbilityStartEvent event = new AbilityStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		this.started = true;
		this.starttime = System.currentTimeMillis();
		final Class<? extends MainAbility> clazz = this.getClass();
		final UUID uuid = this.player.getUniqueId();
		if (!INSTANCES_BY_PLAYER.containsKey(clazz)) {
			INSTANCES_BY_PLAYER.put(clazz, new ConcurrentHashMap<UUID, Map<Integer, MainAbility>>());
		}
		if (!INSTANCES_BY_PLAYER.get(clazz).containsKey(uuid)) {
			INSTANCES_BY_PLAYER.get(clazz).put(uuid, new ConcurrentHashMap<Integer, MainAbility>());
		}
		INSTANCES_BY_PLAYER.get(clazz).get(uuid).put(this.id, this);
		abilities.add(this);
	}

	public boolean isRemoved() {
		return this.removed;
	}
	
	public void remove() {
		if(player == null) {
			return;
		}
		AbilityEndEvent event = new AbilityEndEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		this.started = false;
		this.removed = true;
		final Map<UUID, Map<Integer, MainAbility>> classMap = INSTANCES_BY_PLAYER.get(this.getClass());
		if (classMap != null) {
			final Map<Integer, MainAbility> playerMap = classMap.get(this.player.getUniqueId());
			if (playerMap != null) {
				playerMap.remove(this.id);
				if (playerMap.size() == 0) {
					classMap.remove(this.player.getUniqueId());
				}
			}

			if (classMap.size() == 0) {
				INSTANCES_BY_PLAYER.remove(this.getClass());
			}
		}
		//abilities.remove(this);
	}
	
	public long getStarttime() {
		return this.starttime;
	}

	public Player getPlayer() {
		return player;
	}
	
	public Config getConfig() {
		return new Config(this);
	}
	
	public boolean controlEntity(Entity e) {
		if(e.getUniqueId() == player.getUniqueId()) {
			return false;
		}
		if(!(e instanceof LivingEntity)) {
			return false;
		}
		if(e instanceof Player && ArenaMethods.getTeam(player) == ArenaMethods.getTeam((Player) e)) {
			return false;
		}
		return true;
	}
	
	@Deprecated
	public static void legacyRegisterPluginAbilities(final JavaPlugin plugin, final String packagePrefix) {
		if (plugin == null) {
			return;
		}

		final Class<? extends JavaPlugin> pluginClass = plugin.getClass();
		final ClassLoader loader = pluginClass.getClassLoader();

		final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();

		try {
			for (final ClassInfo info : ClassPath.from(loader).getAllClasses()) {
				if (!info.getPackageName().startsWith(packagePrefix)) {
					continue;
				}

				Class<?> clazz = null;
				try {
					clazz = info.load();
					if (!MainAbility.class.isAssignableFrom(clazz) || clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
						continue;
					}

					final Constructor<?> objDef = MainAbility.class.getDeclaredConstructor();
					final Constructor<?> intConstr = rf.newConstructorForSerialization(clazz, objDef);
					final MainAbility ability = (MainAbility) clazz.cast(intConstr.newInstance());

					if (ability == null || ability.getName() == null) {
						continue;
					}
					ABILITIES_BY_NAME.put(ability.getName(), ability);
					ability.load();
					
				} catch (final Exception e) {
					e.printStackTrace();
				} catch (final Error e) {
					e.printStackTrace();
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
}
