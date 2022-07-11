package com.mactso.harderfarther.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//16.2 - 1.0.0.0 HarderFarther

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.Main;

import net.minecraft.core.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

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

	public static int getDebugLevel() {
		return aDebugLevel;
	}

	public static void setDebugLevel(int debugLevel) {
		if (debugLevel <0 || debugLevel > 2) 
			debugLevel = 0;
		 aDebugLevel = debugLevel;
	}

	public static boolean isOnlyOverworld() {
		return onlyOverworld;
	}	
	
	public static boolean isDimensionOmitted(String dimensionName) {
			return dimensionOmitList.contains(dimensionName);
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

	public static boolean isHpMaxBoosted() {
		if (hpMaxMod > 0) return true;
		return false;
	}

	public static boolean isSpeedBoosted() {
		if (speedMod > 0) return true;
		return false;
	}

	public static boolean isAtkDmgBoosted() {
		if (atkDmgMod > 0) return true;
		return false;
	}

	public static boolean isKnockBackBoosted() {
		if (knockbackMod > 0) return true;
		return false;
	}

	public static int getHpMaxMod() {
		return hpMaxMod;
	}

	public static int getSpeedMod() {
		return speedMod;
	}

	public static int getAtkDmgMod() {
		return atkDmgMod;
	}

	public static int getKnockBackMod() {
		return knockbackMod;
	}

	public static float getHpMaxPercent() {
		return (float) (hpMaxMod/100);
	}

	public static float getSpeedPercent()  {
		return ((float)speedMod/100);
	}

	public static float getAtkPercent()  {
		return (float) (atkDmgMod/100);
	}

	public static float getKnockBackPercent() {
		return (float) (knockbackMod/100);
	}
	
	public static int getMobFarmingLimitingTimer() {
		return limitMobFarmsTimer;
	}

	public static boolean isMakeMonstersHarderFarther() {
		return makeMonstersHarderFarther;
}


	public static int getMinimumSafeAltitude() {
		return minimumSafeAltitude;
	}

	public static int getMaximumSafeAltitude() {
		return maximumSafeAltitude;
	}


	public static int getOddsDropExperienceBottle() {
		return oddsDropExperienceBottle;
	}

	public static boolean isGrimCitadels() {
		return grimCitadels;
	}
	
	public static int getGrimCitadelsCount() {
		return grimCitadelsCount;
	}

	public static int getGrimCitadelBonusDistance() {
		return grimCitadelBonusDistance;
	}
	
	public static int getGrimCitadelBonusDistanceSq() {
		return grimCitadelBonusDistanceSq;
	}
	
	public static int getGrimCitadelPlayerCurseDistance() {
		return grimCitadelPlayerCurseDistance;
	}

	public static int getGrimCitadelPlayerCurseDistanceSq() {
		return grimCitadelPlayerCurseDistanceSq;
	}

	public static List<BlockPos> getGrimCitadelsBlockPosList() {
		return grimCitadelsBlockPosList;
	}

	public static void setGrimCitadelsBlockPosList(List<BlockPos> grimCitadelsBlockPosList) {
		MyConfig.grimCitadelsBlockPosList = grimCitadelsBlockPosList;
	}
	
	public static boolean isGrimHarmPassiveCreatures() {
		return grimHarmAnimals;
	}
	
	public static double getGrimFogRedPercent() {
		return grimFogRedPercent;
	}

	public static double getGrimFogBluePercent() {
		return grimFogBluePercent;
	}

	public static double getGrimFogGreenPercent() {
		return grimFogGreenPercent;
	}
	
	private static int      aDebugLevel;
	private static boolean  onlyOverworld;
	private static int 	    limitMobFarmsTimer;
	private static boolean  makeMonstersHarderFarther;
	private static List<? extends String> dimensionOmitList;
	private static int 	    modifierMaxDistance;
	private static List<? extends String> lootItemsList;
	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;

	private static int  hpMaxMod;
	private static int  speedMod;
	private static int atkDmgMod;
	private static int knockbackMod;

	private static boolean  grimCitadels;
	private static int      grimCitadelsCount;
	private static int 	    grimCitadelBonusDistance;
	private static int 	    grimCitadelBonusDistanceSq;
	private static int 		grimCitadelPlayerCurseDistance;
	private static int 		grimCitadelPlayerCurseDistanceSq;

	private static boolean  grimHarmAnimals;

	private static double 	grimFogRedPercent;
	private static double 	grimFogBluePercent;
	private static double 	grimFogGreenPercent;

	private static List<? extends String> grimCitadelsList;
	private static List<BlockPos> grimCitadelsBlockPosList;
	


	private static int      minimumSafeAltitude;
	private static int      maximumSafeAltitude;
	public static final int KILLER_ANY   = 0;
	public static final int KILLER_MOB_OR_PLAYER = 1;
	public static final int KILLER_PLAYER = 2;

	@SubscribeEvent
	public static <ModConfig> void onModConfigEvent(final ModConfigEvent configEvent)
	{

		if (configEvent.getConfig().getSpec() == MyConfig.COMMON_SPEC)
		{
			bakeConfig();
		}
	}	


	public static void pushValues() {
		COMMON.debugLevel.set(aDebugLevel);
		COMMON.onlyOverworld.set(onlyOverworld);
		COMMON.limitMobFarmsTimer.set(limitMobFarmsTimer);
		COMMON.dimensionOmitList.set(dimensionOmitList);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);
		COMMON.modifierMaxDistance.set(modifierMaxDistance);
		COMMON.lootItemsList.set(lootItemsList);
		COMMON.safeDistance.set(safeDistance);
		COMMON.oddsDropExperienceBottle.set(oddsDropExperienceBottle);
		COMMON.minimumSafeAltitude.set(minimumSafeAltitude);
		COMMON.maximumSafeAltitude.set(maximumSafeAltitude);
		COMMON.hpMaxMod.set(hpMaxMod);
		COMMON.speedMod.set(speedMod);
		COMMON.atkDmgMod.set(atkDmgMod);
		COMMON.knockbackMod.set(knockbackMod);
		COMMON.grimCitadels.set(grimCitadels);
		COMMON.grimCitadelBonusDistance.set(grimCitadelBonusDistance);
		COMMON.grimCitadelPlayerCurseDistance.set(grimCitadelPlayerCurseDistance);
		COMMON.grimCitadelsCount.set(grimCitadelsCount);

		COMMON.grimCitadelsList.set(grimCitadelsList);
		COMMON.grimHarmAnimals.set(grimHarmAnimals);
		COMMON.grimFogRedPercent.set (grimFogRedPercent);
		COMMON.grimFogBluePercent.set (grimFogBluePercent);
		COMMON.grimFogGreenPercent.set (grimFogGreenPercent);
	}
	
	public static void setGrimCitadels(boolean newValue) {
		COMMON.grimCitadels.set(newValue);
		grimCitadels = COMMON.grimCitadels.get();
	}

	
	public static void setBonusRange(int newRange) {
		COMMON.grimCitadelBonusDistance.set(newRange);
		COMMON.grimCitadelPlayerCurseDistance.set((int)(newRange*0.7f));
		bakeGrimRanges();
	}

	private static void bakeGrimRanges() {
		grimCitadelBonusDistance = COMMON.grimCitadelBonusDistance.get();
		grimCitadelBonusDistanceSq = grimCitadelBonusDistance*grimCitadelBonusDistance;
		grimCitadelPlayerCurseDistance = COMMON.grimCitadelPlayerCurseDistance.get();
		grimCitadelPlayerCurseDistanceSq = grimCitadelPlayerCurseDistance * grimCitadelPlayerCurseDistance;
	}

	public static void setOddsDropExperienceBottle(int newOdds) {
		COMMON.oddsDropExperienceBottle.set(newOdds);
		oddsDropExperienceBottle = newOdds;
	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{

		aDebugLevel = COMMON.debugLevel.get();
		onlyOverworld = COMMON.onlyOverworld.get();
		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		dimensionOmitList = COMMON.dimensionOmitList.get();
		makeMonstersHarderFarther = COMMON.makeMonstersHarderFarther.get();
		modifierMaxDistance = COMMON.modifierMaxDistance.get();
		lootItemsList = COMMON.lootItemsList.get();
		LootManager.initLootItems(extract(lootItemsList));
		safeDistance =COMMON.safeDistance.get();
		oddsDropExperienceBottle = COMMON.oddsDropExperienceBottle.get();
		hpMaxMod=COMMON.hpMaxMod.get();
		speedMod=COMMON.speedMod.get();
		atkDmgMod=COMMON.atkDmgMod.get();
		knockbackMod=COMMON.knockbackMod.get();
		minimumSafeAltitude = COMMON.minimumSafeAltitude.get();
		maximumSafeAltitude = COMMON.maximumSafeAltitude.get();
		grimCitadels = COMMON.grimCitadels.get();
		grimCitadelsCount = COMMON.grimCitadelsCount.get();
		bakeGrimRanges();
		grimHarmAnimals = COMMON.grimHarmAnimals.get();
		grimFogRedPercent = COMMON.grimFogRedPercent.get();
		grimFogBluePercent = COMMON.grimFogBluePercent.get();
		grimFogGreenPercent = COMMON.grimFogGreenPercent.get();
		grimCitadelsBlockPosList = getBlockPositions(COMMON.grimCitadelsList.get());
		grimCitadelsList = COMMON.grimCitadelsList.get();
		if (aDebugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + aDebugLevel );
		}

	}
	


	private static List<BlockPos> getBlockPositions(List<? extends String> list) {

		List< BlockPos> returnList = new ArrayList<>();
		for (String pos : list) {
			 String[] posParts = pos.split(",");
			 int x = Integer.valueOf(posParts[0]);
			 int y = 90;
			 int z = Integer.valueOf(posParts[1]);
			 returnList.add(new BlockPos(x,y,z));
		}
		return returnList;
	}

	private static String[] extract(List<? extends String> value)
	{
		return value.toArray(new String[value.size()]);
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final IntValue limitMobFarmsTimer;
		public final BooleanValue onlyOverworld;
		public final ConfigValue<List<? extends String>> dimensionOmitList;	
		public final BooleanValue makeMonstersHarderFarther;
		public final IntValue modifierMaxDistance;

		public final IntValue oddsDropExperienceBottle;
		public final IntValue safeDistance;
		public final IntValue minimumSafeAltitude;
		public final IntValue maximumSafeAltitude;
		
		public final ConfigValue<List<? extends String>> lootItemsList;
		public final IntValue hpMaxMod;
		public final IntValue speedMod;
		public final IntValue atkDmgMod;
		public final IntValue knockbackMod;
		
		public final BooleanValue grimCitadels;
		public final IntValue grimCitadelsCount;
		public final IntValue grimCitadelBonusDistance;
		public final IntValue grimCitadelPlayerCurseDistance;
		
		public final BooleanValue grimHarmAnimals;
		public final DoubleValue grimFogRedPercent;
		public final DoubleValue grimFogBluePercent;
		public final DoubleValue grimFogGreenPercent;

		public final ConfigValue<List<? extends String>> grimCitadelsList;		
		
		public Common(ForgeConfigSpec.Builder builder) {
			List<String> defLootItemsList = Arrays.asList(
					"r,20,minecraft:netherite_scrap,1,1", 
					"u,20,minecraft:diamond,1,1", "u,5,minecraft:emerald,1,3", 
					"c,20,minecraft:glowstone,1,2", "c,3,minecraft:leather_boots,1,1", 
					"c,3,minecraft:emerald,1,1",	"c,3,minecraft:book,1,2");
			
			List<String> defGrimCitadelsList = Arrays.asList(
					"3100,3000","3000,-100", "3000,-3050",
					"0,3096",             "128,-3000",
					"-2970,3016", "-3017,80", "-3128,-3256");

			List<String> defDimensionOmitList = Arrays.asList(
					"minecraft:the_nether","minecraft:the_end");
			
			builder.push("Harder Farther Control Values");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			onlyOverworld= builder
					.comment("Only in minecraft Overworld (true) ")
					.translation(Main.MODID + ".config." + "onlyOverworld")
					.define ("onlyOverworld", () -> true);
			
			dimensionOmitList = builder
					.comment("Dimension Omit List")
					.translation(Main.MODID + ".config" + "dimensionOmitList")
					.defineList("dimensionOmitList", defDimensionOmitList, Common::isString);			
			
			
			limitMobFarmsTimer = builder
					.comment("Limit Mob Farm XP and Drops (0 == no limit).  5 ticks (quarter second) is enough. ")
					.translation(Main.MODID + ".config." + "limitMobFarmsTimer")
					.defineInRange("limitMobFarmsTimer", () -> 5, 0, 120);

			makeMonstersHarderFarther= builder
					.comment("Make Monsters Harder Farther From Spawn (true) ")
					.translation(Main.MODID + ".config." + "makeMonstersHarderFarther")
					.define ("makeMonstersHarderFarther", () -> true);
			
			modifierMaxDistance = builder
					.comment("modifierMaxDistance: Distance til Maximum Modifier Values Applied")
					.translation(Main.MODID + ".config." + "modifierMaxDistance")
					.defineInRange("modifierMaxDistance", () -> 30000, 2000, 100000);
			
			safeDistance = builder
					.comment("Worldspawn Safe Distance: No Mobs Will Spawn In this Range")
					.translation(Main.MODID + ".config." + "safeDistance")
					.defineInRange("safeDistance", () -> 200, 1, 364);			

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
			
			lootItemsList = builder
					.comment("Loot Items List")
					.translation(Main.MODID + ".config" + "lootItemsList")
					.defineList("lootItemsList", defLootItemsList, Common::isString);

			hpMaxMod = builder
					.comment("Boost Max Hit Points (Percent) ")
					.translation(Main.MODID + ".config." + "hpMaxMod")
					.defineInRange("hpMaxMod", () -> 200, 0, 999);

			speedMod = builder
					.comment("Boost Movement Speed (Percent) ")
					.translation(Main.MODID + ".config." + "speedMod")
					.defineInRange("speedMod", () -> 50, 0, 999);
			
			atkDmgMod = builder
					.comment("Boost Attack Damage (percent)")
					.translation(Main.MODID + ".config." + "atkDmgMod")
					.defineInRange("atkDmgMod", () -> 100, 0, 999);
			
			knockbackMod = builder
					.comment("Boost Knockback Resistance (Percent) ")
					.translation(Main.MODID + ".config." + "knockbackMod")
					.defineInRange("knockbackMod", () -> 100, 0, 999);
			
			builder.push("Grim Citadel Settings");
			
			grimCitadels = builder
					.comment("Use Grim Citadels (true) ")
					.translation(Main.MODID + ".config." + "grimCitadels")
					.define ("grimCitadels", () -> false);

			grimCitadelBonusDistance = builder
					.comment("grimCitadelBonusDistance : Mobs get increasing bonuses when closer to grim citadel")
					.translation(Main.MODID + ".config." + "grimCitadelBonusDistance")
					.defineInRange("grimCitadelBonusDistance", () -> 1750, 500, 6000);	

			grimCitadelPlayerCurseDistance = builder
					.comment("grimCitadelPlayerCurseDistance : Players get penalties this far from a grim citadel")
					.translation(Main.MODID + ".config." + "grimCitadelPlayerCurseDistance")
					.defineInRange("grimCitadelPlayerCurseDistance", () -> 1250, 255, 6000);	
					
			grimCitadelsCount = builder
					.comment("grimCitadelsCount : number of grim Citadels kept in the game (if 0 will count down til none left)")
					.translation(Main.MODID + ".config." + "grimCitadelsCount")
					.defineInRange("grimCitadelsCount", () -> 5, 0, 16);	

			grimHarmAnimals = builder
					.comment("grimHarmAnimals : Animals near grim citadels get sick. ")
					.translation(Main.MODID + ".config." + "grimHarmAnimals")
					.define ("grimHarmAnimals", () -> true);
			
			
			grimFogRedPercent = builder
					.comment("grimFogRedPercent : Grim Fog Red Component Multiplier")
					.translation(Main.MODID + ".config." + "grimFogRedPercent")
					.defineInRange("grimFogRedPercent", () -> 0.95, 0.0, 1.0);	

			grimFogBluePercent = builder
					.comment("grimFogBluePercent : Grim Fog Blue Component Multiplier")
					.translation(Main.MODID + ".config." + "grimFogBluePercent")
					.defineInRange("grimFogBluePercent", () -> 0.05, 0.0, 1.0);	

			grimFogGreenPercent = builder
					.comment("grimFogGreenPercent : Grim Fog Green Component Multiplier")
					.translation(Main.MODID + ".config." + "grimFogGreenPercent")
					.defineInRange("grimFogGreenPercent", () -> 0.05, 0.0, 1.0);	

			grimCitadelsList = builder
					.comment("Loot Items List")
					.translation(Main.MODID + ".config" + "grimCitadelsList")
					.defineList("grimCitadelsList", defGrimCitadelsList, Common::isString);
			builder.pop();

			builder.pop();
			
		}
		

		public static boolean isString(Object o)
		{
			return (o instanceof String);
		}
	}




	

	
//	// support for any color chattext
//	public static void sendChat(Player p, String chatMessage, TextColor color) {
//		TextComponent component = new TextComponent (chatMessage);
//		component.getStyle().withColor(color);
//		p.sendMessage(component, p.getUUID());
//	}
//	
//	// support for any color, optionally bold text.
//	public static void sendBoldChat(Player p, String chatMessage, TextColor color) {
//		TextComponent component = new TextComponent (chatMessage);
//
//		component.getStyle().withBold(true);
//		component.getStyle().withColor(color);
//		
//		p.sendMessage(component, p.getUUID());
//	}
//


	
}

