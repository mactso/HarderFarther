package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;
import com.mactso.harderfarther.utility.Utility;

import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerTickEventHandler {
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {

		if ((event.phase != Phase.START))
			return;
		if (MyConfig.isMakeHarderOverTime()) {
			if (!event.player.level.isClientSide) {
				Utility.debugMsg(2, "onTick PlayerTick");
			}
			HarderTimeManager.doScarySpookyThings(event.player);
		}
			
	}
}
