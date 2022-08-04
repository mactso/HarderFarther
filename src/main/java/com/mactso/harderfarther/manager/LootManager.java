package com.mactso.harderfarther.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.registries.ForgeRegistries;

public class LootManager {
	private static int rareDice = 0;
	private static int uncoDice = 0;
	private static int commDice = 0;
	private static final Logger LOGGER = LogManager.getLogger();

	public static Hashtable<Integer, LootItem> lootHashtable = new Hashtable<>();
	public static boolean init = false;
	
	public static ItemStack getLootItem (String Rarity, Random rand) {


		int workroll = 0;
		int worksides = 0;
		if (Rarity.equals("r")) {
			worksides = rareDice;
		}
		if (Rarity.equals("u")) {
			worksides = uncoDice;
		}
		if (Rarity.equals("c")) {
			worksides = commDice;
		}

		if (worksides == 0) return new ItemStack(Items.PAPER);

		workroll = rand.nextInt(worksides);
		// rolldice
		
		int worktotal = 0;
		for (LootItem hi : lootHashtable.values()) {
			if (Rarity.equals(hi.lootRarityKey)) {
				worktotal += hi.lootWeight;
				if (workroll <= worktotal) {
					int amt = rand.nextInt(hi.lootMax)+1;
					if (amt < hi.lootMin ) {
						amt = hi.lootMin;
					}
					return new ItemStack(hi.lootItem, amt);

				}
			}
		}
//		iterate thru hash and subtract weight off dice roll; when <=0 return item;
		return new ItemStack(Items.PAPER, 1);
	}
	
	public static String report ( ) {
		String lootList = "LootList:\n";

		for (LootItem hi : lootHashtable.values()) {
			lootList += asString(hi) + "\n";
		}
		return lootList;
	}
	public static void initLootItems(String [] lootItems) {

		lootHashtable.clear();
		rareDice = 0;
		uncoDice = 0;
		commDice = 0;

		int i = 0;
		for (String lootLine : lootItems) {
			lootLine = lootLine.trim();
			if (lootLine.isEmpty())
				continue;
			try {
				i++;
				StringTokenizer st = new StringTokenizer(lootLine, ",");
				String lootRarityKey = st.nextToken();
				int lootWeight = Integer.parseInt(st.nextToken());
				if (lootWeight <= 0) lootWeight = 1;
				Item lootItem= getItemFromString(st.nextToken());
				int lootMin = Integer.parseInt(st.nextToken());
				if (lootMin <= 0) lootMin = 1;
				int lootMax = Integer.parseInt(st.nextToken());
				if (lootMax <= 0) lootMax = 1;
				lootHashtable.put(i, new LootItem(lootRarityKey, lootWeight, lootItem, lootMin, lootMax));
				if (lootRarityKey.toLowerCase().equals("r")) {
					rareDice += lootWeight;
				} else 	if (lootRarityKey.toLowerCase().equals("u")) {
					uncoDice += lootWeight;
				} else 	if (lootRarityKey.toLowerCase().equals("c"))	{
					commDice += lootWeight;
				}

			} catch (Exception e) {
				System.out.println("Harder Farther Debug:  Bad LootItem Config at line " + i + ",  '" + lootLine + "' ");
			}

		}

	}

	private static Item getItemFromString (String name)
	{
		Item ret = Items.PAPER;
		try {
			ResourceLocation key = new ResourceLocation(name);
			if (ForgeRegistries.ITEMS.containsKey(key))
			{
				ret = ForgeRegistries.ITEMS.getValue(key);
			}
			else
				LOGGER.warn("Unknown item: " + name);
		}
		catch (Exception e)
		{
			LOGGER.warn("Bad item: " + name);
		}
		return ret;
	}
	
	
	
	public static String asString (LootItem li) {
		return ("("+li.lootRarityKey + ":" 
	+ li.lootWeight + ") " 
	+ li.lootItem.getDescription().getString().toString() + ", " 
	+ li.lootMin + " to " 
	+ li.lootMax );
	}
	
	
	
	public static class LootItem {

		 String lootRarityKey; // com,unc,rar
		 int lootWeight;
		 Item lootItem;
		 int lootMin;
		 int lootMax;

		public LootItem(String lootRarityKey, int lootWeight, Item lootItem, int lootMin, int lootMax) {
			this.lootRarityKey = lootRarityKey;
			this.lootWeight = lootWeight;
			this.lootItem = lootItem;
			this.lootMin = lootMin;
			this.lootMax = lootMax;
		}
		

	}
	
	public static void doXPBottleDrop(Entity eventEntity, Collection<ItemEntity> eventItems, Random rand) {
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
	
	public static ItemStack doGetLootStack(Entity eventEntity, Mob me, float distanceModifier, int lootRoll) {
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
	
	private static ItemStack makeLifeSavingPotion(float distanceFactor) {
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

	private static ItemStack makeOgreStrengthPotion(float distanceFactor) {
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
