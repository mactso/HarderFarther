package com.mactso.harderfarther.manager;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;

public class LootTableListManager {

	private static final Logger LOGGER = LogManager.getLogger();
	public static List<ResourceLocation> lootTableList = new ArrayList<>();
	public static boolean init = false;

	public static void init(String[] lootTables) {

		lootTableList.clear();

		int i = 0;
		for (String lootTableLine : lootTables) {
			i++;
			lootTableLine = lootTableLine.trim();
			if (lootTableLine.isEmpty())
				continue;
			try {

				lootTableList.add(new ResourceLocation(lootTableLine));
			} catch (Exception e) {
				LOGGER.warn("Harder Farther Warning:  Bad BonusLootTable At Line " + i + ",  '" + lootTableLine + "' ");
			}

		}
		int x = 3;
	}

	public static String reportConfiguredLootTables() {
		return (lootTableList.toString());
	}
	
	public static boolean isBonusLootTable (ResourceLocation lootTableToCheck) {
		if (lootTableToCheck == null) return false;
		return lootTableList.contains(lootTableToCheck);
	}
}
