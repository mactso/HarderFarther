package com.mactso.harderfarther.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class Utility {
	
	final static int TWO_SECONDS = 40;
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
	
	public static void updateEffect(LivingEntity e, int amplifier,  MobEffect mobEffect, int duration) {
		MobEffectInstance ei = e.getEffect(mobEffect);
		if (amplifier == 10) {
			amplifier = 20;  // player "plaid" speed.
		}
		if (ei != null) {
			if (amplifier > ei.getAmplifier()) {
				e.removeEffect(mobEffect);
			} 
			if (amplifier == ei.getAmplifier() && ei.getDuration() > 10) {
				return;
			}
			if (ei.getDuration() > 10) {
				return;
			}
			e.removeEffect(mobEffect);			
		}
		e.addEffect(new MobEffectInstance(mobEffect, duration, amplifier, true, true));
		return;
	}

}
