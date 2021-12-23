package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.Vec3;
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

		if (!(eventEntity.level instanceof ServerLevel)) {
			return;
		}
		ServerLevel serverWorld = (ServerLevel) eventEntity.level;

		long worldTime = eventEntity.level.getGameTime();
		if (tickTimer > worldTime) {
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("Mob Died: " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
						+ ", " + (int) eventEntity.getZ() + ", " + " inside no bonus loot frame.");
			}
			return;
		}

		if (!(eventEntity instanceof Mob)) {
			return;
		}

		Mob me = (Mob) eventEntity;
		if (me instanceof Animal) {
			return;
		}

		tickTimer = worldTime + (long) 20; // no boosted XP for 1 seconds after a kill.

		Vec3 spawnVec = new Vec3(serverWorld.getLevelData().getXSpawn(), serverWorld.getLevelData().getYSpawn(),
				serverWorld.getLevelData().getZSpawn());
		Vec3 eventVec = new Vec3(eventEntity.getX(), eventEntity.getY(), eventEntity.getZ());
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

						+ (int) eventEntity.getX() + ", " + (int) eventEntity.getY() + ", "
						+ (int) eventEntity.getZ() + ", " + "and xp increased from " + originalExperience + ": ("
						+ newDroppedExperience + ").");
			}

		}
	}
}
