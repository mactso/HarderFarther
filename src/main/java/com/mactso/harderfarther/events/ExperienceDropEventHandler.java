package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ExperienceDropEventHandler {

	public static long tickTimer = 0;

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingExperienceDropEvent event) {

		Entity eventEntity = event.getEntityLiving();

		if (event.getEntity() == null) {
			return;
		}

		if (!(eventEntity.world instanceof ServerWorld)) {
			return;
		}
		ServerWorld serverWorld = (ServerWorld) eventEntity.world;

		long worldTime = eventEntity.world.getGameTime();
		if (tickTimer > worldTime) {
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("Mob Died: " + (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY()
						+ ", " + (int) eventEntity.getPosZ() + ", " + " inside no bonus loot frame.");
			}
			return;
		}

		if (!(eventEntity instanceof MobEntity)) {
			return;
		}

		MobEntity me = (MobEntity) eventEntity;
		if (me instanceof AnimalEntity) {
			return;
		}

		tickTimer = worldTime + (long) 20; // no boosted XP for 1 seconds after a kill.

		Vector3d spawnVec = new Vector3d(serverWorld.getWorldInfo().getSpawnX(), serverWorld.getWorldInfo().getSpawnY(),
				serverWorld.getWorldInfo().getSpawnZ());
		Vector3d eventVec = new Vector3d(eventEntity.getPosX(), eventEntity.getPosY(), eventEntity.getPosZ());
		float distanceModifier = (float) (eventVec.distanceTo(spawnVec) / 1000.0);

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

			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("Harder Farther: A " + eventEntity.getName().getString() + " Died at: "

						+ (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY() + ", "
						+ (int) eventEntity.getPosZ() + ", " + "and xp increased from " + originalExperience + ": ("
						+ newDroppedExperience + ").");
			}

			int debugline = 3;
		}
	}
}
