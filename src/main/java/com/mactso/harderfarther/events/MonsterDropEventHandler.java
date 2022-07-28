package com.mactso.harderfarther.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.LootManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	public static float minCommonDistancePct = 0.01f;  // (1% of max distance before common loot)
	public static float minUncommonDistancePct = 0.1f;  // (10% of max distance before uncommon loot)
	public static float minRareDistancePct = 0.95f;  // (95% of max distance before rare loot)

	private float calcDistanceModifier(float distanceFromSpawn, int y) {

		float pctMax = (float) Math.min(1.0, distanceFromSpawn / MyConfig.getModifierMaxDistance());
		// TODO set up independent Grim Citadel modifier (so might be half as storng as 30000meters
		if (y < MyConfig.getMinimumSafeAltitude()) {
			pctMax *= 1.06f;
		} else if (y > MyConfig.getMaximumSafeAltitude()) {
			pctMax *= 1.09f;
		}
		return pctMax;

	}
	
	private float calcDistanceModifier(LivingDropsEvent event, ServerLevel serverLevel) {
		int eX = (int) event.getEntity().getX();
		int eY = (int) event.getEntity().getY();
		int eZ = (int) event.getEntity().getZ();
		float distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getXSpawn() - eX) / 1000.00f;

		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
			distanceFromSpawn = MyConfig.getModifierMaxDistance();
		}
		if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
			distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getZSpawn() - eZ) / 1000.00f;
			if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
				distanceFromSpawn = MyConfig.getModifierMaxDistance();
			}
		}

		if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
			Vec3 spawnVec = new Vec3(serverLevel.getLevelData().getXSpawn(), serverLevel.getLevelData().getYSpawn(),
					serverLevel.getLevelData().getZSpawn());
			Vec3 eventVec = new Vec3(eX, eY, eZ);
			distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec));
		}

		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
			distanceFromSpawn = MyConfig.getModifierMaxDistance();
		}

		float maxModifierDistance = MyConfig.getModifierMaxDistance();
		float distanceModifier = distanceFromSpawn / maxModifierDistance;
		double spawnHeight = eY;

		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier =  distanceModifier * 1.04f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier =  distanceModifier * 1.06f;
		}

		return distanceModifier;
	}

	private ItemStack doGetLootStack(Entity eventEntity, Mob me, float distanceModifier, int lootRoll) {
		ItemStack itemStackToDrop;
		BlockPos pos = me.blockPosition();
		Utility.debugMsg(1, pos, "doGetLootStack: Roll " + lootRoll + ". " + "distanceModifier = " + distanceModifier  );
		
		if (me instanceof Bat) {
			itemStackToDrop = new ItemStack(Items.LEATHER, (int) 1);
		} else {
			float itemPowerModifier = distanceModifier;
			if (lootRoll < 690) {
				itemStackToDrop = LootManager.getLootItem("c", eventEntity.level.getRandom());
			} else if (lootRoll < 750) {
				itemStackToDrop = makeLifeSavingPotion(itemPowerModifier);
			} else if (lootRoll < 830) {
				itemStackToDrop = makeOgreStrengthPotion(itemPowerModifier);
			} else if (lootRoll < 975) {
				if (me instanceof CaveSpider) {
					itemStackToDrop = new ItemStack(Items.COAL, (int) 1);
				} else {
					itemStackToDrop = LootManager.getLootItem("u", eventEntity.level.getRandom());
				}
			} else {
				if (distanceModifier > 0.95) {
					itemStackToDrop = LootManager.getLootItem("r", eventEntity.level.getRandom());
				} else {
					itemStackToDrop = LootManager.getLootItem("u", eventEntity.level.getRandom());
				}
			}
		}
		return itemStackToDrop;
	}

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

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingDropsEvent event) {

		LivingEntity eventEntity = event.getEntityLiving();

		if (event.getEntity() == null) {
			return;
		}

		if (!(eventEntity.level instanceof ServerLevel)) {
			return;
		}

		if (!(eventEntity instanceof Mob)) {
			return;
		}
		
		Mob me = (Mob) eventEntity;
		if (me instanceof Animal) {
			return;
		}
		
		if (eventEntity instanceof Slime) {
			Slime se = (Slime) eventEntity;

			if (se.getSize() < 4) {
				return;
			}
		}
		
		if (eventEntity instanceof Bat) {
			return;
		}
		if (eventEntity instanceof AbstractFish) {
			return;
		}
		


		ServerLevel serverLevel = (ServerLevel) eventEntity.level;
		LevelData winfo = serverLevel.getLevelData();
		Random rand = serverLevel.getRandom();
		DamageSource dS = event.getSource();
		BlockPos pos = new BlockPos(eventEntity.getX(), eventEntity.getY(), eventEntity.getZ());


		// in this section prevent ALL drops if players are killing mobs too quickly.

		boolean cancel = doLimitDropSpeed(serverLevel, eventEntity, pos);
		if (cancel) {
			event.setCanceled(true);
			return;
		}
		
		// In this section, give bonus loot
		if (!(MyConfig.isMakeMonstersHarderFarther())) {
			return;
		}

		// Has to have been killed by a player to drop bonus loot.
		if (dS.getEntity() == null) {
			return;
		}		

		Entity mobKillerEntity = dS.getEntity();
		if (!(mobKillerEntity instanceof ServerPlayer)) {
			return;
		}

		Collection<ItemEntity> eventItems = event.getDrops();

		double xzf = serverLevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		
		float distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec));
		distanceFromSpawn = GrimCitadelManager.doGrimDistanceAdjustment(serverLevel, eventEntity, distanceFromSpawn);
		float distanceModifier = calcDistanceModifier(distanceFromSpawn, (int) pos.getY());

		// float distanceModifier = calcDistanceModifier(event, serverLevel);
		
		doXPBottleDrop(eventEntity, eventItems, rand);
		
		float odds = 100 + (333 * distanceModifier);

		int d1000 = (int) (Math.ceil(rand.nextDouble() * 1000));

		if (d1000 > odds) {
			Utility.debugMsg(1, pos, "No Loot Upgrade: Roll " + d1000 + " odds " + odds);
			return;
		}

		d1000 = (int) (Math.ceil(eventEntity.level.getRandom().nextDouble() * 1000));
		if (d1000 < 640) {
			d1000 += odds/10;
		}


		ItemStack itemStackToDrop = doGetLootStack(eventEntity, me, distanceModifier, d1000);

		ItemEntity myItemEntity = new ItemEntity(eventEntity.level, eventEntity.getX(), eventEntity.getY(),
				eventEntity.getZ(), itemStackToDrop);
		eventItems.add(myItemEntity);

		Utility.debugMsg(1, pos, eventEntity.getName().getString() + " died and dropped loot: "
				+ itemStackToDrop.getItem().getRegistryName());
	}

	private void doXPBottleDrop(Entity eventEntity, Collection<ItemEntity> eventItems, Random rand) {
		int d100 = (int) (Math.ceil(rand.nextDouble() * 100));
		if (d100 < MyConfig.getOddsDropExperienceBottle()) {
			Utility.debugMsg(1, "XP Bottle dropped with roll " + d100);
			ItemStack itemStackToDrop;
			itemStackToDrop = new ItemStack(Items.EXPERIENCE_BOTTLE, (int) 1);
			ItemEntity myItemEntity = new ItemEntity(eventEntity.level, eventEntity.getX(), eventEntity.getY(),
					eventEntity.getZ(), itemStackToDrop);
			eventItems.add(myItemEntity);
		}
	}

	private ItemStack makeLifeSavingPotion(float distanceFactor) {
		ItemStack itemStackToDrop;
		int durationAbsorb = (int) (24000 * distanceFactor);
		int effectAbsorb = (int) (2 + 4 * distanceFactor);
		int durationRegen = (int) (20 + 200 * distanceFactor);
		int effectRegen = (int) (1 + 4 * distanceFactor);

		TextComponent potionName = new TextComponent("Life Saving Potion");
		ItemStack potion = new ItemStack(Items.POTION).setHoverName(potionName);
		Collection<MobEffectInstance> col = new ArrayList<MobEffectInstance>();
		col.add(new MobEffectInstance(MobEffects.ABSORPTION, durationAbsorb, effectAbsorb));
		col.add(new MobEffectInstance(MobEffects.REGENERATION, durationRegen, effectRegen));
		PotionUtils.setCustomEffects(potion, col);
		CompoundTag compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 1369022);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}

	private ItemStack makeOgreStrengthPotion(float distanceFactor) {
		ItemStack itemStackToDrop;
		TextComponent potionName = new TextComponent("Ogre Power Potion");
		ItemStack potion = new ItemStack(Items.POTION).setHoverName(potionName);
		Collection<MobEffectInstance> col = new ArrayList<MobEffectInstance>();
		int durationAbsorb = (int) (12000 * distanceFactor);
		int effectAbsorb = (int)  (1 + (4 * distanceFactor));
		if (effectAbsorb > 2)
			effectAbsorb = 2;
		col.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, durationAbsorb, effectAbsorb));
		col.add(new MobEffectInstance(MobEffects.NIGHT_VISION, 60 + durationAbsorb / 2, 0));
		col.add(new MobEffectInstance(MobEffects.REGENERATION, 120, effectAbsorb));
		PotionUtils.setCustomEffects(potion, col);
		CompoundTag compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 13415603);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}

}
