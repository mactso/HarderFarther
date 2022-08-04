package com.mactso.harderfarther.manager;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

public class HarderFartherManager {

	public static float calcDistanceModifier(Vec3 eventVec, Vec3 spawnVec) {
		Float f = (float) Math.min(1.0f, (eventVec.distanceTo(spawnVec) / MyConfig.getModifierMaxDistance()));
		if (f < Utility.Pct02)
			f= 0.0f;
		return f;
	}
	
	public static float doApplyHeightFactor(float difficulty, int y) {

		if (y < MyConfig.getMinimumSafeAltitude()) {
			difficulty *= 1.06f;
		} else if (y > MyConfig.getMaximumSafeAltitude()) {
			difficulty *= 1.09f;
		}

		return difficulty;

	}
	
	public static float getDifficultyHere(LivingEntity eventEntity) {
		
		Level level = eventEntity.level;
		BlockPos pos = eventEntity.blockPosition();

		double xzf = level.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		LevelData winfo = level.getLevelData();
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

		float timeDifficulty = 0;
		timeDifficulty = HarderTimeManager.getTimeDifficulty(level, eventEntity);

		float gcDifficultyPct = 0;
		gcDifficultyPct = GrimCitadelManager.getGrimDifficulty(level, eventEntity);

		float hfDifficulty = HarderFartherManager.calcDistanceModifier(eventVec, spawnVec);
		float finalDifficulty = Math.max(timeDifficulty, hfDifficulty);
		finalDifficulty = Math.max(gcDifficultyPct, finalDifficulty);
		return finalDifficulty;
	}
	
}
