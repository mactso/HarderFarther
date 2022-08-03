package com.mactso.harderfarther.config;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

public class HarderFartherManager {

	public static float calcDistanceModifier(Vec3 eventVec, Vec3 spawnVec) {

		return (float) Math.min(1.0, (eventVec.distanceTo(spawnVec) / MyConfig.getModifierMaxDistance()));

	}
	
	public static float doApplyHeightFactor(float difficulty, int y) {

		if (y < MyConfig.getMinimumSafeAltitude()) {
			difficulty *= 1.06f;
		} else if (y > MyConfig.getMaximumSafeAltitude()) {
			difficulty *= 1.09f;
		}

		return difficulty;

	}
	
	public static float getDifficultyPct(LivingEntity eventEntity, ServerLevel serverLevel, LevelData winfo, BlockPos pos) {
		double xzf = serverLevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

		float timeDifficultyPct = 0;
		timeDifficultyPct = HarderTimeManager.getTimeDifficulty(serverLevel, eventEntity);

		float grimDifficultyPct = 0;
		grimDifficultyPct = GrimCitadelManager.getGrimDifficulty(serverLevel, eventEntity);

		float distanceDifficultyPct = HarderFartherManager.calcDistanceModifier(eventVec, spawnVec);

		float difficultyPct = Math.max(timeDifficultyPct, distanceDifficultyPct);
		difficultyPct = Math.max(grimDifficultyPct, difficultyPct);
		return difficultyPct;
	}
	
}
