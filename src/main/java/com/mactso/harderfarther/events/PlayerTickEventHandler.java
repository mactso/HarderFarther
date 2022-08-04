package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerTickEventHandler {
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {

		if ((event.phase != Phase.START) || !(MyConfig.isMakeHarderOverTime()))
			return;
		if (event.player instanceof ServerPlayer sp) {
			HarderTimeManager.doScarySpookyThings(sp);
		}
	}
}
