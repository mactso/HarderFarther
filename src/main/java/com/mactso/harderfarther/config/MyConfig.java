package com.mactso.harderfarther.config;

//16.2 - 1.0.0.0 HarderFarther

import org.apache.commons.lang3.tuple.Pair;

import com.mactso.harderfarther.Main;

import net.minecraft.entity.player.PlayerEntity;
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

@Mod.EventBusSubscriber(modid = Main.MODID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class MyConfig {
	
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



	
	private static int      aDebugLevel;
	private static int 	    modifierMaxDistance;
	private static int      safeDistance;
	private static String itemCommon;
	private static String itemUncommon;
	private static String itemRare;
	private static String itemVeryRare;	
	private static boolean  hpMaxMod;
	private static boolean  speedMod;
	private static boolean  atkDmgMod;
	private static boolean  knockbackMod;
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

	public static void pushDebugLevel() {
		COMMON.debugLevel.set(aDebugLevel);
		COMMON.modifierMaxDistance.set(modifierMaxDistance);
		COMMON.safeDistance.set(safeDistance);
		COMMON.hpMaxMod.set(hpMaxMod);
		COMMON.speedMod.set(speedMod);
		COMMON.atkDmgMod.set(atkDmgMod);
		COMMON.knockbackMod.set(knockbackMod);
	}
	
	public static void pushValues() {

	}
	
	// remember need to push each of these values separately once we have commands.
	public static void bakeConfig()
	{
		aDebugLevel = COMMON.debugLevel.get();
		modifierMaxDistance = COMMON.modifierMaxDistance.get();
		safeDistance =COMMON.safeDistance.get();
		hpMaxMod=COMMON.hpMaxMod.get();
		speedMod=COMMON.speedMod.get();
		atkDmgMod=COMMON.atkDmgMod.get();
		knockbackMod=COMMON.knockbackMod.get();

		if (aDebugLevel > 0) {
			System.out.println("Harder Farther Debug Level: " + aDebugLevel );
		}
	}
	
	public static class Common {

		public final IntValue debugLevel;
		public final IntValue modifierMaxDistance;
		public final IntValue safeDistance;
		public final BooleanValue hpMaxMod;
		public final BooleanValue speedMod;
		public final BooleanValue atkDmgMod;
		public final BooleanValue knockbackMod;

		
		
		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("Regrowth Control Values");
			
			debugLevel = builder
					.comment("Debug Level: 0 = Off, 1 = Log, 2 = Chat+Log")
					.translation(Main.MODID + ".config." + "debugLevel")
					.defineInRange("debugLevel", () -> 0, 0, 2);
			
			modifierMaxDistance = builder
					.comment("modifierMaxDistance: 2000 to 100,000, default 30,000")
					.translation(Main.MODID + ".config." + "modifierMaxDistance")
					.defineInRange("modifierMaxDistance", () -> 2000, 30000, 100000);
			
			safeDistance = builder
					.comment("safeDistance: 0 to 160, default 64 ")
					.translation(Main.MODID + ".config." + "safeDistance")
					.defineInRange("safeDistance", () -> 0, 64, 160);			


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

