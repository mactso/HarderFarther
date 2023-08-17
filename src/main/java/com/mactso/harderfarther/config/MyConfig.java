package com.mactso.harderfarther.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//16.2 - 1.0.0.0 HarderFarther

import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.manager.ChestLootManager;
import com.mactso.harderfarther.manager.LootManager;
import com.mactso.harderfarther.manager.LootTableListManager;

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
		return debugLevel;
	}

	public static void setDebugLevel(int newValue) {
		if (newValue <0 || newValue > 2) // TODO: this should be redundant 
			newValue = 0;
		 debugLevel = newValue;
	}

	public static boolean isOnlyOverworld() {
		return onlyOverworld;
	}


	//outposts stuff
	public static boolean isSpawnAnOutpost(){
		return useSpawnAsOutpost;
	}
	public static Vec3[] getOutpostPositions(){
		return outpostVecPositions;
	}

	//Max armor damage getter
	public static int getMaximumArmorDamage(){
		return maxArmorDamage;
	}

	
	public static boolean isUseLootDrops() {
		return useLootDrops;
	}

	public static void setUseLootDrops(boolean newValue) {
		MyConfig.useLootDrops = newValue;
		COMMON.useLootDrops.set(newValue);
	}

	
	
	
	public static int getBonusLootEnchantmentLevelModifier() {
		return bonusLootEnchantmentLevelModifier;
	}

	public static void setBonusLootEnchantmentLevelModifier(int bonusLootEnchantmentLevelModifier) {
		MyConfig.bonusLootEnchantmentLevelModifier = bonusLootEnchantmentLevelModifier;
	}

	public static boolean isDimensionOmitted(String dimensionName) {
			return dimensionOmitList.contains(dimensionName);
	}

	public static int getBoostMaxDistance() {
		return boostMaxDistance;
	}

	public static void setBoostMaxDistance(int modifierMaxDistance) {
		MyConfig.boostMaxDistance = modifierMaxDistance;
	}

	public static int getBoostMinDistance() {
		if (boostMinDistance >= boostMaxDistance) {
			return boostMaxDistance - 1;
		}
		return boostMinDistance;
	}

	public static void setBoostMinDistance(int boostMinDistance) {
		MyConfig.boostMinDistance = boostMinDistance;
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
	
	public static boolean isMakeHarderOverTime() {
		return makeHarderOverTime;
	}

	public static void setMakeHarderOverTime(boolean newValue) {
		MyConfig.makeHarderOverTime = newValue;
		COMMON.makeHarderOverTime.set(newValue);
	}

	public static int getMaxHarderTimeMinutes() {
		return maxHarderTimeMinutes;
	}

	public static void setMaxHarderTimeMinutes(int newValue) {
		MyConfig.maxHarderTimeMinutes = newValue;
		COMMON.maxHarderTimeMinutes.set(newValue);	
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

	public static int getGrimCitadelMaxBoostValue() {
		return grimCitadelMaxBoostPercent;
	}

	public static float getGrimCitadelMaxBoostPercent() {
		return (float)(grimCitadelMaxBoostPercent)/100;
	}
	
	public static void setGrimCitadelMaxBoostPercent(int newValue) {
		MyConfig.grimCitadelMaxBoostPercent = newValue;
		COMMON.grimCitadelMaxBoostPercent.set(newValue);
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
	
	public static boolean isGrimEffectTrees() {
		return grimEffectTrees;
	}

	public static void setGrimEffectTrees(boolean grimEffectTrees) {
		MyConfig.grimEffectTrees = grimEffectTrees;
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
	
	public static int getGrimLifeheartPulseSeconds() {
		return grimLifeheartPulseSeconds;
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

	private static int      debugLevel;
	private static boolean  onlyOverworld;
	private static int 	    limitMobFarmsTimer;
	private static boolean  makeMonstersHarderFarther;
	private static boolean  useLootDrops;
	private static List<? extends String> dimensionOmitList;

	//Outpost values
	private static boolean useSpawnAsOutpost;
	private static List<? extends String> outpostList;
	private static Vec3[] outpostVecPositions;

	private static int 	    boostMaxDistance;
	private static int 	    boostMinDistance;
	private static int 		maxArmorDamage;
	private static List<? extends String> lootItemsList;
	private static List<? extends String> bonusChestLootList;
	private static List<? extends String> bonusLootTableList;
	private static int bonusLootEnchantmentLevelModifier;
	
	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;

	//Boost config values
	private static int hpMaxBoost;
	private static int speedBoost;
	private static int atkDmgBoost;
	private static int knockbackBoost;

	private static boolean  makeHarderOverTime;
	private static int      maxHarderTimeMinutes;

	private static boolean  useGrimCitadels;
	private static int      grimCitadelsRadius;
	private static int      grimCitadelsCount;
	private static int 		grimCitadelMaxBoostPercent;
	private static int 	    grimCitadelBonusDistance;
	private static int 	    grimCitadelBonusDistanceSq;
	private static int 		grimCitadelPlayerCurseDistance;
	private static int 		grimCitadelPlayerCurseDistanceSq;

	private static boolean  grimEffectTrees;
	private static boolean  grimEffectAnimals;
	private static boolean  grimEffectPigs;
	private static boolean  grimEffectVillagers;
	private static int      grimLifeheartPulseSeconds;
	
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


	//Is this being used for anything or reserved?
	public static void pushValues() {
		COMMON.debugLevel.set(debugLevel);

		COMMON.limitMobFarmsTimer.set(limitMobFarmsTimer);
		
		COMMON.onlyOverworld.set(onlyOverworld);
		COMMON.dimensionOmitList.set(dimensionOmitList);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);
		COMMON.useLootDrops.set(useLootDrops);
		COMMON.boostMaxDistance.set(boostMaxDistance);
		COMMON.boostMinDistance.set(boostMinDistance);
		COMMON.maxArmorDamage.set(maxArmorDamage);

		COMMON.useSpawnAsOutpost.set(useSpawnAsOutpost);
		COMMON.outpostList.set(outpostList);

		COMMON.safeDistance.set(safeDistance);
		COMMON.minimumSafeAltitude.set(minimumSafeAltitude);
		COMMON.maximumSafeAltitude.set(maximumSafeAltitude);

		COMMON.lootItemsList.set(lootItemsList);
		COMMON.bonusChestLootList.set(bonusChestLootList);
		COMMON.bonusLootTableList.set(bonusLootTableList);
		COMMON.bonusLootEnchantmentLevelModifier.set(bonusLootEnchantmentLevelModifier);
		COMMON.oddsDropExperienceBottle.set(oddsDropExperienceBottle);
		
		COMMON.hpMaxBoost.set(hpMaxBoost);
		COMMON.speedBoost.set(speedBoost);
		COMMON.atkDmgBoost.set(atkDmgBoost);
		COMMON.knockbackBoost.set(knockbackBoost);
		
		COMMON.useGrimCitadels.set(useGrimCitadels);
		COMMON.grimCitadelsRadius.set(grimCitadelsRadius);
		COMMON.grimCitadelsCount.set(grimCitadelsCount);
		COMMON.grimCitadelsList.set(grimCitadelsList);
		COMMON.grimCitadelMaxBoostPercent.set(grimCitadelMaxBoostPercent);
		COMMON.grimCitadelBonusDistance.set(grimCitadelBonusDistance);
		COMMON.grimCitadelPlayerCurseDistance.set(grimCitadelPlayerCurseDistance);
		
		COMMON.grimEffectTrees.set(grimEffectTrees);
		COMMON.grimEffectAnimals.set(grimEffectAnimals);
		COMMON.grimEffectPigs.set(grimEffectPigs);
		COMMON.grimEffectVillagers.set(grimEffectVillagers);
		COMMON.grimLifeheartPulseSeconds.set(grimLifeheartPulseSeconds);
		
		COMMON.grimFogRedPercent.set (grimFogRedPercent);
		COMMON.grimFogBluePercent.set (grimFogBluePercent);
		COMMON.grimFogGreenPercent.set (grimFogGreenPercent);
	}
	
	public static void setUseGrimCitadels(boolean newValue) {
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
		debugLevel = COMMON.debugLevel.get();

		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		
		onlyOverworld = COMMON.onlyOverworld.get();

		//outposts config
		useSpawnAsOutpost = COMMON.useSpawnAsOutpost.get();
		outpostVecPositions = getOutpostVecArray(COMMON.outpostList.get());
		outpostList = COMMON.outpostList.get();

		dimensionOmitList = COMMON.dimensionOmitList.get();
		makeMonstersHarderFarther = COMMON.makeMonstersHarderFarther.get();
		maxArmorDamage = COMMON.maxArmorDamage.get();
		boostMaxDistance = COMMON.boostMaxDistance.get();
		boostMinDistance = COMMON.boostMinDistance.get();
		if (boostMinDistance >= boostMaxDistance) {
			LOGGER.error("ERROR: boostMinDistance should be less than boostMaxDistance.");
			LOGGER.error("ERROR: boostMinDistance will use (boostMaxDistance - 1).");
			boostMinDistance = boostMaxDistance-1;
			COMMON.boostMinDistance.set(boostMinDistance);
		}
		minimumSafeAltitude = COMMON.minimumSafeAltitude.get();
		maximumSafeAltitude = COMMON.maximumSafeAltitude.get();
		safeDistance =COMMON.safeDistance.get();

		lootItemsList = COMMON.lootItemsList.get();
		LootManager.init(extract(lootItemsList));
		bonusChestLootList = COMMON.bonusChestLootList.get();
		ChestLootManager.init(extract(bonusChestLootList));
		bonusLootTableList = COMMON.bonusLootTableList.get();
		LootTableListManager.init(extract(bonusLootTableList));
		bonusLootEnchantmentLevelModifier = COMMON.bonusLootEnchantmentLevelModifier.get();
		
		useLootDrops = COMMON.useLootDrops.get();
		oddsDropExperienceBottle = COMMON.oddsDropExperienceBottle.get();

		//boosts config
		hpMaxBoost=COMMON.hpMaxBoost.get();
		speedBoost=COMMON.speedBoost.get();
		atkDmgBoost=COMMON.atkDmgBoost.get();
		knockbackBoost=COMMON.knockbackBoost.get();

		makeHarderOverTime = COMMON.makeHarderOverTime.get() ;
		maxHarderTimeMinutes = COMMON.maxHarderTimeMinutes.get() ;

		//Grim citadel config
		useGrimCitadels = COMMON.useGrimCitadels.get();
		grimCitadelsBlockPosList = getBlockPositions(COMMON.grimCitadelsList.get());
		grimCitadelsList = COMMON.grimCitadelsList.get();
		grimCitadelsCount = COMMON.grimCitadelsCount.get();
		grimCitadelsRadius= COMMON.grimCitadelsRadius.get();
		grimCitadelMaxBoostPercent = COMMON.grimCitadelMaxBoostPercent.get();
		bakeGrimRanges();


		grimEffectTrees = COMMON.grimEffectTrees.get();
		grimEffectAnimals = COMMON.grimEffectAnimals.get();
		grimEffectPigs = COMMON.grimEffectPigs.get();
		grimEffectVillagers = COMMON.grimEffectVillagers.get();
		grimLifeheartPulseSeconds = COMMON.grimLifeheartPulseSeconds.get();

		//fog config
		grimFogRedPercent = COMMON.grimFogRedPercent.get();
		grimFogBluePercent = COMMON.grimFogBluePercent.get();
		grimFogGreenPercent = COMMON.grimFogGreenPercent.get();
		
		if (debugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + debugLevel );
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

	//For outposts
	private static Vec3[] getOutpostVecArray(List<? extends String> list){

		Vec3[] positions = new Vec3[COMMON.outpostList.get().size() + 1];

		int index = 1;
		for (String pos : list) {
			if(!pos.equals("")) {
				String[] posParts = pos.split(",");
				int x = Integer.valueOf(posParts[0]);
				int y = -1;
				int z = Integer.valueOf(posParts[1]);
				positions[index] = new Vec3(x, y, z);
			}else return new Vec3[1];
		}
		return positions;
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
		public final BooleanValue useSpawnAsOutpost;
		public final IntValue boostMaxDistance;
		public final IntValue boostMinDistance;
		public final IntValue maxArmorDamage;

		public final IntValue oddsDropExperienceBottle;
		public final IntValue safeDistance;
		public final IntValue minimumSafeAltitude;
		public final IntValue maximumSafeAltitude;
		
		public final BooleanValue useLootDrops;
		public final ConfigValue<List<? extends String>> lootItemsList;
		public final ConfigValue<List<? extends String>> bonusChestLootList; 
		public final ConfigValue<List<? extends String>> bonusLootTableList; 
		public final IntValue bonusLootEnchantmentLevelModifier;
		
		public final IntValue hpMaxBoost;
		public final IntValue speedBoost;
		public final IntValue atkDmgBoost;
		public final IntValue knockbackBoost;

		public final BooleanValue  makeHarderOverTime;
		public final IntValue      maxHarderTimeMinutes;
		
		public final BooleanValue useGrimCitadels;
		public final IntValue grimCitadelsRadius;
		public final IntValue grimCitadelsCount;
		public final IntValue grimCitadelBonusDistance;
		public final IntValue grimCitadelPlayerCurseDistance;
		public final IntValue grimCitadelMaxBoostPercent;
		
		public final BooleanValue grimEffectTrees;
		public final BooleanValue grimEffectAnimals;
		public final BooleanValue grimEffectPigs;
		public final BooleanValue grimEffectVillagers;
		public final IntValue grimLifeheartPulseSeconds;

		
		public final DoubleValue grimFogRedPercent;
		public final DoubleValue grimFogBluePercent;
		public final DoubleValue grimFogGreenPercent;

		public final ConfigValue<List<? extends String>> grimCitadelsList;
		public final ConfigValue<List<? extends String>> outpostList;
		
		public Common(ForgeConfigSpec.Builder builder) {
			List<String> defLootItemsList = Arrays.asList(
					"r,23,minecraft:netherite_scrap,1,1","r,1,minecraft:nether_wart,1,2",
					"r,1,minecraft:music_disc_far,1,1", 
					"u,2,minecraft:nether_wart,1,1", "u,3,minecraft:golden_carrot,1,1",
					"u,12,minecraft:diamond,1,1", "u,5,minecraft:emerald,1,3",
					"u,3,minecraft:oak_planks,1,5","u,1,minecraft:book,1,1",
					"u,1,minecraft:gold_ingot,1,1", "u,2,minecraft:chicken,1,2", 
					"u,5,minecraft:glowstone_dust,1,2", "u,1,minecraft:lead,1,1",
					"u,5,minecraft:stone_axe,1,2", "u,3,minecraft:stone_pickaxe,1,1", 
					"u,1,minecraft:iron_axe,1,1", "u,1,minecraft:beetroot_seeds,1,1", 
					"c,3,minecraft:leather_boots,1,1", "c,2,minecraft:gold_nugget,1,3",
					"c,2,minecraft:candle,1,2", "c,5,minecraft:baked_potato,1,2",
					"c,2,minecraft:fishing_rod,1,1", "c,5,minecraft:cooked_cod,1,3",
					"c,3,minecraft:string,1,2",	"c,3,minecraft:iron_nugget,1,3",
					"c,3,minecraft:honey_bottle,1,2",	"c,3,minecraft:stick,1,3",
					"c,1,minecraft:emerald,1,1","c,1,minecraft:paper,1,2");
			
			List<String> defGrimCitadelsList = Arrays.asList(
					"3600,3500","3500,-100", "3500,-3550",
					"0,3596",             "128,-3500",
					"-2970,3516", "-3517,80", "-3528,-3756");

			List<String> defDimensionOmitList = Arrays.asList(
					"minecraft:the_nether","minecraft:the_end");

			List<String> defBonusChestLootList = Arrays.asList(
								"01,minecraft:stone_pickaxe,1,1",
								"02,minecraft:stone_axe,1,1",
								"03,minecraft:leather_helmet,1,1",
								"04,minecraft:leather_chestplate,1,1",
								"05,minecraft:leather_leggings,1,1",
								"06,minecraft:leather_boots,1,1",
								"07,minecraft:tipped_arrow,12,18",
								"08,minecraft:emerald,2,5",
								"09,minecraft:iron_pickaxe,1,1",
								"10,minecraft:chainmail_helmet,1,1",
								"11,minecraft:chainmail_chestplate,1,1",
								"12,minecraft:chainmail_leggings,1,1",
								"13,minecraft:chainmail_boots,1,1",
								"14,minecraft:lapis_lazuli,7,11",
								"15,minecraft:honey_bottle,1,2",
								"16,minecraft:glowstone,7,9",
								"17,minecraft:iron_shovel,1,1",
								"18,minecraft:iron_axe,1,1",
								"19,minecraft:cooked_beef,1,5",
								"20,harderfarther:burnishing_stone,1,1",
								"21,minecraft:obsidian,1,3",
								"22,minecraft:emerald,1,6",
								"23,minecraft:diamond,1,1",
								"24,minecraft:iron_helmet,1,1",
								"25,minecraft:iron_chestplate,1,1",
								"26,minecraft:iron_leggings,1,1",
								"27,minecraft:iron_boots,1,1",
								"28,minecraft:iron_axe,1,1",
								"29,minecraft:glowstone_dust,11,23",
								"30,minecraft:moss_block,1,1",
								"31,minecraft:nautilus_shell,1,1",
								"32,minecraft:cooked_mutton,1,1",
								"33,minecraft:amethyst_block,13,18",
								"34,minecraft:budding_amethyst,1,1",
								"35,minecraft:potion,1,1",
								"36,minecraft:glow_squid_spawn_egg,1,1",
								"37,minecraft:golden_apple,1,1",
								"38,minecraft:jack_o_lantern,1,6",
								"39,minecraft:end_rod,1,3",
								"40,harderfarther:burnishing_stone,1,2",
								"41,minecraft:end_stone_bricks,11,20",
								"42,minecraft:amethyst_shard,11,17",
								"43,minecraft:diamond_helmet,1,1",
								"44,minecraft:diamond_chestplate,1,1",
								"45,minecraft:diamond_leggings,1,1",
								"46,minecraft:diamond_boots,1,1",
								"47,minecraft:glow_lichen,1,7",
								"48,minecraft:tnt,2,5",
								"49,minecraft:ice,31,37",
								"50,minecraft:infested_cobblestone,31,64",
								"51,minecraft:red_mushroom_block,31,64",
								"52,minecraft:mushroom_stem,31,64",
								"53,minecraft:brown_mushroom_block,31,64",
								"54,minecraft:chipped_anvil,1,1",
								"55,minecraft:turtle_egg,1,2",
								"56,minecraft:blaze_spawn_egg,1,2",
								"57,minecraft:llama_spawn_egg,1,2",
								"58,minecraft:evoker_spawn_egg,1,1",
								"59,minecraft:zombie_spawn_egg,1,3",
								"60,minecraft:drowned_spawn_egg,3,5",
								"61,minecraft:strider_spawn_egg,1,2",
								"62,minecraft:fox_spawn_egg,1,3",
								"63,minecraft:ocelot_spawn_egg,1,3",
								"64,minecraft:parrot_spawn_egg,1,2",
								"65,minecraft:terracotta,31,64",
								"66,minecraft:coal_block,11,16",
								"67,minecraft:packed_ice,24,48",
								"68,minecraft:green_stained_glass,49,64",
								"69,minecraft:sea_lantern,11,16",
								"70,minecraft:piston,9,16",
								"71,minecraft:bone_block,31,64",
								"72,minecraft:diamond,3,7",
								"73,minecraft:gold_nugget,33,64",
								"74,minecraft:iron_nugget,33,64",
								"75,minecraft:gunpowder,24,48",
								"76,minecraft:powder_snow_bucket,1,1",
								"77,minecraft:green_concrete_powder,56,64",
								"78,minecraft:brain_coral_fan,1,1",
								"79,minecraft:creeper_head,1,1",
								"80,minecraft:zombie_head,1,1",
								"81,minecraft:wither_skeleton_skull,1,1",
								"82,minecraft:skeleton_skull,1,1",
								"83,minecraft:firework_rocket,31,60",
								"84,minecraft:netherite_helmet,1,1",
								"85,minecraft:netherite_boots,1,1",
								"86,minecraft:endermite_spawn_egg,1,5",
								"87,minecraft:elder_guardian_spawn_egg,1,1",
								"88,minecraft:mooshroom_spawn_egg,1,1",
								"89,minecraft:ghast_spawn_egg,1,1",
								"90,minecraft:beacon,1,1",
								"91,minecraft:ender_chest,1,1",
								"92,minecraft:dragon_breath,11,15",
								"93,minecraft:dragon_head,1,1",
								"94,minecraft:diamond_pickaxe,1,1",
								"95,minecraft:diamond_axe,1,1",
								"96,minecraft:diamond_shovel,1,1",
								"97,minecraft:dragon_egg,1,1",
								"98,minecraft:elytra,1,1",
								"99,minecraft:end_portal_frame,1,1"
							 );

			List<String> defBonusLootTableList = Arrays.asList(
					"minecraft:chests/end_city_treasure",
					"minecraft:chests/simple_dungeon",
					"minecraft:chests/village/village_weaponsmith",
					"minecraft:chests/abandoned_mineshaft",
					"minecraft:chests/nether_bridge",
					"minecraft:chests/stronghold_crossing",
					"minecraft:chests/stronghold_corridor",
					"minecraft:chests/desert_pyramid",
					"minecraft:chests/jungle_temple",
					"minecraft:chests/igloo_chest",
					"minecraft:chests/woodland_mansion",
					"minecraft:chests/underwater_ruin_small",
					"minecraft:chests/underwater_ruin_big",
					"minecraft:chests/buried_treasure",
					"minecraft:chests/shipwreck_treasure",
					"minecraft:chests/pillager_outpost",
					"minecraft:chests/bastion_treasure",
					"minecraft:chests/ancient_city",
					"minecraft:chests/ruined_portal"
			);
			
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

			maxArmorDamage = builder
					.comment("Max damage mobs can do to armor")
					.translation(Main.MODID + ".config." + "maxArmorDamage")
					.defineInRange("maxArmorDamage",() -> 6, 0, 100);
			
			boostMaxDistance = builder
					.comment("boostMaxDistance: Distance til Maximum Boost Values Applied")
					.translation(Main.MODID + ".config." + "boostMaxDistance")
					.defineInRange("boostMaxDistance", () -> 30000, 1000, 6000000);

			boostMinDistance = builder
					.comment("boostMinDistance: Distance til Boost Values Start.  Should be less than boostMaxDistance")
					.translation(Main.MODID + ".config." + "boostMinDistance")
					.defineInRange("boostMinDistance", () -> 1000, 64, 1000000);
			
			safeDistance = builder
					.comment("Worldspawn Safe Distance: No Mobs Will Spawn In this Range")
					.translation(Main.MODID + ".config." + "safeDistance")
					.defineInRange("safeDistance", () -> 64, 1, 1000);			

			minimumSafeAltitude = builder
					.comment("minimumSafeAltitude: Mobs are 6% tougher below this altitude. ")
					.translation(Main.MODID + ".config." + "minimumSafeAltitude")
					.defineInRange("minimumSafeAltitude", () -> 32, -32, 64);			

			maximumSafeAltitude = builder
					.comment("maximumSafeAltitude: Mobs are 9% tougher above this altitude.")
					.translation(Main.MODID + ".config." + "maximumSafeAltitude")
					.defineInRange("maximumSafeAltitude", () -> 99, 65, 256);			
			builder.pop();

			builder.push("Outposts Settings");

			useSpawnAsOutpost = builder
					.comment("Should spawn be considered an outposts?")
					.translation(Main.MODID + ".config" + "useSpawnAsOutpost")
					.define("useSpawnAsOutpost", () -> true);

			outpostList = builder
					.comment("Outposts List")
					.translation(Main.MODID + ".config" + "outpostList")
					.defineList("outpostList", Arrays.asList(""), Common::isString);

			builder.pop();
			
			builder.push("Loot Settings");

			useLootDrops = builder
					.comment("Use enhanced harder farther loot?")
					.translation(Main.MODID + ".config." + "useLootDrops")
					.define ("useLootDrops", () -> true);

			
			oddsDropExperienceBottle = builder
					.comment("oddsDropExperienceBottle: Chance to drop 1 experience bottle.")
					.translation(Main.MODID + ".config." + "oddsDropExperienceBottle")
					.defineInRange("oddsDropExperienceBottle", () -> 33, 0, 100);
			
			lootItemsList = builder
					.comment("Loot Items List")
					.translation(Main.MODID + ".config" + "lootItemsList")
					.defineList("lootItemsList", defLootItemsList, Common::isString);
			
			
			
			bonusChestLootList = builder
			.comment("Loot Items List")
			.translation(Main.MODID + ".config" + "bonusChestLootList ")
			.defineList("bonusChestLootList ", defBonusChestLootList, Common::isString);

			
			bonusLootTableList = builder
			.comment("List of Loot Tables (usually containers) that will get bonus loot based on distance")
			.translation(Main.MODID + ".config" + "bonusLootTableList ")
			.defineList("bonusLootTableList ", defBonusLootTableList, Common::isString);

			bonusLootEnchantmentLevelModifier = builder
					.comment("Bonus Loot Enchantment Level Modifier")
					.translation(Main.MODID + ".config." + "bonusLootEnchantmentLevelModifier")
					.defineInRange("bonusLootEnchantmentLevelModifier", () -> 1, 0, 19);
			
			builder.pop();
			builder.push("Boost Settings");
			hpMaxBoost = builder
					.comment("Boost Max Hit Points (Percent) ")
					.translation(Main.MODID + ".config." + "hpMaxBoost")
					.defineInRange("hpMaxBoost", () -> 200, 0, 30000000);

			speedBoost = builder
					.comment("Boost Movement Speed (Percent). Very sensative setting. Over 50 kinda ridiculous ")
					.translation(Main.MODID + ".config." + "speedBoost")
					.defineInRange("speedBoost", () -> 20, 0, 999);
			
			atkDmgBoost = builder
					.comment("Boost Attack Damage (percent).  Mobs base damage is about 3 points + 2 for hard mode.")
					.translation(Main.MODID + ".config." + "atkDmgBoost")
					.defineInRange("atkDmgBoost", () -> 100, 0, 30000000);
			
			knockbackBoost = builder
					.comment("Boost Knockback Resistance (Percent) over 100 has no additional effect.")
					.translation(Main.MODID + ".config." + "knockbackBoost")
					.defineInRange("knockbackBoost", () -> 95, 0, 999);
			
			builder.pop();
			builder.push("Harder Over Time Settings");
//			public final BooleanValue  useHarderOverTime;
			makeHarderOverTime= builder
					.comment("use Harder Over Time (false) ")
					.translation(Main.MODID + ".config." + "makeHarderOverTime")
					.define ("makeHarderOverTime", () -> false);

			maxHarderTimeMinutes = builder
					.comment("How many minutes before area hits maximum difficulty.")
					.translation(Main.MODID + ".config." + "maxHarderTimeMinutes")
					.defineInRange("maxHarderTimeMinutes", () -> 720, 20, 28800);	

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
			
			grimCitadelMaxBoostPercent = builder
					.comment("grimCitadelMaxBoostPercent : max Boost a grim citadel can give")
					.translation(Main.MODID + ".config." + "grimCitadelMaxBoostPercent")
					.defineInRange("grimCitadelMaxBoostPercent", () -> 96, 0, 100);
			
			builder.pop();
			builder.push("Grim Effects Settings");					

			grimEffectTrees = builder
					.comment("grimEffectTrees : Master Switch Trees suffer Grim Effects. ")
					.translation(Main.MODID + ".config." + "grimEffectTrees")
					.define ("grimEffectTrees", () -> true);

			
			grimEffectAnimals = builder
					.comment("grimEffectAnimals : Master Switch Animals suffer grim effects. ")
					.translation(Main.MODID + ".config." + "grimEffectAnimals")
					.define ("grimEffectAnimals", () -> true);

			grimEffectPigs = builder
					.comment("grimEffectPig : Pigs in grim area become Piglins, Zombified Piglins, or Hoglins over time. ")
					.translation(Main.MODID + ".config." + "grimEffectPigs")
					.define ("grimEffectPigs", () -> true);

			grimEffectVillagers = builder
					.comment("grimEffectVillagers : Villagers in grim area become witches. ")
					.translation(Main.MODID + ".config." + "grimEffectVillagers")
					.define ("grimEffectVillagers", () -> true);
			grimLifeheartPulseSeconds = builder
					.comment("grimLifeheartPulseSeconds : number of grim Citadels kept in the game (if 0 will count down til none left)")
					.translation(Main.MODID + ".config." + "grimLifeheartPulseSeconds")
					.defineInRange("grimLifeheartPulseSeconds", () -> 120, 60, 600);	
			
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

