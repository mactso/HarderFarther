package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import net.minecraft.util.RandomSource;
import com.mactso.harderfarther.client.GrimSongManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Boosts;
import com.mactso.harderfarther.utility.Glooms;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class LivingEventMovementHandler {
	
	/**
	 * @param event
	 */
	@SubscribeEvent
	public static void onLivingUpdate(LivingTickEvent event) {

		LivingEntity le = event.getEntity();
		RandomSource rand = le.level().getRandom();

		if (le.level().isClientSide()) {
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

		ServerLevel serverLevel = (ServerLevel) le.level();

		if (le instanceof ServerPlayer sp) {
			Utility.debugMsg(2, "LivingEventMovementHandler");

			boolean hasLifeHeart = sp.getInventory().contains(ModItems.LIFE_HEART_STACK);

			if ((sp.getHealth() < sp.getMaxHealth()) && (hasLifeHeart)) {
				int dice = MyConfig.getGrimLifeheartPulseSeconds() * Utility.TICKS_PER_SECOND;
				int roll = rand.nextInt(dice);
				int duration = Utility.FOUR_SECONDS;
				if (roll == 42) { // once per 2 minutes
					int slot = sp.getInventory().findSlotMatchingItem(ModItems.LIFE_HEART_STACK);
					slot /= 9;
					int healingpower = 1;
					duration = Utility.FOUR_SECONDS * 3;
					if (slot != 1) { // first top row no sound
						float volume = 0.48f; // default loud
						healingpower = 3;
						duration = Utility.FOUR_SECONDS;

						if (slot == 3) // second row quiet
						{
							volume = 0.12f;
							healingpower = 2;
							duration = Utility.FOUR_SECONDS;
						}
						if (slot == 3) // third row just over hotbar
						{
							volume = 0.24f;
							healingpower = 2;
							duration = Utility.FOUR_SECONDS + Utility.FOUR_SECONDS;

						}
						serverLevel.playSound(null, sp.blockPosition(), SoundEvents.NOTE_BLOCK_CHIME.get(),
								SoundSource.PLAYERS, volume, 0.86f);
					}
					Utility.updateEffect((LivingEntity) sp, healingpower, MobEffects.REGENERATION,
							Utility.FOUR_SECONDS);
				}
			}

			long gameTime = serverLevel.getGameTime();

			float difficulty = DifficultyCalculator.getDifficultyHere(serverLevel, le);

			if (difficulty > 0) {
				if (GrimCitadelManager.isGCNear(difficulty)) {
					Utility.slowFlyingMotion(le);
				}
				if (gameTime % 10 != le.getId() % 10)
					return;

				Utility.debugMsg(2, le, "Living Event " + EntityType.getKey((event.getEntity().getType())).toString()
						+ " dif: " + difficulty);
				if ((le instanceof ServerPlayer) && (rand.nextInt(300000) == 4242) && (difficulty > Utility.Pct09)) {
					Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
				}

				if ((difficulty > Utility.Pct84)) {
					if (le.hasEffect(MobEffects.SLOW_FALLING)) {
						le.removeEffect(MobEffects.SLOW_FALLING);
					}
				}

				if (GrimCitadelManager.getGrimDifficulty(le) > 0) {
					Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.GRIM);
					if ((le instanceof ServerPlayer) && (rand.nextInt(144000) == 4242)
							&& (difficulty > Utility.Pct09)) {
						Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
					}
				}
				if (HarderTimeManager.getTimeDifficulty(serverLevel, le) > 0) {
					
					Glooms.doGlooms(serverLevel, gameTime, difficulty, le, Glooms.TIME);
					
				}
			}

		} else {
			// "enter world event" horked as of 1.19.  Move boosts here..
			if (event.getEntity() instanceof Monster me) {
				Utility.debugMsg(2, "entering doBoostAbilities");
				String eDsc = EntityType.getKey(me.getType()).toString();
				Boosts.doBoostAbilities(me, eDsc);
			}
		}

	}

}
