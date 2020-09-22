package com.mactso.harderfarther.events;

import java.util.ArrayList;
import net.minecraft.nbt.CompoundNBT;
import java.util.Collection;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BatEntity;
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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MonsterDropEventHandler {

	public static long tickTimer = 0;
	@SubscribeEvent
	public void handleMonsterDropsEvent(LivingDropsEvent event) {

		Entity eventEntity = event.getEntityLiving();

		if (event.getEntity() == null) {
			return;
		}

		if (!(eventEntity.world instanceof ServerWorld)) {
			return;
		}
    	ServerWorld serverWorld = (ServerWorld) eventEntity.world;
    	
		long worldTime = eventEntity.world.getGameTime();
		if (tickTimer > worldTime) {
			if (MyConfig.getaDebugLevel() > 0) {
				System.out.println("Mob Died: " + (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY()
						+ ", " + (int) eventEntity.getPosZ() + ", " + " inside no bonus loot frame.");
			}
			return;
		}

		if (!(eventEntity instanceof MobEntity)) {
			return;
		}
		

		MobEntity me = (MobEntity) eventEntity;
		if (me instanceof AnimalEntity) {
			return;
		}
		
		float hp = me.getMaxHealth();
		
		tickTimer = worldTime + (long) 40; // no drops for 2 seconds after a kill.
		Collection<ItemEntity> eventItems = event.getDrops();
		// Has to have been killed by a player
		DamageSource dS = event.getSource();
		if (dS.getTrueSource() != null) {
			Entity mobKillerEntity = dS.getTrueSource();
			if (!(mobKillerEntity instanceof ServerPlayerEntity)) {
				return;
			}
		}
		
    	Vector3d spawnVec = new Vector3d (serverWorld.getWorldInfo().getSpawnX(),serverWorld.getWorldInfo().getSpawnY(),serverWorld.getWorldInfo().getSpawnZ());
    	Vector3d eventVec = new Vector3d (eventEntity.getPosX(),eventEntity.getPosY(),eventEntity.getPosZ());
    	float distanceModifier = (float) (eventVec.distanceTo(spawnVec) / 1000.0) ;
    	
    	if (distanceModifier < 1.0) {
    		return;
    	}
    	
    	if (distanceModifier > 30) distanceModifier = 30;

    	float oddsMultiplier= distanceModifier/30.0f;
    	float odds = 333 * oddsMultiplier;
		int debug = 5;
		int randomLootRoll = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 1000));
		// String meName = me.getName().getString();		
		//temp xxzzy

		
		if (randomLootRoll > odds) {
			return;
		}

		randomLootRoll = (int) (Math.ceil(eventEntity.world.rand.nextDouble() * 1000));

		ItemStack itemStackToDrop;

		if (me instanceof BatEntity) {
			itemStackToDrop = new ItemStack(Items.LEATHER, (int) 1);
		} else {
			if (randomLootRoll < 100) {
				int durationAbsorb = (int) (30000 * oddsMultiplier);
				int effectAbsorb = (int) (1 + 8 * oddsMultiplier);
				int durationRegen = (int) (200 * oddsMultiplier);
				int effectRegen = (int) (5 * oddsMultiplier);

				StringTextComponent potionName = new StringTextComponent("Life Saving Potion");
				ItemStack potion = new ItemStack(Items.POTION).setDisplayName(potionName);
				Collection<EffectInstance> col = new ArrayList<EffectInstance>();
				col.add(new EffectInstance(Effects.ABSORPTION, durationAbsorb , effectAbsorb ));
				col.add(new EffectInstance(Effects.REGENERATION, durationRegen, effectRegen ));
				PotionUtils.appendEffects(potion, col);
				CompoundNBT compoundnbt = potion.getTag();
				compoundnbt.putInt("CustomPotionColor", 369022);
				itemStackToDrop = potion;

			} else if (randomLootRoll < 650) {
				itemStackToDrop = new ItemStack(Items.EMERALD, (int) 1);

			} else if (randomLootRoll < 850) {
				itemStackToDrop = new ItemStack(ModItems.BLESSED_APPLE, (int) 1);
			} else if (randomLootRoll < 950) {
				if (me instanceof CaveSpiderEntity) {
					itemStackToDrop = new ItemStack(Items.COAL, (int) 1);
				} else {
					itemStackToDrop = new ItemStack(Items.DIAMOND, (int) 1);
				}
			} else {
				itemStackToDrop = new ItemStack(Items.NETHERITE_SCRAP, (int) 1);
			}
		}
	
		ItemEntity myItemEntity = new ItemEntity(eventEntity.world, eventEntity.getPosX(), eventEntity.getPosY(),
				eventEntity.getPosZ(), itemStackToDrop);
		eventItems.add(myItemEntity);

		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println("Harder Farther: A " + eventEntity.getName().getString() + " Died at: "

					+ (int) eventEntity.getPosX() + ", " + (int) eventEntity.getPosY() + ", "
					+ (int) eventEntity.getPosZ() + ", " + "and dropped loot # " + randomLootRoll + ": ("
					+ itemStackToDrop.getItem().getName().getString() + ").");
		}

		int debugline = 3;
	}
}
