package com.mactso.harderfarther.api;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncDifficultyToClientsPacket;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class DifficultyCalculator {

	private static List<DifficultyOverrideListener> listeners = new ArrayList<DifficultyOverrideListener>();

	public static float calcDistanceModifier(Vec3 eventVec, Vec3 nearestOutpostVec) {
		double distance = eventVec.distanceTo(nearestOutpostVec);
		distance = Math.max(0, distance - PrimaryConfig.getBoostMinDistance());
		Float f = (float) Math.min(1.0f, distance / PrimaryConfig.getBoostMaxDistance());
		return f;
	}

	public static float doApplyHeightFactor(float difficulty, int y) {

		if (y < PrimaryConfig.getMinimumSafeAltitude()) {
			difficulty *= 1.06f;
		} else if (y > PrimaryConfig.getMaximumSafeAltitude()) {
			difficulty *= 1.09f;
		}

		return difficulty;

	}



	public static float getDistanceDifficultyHere (ServerLevel serverLevel, Vec3 eventVec) {
		double xzf = serverLevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		LevelData winfo = serverLevel.getLevelData();
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);


		//Add spawn to outpost list if enabled & get nearest outpost
		Vec3[] outposts = PrimaryConfig.getOutpostPositions();
		if(PrimaryConfig.isSpawnAnOutpost()){
			outposts[0] = spawnVec;
		}

		Vec3 nearestOutpost = getNearestOutpost(outposts, eventVec);

		float difficulty = DifficultyCalculator.calcDistanceModifier(eventVec, nearestOutpost);
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

		//Add spawn to outpost list if enabled & get nearest outpost
		Vec3[] outposts = PrimaryConfig.getOutpostPositions();
		if(PrimaryConfig.isSpawnAnOutpost()){
			outposts[0] = spawnVec;
		}

		Vec3 nearestOutpost = getNearestOutpost(outposts, eventVec);

		Utility.debugMsg(2, "getTimedifficulty here top");
		float timeDifficulty = 0;
		timeDifficulty = HarderTimeManager.getTimeDifficulty(serverLevel, le);

		Utility.debugMsg(2, "getGrimdifficulty here top");
		float gcDifficultyPct = 0;
		gcDifficultyPct = GrimCitadelManager.getGrimDifficulty(le);

		Utility.debugMsg(2, "getCalcDistanceModifier top");
		float hfDifficulty = DifficultyCalculator.calcDistanceModifier(eventVec, nearestOutpost);

		float highDifficulty[] = new float[]{Math.max(timeDifficulty, hfDifficulty)};
		highDifficulty[0] = Math.max(gcDifficultyPct, highDifficulty[0]);

		if (le instanceof ServerPlayer sp) {
//			System.out.println("HFM sending hf:"+hfDifficulty + " gc:" + gcDifficultyPct + " tm:" + timeDifficulty);
			Utility.debugMsg(2, "getdifficulty here network message");

			SyncDifficultyToClientsPacket msg = new SyncDifficultyToClientsPacket(hfDifficulty,gcDifficultyPct,timeDifficulty);
			Network.sendToClient(msg, sp);

		}

		//Allow other mods to modify final difficulty. I personally will be using this to force the nether to always have a constant difficulty in a helper mod for my modpack.
		difficultyOverride(highDifficulty, serverLevel, outposts, PrimaryConfig.getBoostMinDistance(), PrimaryConfig.getBoostMaxDistance());

		Utility.debugMsg(2, "getdifficulty returning " + highDifficulty);
		return highDifficulty[0];
	}




	public static Vec3 getNearestOutpost(Vec3[] outposts, Vec3 eventVec){

		int iterator=0;
		if(!PrimaryConfig.isSpawnAnOutpost()){
			iterator++;
		}

		int nearestOutpostDistance = Integer.MAX_VALUE;
		Vec3 nearestOutpost = outposts[iterator];
		iterator ++;
		for(;iterator<outposts.length; iterator++){
			if(outposts[iterator].distanceTo(eventVec)<nearestOutpostDistance){
				nearestOutpost = outposts[iterator];
			}
		}

		return nearestOutpost;
	}

	public static void registerOverrideEvent(DifficultyOverrideListener listener){

		listeners.add(listener);

	}

	private static void difficultyOverride(float currentDifficulty[], ServerLevel world, Vec3[] outposts, int minBoostDistance, int maxBoostDistance){

		for( DifficultyOverrideListener listener : listeners) {

			listener.interact(currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance);

		}

	}


}
