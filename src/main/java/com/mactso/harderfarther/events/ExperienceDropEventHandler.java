package com.mactso.harderfarther.events;

import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


// this method only *limits* xp drops that happen to past.  it is part of the farm limiter.

public class ExperienceDropEventHandler {

	public static long tickTimer = 0;

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingExperienceDropEvent event) {

		Entity e = event.getEntityLiving();
		BlockPos ePos = e.blockPosition();
	
		if (event.getEntity() == null) {
			return;
		}

		if (!(e.level instanceof ServerLevel)) {
			return;
		}
		ServerLevel serverWorld = (ServerLevel) e.level;

		long worldTime = e.level.getGameTime();
		if (tickTimer > worldTime) {
			Utility.debugMsg(2, ePos, "Mob Died inside no bonus loot frame.");
			return;
		}

		if (!(e instanceof Mob)) {
			return;
		}

		Mob me = (Mob) e;
		if (me instanceof Animal) {
			return;
		}

		tickTimer = worldTime + (long) 20; // no boosted XP for 1 seconds after a kill.

		Vec3 spawnVec = new Vec3(serverWorld.getLevelData().getXSpawn(), serverWorld.getLevelData().getYSpawn(),
				serverWorld.getLevelData().getZSpawn());
		Vec3 eventVec = new Vec3(ePos.getX(), ePos.getY(), ePos.getZ());
		float distanceModifier = (float) (eventVec.distanceTo(spawnVec) / 1000.0);

		if (distanceModifier < 1.0) {
			return;
		}
		

	}
}
