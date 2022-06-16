package com.mactso.harderfarther.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class Utility {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void debugMsg (int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level + ":" + dMsg);
		}
		
	}

	public static void debugMsg (int level, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level+" ("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): " + dMsg);
		}
		
	}

	public static void sendBoldChat(ServerPlayer p, String chatMessage, TextColor textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withBold(true);
		component.getStyle().withColor(textColor);
		p.sendMessage(component, p.getUUID());

	}	
	
	public static void sendChat(ServerPlayer p, String chatMessage, TextColor textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.getStyle().withColor(textColor);
		p.sendMessage(component, p.getUUID());

	}

}
