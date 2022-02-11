package com.vantahub.chilieutenant.abilitymaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class TempBlock {

	private Material prevMaterial;
	private Block block;
	public static Map<Block, TempBlock> instances = new ConcurrentHashMap<Block, TempBlock>();
	
	public TempBlock(Block block, Material newType) {
		if(isTempBlock(block)) {
			return;
		}
		prevMaterial = block.getType();
		this.block = block;
		block.setType(newType);
		instances.put(block, this);
	}
	
	public boolean isTempBlock(final Block block) {
		return block != null && instances.containsKey(block);
	}
	
	public void setRevertTime(long time) {
		new BukkitRunnable() {
			public void run() {
				if(block == null) {
					this.cancel();
					return;
				}
				block.setType(prevMaterial);
				if(instances.containsKey(block)) {
					instances.remove(block);
				}
				this.cancel();
			}
		}.runTaskLater(main.getInstance(), time);
	}
	
	public void revertBlock() {
		block.setType(prevMaterial);
		if(instances.containsKey(block)) {
			instances.remove(block);
		}
	}
}
