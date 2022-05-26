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
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
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

	public static int getaDebugLevel() {
		return aDebugLevel;
	}

	public static void setaDebugLevel(int aDebugLevel) {
		MyConfig.aDebugLevel = aDebugLevel;
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


	public static int getMinimumSafeAltitude() {
		return minimumSafeAltitude;
	}

	public static int getMaximumSafeAltitude() {
		return maximumSafeAltitude;
	}

	public static int getModifierValue() {
		return modifierValue;
	}
	public static int getOddsDropExperienceBottle() {
		return oddsDropExperienceBottle;
	}

	public static boolean isGrimCitadels() {
		return grimCitadels;
	}
	
	public static int getGrimCitadelDistance (BlockPos pos) {
		int closest = Integer.MAX_VALUE;

		for (BlockPos b : grimCitadelsBlockPosList) {
			closest = Math.min(b.distManhattan(pos), closest);
		}
		return closest;
	}

	public static int getGrimCitadelBonusDistance() {
		return grimCitadelBonusDistance;
	}
	
	public static List<BlockPos> getGrimCitadelsBlockPosList() {
		return grimCitadelsBlockPosList;
	}

	public static void setGrimCitadelsBlockPosList(List<BlockPos> grimCitadelsBlockPosList) {
		MyConfig.grimCitadelsBlockPosList = grimCitadelsBlockPosList;
	}
	
	private static int      aDebugLevel;
	private static int 	    limitMobFarmsTimer;
	private static boolean  makeMonstersHarderFarther;
	private static List<? extends String> dimensionOmitList;
	private static int 	    modifierMaxDistance;
	private static int 		modifierValue;
	private static int      safeDistance;
	private static int      oddsDropExperienceBottle;

	private static boolean  hpMaxMod;
	private static boolean  speedMod;
	private static boolean  atkDmgMod;
	private static boolean  knockbackMod;

	private static boolean  grimCitadels;
	private static int 	    grimCitadelBonusDistance;



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
		COMMON.dimensionOmitList.set(dimensionOmitList);
		COMMON.makeMonstersHarderFarther.set(makeMonstersHarderFarther);
		COMMON.modifierMaxDistance.set(modifierMaxDistance);
		COMMON.modifierValue.set(modifierValue);
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
		COMMON.grimCitadelsList.set(grimCitadelsList);
	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{

		aDebugLevel = COMMON.debugLevel.get();
		limitMobFarmsTimer = COMMON.limitMobFarmsTimer.get();
		dimensionOmitList = COMMON.dimensionOmitList.get();
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
		grimCitadels = COMMON.grimCitadels.get();
		grimCitadelBonusDistance = COMMON.grimCitadelBonusDistance.get();
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
		public final ConfigValue<List<? extends String>> dimensionOmitList;	
		public final BooleanValue makeMonstersHarderFarther;
		public final IntValue modifierMaxDistance;
		public final IntValue modifierValue;
		public final IntValue oddsDropExperienceBottle;
		public final IntValue safeDistance;
		public final IntValue minimumSafeAltitude;
		public final IntValue maximumSafeAltitude;
		public final ConfigValue<List<? extends String>> lootItemsList;
		public final BooleanValue hpMaxMod;
		public final BooleanValue speedMod;
		public final BooleanValue atkDmgMod;
		public final BooleanValue knockbackMod;
		
		public final BooleanValue grimCitadels;
		public final IntValue grimCitadelBonusDistance;
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
					"");
			
			builder.push("Harder Farther Control Values");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);

			dimensionOmitList = builder
					.comment("Dimension Omit List")
					.translation(Main.MODID + ".config" + "dimensionOmitList")
					.defineList("dimensionOmitList", defDimensionOmitList, Common::isString);			
			
			
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
					.defineInRange("modifierValue", () -> 99, 1, 999);
			
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
			
			
			lootItemsList = builder
					.comment("Loot Items List")
					.translation(Main.MODID + ".config" + "lootItemsList")
					.defineList("lootItemsList", defLootItemsList, Common::isString);

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
			
			builder.push("Grim Citadel Settings");
			
			grimCitadels = builder
					.comment("Use Grim Citadels (true) ")
					.translation(Main.MODID + ".config." + "grimCitadels")
					.define ("grimCitadels", () -> false);
			
			grimCitadelBonusDistance = builder
					.comment("grimCitadelBonusDistance : Mobs get increasing bonuses when closer to grim citadel")
					.translation(Main.MODID + ".config." + "grimCitadelBonusDistance")
					.defineInRange("grimCitadelBonusDistance", () -> 1000, 2000, 6000);	
			
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
	

	
	// support for any color chattext
	public static void sendChat(Player p, String chatMessage, TextColor color) {
		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withColor(color);
		p.sendMessage(component, p.getUUID());
	}
	
	// support for any color, optionally bold text.
	public static void sendBoldChat(Player p, String chatMessage, TextColor color) {
		TextComponent component = new TextComponent (chatMessage);

		component.getStyle().withBold(true);
		component.getStyle().withColor(color);
		
		p.sendMessage(component, p.getUUID());
	}

	
}

