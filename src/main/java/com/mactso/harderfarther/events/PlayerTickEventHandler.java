package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderTimeManager;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class PlayerTickEventHandler {
	
	@SubscribeEvent
	public static void onTick(PlayerTickEvent event) {

		if ((event.phase != Phase.START))
			return;
		if (MyConfig.isMakeHarderOverTime()) {
			HarderTimeManager.doScarySpookyThings(event.player);
		}
			
	}
}
