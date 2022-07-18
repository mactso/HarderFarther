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
		return boostMaxDistance;
	}

	public static void setModifierMaxDistance(int modifierMaxDistance) {
		MyConfig.boostMaxDistance = modifierMaxDistance;
	}

	public static int getSafeDistance() {
		return safeDistance;
	}

	public static void setSafeDistance(int safeDistance) {
		MyConfig.safeDistance = safeDistance;
	}

	public static boolean isHpMaxBoosted() {
		if (hpMaxBoost > 0) return true;
		return false;
	}

	public static boolean isSpeedBoosted() {
		if (speedBoost > 0) return true;
		return false;
	}

	public static boolean isAtkDmgBoosted() {
		if (atkDmgBoost > 0) return true;
		return false;
	}

	public static boolean isKnockBackBoosted() {
		if (knockbackBoost > 0) return true;
		return false;
	}

	public static int getHpMaxBoost() {
		return hpMaxBoost;
	}

	public static int getSpeedBoost() {
		return speedBoost;
	}

	public static int getAtkDmgBoost() {
		return atkDmgBoost;
	}

	public static int getKnockBackMod() {
		return knockbackBoost;
	}

	public static float getHpMaxPercent() {
		return (float) (hpMaxBoost/100);
	}

	public static float getSpeedPercent()  {
		return ((float)speedBoost/100);
	}

	public static float getAtkPercent()  {
		return (float) (atkDmgBoost/100);
	}

	public static float getKnockBackPercent() {
		return (float) (knockbackBoost/100);
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

	public static boolean isUseGrimCitadels() {
		return useGrimCitadels;
	}
	
	public static int getGrimCitadelsRadius() {
		return grimCitadelsRadius;
	}

	public static void setGrimCitadelsRadius(int grimCitadelsRadius) {
		MyConfig.grimCitadelsRadius = grimCitadelsRadius;
		COMMON.grimCitadelsRadius.set(grimCitadelsRadius);
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
	
	public static boolean isGrimEffectAnimals() {
		return grimEffectAnimals;
	}
	
	public static boolean isGrimEffectPigs() {
		return grimEffectPigs;
	}

	public static boolean isGrimEffectVillagers() {
		return grimEffectVillagers;
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
	
	public static void setGrimFogRedPercent(double grimFogRedPercent) {
		MyConfig.grimFogRedPercent = grimFogRedPercent/100;
		COMMON.grimFogRedPercent.set(grimFogRedPercent/100);
	}
	public static void setGrimFogGreenPercent(double grimFogGreenPercent) {
		MyConfig.grimFogGreenPercent = grimFogGreenPercent/100;
		COMMON.grimFogGreenPercent.set(grimFogGreenPercent/100);
	}

	public static void setGrimFogBluePercent(double grimFogBluePercent) {
		MyConfig.grimFogBluePercent = grimFogBluePercent/100;
		COMMON.grimFogBluePercent.set(grimFogBluePercent/100);
	}

	private static int      aDebugLevel;
	private static boolean  onlyOverworld;
	private static int 	    limitMobFarmsTimer;
	private static boolean  makeMonstersHarderFarther;
	private static List<? extends String> dimensionOmitList;
	private static int 	    boostMaxDistance;
	private static List<? extends String> lootItemsList;
	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;

	private static int hpMaxBoost;
	private static int speedBoost;
	private static int atkDmgBoost;
	private static int knockbackBoost;

	private static boolean  useHarderOverTime;
	private static boolean  fadeHarderOverTime;
	private static int      maxHarderTimeSeconds;
	private static int      minHarderTimeSeconds;
	
	private static boolean  useGrimCitadels;
	private static int      grimCitadelsRadius;
	private static int      grimCitadelsCount;
	private static int 	    grimCitadelBonusDistance;
	private static int 	    grimCitadelBonusDistanceSq;
	private static int 		grimCitadelPlayerCurseDistance;
	private static int 		grimCitadelPlayerCurseDistanceSq;

	private static boolean  grimEffectAnimals;
	private static boolean  grimEffectPigs;
	private static boolean  grimEffectVillagers;

	private static double 	grimFogRedPercent;
	private static double 	grimFogGreenPercent;
	private static double 	grimFogBluePercent;
	
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

		COMMON.limitMobFarmsTimer.set(limitMobFarmsTimer);
		
		COMMON.onlyOverworld.set(onlyOverworld);
		COMMON.dimensionOmitList.set(dimensionOmitList);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);

		COMMON.modifierMaxDistance.set(boostMaxDistance);
		COMMON.safeDistance.set(safeDistance);
		COMMON.minimumSafeAltitude.set(minimumSafeAltitude);
		COMMON.maximumSafeAltitude.set(maximumSafeAltitude);

		COMMON.lootItemsList.set(lootItemsList);
		COMMON.oddsDropExperienceBottle.set(oddsDropExperienceBottle);
		
		COMMON.hpMaxBoost.set(hpMaxBoost);
		COMMON.speedBoost.set(speedBoost);
		COMMON.atkDmgBoost.set(atkDmgBoost);
		COMMON.knockbackBoost.set(knockbackBoost);
		
		COMMON.useGrimCitadels.set(useGrimCitadels);
		COMMON.grimCitadelsRadius.set(grimCitadelsRadius);
		COMMON.grimCitadelsCount.set(grimCitadelsCount);
		COMMON.grimCitadelsList.set(grimCitadelsList);
		COMMON.grimCitadelBonusDistance.set(grimCitadelBonusDistance);
		COMMON.grimCitadelPlayerCurseDistance.set(grimCitadelPlayerCurseDistance);
		COMMON.grimEffectAnimals.set(grimEffectAnimals);
		COMMON.grimEffectPigs.set(grimEffectPigs);
		COMMON.grimEffectVillagers.set(grimEffectVillagers);
		COMMON.grimFogRedPercent.set (grimFogRedPercent);
		COMMON.grimFogBluePercent.set (grimFogBluePercent);
		COMMON.grimFogGreenPercent.set (grimFogGreenPercent);
	}
	
	public static void setGrimCitadels(boolean newValue) {
		COMMON.useGrimCitadels.set(newValue);
		useGrimCitadels = COMMON.useGrimCitadels.get();
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
	// this copies file changes into the running program variables.
	
	public static void bakeConfig()
	{
		aDebugLevel = COMMON.debugLevel.get();

		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		
		onlyOverworld = COMMON.onlyOverworld.get();

		dimensionOmitList = COMMON.dimensionOmitList.get();
		makeMonstersHarderFarther = COMMON.makeMonstersHarderFarther.get();
		boostMaxDistance = COMMON.modifierMaxDistance.get();
		minimumSafeAltitude = COMMON.minimumSafeAltitude.get();
		maximumSafeAltitude = COMMON.maximumSafeAltitude.get();
		safeDistance =COMMON.safeDistance.get();

		lootItemsList = COMMON.lootItemsList.get();
		LootManager.initLootItems(extract(lootItemsList));
		oddsDropExperienceBottle = COMMON.oddsDropExperienceBottle.get();
		
		hpMaxBoost=COMMON.hpMaxBoost.get();
		speedBoost=COMMON.speedBoost.get();
		atkDmgBoost=COMMON.atkDmgBoost.get();
		knockbackBoost=COMMON.knockbackBoost.get();

		useHarderOverTime = COMMON.useHarderOverTime.get() ;
		fadeHarderOverTime = COMMON.fadeHarderOverTime.get() ;
		maxHarderTimeSeconds = COMMON.maxHarderTimeSeconds.get() ;
		minHarderTimeSeconds = COMMON.minHarderTimeSeconds.get() ;
		
		useGrimCitadels = COMMON.useGrimCitadels.get();
		grimCitadelsBlockPosList = getBlockPositions(COMMON.grimCitadelsList.get());
		grimCitadelsList = COMMON.grimCitadelsList.get();
		grimCitadelsCount = COMMON.grimCitadelsCount.get();
		grimCitadelsRadius= COMMON.grimCitadelsRadius.get();

		bakeGrimRanges();
		
		grimEffectAnimals = COMMON.grimEffectAnimals.get();
		grimEffectPigs = COMMON.grimEffectPigs.get();
		grimEffectVillagers = COMMON.grimEffectVillagers.get();
		
		grimFogRedPercent = COMMON.grimFogRedPercent.get();
		grimFogBluePercent = COMMON.grimFogBluePercent.get();
		grimFogGreenPercent = COMMON.grimFogGreenPercent.get();
		
		if (aDebugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + aDebugLevel );
		}
	}
	


	private static List<BlockPos> getBlockPositions(List<? extends String> list) {

		List< BlockPos> returnList = new ArrayList<>();
		for (String pos : list) {
			 String[] posParts = pos.split(",");
			 int x = Integer.valueOf(posParts[0]);
			 int y = -1;
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
		public final IntValue hpMaxBoost;
		public final IntValue speedBoost;
		public final IntValue atkDmgBoost;
		public final IntValue knockbackBoost;

		public final BooleanValue  useHarderOverTime;
		public final BooleanValue  fadeHarderOverTime;
		public final IntValue      maxHarderTimeSeconds;
		public final IntValue      minHarderTimeSeconds;
		
		public final BooleanValue useGrimCitadels;
		public final IntValue grimCitadelsRadius;
		public final IntValue grimCitadelsCount;
		public final IntValue grimCitadelBonusDistance;
		public final IntValue grimCitadelPlayerCurseDistance;
		
		public final BooleanValue grimEffectAnimals;
		public final BooleanValue grimEffectPigs;
		public final BooleanValue grimEffectVillagers;
		public final DoubleValue grimFogRedPercent;
		public final DoubleValue grimFogBluePercent;
		public final DoubleValue grimFogGreenPercent;

		public final ConfigValue<List<? extends String>> grimCitadelsList;		
		
		public Common(ForgeConfigSpec.Builder builder) {
			List<String> defLootItemsList = Arrays.asList(
					"r,23,minecraft:netherite_scrap,1,1","r,1,minecraft:nether_wart,1,2",
					"r,1,minecraft:music_disc_far,1,1", 
					"u,2,minecraft:nether_wart,1,1", "u,3,minecraft:golden_carrot,1,1",
					"u,20,minecraft:diamond,1,1", "u,05,minecraft:emerald,1,3",
					"u,3,minecraft:oak_planks,1,5","u,1,minecraft:book,1,1",
					"u,01,minecraft:gold_ingot,1,1", "u,02,minecraft:chicken,1,2", 
					"u,5,minecraft:glowstone_dust,1,2", "u,1,minecraft:lead,1,1",
					"u,5,minecraft:stone_axe,1,2", 
					"c,3,minecraft:leather_boots,1,1", "c,2,minecraft:gold_nugget,1,3",
					"c,2,minecraft:candle,1,2", "c,5,minecraft:baked_potato,1,2",
					"c,2,minecraft:fishing_rod,1,1", "c,5,minecraft:cooked_cod,1,3",
					"c,3,minecraft:string,1,2",	"c,3,minecraft:iron_nugget,1,3",
					"c,1,minecraft:emerald,1,1","c,1,minecraft:paper,1,2");
			
			List<String> defGrimCitadelsList = Arrays.asList(
					"3100,3000","3000,-100", "3000,-3050",
					"0,3096",             "128,-3000",
					"-2970,3016", "-3017,80", "-3128,-3256");

			List<String> defDimensionOmitList = Arrays.asList(
					"minecraft:the_nether","minecraft:the_end");
			
			builder.push("Harder Farther Control Values");
			builder.push("Debug Settings");			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);
			builder.pop();
			builder.push("Farm Limiter Settings");
			limitMobFarmsTimer = builder
					.comment("Limit Mob Farm XP and Drops (0 == disabled).  5 ticks (quarter second) is enough. ")
					.translation(Main.MODID + ".config." + "limitMobFarmsTimer")
					.defineInRange("limitMobFarmsTimer", () -> 5, 0, 600);
			builder.pop();
			builder.push("HarderFarther Settings");
			onlyOverworld= builder
					.comment("Only in minecraft Overworld (true) ")
					.translation(Main.MODID + ".config." + "onlyOverworld")
					.define ("onlyOverworld", () -> true);
			
			dimensionOmitList = builder
					.comment("Dimension Omit List")
					.translation(Main.MODID + ".config" + "dimensionOmitList")
					.defineList("dimensionOmitList", defDimensionOmitList, Common::isString);			

			makeMonstersHarderFarther= builder
					.comment("Make Monsters Harder Farther From Spawn (true) ")
					.translation(Main.MODID + ".config." + "makeMonstersHarderFarther")
					.define ("makeMonstersHarderFarther", () -> true);
			
			modifierMaxDistance = builder
					.comment("modifierMaxDistance: Distance til Maximum Modifier Values Applied")
					.translation(Main.MODID + ".config." + "modifierMaxDistance")
					.defineInRange("modifierMaxDistance", () -> 30000, 1000, 6000000);
			
			safeDistance = builder
					.comment("Worldspawn Safe Distance: No Mobs Will Spawn In this Range")
					.translation(Main.MODID + ".config." + "safeDistance")
					.defineInRange("safeDistance", () -> 256, 1, 1000);			

			minimumSafeAltitude = builder
					.comment("minimumSafeAltitude: Mobs are 6% tougher below this altitude. ")
					.translation(Main.MODID + ".config." + "minimumSafeAltitude")
					.defineInRange("minimumSafeAltitude", () -> 32, -32, 64);			

			maximumSafeAltitude = builder
					.comment("maximumSafeAltitude: Mobs are 9% tougher above this altitude.")
					.translation(Main.MODID + ".config." + "maximumSafeAltitude")
					.defineInRange("maximumSafeAltitude", () -> 99, 65, 256);			
			builder.pop();
			
			builder.push("Loot Settings");
			oddsDropExperienceBottle = builder
					.comment("oddsDropExperienceBottle: Chance to drop 1 experience bottle.")
					.translation(Main.MODID + ".config." + "oddsDropExperienceBottle")
					.defineInRange("oddsDropExperienceBottle", () -> 33, 0, 100);
			
			lootItemsList = builder
					.comment("Loot Items List")
					.translation(Main.MODID + ".config" + "lootItemsList")
					.defineList("lootItemsList", defLootItemsList, Common::isString);
			builder.pop();
			builder.push("Boost Settings");
			hpMaxBoost = builder
					.comment("Boost Max Hit Points (Percent) ")
					.translation(Main.MODID + ".config." + "hpMaxBoost")
					.defineInRange("hpMaxBoost", () -> 200, 0, 999);

			speedBoost = builder
					.comment("Boost Movement Speed (Percent) ")
					.translation(Main.MODID + ".config." + "speedBoost")
					.defineInRange("speedBoost", () -> 50, 0, 999);
			
			atkDmgBoost = builder
					.comment("Boost Attack Damage (percent)")
					.translation(Main.MODID + ".config." + "atkDmgBoost")
					.defineInRange("atkDmgBoost", () -> 100, 0, 999);
			
			knockbackBoost = builder
					.comment("Boost Knockback Resistance (Percent) ")
					.translation(Main.MODID + ".config." + "knockbackBoost")
					.defineInRange("knockbackBoost", () -> 100, 0, 999);
			
			builder.pop();
			builder.push("Harder Over Time Settings");
//			public final BooleanValue  useHarderOverTime;
			useHarderOverTime= builder
					.comment("use Harder Over Time (false) ")
					.translation(Main.MODID + ".config." + "useHarderOverTime")
					.define ("useHarderOverTime", () -> false);

			//			public final BooleanValue  fadeHarderOverTime;
			fadeHarderOverTime= builder
					.comment("Do areas get easier while uninhabited? (true) ")
					.translation(Main.MODID + ".config." + "fadeHarderOverTime")
					.define ("fadeHarderOverTime", () -> true);
			//			public final IntValue      minHarderTimeSeconds;	
			minHarderTimeSeconds = builder
					.comment("How many seconds before area starts getting harder (6000s/5 gamedays)")
					.translation(Main.MODID + ".config." + "minHarderTimeSeconds")
					.defineInRange("minHarderTimeSeconds", () -> 6000, 60, 24000);	// 1 minute to 20 game days.
//			public final IntValue       maxHarderTimeSeconds;
			maxHarderTimeSeconds = builder
					.comment("How many seconds before area is max difficulty (12000s/10 gamedays)")
					.translation(Main.MODID + ".config." + "maxHarderTimeSeconds")
					.defineInRange("maxHarderTimeSeconds", () -> 12000, 60, 144000);	

			builder.pop();

			builder.push("Grim Citadel Settings");
			
			useGrimCitadels = builder
					.comment("Use Grim Citadels (true) ")
					.translation(Main.MODID + ".config." + "useGrimCitadels")
					.define ("useGrimCitadels", () -> true);
			
			grimCitadelsList = builder
					.comment("Grim Citadels List")
					.translation(Main.MODID + ".config" + "grimCitadelsList")
					.defineList("grimCitadelsList", defGrimCitadelsList, Common::isString);

			grimCitadelsCount = builder
					.comment("grimCitadelsCount : number of grim Citadels kept in the game (if 0 will count down til none left)")
					.translation(Main.MODID + ".config." + "grimCitadelsCount")
					.defineInRange("grimCitadelsCount", () -> 5, 0, 16);	

			grimCitadelsRadius= builder
					.comment("grimCitadelsRadius: Sug.  4-5 for one player, 5-7 for multiplayer.  Higher may slow server briefly while building.")
					.translation(Main.MODID + ".config." + "grimCitadelsRadius")
					.defineInRange("grimCitadelsRadius", () -> 5, 4, 11);	
			
			grimCitadelBonusDistance = builder
					.comment("grimCitadelBonusDistance : Mobs get increasing bonuses when closer to grim citadel")
					.translation(Main.MODID + ".config." + "grimCitadelBonusDistance")
					.defineInRange("grimCitadelBonusDistance", () -> 1750, 500, 6000);	

			grimCitadelPlayerCurseDistance = builder
					.comment("grimCitadelPlayerCurseDistance : Players get penalties this far from a grim citadel")
					.translation(Main.MODID + ".config." + "grimCitadelPlayerCurseDistance")
					.defineInRange("grimCitadelPlayerCurseDistance", () -> 1250, 255, 6000);	
			builder.pop();
			builder.push("Grim Effects Settings");					
			grimEffectAnimals = builder
					.comment("grimEffectAnimals : Master Switch Animals suffer grim effects. ")
					.translation(Main.MODID + ".config." + "grimEffectsAnimals")
					.define ("grimEffectAnimals", () -> true);

			grimEffectPigs = builder
					.comment("grimEffectPig : Pigs in grim area become Piglins, Zombified Piglins, or Hoglins over time. ")
					.translation(Main.MODID + ".config." + "grimEffectPigs")
					.define ("grimEffectPigs", () -> true);

			grimEffectVillagers = builder
					.comment("grimEffectVillagers : Villagers in grim area become witches. ")
					.translation(Main.MODID + ".config." + "grimEffectVillagers")
					.define ("grimEffectVillagers", () -> true);
			builder.push("Grim Fog Color Settings");			
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

			builder.pop();
			builder.pop();
			builder.pop();
			
		}
		

		public static boolean isString(Object o)
		{
			return (o instanceof String);
		}
	}
	
}

