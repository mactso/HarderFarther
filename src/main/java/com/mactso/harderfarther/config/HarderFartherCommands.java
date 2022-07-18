package com.mactso.harderfarther.config;

import java.util.Iterator;
import java.util.List;

import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import com.mactso.harderfarther.utility.Utility;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class HarderFartherCommands {


	private static void printColorInfo(ServerPlayer p) {

		String chatMessage = "\nFog Color Current Values";
		Utility.sendBoldChat(p, chatMessage, ChatFormatting.DARK_GREEN);
		chatMessage = "R (" + MyConfig.getGrimFogRedPercent() + ")" + " G (" + MyConfig.getGrimFogGreenPercent() + ")"
				+ " B (" + MyConfig.getGrimFogBluePercent() + ")";
		Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);

	}

	private static void printGrimEffectsInfo(ServerPlayer p) {

		Utility.sendBoldChat(p, "\nGrim Effects Info", ChatFormatting.DARK_GREEN);
		if (MyConfig.isUseGrimCitadels()) {
			String chatMessage = (
					"\n  Effect Villagers ..................................: " + MyConfig.isGrimEffectVillagers()
				+	"\n  Effect Animals ..................................: " + MyConfig.isGrimEffectAnimals()
				+	"\n  Effect Pigs................................: " + MyConfig.isGrimEffectPigs()
				);

			Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);
		} else {
			Utility.sendChat(p, "\n  Grim Citadels Disabled", ChatFormatting.DARK_GREEN);
		}

	}

	private static void printGrimInfo(ServerPlayer p) {
		BlockPos pPos = p.blockPosition();
		Utility.sendBoldChat(p, "\nGrim Citadel Information", ChatFormatting.DARK_GREEN);
		if (MyConfig.isUseGrimCitadels()) {
			String chatMessage = ("   Grim Citadels are Enabled" 
					+ "\n   Nearest Grim Citadel ..................................: "
					+ (int) Math.sqrt(GrimCitadelManager.getClosestGrimCitadelDistanceSq(pPos)) + " meters at "
					+ "\n   " + GrimCitadelManager.getClosestGrimCitadelPos(pPos)
					+ "\n   Grim Citadel Aura Range ...............................: "
					+ MyConfig.getGrimCitadelBonusDistance() + " blocks." + "\n   Grim Citadel Player Curse Range ...: "
					+ MyConfig.getGrimCitadelPlayerCurseDistance() + " blocks."
					+ "\n   Grim Citadel Radius ......................................: "
					+ GrimCitadelManager.getGrimRadius());
			Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);
		} else {
			Utility.sendChat(p, "\n  Grim Citadels Disabled", ChatFormatting.DARK_GREEN);
		}

	}

	private static void printGrimMusicInfo(ServerPlayer p) {
		String chatMessage = "\nMusic Attribution";
		Utility.sendBoldChat(p, chatMessage, ChatFormatting.DARK_GREEN);
		chatMessage = "Attribution Tags for Ambient Music\n"
				+ "\n"
				+ "Lake of Destiny by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted by https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported License\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ "\n"
				+ "Dusty Memories by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted by https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported License\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ " \n"
				+ "Labyrinth of Lost Dreams by Darren Curtis | https://www.darrencurtismusic.com/\n"
				+ "Music promoted on https://www.chosic.com/free-music/all/\n"
				+ "Creative Commons Attribution 3.0 Unported (CC BY 3.0)\n"
				+ "https://creativecommons.org/licenses/by/3.0/\n"
				+ " \n"
				+ "\n"
				+ "";
		Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);
		
	}
	private static void printInfo(ServerPlayer p) {

		String dimensionName = p.level.dimension().location().toString();

		String chatMessage = "\nDimension: " + dimensionName + "\n Current Values";
		Utility.sendBoldChat(p, chatMessage, ChatFormatting.DARK_GREEN);

		chatMessage = "  Harder Max Distance From Spawn....: " + MyConfig.getModifierMaxDistance() + " blocks."
				+ "\n  Spawn Safe Distance ..................................: " + MyConfig.getSafeDistance()
				+ " blocks." + "\n  Debug Level .......................................................: "
				+ MyConfig.getDebugLevel() + "\n  Only In Overworld .........................................: "
				+ MyConfig.isOnlyOverworld() + "\n  Grim Citadels Active .....................................: "
				+ MyConfig.isUseGrimCitadels();
		Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);

	}
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("harderfarther").requires((source) -> 
			{
				return source.hasPermission(3);
			}
		)
		.then(Commands.literal("setDebugLevel").then(
				Commands.argument("debugLevel", IntegerArgumentType.integer(0,2)).executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					return setDebugLevel(p, IntegerArgumentType.getInteger(ctx, "debugLevel"));
				})))
				// update or add a speed value for the block the player is standing on.
				.then(Commands.literal("setBonusRange").then(
						Commands.argument("bonusRange", IntegerArgumentType.integer(500, 6000)).executes(ctx -> {
							ServerPlayer p = ctx.getSource().getPlayerOrException();
							return setBonusRange(p, IntegerArgumentType.getInteger(ctx, "bonusRange"));
						})))
				.then(Commands.literal("setGrimCitadels").then(
						Commands.argument("grimCitadels", BoolArgumentType.bool()).executes(ctx -> {
							ServerPlayer p = ctx.getSource().getPlayerOrException();
							return setGrimCitadels(p, BoolArgumentType.getBool(ctx, "grimCitadels"));
						})))
				.then(Commands.literal("setGrimCitadelsRadius").then(
						Commands.argument("grimCitadelsRadius", IntegerArgumentType.integer(4,11)).executes(ctx -> {
							ServerPlayer p = ctx.getSource().getPlayerOrException();
							return setGrimCitadelsRadius(p, IntegerArgumentType.getInteger(ctx, "grimCitadelsRadius"));
						})))
				.then(Commands.literal("setXpBottleChance").then(
						Commands.argument("xpBottleChance", IntegerArgumentType.integer(0, 33)).executes(ctx -> {
							ServerPlayer p = ctx.getSource().getPlayerOrException();
							return setOddsDropExperienceBottle(p, IntegerArgumentType.getInteger(ctx, "xpBottleChance"));
						})))
				.then(Commands.literal("chunkReport").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					p.level.gatherChunkSourceStats();
					Utility.sendChat(p, "\nChunk\n" + p.level.gatherChunkSourceStats(), ChatFormatting.GREEN);
					return 1;
				}))
				.then(Commands.literal("report").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					String report = "Grim Citadels at : " + GrimCitadelManager.getCitadelListAsString();
					Utility.sendChat(p, report, ChatFormatting.GREEN);
					return 1;
				})).then(Commands.literal("reportLoot").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					String report = LootManager.report();
					Utility.sendChat(p, report, ChatFormatting.GREEN);
					reportOddsXpBottleDrop(p);
					return 1;
				})).then(Commands.literal("info").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printInfo(p);
					return 1;
					// return 1;
				})).then(Commands.literal("grimEffectsInfo").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printGrimEffectsInfo(p);
					return 1;
				})).then(Commands.literal("grimInfo").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printGrimInfo(p);
					return 1;
				})).then(Commands.literal("colorInfo").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printColorInfo(p);
					return 1;
				})).then(Commands.literal("boostInfo").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					String chatMessage = "\nHarder Farther Maximum Monster Boosts";
					Utility.sendBoldChat(p, chatMessage, ChatFormatting.DARK_GREEN);

					chatMessage = "  Monster Health ..........................: " + MyConfig.getHpMaxBoost() + " %."
							+ "\n  Damage ..............................................: " + MyConfig.getAtkDmgBoost()
							+ " %." + "\n  Movement .........................................: "
							+ MyConfig.getSpeedBoost() + " %." + "\n  KnockBack Resistance .........: "
							+ MyConfig.getKnockBackMod() + " %.";
					Utility.sendChat(p, chatMessage, ChatFormatting.GREEN);
					return 1;
				}))
				.then(Commands.literal("musicInfo").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printGrimMusicInfo(p);
					return 1;
				})).then(Commands.literal("setFogColors")
						.then(Commands.argument("R", IntegerArgumentType.integer(0,100))
						.then(Commands.argument("G", IntegerArgumentType.integer(0,100))
						.then(Commands.argument("B", IntegerArgumentType.integer(0,100))
						.executes(ctx -> {
							ServerPlayer p = ctx.getSource().getPlayerOrException();
							int r = IntegerArgumentType.getInteger(ctx,"R");
							int g = IntegerArgumentType.getInteger(ctx,"G");
							int b = IntegerArgumentType.getInteger(ctx,"B");
							return setFogColors(p, r, g, b);
						}))))
				));

	}

	private static void reportOddsXpBottleDrop(ServerPlayer p) {
		Utility.sendChat(p, "  OddsXpBottleDrop .....................: " + MyConfig.getOddsDropExperienceBottle() +"%", ChatFormatting.DARK_AQUA);
	}
	
	public static int setBonusRange(ServerPlayer p, int newRange) {
		MyConfig.setBonusRange(newRange);
		printGrimInfo(p);
		return 1;
	}

	public static int setDebugLevel(ServerPlayer p, int newDebugLevel) {
		MyConfig.setDebugLevel(newDebugLevel);
		printInfo(p);
		return 1;
	}


	private static int setFogColors(ServerPlayer p, int r, int g, int b) {
		MyConfig.setGrimFogRedPercent(r);
		MyConfig.setGrimFogGreenPercent(g);
		MyConfig.setGrimFogBluePercent(b);
		updateGCFogToAllClients ((ServerLevel)p.level, (double)r/100, (double)g/100, (double)b/100);
		printColorInfo(p);
		return 1;
	}
	
	public static int setGrimCitadels(ServerPlayer p, boolean newValue) {
		MyConfig.setGrimCitadels(newValue);
		printGrimInfo(p);
		return 1;
	}

	private static int setGrimCitadelsRadius(ServerPlayer p, int radius) {
		MyConfig.setGrimCitadelsRadius(radius);
		printGrimInfo(p);
		return 0;
	}
	
	public static int setOddsDropExperienceBottle(ServerPlayer p, int newOdds) {
		MyConfig.setOddsDropExperienceBottle(newOdds);
		reportOddsXpBottleDrop(p);
		return 1;
	}
	
	private static void updateGCFogToAllClients(ServerLevel level, double r , double g , double b) {
		List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
		Iterator<ServerPlayer> apI = allPlayers.iterator();
		SyncFogToClientsPacket msg = new SyncFogToClientsPacket(r,g,b);
		while (apI.hasNext()) { // sends to all players online.
			Network.sendToClient(msg, apI.next());
		}
	}
	
	String subcommand = "";
	
	String value = "";

}