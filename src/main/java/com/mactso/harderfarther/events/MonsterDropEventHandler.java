package com.mactso.harderfarther.events;

import java.util.ArrayList;
import net.minecraft.nbt.CompoundNBT;
import java.util.Collection;

import com.ibm.icu.util.IslamicCalendar.CalculationType;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.timer.CapabilityChunkLastMobDeathTime;
import com.mactso.harderfarther.timer.IChunkLastMobDeathTime;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterDropEventHandler {

	public static long lastMobDeathTime = 0;

	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingDropsEvent event) {

		Entity eventEntity = event.getEntityLiving();

		if (event.getEntity() == null) {
			return;
		}

		if (!(eventEntity.world instanceof ServerWorld)) {
			return;
		}
		
		if (!(eventEntity instanceof MobEntity)) {
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

		ServerWorld serverWorld = (ServerWorld) eventEntity.world;
		DamageSource dS = event.getSource();
		long worldTime = eventEntity.world.getGameTime();

		BlockPos pos = new BlockPos(eventEntity.getPosX(), eventEntity.getPosY(), eventEntity.getPosZ());
		IChunk ichunk = serverWorld.getChunk(pos);
		IChunkLastMobDeathTime cap;

	

		// in this section prevent ALL drops if players are killing mobs too quickly.
		
		if (ichunk instanceof Chunk) {
			Chunk chunk = (Chunk) ichunk;
			cap = chunk.getCapability(CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME).orElse(null);
			lastMobDeathTime = 0;
			if (cap != null) {
				lastMobDeathTime = cap.getLastKillTime();
				long nextLootTime = lastMobDeathTime + MyConfig.getMobFarmingLimitingTimer();
				if (worldTime < nextLootTime) {
					
					if (MyConfig.getaDebugLevel() > 0) {
						System.out
								.println("Mobs Dying Too Quickly at: " + (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY()
										+ ", " + (int) eventEntity.getPosZ() + ", " + " loot and xp denied.  Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
					}
					event.setCanceled(true);
					return;
				}
				if (MyConfig.getaDebugLevel() > 0) {
					System.out
							.println("Mobs Dropping Loot at : " + (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY()
									+ ", " + (int) eventEntity.getPosZ() + " Current Time:" +worldTime+ " nextLoot Time: "+nextLootTime+".");
				}
				cap.setLastKillTime(worldTime);
				
			}
		}

		// In this section, give bonus loot
		if (!(MyConfig.isMakeMonstersHarderFarther())) {
			return;
		}

		MobEntity me = (MobEntity) eventEntity;
		if (me instanceof AnimalEntity) {
			return;
		}

		float hp = me.getMaxHealth();

		Collection<ItemEntity> eventItems = event.getDrops();

		// Has to have been killed by a player to drop bonus loot.
		if (dS.getTrueSource() == null) { return; }
		 
		Entity mobKillerEntity = dS.getTrueSource();
		if (!(mobKillerEntity instanceof ServerPlayerEntity)) {
			return;
		}
		
		if (eventEntity instanceof SlimeEntity) {
			SlimeEntity se = (SlimeEntity) eventEntity;

			if (se.getSlimeSize() < 4) {
				return;
			}
		}
		

		float distanceModifier = calcDistanceModifier(event, serverWorld);
		
		if (distanceModifier < 1.0) {
			return;
		}

		if (distanceModifier > MyConfig.getModifierValue())
			distanceModifier = MyConfig.getModifierValue();
		if (eventEntity.getPosY() > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}
		
		if (eventEntity.getPosY() < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}
		
		float oddsMultiplier = distanceModifier / MyConfig.getModifierValue();
		float odds = 333 * oddsMultiplier;
		int debug = 5;
		int randomLootRolld1000 = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 1000));
		// String meName = me.getName().getString();
		

		int randomLootRolld100 = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 100));
		if (randomLootRolld100 < MyConfig.getOddsDropExperienceBottle()) {
			ItemStack itemStackToDrop;		
			itemStackToDrop = new ItemStack(Items.EXPERIENCE_BOTTLE, (int) 1);			
			ItemEntity myItemEntity = new ItemEntity(eventEntity.world, eventEntity.getPosX(), eventEntity.getPosY(),
					eventEntity.getPosZ(), itemStackToDrop);
			eventItems.add(myItemEntity);
		}
		randomLootRolld1000 = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 1000));
        int debug9 = 3;
		if (randomLootRolld1000 >  odds) {
			return;
		}
		
		int randomLootItemRoll = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 1000));

		
		ItemStack itemStackToDrop;
		float itemPowerModifier = oddsMultiplier;

		if (me instanceof BatEntity) {
			itemStackToDrop = new ItemStack(Items.LEATHER, (int) 1);
		} else {
			if (randomLootItemRoll < 690) {
				itemStackToDrop = new ItemStack(MyConfig.getLootItemCommon(), (int) 1);

			} else if (randomLootItemRoll < 750) {
				itemStackToDrop = makeLifeSavingPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 830) {
				itemStackToDrop = makeOgreStrengthPotion(itemPowerModifier);
			} else if (randomLootItemRoll < 975) {
				if (me instanceof CaveSpiderEntity) {
					itemStackToDrop = new ItemStack(Items.COAL, (int) 1);
				} else {
					itemStackToDrop = new ItemStack(MyConfig.getLootItemUncommon(), (int) 1);
				}
			} else {
				if (itemPowerModifier > 0.95) {
					itemStackToDrop = new ItemStack(MyConfig.getLootItemRare(), (int) 1);
				} else {
					itemStackToDrop = new ItemStack(MyConfig.getLootItemUncommon(), (int) 1);
				}
			}
		}

		ItemEntity myItemEntity = new ItemEntity(eventEntity.world, eventEntity.getPosX(), eventEntity.getPosY(),
				eventEntity.getPosZ(), itemStackToDrop);
		eventItems.add(myItemEntity);

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println("Harder Farther: A " + eventEntity.getName().getString() + " Died at: "

					+ (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY() + ", "
					+ (int) eventEntity.getPosZ() + ", " + "and dropped loot # " + randomLootItemRoll + ": ("
					+ itemStackToDrop.getItem().getRegistryName() + ").");
		}

		int debugline = 3;
	}

	private ItemStack makeOgreStrengthPotion(float oddsMultiplier) {
		ItemStack itemStackToDrop;
		StringTextComponent potionName = new StringTextComponent("Ogre Power Potion");
		ItemStack potion = new ItemStack(Items.POTION).setDisplayName(potionName);
		Collection<EffectInstance> col = new ArrayList<EffectInstance>();
		int durationAbsorb = (int) (3000 * oddsMultiplier);
		int effectAbsorb = (int) (2 * oddsMultiplier);
		if (effectAbsorb > 2) effectAbsorb = 2;
		col.add(new EffectInstance(Effects.STRENGTH, durationAbsorb, effectAbsorb));
		col.add(new EffectInstance(Effects.NIGHT_VISION, 120, effectAbsorb));
		col.add(new EffectInstance(Effects.REGENERATION, 60, effectAbsorb));
		PotionUtils.appendEffects(potion, col);
		CompoundNBT compoundnbt = potion.getTag();
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

		StringTextComponent potionName = new StringTextComponent("Life Saving Potion");
		ItemStack potion = new ItemStack(Items.POTION).setDisplayName(potionName);
		Collection<EffectInstance> col = new ArrayList<EffectInstance>();
		col.add(new EffectInstance(Effects.ABSORPTION, durationAbsorb, effectAbsorb));
		col.add(new EffectInstance(Effects.REGENERATION, durationRegen, effectRegen));
		PotionUtils.appendEffects(potion, col);
		CompoundNBT compoundnbt = potion.getTag();
		compoundnbt.putInt("CustomPotionColor", 1369022);
		itemStackToDrop = potion;
		return itemStackToDrop;
	}
	
	private float calcDistanceModifier(LivingDropsEvent event, ServerWorld serverWorld) {
		int eX = (int) event.getEntity().getPosX();
		int eY = (int) event.getEntity().getPosY();
		int eZ = (int) event.getEntity().getPosZ();
		float distanceFromSpawn = (float) Math.abs(serverWorld.getWorldInfo().getSpawnX()-eX) / 1000.00f;
		
		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = (float) Math.abs(serverWorld.getWorldInfo().getSpawnZ()-eZ) / 1000.00f;    		
        	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
        		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
        	}
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
        	Vector3d spawnVec = new Vector3d (serverWorld.getWorldInfo().getSpawnX(),serverWorld.getWorldInfo().getSpawnY(),serverWorld.getWorldInfo().getSpawnZ());
        	Vector3d eventVec = new Vector3d (eX, eY, eZ);
        	distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec)) ;
    	} 

    	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance();
    	}
    	
    	int safe = MyConfig.getSafeDistance();
    	int mdist = MyConfig.getModifierMaxDistance();
    	int mval = MyConfig.getModifierValue();
    	float maxModifierDistance = MyConfig.getModifierMaxDistance();
    	float modifierValue = MyConfig.getModifierValue();
    	float pctDistanceToMax = distanceFromSpawn/maxModifierDistance ;
    	float distanceModifier = modifierValue * pctDistanceToMax;

    	double spawnHeight = eY;
    	int minSafeAltitude = MyConfig.getMinimumSafeAltitude();
    	int maxSafeAltitude = MyConfig.getMaximumSafeAltitude();
    	
		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}

		return distanceModifier;
	}

}
