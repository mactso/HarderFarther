package com.mactso.harderfarther.config;

import java.util.Hashtable;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class LootManager {
	private static int rareDice = 0;
	private static int uncoDice = 0;
	private static int commDice = 0;
	private static final Logger LOGGER = LogManager.getLogger();

	public static Hashtable<Integer, LootItem> lootHashtable = new Hashtable<>();

	
	public static ItemStack getLootItem (String Rarity, Random rand) {
		Item lootItem;
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
				if (workroll < worktotal) {
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

}
