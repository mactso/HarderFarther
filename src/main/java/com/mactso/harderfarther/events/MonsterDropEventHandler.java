package com.mactso.harderfarther.events;

import java.util.ArrayList;
import java.util.Collection;

import com.mactso.harderfarther.config.LootManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;
import com.mactso.harderfarther.utility.Mob;
import com.mactso.harderfarther.utility.ServerLevel;
import com.mactso.harderfarther.utility.ServerPlayer;
import com.mactso.harderfarther.utility.TextComponent;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingDropsEvent event) {

		Entity eventEntity = event.getEntityLiving();

		if (event.getEntity() == null) {
			return;
		}

		if (!(eventEntity.level instanceof ServerLevel)) {
			return;
		}
		
		if (!(eventEntity instanceof Mob)) {
			return;
		}
		
		if (eventEntity instanceof BatEntity) {
			return;
		}
		if (eventEntity instanceof CodEntity) {
			return;
		}
		if (eventEntity instanceof SalmonEntity) {
			return;
		}


		
		ServerLevel serverWorld = (ServerLevel) eventEntity.level;
		DamageSource dS = event.getSource();
		long worldTime = eventEntity.level.getGameTime();

		BlockPos pos = new BlockPos(eventEntity.getX(), eventEntity.getY(), eventEntity.getZ());
		IChunk ichunk = serverWorld.getChunk(pos);
		IChunkLastMobDeathTime cap;

		// in this section prevent ALL drops if players are killing mobs too quickly.
		
		if (ichunk instanceof Chunk) {
			Chunk chunk = (Chunk)ichunk;
			cap = chunk.getCapability(CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME).orElse(null);
			lastMobDeathTime = 0;
			if (cap != null) {
				lastMobDeathTime = cap.getLastKillTime();
				long nextLootTime = lastMobDeathTime + MyConfig.getMobFarmingLimitingTimer();
				if (worldTime < nextLootTime) {
					Utility.debugMsg(2, pos, "Mobs Dying Too Quickly at: " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
										+ ", " + (int) eventEntity.getZ() + ", " + " loot and xp denied.  Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
					event.setCanceled(true);
					return;
				} else {
					Utility.debugMsg(1, pos, "Mobs Dropping Loot at : " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
					+ ", " + (int) eventEntity.getZ() + " Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
				}
				cap.setLastKillTime(worldTime);
			}
		}

		// In this section, give bonus loot
		if (!(MyConfig.isMakeMonstersHarderFarther())) {
			return;
		}

		if (eventEntity instanceof AnimalEntity) {
			return;
		}

	
		// float hp = me.getMaxHealth();

		Collection<ItemEntity> eventItems = event.getDrops();

		// Has to have been killed by a player to drop bonus loot.
		if ( dS.getEntity() == null) { return; }
		 
		Entity mobKillerEntity = dS.getEntity(); // TODO was "gettruesource" need to verify this.
		if (!(mobKillerEntity instanceof ServerPlayer)) {
			return;
		}
		
		if (eventEntity instanceof SlimeEntity) {
			SlimeEntity se = (SlimeEntity) eventEntity;

			if (se.getSize() < 4) {
				return;
			}
		}
		

		float distanceModifier = calcDistanceModifier(event, serverWorld);
		
		if (eventEntity.getY() > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier * 1.03f;
		}
		
		if (eventEntity.getY() < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier * 1.02f;
		}
		
;
		float odds = 333 * distanceModifier;

		int randomLootRolld1000 = (int) (Math.ceil(eventEntity.level.getRandom().nextDouble() * 1000));
		// String meName = me.getName().getString();
		

		int randomLootRolld100 = (int) (Math.ceil(eventEntity.level.getRandom().nextDouble() * 100));
		if (randomLootRolld100 < MyConfig.getOddsDropExperienceBottle()) {
			ItemStack itemStackToDrop;		
			itemStackToDrop = new ItemStack(Items.EXPERIENCE_BOTTLE, (int) 1);			
			ItemEntity myItemEntity = new ItemEntity(eventEntity.level, eventEntity.getX(), eventEntity.getY(),
					eventEntity.getZ(), itemStackToDrop);
			eventItems.add(myItemEntity);
		}
		randomLootRolld1000 = (int) (Math.ceil(eventEntity.level.getRandom().nextDouble() * 1000));

		if (randomLootRolld1000 >  odds) {
			return;
		}
		
		int randomLootItemRoll = (int) (Math.ceil(eventEntity.level.getRandom().nextDouble() * 1000));
		if (randomLootItemRoll < 640) {
			randomLootItemRoll += odds;
		}
		
		ItemStack itemStackToDrop;
		float itemPowerModifier = distanceModifier;

		if (eventEntity instanceof BatEntity) {
			itemStackToDrop = new ItemStack(Items.LEATHER, (int) 1);
		} else {
			if (randomLootItemRoll < 690) {
				itemStackToDrop = LootManager.getLootItem("c",eventEntity.level.getRandom());
			} else if (randomLootItemRoll < 750) {
				itemStackToDrop = makeLifeSavingPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 830) {
				itemStackToDrop = makeOgreStrengthPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 975) {
				if (eventEntity instanceof CaveSpiderEntity) {
					itemStackToDrop = new ItemStack(Items.COAL, (int) 1);
				} else {
					itemStackToDrop = LootManager.getLootItem("u",eventEntity.level.getRandom());

				}
			} else {
				if (itemPowerModifier > 0.95) {
					itemStackToDrop = LootManager.getLootItem("r",eventEntity.level.getRandom());
				} else {
					itemStackToDrop = LootManager.getLootItem("u",eventEntity.level.getRandom());

				}
			}
		}

		ItemEntity myItemEntity = new ItemEntity(eventEntity.level, eventEntity.getX(), eventEntity.getY(),
				eventEntity.getZ(), itemStackToDrop);
		eventItems.add(myItemEntity);

		Utility.debugMsg(2, pos, eventEntity.getName().getString() + " died and dropped loot: " +itemStackToDrop.getItem().getRegistryName() );
	}

	private ItemStack makeOgreStrengthPotion(float oddsMultiplier) {
		ItemStack itemStackToDrop;
		TextComponent potionName = new TextComponent("Ogre Power Potion");
		ItemStack potion = new ItemStack(Items.POTION).setHoverName(potionName); // TODO: Verify this 
		Collection<EffectInstance> col = new ArrayList<EffectInstance>();
		int durationAbsorb = (int) (4800 * oddsMultiplier);
		int effectAbsorb = (int) (4 * oddsMultiplier);
		if (effectAbsorb > 2) effectAbsorb = 2;
		col.add(new EffectInstance(Effects.DAMAGE_BOOST, durationAbsorb, effectAbsorb, false, false));
		col.add(new EffectInstance(Effects.NIGHT_VISION, durationAbsorb/2, 0, false, false));
		col.add(new EffectInstance(Effects.REGENERATION, 120, effectAbsorb, false, false));
		PotionUtils.setCustomEffects(potion, col);
		CompoundNBT compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 13415603);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}

	private ItemStack makeLifeSavingPotion(float oddsMultiplier) {
		ItemStack itemStackToDrop;
		int durationAbsorb = (int) (18000 * oddsMultiplier);
		int effectAbsorb = (int) (2 + 4 * oddsMultiplier);
		int durationRegen = (int) (200 * oddsMultiplier);
		int effectRegen = (int) (4 * oddsMultiplier);

		TextComponent potionName = new TextComponent("Life Saving Potion");
		ItemStack potion = new ItemStack(Items.POTION).setHoverName(potionName);
		Collection<EffectInstance> col = new ArrayList<EffectInstance>();
		col.add(new EffectInstance(Effects.ABSORPTION, durationAbsorb, effectAbsorb));
		col.add(new EffectInstance(Effects.REGENERATION, durationRegen, effectRegen));
		PotionUtils.setCustomEffects(potion, col);
		CompoundNBT compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 1369022);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}
	
	private float calcDistanceModifier(LivingDropsEvent event, ServerLevel serverLevel) {
		int eX = (int) event.getEntity().getX();
		int eY = (int) event.getEntity().getY();
		int eZ = (int) event.getEntity().getZ();
		float distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getXSpawn()-eX) / 1000.00f;
		
		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getZSpawn()-eZ) / 1000.00f;    		
        	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
        		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
        	}
    	}
    	
    	
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
        	Vector3i spawnVec = new Vector3i (serverLevel.getLevelData().getXSpawn(),serverLevel.getLevelData().getYSpawn(),serverLevel.getLevelData().getZSpawn());
        	Vector3i eventVec = new Vector3i (eX, eY, eZ);
        	distanceFromSpawn = (float) Math.sqrt(eventVec.distSqr(spawnVec)) ;
    	} 

    	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance();
    	}


    	float maxModifierDistance = MyConfig.getModifierMaxDistance();
       	float distanceModifier = distanceFromSpawn/maxModifierDistance ;
    	double spawnHeight = eY;

		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}

		return distanceModifier;
	}

}
