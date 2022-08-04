package com.mactso.harderfarther.events;

import java.util.Random;

import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {


		LivingEntity le = event.getEntityLiving();
		Level level = le.level;
		Random rand = level.getRandom();
		
		long gameTime = level.getGameTime();

		float difficulty = HarderFartherManager.getDifficultyHere(le);
		if (difficulty > 0) {
			if (GrimCitadelManager.isTooCloseToFly(difficulty)) {
				Utility.slowFlyingMotion(le);
			}
			if (gameTime % 10 != le.getId() % 10)
				return;

			Utility.debugMsg(2, le, "Living Event " + event.getEntity().getType().getRegistryName().toString() + " dif: "+ difficulty);


			// note: Synced when a player logs in and when hearts destroyed.
			if (level.isClientSide()) {
				if (le instanceof Player cp) {
					GrimCitadelManager.playOptionalSoundCues(cp, difficulty);
				}
				return;
			}

			ServerLevel serverLevel = (ServerLevel) le.level;
			
			if ((le instanceof ServerPlayer sp) && (rand.nextInt(144000) == 4242) && (difficulty <  Utility.Pct91)) {
				Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
			}
			
			if ( (difficulty > Utility.Pct84)) {
				if (le.hasEffect(MobEffects.SLOW_FALLING)) {
					le.removeEffect(MobEffects.SLOW_FALLING);
				}
			}
			if (GrimCitadelManager.getGrimDifficulty(level, le) > 0) {
				Glooms.doGlooms(le, serverLevel, gameTime, difficulty);
			}
		}

	}

}
