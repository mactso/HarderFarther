package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerTickEventHandler {
	
	@SubscribeEvent
	public void onTick(PlayerTickEvent event) {

		if ((event.phase != Phase.START))
			return;
		if (PrimaryConfig.isMakeHarderOverTime()) {
			HarderTimeManager.doScarySpookyThings(event.player);
		}
			
	}
}
