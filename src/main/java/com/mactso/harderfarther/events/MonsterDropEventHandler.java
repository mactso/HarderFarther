package com.mactso.harderfarther.events;

import java.util.ArrayList;
import java.util.Collection;

import com.mactso.harderfarther.config.LootManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
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
		
		if (eventEntity instanceof Bat) {
			return;
		}
		if (eventEntity instanceof Cod) {
			return;
		}
		if (eventEntity instanceof Salmon) {
			return;
		}


		
		ServerLevel serverWorld = (ServerLevel) eventEntity.level;
		DamageSource dS = event.getSource();
		long worldTime = eventEntity.level.getGameTime();

		BlockPos pos = new BlockPos(eventEntity.getX(), eventEntity.getY(), eventEntity.getZ());
		ChunkAccess ichunk = serverWorld.getChunk(pos);
		IChunkLastMobDeathTime cap;

		// in this section prevent ALL drops if players are killing mobs too quickly.
		
		if (ichunk instanceof LevelChunk chunk) {
			cap = chunk.getCapability(CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME).orElse(null);
			lastMobDeathTime = 0;
			if (cap != null) {
				lastMobDeathTime = cap.getLastKillTime();
				long nextLootTime = lastMobDeathTime + MyConfig.getMobFarmingLimitingTimer();
				if (worldTime < nextLootTime) {
					
					if (MyConfig.getaDebugLevel() > 0) {
						System.out
								.println("Mobs Dying Too Quickly at: " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
										+ ", " + (int) eventEntity.getZ() + ", " + " loot and xp denied.  Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
					}
					event.setCanceled(true);
					return;
				}
				if (MyConfig.getaDebugLevel() > 0) {
					System.out
							.println("Mobs Dropping Loot at : " + (int) eventEntity.getX() + ", " + (int) eventEntity.getY()
									+ ", " + (int) eventEntity.getZ() + " Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
				}
				cap.setLastKillTime(worldTime);
				
			}
		}

		// In this section, give bonus loot
		if (!(MyConfig.isMakeMonstersHarderFarther())) {
			return;
		}

		Mob me = (Mob) eventEntity;
		if (me instanceof Animal) {
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
		
		if (eventEntity instanceof Slime) {
			Slime se = (Slime) eventEntity;

			if (se.getSize() < 4) {
				return;
			}
		}
		

		float distanceModifier = calcDistanceModifier(event, serverWorld);
		
		if (distanceModifier < 1.0) {
			return;
		}

		
		if (distanceModifier > MyConfig.getModifierValue())
			distanceModifier = MyConfig.getModifierValue();
		if (eventEntity.getY() > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}
		
		if (eventEntity.getY() < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}
		
		float oddsMultiplier = distanceModifier / MyConfig.getModifierValue();
		float odds = 333 * oddsMultiplier;

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

		
		ItemStack itemStackToDrop;
		float itemPowerModifier = oddsMultiplier;

		if (me instanceof Bat) {
			itemStackToDrop = new ItemStack(Items.LEATHER, (int) 1);
		} else {
			if (randomLootItemRoll < 690) {
				itemStackToDrop = LootManager.getLootItem("c",eventEntity.level.getRandom());
			} else if (randomLootItemRoll < 750) {
				itemStackToDrop = makeLifeSavingPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 830) {
				itemStackToDrop = makeOgreStrengthPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 975) {
				if (me instanceof CaveSpider) {
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

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println("Harder Farther: A " + eventEntity.getName().getString() + " Died at: "

					+ (int) eventEntity.getX() + ", " + (int) eventEntity.getY() + ", "
					+ (int) eventEntity.getZ() + ", " + "and dropped loot # " + randomLootItemRoll + ": ("
					+ itemStackToDrop.getItem().getRegistryName() + ").");
		}
	}

	private ItemStack makeOgreStrengthPotion(float oddsMultiplier) {
		ItemStack itemStackToDrop;
		TextComponent potionName = new TextComponent("Ogre Power Potion");
		ItemStack potion = new ItemStack(Items.POTION).setHoverName(potionName); // TODO: Verify this 
		Collection<MobEffectInstance> col = new ArrayList<MobEffectInstance>();
		int durationAbsorb = (int) (3000 * oddsMultiplier);
		int effectAbsorb = (int) (2 * oddsMultiplier);
		if (effectAbsorb > 2) effectAbsorb = 2;
		col.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, durationAbsorb, effectAbsorb));
		col.add(new MobEffectInstance(MobEffects.NIGHT_VISION, 120, effectAbsorb));
		col.add(new MobEffectInstance(MobEffects.REGENERATION, 60, effectAbsorb));
		PotionUtils.setCustomEffects(potion, col);
		CompoundTag compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 13415603);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}

	private ItemStack makeLifeSavingPotion(float oddsMultiplier) {
		ItemStack itemStackToDrop;
		int durationAbsorb = (int) (30000 * oddsMultiplier);
		int effectAbsorb = (int) (1 + 8 * oddsMultiplier);
		int durationRegen = (int) (200 * oddsMultiplier);
		int effectRegen = (int) (5 * oddsMultiplier);

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
        	Vec3 spawnVec = new Vec3 (serverLevel.getLevelData().getXSpawn(),serverLevel.getLevelData().getYSpawn(),serverLevel.getLevelData().getZSpawn());
        	Vec3 eventVec = new Vec3 (eX, eY, eZ);
        	distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec)) ;
    	} 

    	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance();
    	}
    	int debug = 3;
//    	int safe = MyConfig.getSafeDistance();
//    	int mdist = MyConfig.getModifierMaxDistance();
//    	int mval = MyConfig.getModifierValue();
    	float maxModifierDistance = MyConfig.getModifierMaxDistance();
    	float modifierValue = MyConfig.getModifierValue();
    	float pctDistanceToMax = distanceFromSpawn/maxModifierDistance ;
    	float distanceModifier = modifierValue * pctDistanceToMax;

    	double spawnHeight = eY;
//    	int minSafeAltitude = MyConfig.getMinimumSafeAltitude();
//    	int maxSafeAltitude = MyConfig.getMaximumSafeAltitude();
    	
		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}

		return distanceModifier;
	}

}
