package com.mactso.harderfarther.events;

import java.util.Collection;
import java.util.Random;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.manager.HarderFartherManager;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	public static float minCommonDistancePct = 0.01f; // (1% of max distance before common loot)
	public static float minUncommonDistancePct = 0.1f; // (10% of max distance before uncommon loot)
	public static float minRareDistancePct = 0.95f; // (95% of max distance before rare loot)



	
	
	private boolean doLimitDropSpeed(ServerLevel serverLevel, Entity eventEntity, BlockPos pos) {
		long worldTime = serverLevel.getGameTime();
		ChunkAccess ichunk = serverLevel.getChunk(pos);
		IChunkLastMobDeathTime cap;
		boolean cancel = false;
		if (ichunk instanceof LevelChunk chunk) {
			cap = chunk.getCapability(CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME).orElse(null);
			lastMobDeathTime = 0;
			if (cap != null) {
				lastMobDeathTime = cap.getLastKillTime();
				long nextLootTime = lastMobDeathTime + MyConfig.getMobFarmingLimitingTimer();
				if (worldTime < nextLootTime) {
					Utility.debugMsg(2, pos,
							"Mobs Dying Too Quickly at: " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
									+ ", " + (int) eventEntity.getZ() + ", " + " loot and xp denied.  Current Time:"
									+ worldTime + " nextLoot Time: " + nextLootTime + ".");
					cancel = true;
				} else {
					Utility.debugMsg(1, pos,
							"Mobs Dropping Loot at : " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
									+ ", " + (int) eventEntity.getZ() + " Current Time:" + worldTime
									+ " nextLoot Time: " + nextLootTime + ".");
				}
				cap.setLastKillTime(worldTime);
			}
		}
		return cancel;
	}

	
	@SubscribeEvent  // serverside only.
	public boolean onMonsterDropsEvent(LivingDropsEvent event) {

		LivingEntity le = event.getEntityLiving();
		DamageSource dS = event.getSource();

		if (!isDropsSpecialLoot(event, le, dS))
			return false;

		ServerLevel serverLevel = (ServerLevel) le.level;

		Random rand = serverLevel.getRandom();
		BlockPos pos = new BlockPos(le.getX(), le.getY(), le.getZ());

		// in this section prevent ALL drops if players are killing mobs too quickly.

		boolean cancel = doLimitDropSpeed(serverLevel, le, pos);
		if (cancel) {
			event.setCanceled(true);
			return false;
		}

		// In this section, give bonus loot

		Collection<ItemEntity> eventItems = event.getDrops();

		LootManager.doXPBottleDrop(le, eventItems, rand);

		float boostDifficulty = HarderFartherManager.getDifficultyHere(serverLevel,le);
		if (boostDifficulty == 0)
			return false;
		if (boostDifficulty > MyConfig.getGrimCitadelMaxBoostPercent()) {
			if (boostDifficulty == GrimCitadelManager.getGrimDifficulty(le)) {
				boostDifficulty = MyConfig.getGrimCitadelMaxBoostPercent();
			}
		}		
		
		float odds = 100 + (333 * boostDifficulty);
		float health = le.getMaxHealth(); // todo debugging
		int d1000 = (int) (Math.ceil(rand.nextDouble() * 1000));

		if (d1000 > odds) {
			Utility.debugMsg(1, pos, "No Loot Upgrade: Roll " + d1000 + " odds " + odds);
			return false;
		}

		d1000 = (int) (Math.ceil(le.level.getRandom().nextDouble() * 1000));
		if (d1000 < 640) {
			d1000 += odds / 10;
		}

		Mob me = (Mob) event.getEntityLiving();
		ItemStack itemStackToDrop = LootManager.doGetLootStack(le, me, boostDifficulty, d1000);

		ItemEntity myItemEntity = new ItemEntity(le.level, le.getX(), le.getY(),
				le.getZ(), itemStackToDrop);

		eventItems.add(myItemEntity);

		Utility.debugMsg(1, pos, le.getName().getString() + " died and dropped loot: "
				+ itemStackToDrop.getItem().getRegistryName());
		return true;
	}




	
	
	private boolean isDropsSpecialLoot(LivingDropsEvent event, LivingEntity eventEntity, DamageSource dS) {

		if (!(MyConfig.isMakeMonstersHarderFarther()))
			return false;

		if (!(MyConfig.isUseLootDrops()))
			return false;

		if (event.getEntity() == null) {
			return false;
		}

		if (!(eventEntity.level instanceof ServerLevel)) {
			return false;
		}

		// Has to have been killed by a player to drop bonus loot.
		if ((dS != null) && (dS.getEntity() == null)) {
			return false;
		}

		if (!(dS.getEntity() instanceof ServerPlayer)) {
			return false;
		}

		if (!(eventEntity instanceof Mob)) { // TODO this should be 'enemy' I think
			return false;
		}

		if (eventEntity instanceof Slime) {
			Slime se = (Slime) eventEntity;

			if (se.getSize() < 4) {
				return false;
			}
		}

		if (!(eventEntity instanceof Enemy)) {

			if (eventEntity instanceof AbstractFish) {
				return false;
			}

			if (eventEntity instanceof WaterAnimal) {
				return false;
			}

			if (eventEntity instanceof Animal) {
				return false;
			}
			
			return false;
		}

		return true;
	}

}
