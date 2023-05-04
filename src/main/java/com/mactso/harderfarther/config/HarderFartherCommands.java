package com.mactso.harderfarther.config;

import com.mactso.harderfarther.utility.ServerPlayer;
import com.mactso.harderfarther.utility.Utility;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TextFormatting;

public class HarderFartherCommands {


	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{

		dispatcher.register(Commands.literal("harderfarther").requires((source) -> 
			{
				return source.hasPermission(2);
			}
		)
		.then(Commands.literal("debugLevel").then(
				Commands.argument("debugLevel", IntegerArgumentType.integer(0,2)).executes(ctx -> {
					ServerPlayer p = (ServerPlayer) ctx.getSource().getPlayerOrException();
					return setDebugLevel(p, IntegerArgumentType.getInteger(ctx, "debugLevel"));
				}))).then(Commands.literal("setXpBottleChance").then(
						Commands.argument("setXpBottleChance", IntegerArgumentType.integer(0, 33)).executes(ctx -> {
							ServerPlayer p = (ServerPlayer) ctx.getSource().getPlayerOrException();
							return setOddsDropExperienceBottle(p, IntegerArgumentType.getInteger(ctx, "setXpBottleChance"));
				})))				
				.then(Commands.literal("reportLoot").executes(ctx -> {
					ServerPlayer p = (ServerPlayer) ctx.getSource().getPlayerOrException();
					String report = LootManager.report();
					Utility.sendChat(p, report, TextFormatting.GREEN);
					reportOddsXpBottleDrop(p);
					return 1;
				})).then(Commands.literal("info").executes(ctx -> {
					ServerPlayer p = (ServerPlayer) ctx.getSource().getPlayerOrException();
					printInfo(p);
					return 1;
					// return 1;
				})).then(Commands.literal("boostInfo").executes(ctx -> {
					ServerPlayer p = (ServerPlayer) ctx.getSource().getPlayerOrException();
					String chatMessage = "\nHarder Farther Maximum Monster Boosts";
					Utility.sendBoldChat(p, chatMessage, TextFormatting.DARK_GREEN);

					chatMessage = "  Monster Health ..........................: " + MyConfig.getHpMaxBoost() + " %."
							+ "\n  Damage ..............................................: " + MyConfig.getAtkDmgBoost()
							+ " %." + "\n  Movement .........................................: "
							+ MyConfig.getSpeedBoost() + " %." + "\n  KnockBack Resistance .........: "
							+ MyConfig.getKnockBackMod() + " %.";
					Utility.sendChat(p, chatMessage, TextFormatting.GREEN);
					return 1;
				})));

	}

	private static void printInfo(ServerPlayer p) {

		String dimensionName = p.level.dimension().location().toString();

		String chatMessage = "\nDimension: " + dimensionName + "\n Current Values";
		Utility.sendBoldChat(p, chatMessage, TextFormatting.DARK_GREEN);

		chatMessage = "  Harder Max Distance From Spawn....: " + MyConfig.getModifierMaxDistance() + " blocks."
				+ "\n  Spawn Safe Distance ..................................: " + MyConfig.getSafeDistance()
				+ " blocks." + "\n  Debug Level .......................................................: "
				+ MyConfig.getDebugLevel() + "\n  Only In Overworld .........................................: ";
		Utility.sendChat(p, chatMessage, TextFormatting.GREEN);

	}
	
	private static void reportOddsXpBottleDrop(ServerPlayer p) {
		Utility.sendChat(p, "  OddsXpBottleDrop .....................: " + MyConfig.getOddsDropExperienceBottle() +"%", TextFormatting.DARK_AQUA);
	}

	public static int setDebugLevel(ServerPlayer p, int newDebugLevel) {
		MyConfig.setDebugLevel(newDebugLevel);
		printInfo(p);
		return 1;
	}
	
	
	public static int setOddsDropExperienceBottle(ServerPlayer p, int newOdds) {
		MyConfig.setOddsDropExperienceBottle(newOdds);
		reportOddsXpBottleDrop(p);
		return 1;
	}
	
	String subcommand = "";
	
	String value = "";

}
