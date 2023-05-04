package com.mactso.harderfarther.events;

import com.mactso.harderfarther.utility.Utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExperienceDropEventHandler {

	public static long tickTimer = 0;

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingExperienceDropEvent event) {

		Entity e = event.getEntityLiving();
		BlockPos ePos = e.blockPosition();
		if (event.getEntity() == null) {
			return;
		}

		if (!(e.level instanceof ServerWorld)) {
			return;
		}
		ServerWorld serverWorld = (ServerWorld) e.level;

		long worldTime = e.level.getGameTime();
		if (tickTimer > worldTime) {
			Utility.debugMsg(2, e.blockPosition(), "Mob Died inside no bonus loot frame.");
			return;
		}

		if (!(e instanceof MobEntity)) {
			return;
		}

		MobEntity me = (MobEntity) e;
		if (me instanceof AnimalEntity) {
			return;
		}

		tickTimer = worldTime + (long) 20; // no boosted XP for 1 seconds after a kill.

		Vector3i spawnVec = new Vector3i(serverWorld.getLevelData().getXSpawn(), serverWorld.getLevelData().getYSpawn(),
				serverWorld.getLevelData().getZSpawn());
		Vector3i eventVec = new Vector3i(ePos.getX(), ePos.getY(), ePos.getZ());
		float distanceModifier = (float) (Math.sqrt(eventVec.distSqr(spawnVec)) / 1000.0);
		if (distanceModifier < 1.0) {
			return;
		}

		if (distanceModifier > 30)
			distanceModifier = 30;

		float PctMultiplier = distanceModifier / 30.0f;

		int originalExperience = event.getOriginalExperience();
		int droppedExperience = event.getDroppedExperience();

		// if someone else already modified xp leave it alone.
		// Boost XP by 1% to 100%.
		if (originalExperience == droppedExperience) {
			int newDroppedExperience = originalExperience + (int) (originalExperience * PctMultiplier);
			event.setDroppedExperience(newDroppedExperience);

			Utility.debugMsg(1, ePos, "Harder Farther: A " + 
			e.getName().getString() + " died and xp increased from " + originalExperience + ": ("
						+ newDroppedExperience + ").");
		}
	}
}
