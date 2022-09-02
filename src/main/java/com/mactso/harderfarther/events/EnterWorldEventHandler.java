package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.utility.Boosts;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EnterWorldEventHandler {
	@SubscribeEvent(priority = EventPriority.LOW)
	public void onEnterWorldEvent(EntityJoinWorldEvent event) {
		
		Level level = event.getWorld();
		if (level.isClientSide) 
			return;

		if (event.loadedFromDisk()) 
			return;

		if (!(MyConfig.isMakeMonstersHarderFarther()) && (!MyConfig.isMakeHarderOverTime())
				&& (!MyConfig.isUseGrimCitadels()))
			return;		
		
		ServerLevel serverLevel = (ServerLevel) event.getWorld();
		if (MyConfig.isOnlyOverworld() && (serverLevel.dimension() != Level.OVERWORLD)) {
			return;
		}

		String dimensionName = serverLevel.dimension().location().toString();
		if (MyConfig.isDimensionOmitted(dimensionName)) {
			return;
		}
		

		
//		EntityType<?> type = le.getType();
//		if (type.getCategory().isFriendly()) {
//			return;
//		}

		if (event.getEntity() instanceof Monster me) {
			float boostDifficulty = HarderFartherManager.getDifficultyHere(serverLevel, me );
			if (boostDifficulty == 0)
				return;
			
			if (boostDifficulty > MyConfig.getGrimCitadelMaxBoostPercent()) {
				if (boostDifficulty == GrimCitadelManager.getGrimDifficulty( me )) {
					boostDifficulty = MyConfig.getGrimCitadelMaxBoostPercent();
				}
			}
			
			String eDsc = me.getType().getRegistryName().toString();	
			Boosts.doBoostAbilities(me, eDsc, boostDifficulty);
		}
			
	}
}
