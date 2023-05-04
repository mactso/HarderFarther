package com.mactso.harderfarther.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.Heightmap.Type;


public class Utility {
	
	final static int TWO_SECONDS = 40;
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static void debugMsg (int level, String dMsg) {

		if (MyConfig.getDebugLevel() > level-1) {
			LOGGER.info("L"+level + ":" + dMsg);
		}
		
	}

	public static void debugMsg (int dbglevel, BlockPos pos, String dMsg) {

		if (MyConfig.getDebugLevel() > dbglevel-1) {
			LOGGER.info("L"+dbglevel+" ("+pos.getX()+","+pos.getY()+","+pos.getZ()+"): " + dMsg);
		}
		
	}

	public static void sendBoldChat(ServerPlayer p, String chatMessage, TextFormatting textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withBold(true));
		component.setStyle(component.getStyle().withColor(Color.fromLegacyFormat(textColor)));
		p.sendMessage(component, p.getUUID());

	}	
	

	public static void sendChat(ServerPlayer p, String chatMessage, TextFormatting textColor) {

		TextComponent component = new TextComponent (chatMessage);
		component.setStyle(component.getStyle().withColor(Color.fromLegacyFormat(textColor)));
		p.sendMessage(component, p.getUUID());

	}
	
//	private static void updateEffect(LivingEntity e, int amplifier,  Effects mobEffect, int duration) {
//		MobEffectInstance ei = e.getEffect(mobEffect);
//		if (amplifier == 10) {
//			amplifier = 20;  // player "plaid" speed.
//		}
//		if (ei != null) {
//			if (amplifier > ei.getAmplifier()) {
//				e.removeEffect(mobEffect);
//			} 
//			if (amplifier == ei.getAmplifier() && ei.getDuration() > 10) {
//				return;
//			}
//			if (ei.getDuration() > 10) {
//				return;
//			}
//			e.removeEffect(mobEffect);			
//		}
//		e.addEffect(new EffectInstance(mobEffect, duration, amplifier, true, true));
//		return;
//	}
	
	public static boolean isOutside(BlockPos pos, ServerLevel serverLevel) {
		return serverLevel.getHeightmapPos(Type.MOTION_BLOCKING_NO_LEAVES, pos) == pos;
	}

}
