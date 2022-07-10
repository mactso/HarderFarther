package com.mactso.harderfarther.config;

import com.mactso.harderfarther.utility.Utility;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class HarderFartherCommands {
	String subcommand = "";
	String value = "";
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
	{
		dispatcher.register(Commands.literal("harderfarther").requires((source) -> 
			{
				return source.hasPermission(2);
			}
		)
		.then(Commands.literal("debugLevel").then(
				Commands.argument("debugLevel", IntegerArgumentType.integer(0,2)).executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					return setDebugLevel(p, IntegerArgumentType.getInteger(ctx, "debugLevel"));
			}
			)
			)
			)
		// update or add a speed value for the block the player is standing on.
		.then(Commands.literal("setBonusRange").then(
				Commands.argument("setBonusRange", IntegerArgumentType.integer(500,6000)).executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					return setBonusRange(p,IntegerArgumentType.getInteger(ctx, "setBonusRange"));
			}
			)
			)
			)		
		.then(Commands.literal("report").executes(ctx -> {
			ServerPlayer p = ctx.getSource().getPlayerOrException();
			String report = "Grim Citadels at : " + GrimCitadelManager.getCitadelListAsString();
	        Utility.sendChat (p,report,ChatFormatting.GREEN);
			return 1;
		}
		)
		)
		.then(Commands.literal("info").executes(ctx -> {
					ServerPlayer p = ctx.getSource().getPlayerOrException();
					printInfo(p);
					return 1;
					// return 1;
			}
			)
			)	
		.then(Commands.literal("harderInfo").executes(ctx -> {
			ServerPlayer p = ctx.getSource().getPlayerOrException();
	        String chatMessage = "\nHarder Farther Maximum Monster Boosts";
	        Utility.sendBoldChat (p,chatMessage, ChatFormatting.DARK_GREEN);

            chatMessage = 
            		 "  Monster Health ..........................: " + MyConfig.getHpMaxMod() + " %."
              		+ "\n  Damage ..............................................: " + MyConfig.getAtkDmgMod() + " %."	            		
              		+ "\n  Movement .........................................: " + MyConfig.getSpeedMod()+ " %."
              		+ "\n  KnockBack Resistance .........: " + MyConfig.getKnockBackMod()+ " %."
            		;
	        Utility.sendChat (p,chatMessage,ChatFormatting.GREEN);

	        return 1;
	}
	)
	)	
		);

	}

	private static void printInfo(Player p)  {
		BlockPos playerBlockPos = p.blockPosition();
		String dimensionName = p.level.dimension().location().toString();
		
		String chatMessage = "\nDimension: " + dimensionName + "\n Current Values";
		Utility.sendBoldChat (p,chatMessage, ChatFormatting.DARK_GREEN);


		chatMessage = 
				 "  Harder Max Distance From Spawn....: " + MyConfig.getModifierMaxDistance() + " blocks."
		  		+ "\n  Spawn Safe Distance ..................................: " + MyConfig.getSafeDistance() + " blocks."		            		
		  		+ "\n  Debug Level .......................................................: " + MyConfig.getDebugLevel()	
		  		+ "\n  Only In Overworld .........................................: " + MyConfig.isOnlyOverworld()
		  		+ "\n  Grim Citadels Active .....................................: " + MyConfig.isGrimCitadels()
				;
		Utility.sendChat (p,chatMessage,ChatFormatting.GREEN);
		
		if (MyConfig.isGrimCitadels()) {
			chatMessage = (
					"   Grim Citadel Range ......................................: " + MyConfig.getGrimCitadelBonusDistance() + " blocks."
				+ "\n   Grim Citadel Player Curse Range ...: " + MyConfig.getGrimCitadelPlayerCurseDistance() + " blocks."
			);
		    Utility.sendChat (p,chatMessage,ChatFormatting.GREEN);
		}
	}
	
	public static int setDebugLevel (Player p, int newDebugLevel) {
		MyConfig.setDebugLevel(newDebugLevel);
		printInfo(p);
		return 1;
	}

	public static int setBonusRange (Player p, int newRange) {
		MyConfig.setBonusRange (newRange);
		printInfo(p);
		return 1;
	}	

}
