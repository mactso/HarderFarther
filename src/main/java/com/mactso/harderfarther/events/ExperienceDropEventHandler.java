package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


// this method only *limits* xp drops that happen to past.  it is part of the farm limiter.

public class ExperienceDropEventHandler {

	public static long tickTimer = 0;

	@SubscribeEvent
	public void onMonsterDrops(LivingExperienceDropEvent event) {
		
		LivingEntity le = event.getEntityLiving();
		if (le == null)   {
			return;
		}
		
		if (le.getLevel().isClientSide()) {
			return;
		}
		
		if (!(le instanceof Mob)) {
			return;
		}
		
		if (le instanceof Animal) {
			return;
		}
		
		ServerLevel serverLevel = (ServerLevel) le.level;
		
		if (closeToWorldSpawn(serverLevel, le))
			return;
		
		if (tickTimer > serverLevel.getGameTime()) {
			Utility.debugMsg(2, le, "Mob Died inside no bonus loot frame.");
			return;
		}
		tickTimer = serverLevel.getGameTime() + (long) 20; // no boosted XP for 1 seconds after a kill.

	}

	private boolean closeToWorldSpawn(ServerLevel serverLevel, LivingEntity le) {

		Vec3 spawnVec = new Vec3(serverLevel.getLevelData().getXSpawn(), serverLevel.getLevelData().getYSpawn(),
				serverLevel.getLevelData().getZSpawn());

		BlockPos pos = le.blockPosition();
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		
		if (eventVec.distanceTo(spawnVec) < MyConfig.getSafeDistance()*8)
			return true;

		return false;
	}
}
