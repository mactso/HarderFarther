package com.mactso.harderfarther.events;

import java.util.Random;

import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Glooms;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {


		LivingEntity le = event.getEntityLiving();
		Random rand = le.getLevel().getRandom();
		
		if (le.level.isClientSide()) {
			if (le instanceof Player cp) {
				GrimCitadelManager.playGCOptionalSoundCues(cp);
			}
			if (FogColorsEventHandler.getServerTimeDifficulty() == 0) {
				if ((le instanceof Player cp) && (rand.nextInt(144000) == 42)) {
					GrimSongManager.startSong(ModSounds.NUM_LAKE_DESTINY);
				}
			}
			return;
		}
		
		ServerLevel serverLevel = (ServerLevel) le.getLevel();

		long gameTime = serverLevel.getGameTime();

		float difficulty = HarderFartherManager.getDifficultyHere(serverLevel, le);
		
		if (difficulty > 0) {
			if (GrimCitadelManager.isGCNear(difficulty)) {
				Utility.slowFlyingMotion(le);
			}
			if (gameTime % 10 != le.getId() % 10)
				return;

			Utility.debugMsg(2, le, "Living Event " + event.getEntity().getType().getRegistryName().toString() + " dif: "+ difficulty);

			if ((le instanceof ServerPlayer sp) && (rand.nextInt(144000) == 4242) && (difficulty >  Utility.Pct09)) {
				Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
			}
			


			
			if ( (difficulty > Utility.Pct84)) {
				if (le.hasEffect(MobEffects.SLOW_FALLING)) {
					le.removeEffect(MobEffects.SLOW_FALLING);
				}
			}
			if (GrimCitadelManager.getGrimDifficulty(le) > 0) {
				Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.GRIM);
			}
			if (HarderTimeManager.getTimeDifficulty(serverLevel, le) > 0) {
				Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.TIME);
			}
		}

	}
	

}
