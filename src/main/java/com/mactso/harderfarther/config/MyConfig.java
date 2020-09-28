package com.mactso.harderfarther.config;

//16.2 - 1.0.0.0 HarderFarther

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.Main;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {

	private static final Logger LOGGER = LogManager.getLogger();
	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static
	{
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	


	public static int getaDebugLevel() {
		return aDebugLevel;
	}

	public static void setaDebugLevel(int aDebugLevel) {
		MyConfig.aDebugLevel = aDebugLevel;
	}

	public static int getModifierMaxDistance() {
		return modifierMaxDistance;
	}

	public static void setModifierMaxDistance(int modifierMaxDistance) {
		MyConfig.modifierMaxDistance = modifierMaxDistance;
	}

	public static int getSafeDistance() {
		return safeDistance;
	}

	public static void setSafeDistance(int safeDistance) {
		MyConfig.safeDistance = safeDistance;
	}

	public static boolean isHpMaxModified() {
		return hpMaxMod;
	}

	public static boolean isSpeedModified() {
		return speedMod;
	}



	public static boolean isAtkDmgModified() {
		return atkDmgMod;
	}


	public static boolean isKnockBackModified() {
		return knockbackMod;
	}

	public static int getMobFarmingLimitingTimer() {
		return limitMobFarmsTimer;
	}

	public static boolean isMakeMonstersHarderFarther() {
		return makeMonstersHarderFarther;
	}

	public static String getItemCommon() {
		return itemCommonConfigValue;
	}

	public static String getItemUncommon() {
		return itemUncommonConfigValue;
	}

	public static String getItemRare() {
		return itemRareConfigValue;
	}

	public static int getMinimumSafeAltitude() {
		return minimumSafeAltitude;
	}

	public static int getMaximumSafeAltitude() {
		return maximumSafeAltitude;
	}
	public static Item getLootItemCommon() {
		return lootItemCommon;
	}

	public static Item getLootItemUncommon() {
		return lootItemUncommon;
	}

	public static Item getLootItemRare() {
		return lootItemRare;
	}
	public static int getModifierValue() {
		return modifierValue;
	}
	public static int getOddsDropExperienceBottle() {
		return oddsDropExperienceBottle;
	}
	
	private static int      aDebugLevel;
	private static int 	    limitMobFarmsTimer;
	private static boolean  makeMonstersHarderFarther;
	private static int 	    modifierMaxDistance;
	private static int 		modifierValue;
	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;
	private static String 	itemCommonConfigValue;
	private static String 	itemUncommonConfigValue;
	private static String 	itemRareConfigValue;
	private static Item     lootItemCommon;
	private static Item     lootItemUncommon;
	private static Item     lootItemRare;
	private static boolean  hpMaxMod;
	private static boolean  speedMod;
	private static boolean  atkDmgMod;
	private static boolean  knockbackMod;



	private static int      minimumSafeAltitude;
	private static int      maximumSafeAltitude;
	public static final int KILLER_ANY   = 0;
	public static final int KILLER_MOB_OR_PLAYER = 1;
	public static final int KILLER_PLAYER = 2;

	@SubscribeEvent
	public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent)
	{
		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	


	
	public static void pushValues() {
		COMMON.debugLevel.set(aDebugLevel);
		COMMON.limitMobFarmsTimer.set(limitMobFarmsTimer);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);
		COMMON.modifierMaxDistance.set(modifierMaxDistance);
		COMMON.modifierValue.set(modifierValue);
		COMMON.safeDistance.set(safeDistance);
		COMMON.oddsDropExperienceBottle.set(oddsDropExperienceBottle);
		COMMON.minimumSafeAltitude.set(minimumSafeAltitude);
		COMMON.maximumSafeAltitude.set(maximumSafeAltitude);
		Common.itemCommon.set(itemCommonConfigValue);
		Common.itemUncommon.set(itemUncommonConfigValue);
		Common.itemRare.set(itemRareConfigValue);
		COMMON.hpMaxMod.set(hpMaxMod);
		COMMON.speedMod.set(speedMod);
		COMMON.atkDmgMod.set(atkDmgMod);
		COMMON.knockbackMod.set(knockbackMod);
	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{

		aDebugLevel = COMMON.debugLevel.get();
		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		makeMonstersHarderFarther = COMMON.makeMonstersHarderFarther.get();
		modifierMaxDistance = COMMON.modifierMaxDistance.get();
		modifierValue = COMMON.modifierValue.get();
		safeDistance =COMMON.safeDistance.get();
		oddsDropExperienceBottle = COMMON.oddsDropExperienceBottle.get();
		hpMaxMod=COMMON.hpMaxMod.get();
		speedMod=COMMON.speedMod.get();
		atkDmgMod=COMMON.atkDmgMod.get();
		knockbackMod=COMMON.knockbackMod.get();
		minimumSafeAltitude = COMMON.minimumSafeAltitude.get();
		maximumSafeAltitude = COMMON.maximumSafeAltitude.get();
		itemCommonConfigValue = COMMON.itemCommon.get();
		itemUncommonConfigValue = COMMON.itemUncommon.get();
		itemRareConfigValue = COMMON.itemRare.get();
		lootItemCommon = getItemFromString(itemCommonConfigValue);
		lootItemUncommon = getItemFromString(itemUncommonConfigValue);
		lootItemRare = getItemFromString(itemRareConfigValue);
		
		if (aDebugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + aDebugLevel );
		}
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final IntValue limitMobFarmsTimer;
		public final BooleanValue makeMonstersHarderFarther;
		public final IntValue modifierMaxDistance;
		public final IntValue modifierValue;
		public final IntValue oddsDropExperienceBottle;
		public final IntValue safeDistance;
		public final IntValue minimumSafeAltitude;
		public final IntValue maximumSafeAltitude;
		private static ConfigValue<String> 	itemCommon;
		private static ConfigValue<String> 	itemUncommon;
		private static ConfigValue<String> 	itemRare;
		public final BooleanValue hpMaxMod;
		public final BooleanValue speedMod;
		public final BooleanValue atkDmgMod;
		public final BooleanValue knockbackMod;

		
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Harder Farther Control Values");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			limitMobFarmsTimer = builder
					.comment("Limit Mob Farm XP and Drops (0 == no limit).  5 ticks (quarter second) is enough. ")
					.translation(Main.MODID + ".config." + "limitMobFarmsTimer")
					.defineInRange("limitMobFarmsTimer", () -> 5, 0, 120);

			makeMonstersHarderFarther= builder
					.comment("Modify Max Hit Points (true) ")
					.translation(Main.MODID + ".config." + "makeMonstersHarderFarther")
					.define ("makeMonstersHarderFarther", () -> true);
			
			modifierMaxDistance = builder
					.comment("modifierMaxDistance: Distance til Maximum ModifierValue Applied")
					.translation(Main.MODID + ".config." + "modifierMaxDistance")
					.defineInRange("modifierMaxDistance", () -> 30000, 2000, 100000);

			modifierValue = builder
					.comment("modifierValue: Increase Mob Values by 1% to 999%")
					.translation(Main.MODID + ".config." + "modifierValue")
					.defineInRange("modifierValue", () -> 30, 1, 999);
			
			safeDistance = builder
					.comment("Worldspawn Safe Distance: No Mobs Will Spawn In this Range")
					.translation(Main.MODID + ".config." + "safeDistance")
					.defineInRange("safeDistance", () -> 64, 64, 160);			

			minimumSafeAltitude = builder
					.comment("minimumSafeAltitude: Mobs are 6% tougher below this altitude. ")
					.translation(Main.MODID + ".config." + "minimumSafeAltitude")
					.defineInRange("minimumSafeAltitude", () -> 32, 1, 48);			

			maximumSafeAltitude = builder
					.comment("maximumSafeAltitude: Mobs are 9% tougher above this altitude.")
					.translation(Main.MODID + ".config." + "maximumSafeAltitude")
					.defineInRange("maximumSafeAltitude", () -> 99, 76, 160);			

			oddsDropExperienceBottle = builder
					.comment("oddsDropExperienceBottle: Chance to drop 1 experience bottle.")
					.translation(Main.MODID + ".config." + "oddsDropExperienceBottle")
					.defineInRange("oddsDropExperienceBottle", () -> 33, 0, 100);
			
			itemCommon = builder
					.comment("Common Loot Item: ")
					.translation(Main.MODID + ".config" + "itemCommon")
					.define("itemCommon", "minecraft:emerald");

			itemUncommon = builder
					.comment("Uncommon Loot Item")
					.translation(Main.MODID + ".config" + "itemUncommon")
					.define("itemUncommon", "minecraft:diamond");

			itemRare = builder
					.comment("Rare Loot Item")
					.translation(Main.MODID + ".config" + "itemRare")
					.define("itemRare", "minecraft:netherite_scrap");

			hpMaxMod = builder
					.comment("Modify Max Hit Points (true) ")
					.translation(Main.MODID + ".config." + "hpMaxMod")
					.define ("hpMaxMod", () -> true);

			speedMod = builder
					.comment("Modify Movement Speed (true) ")
					.translation(Main.MODID + ".config." + "speedMod")
					.define ("speedMod", () -> true);
			
			atkDmgMod = builder
					.comment("Modify Max Hit Points (true)")
					.translation(Main.MODID + ".config." + "atkDmgMod")
					.define ("atkDmgMod", () -> true);
			
			knockbackMod = builder
					.comment("Modify Knockback Resistance (true) ")
					.translation(Main.MODID + ".config." + "knockbackMod")
					.define ("knockbackMod", () -> true);

			builder.pop();
			
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
	
	// support for any color chattext
	public static void sendChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);
		component.getStyle().setColor(color);
		p.sendMessage(component, p.getUniqueID());
	}
	
	// support for any color, optionally bold text.
	public static void sendBoldChat(PlayerEntity p, String chatMessage, Color color) {
		StringTextComponent component = new StringTextComponent (chatMessage);

		component.getStyle().setBold(true);
		component.getStyle().setColor(color);
		
		p.sendMessage(component, p.getUniqueID());
	}
	
}

