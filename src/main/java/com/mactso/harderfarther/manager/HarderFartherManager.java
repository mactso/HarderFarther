package com.mactso.harderfarther.manager;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncDifficultyToClientsPacket;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

public class HarderFartherManager {

	public static float calcDistanceModifier(Vec3 eventVec, Vec3 spawnVec) {
		double distance = eventVec.distanceTo(spawnVec);
		distance = Math.max(0, distance - MyConfig.getBoostMinDistance());
		Float f = (float) Math.min(1.0f, distance / MyConfig.getBoostMaxDistance());
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
	
	public static float getDifficultyHere(ServerLevel serverLevel, LivingEntity le) {
		
		Utility.debugMsg(2, "getdifficulty here top");
		BlockPos pos = le.blockPosition();

		double xzf = serverLevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		LevelData winfo = serverLevel.getLevelData();
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

		Utility.debugMsg(2, "getTimedifficulty here top");
		float timeDifficulty = 0;
		timeDifficulty = HarderTimeManager.getTimeDifficulty(serverLevel, le);

		Utility.debugMsg(2, "getGrimdifficulty here top");
		float gcDifficultyPct = 0;
		gcDifficultyPct = GrimCitadelManager.getGrimDifficulty(le);

		Utility.debugMsg(2, "getCalcDistanceModifier top");
		float hfDifficulty = HarderFartherManager.calcDistanceModifier(eventVec, spawnVec);
		
		float highDifficulty = Math.max(timeDifficulty, hfDifficulty);
		highDifficulty = Math.max(gcDifficultyPct, highDifficulty);

		if (le instanceof ServerPlayer sp) {
//			System.out.println("HFM sending hf:"+hfDifficulty + " gc:" + gcDifficultyPct + " tm:" + timeDifficulty);
			Utility.debugMsg(2, "getdifficulty here network message");

			SyncDifficultyToClientsPacket msg = new SyncDifficultyToClientsPacket(hfDifficulty,gcDifficultyPct,timeDifficulty);
			Network.sendToClient(msg, sp);

		}	

		Utility.debugMsg(2, "getdifficulty returning " + highDifficulty);
		return highDifficulty;
	}
	
}
