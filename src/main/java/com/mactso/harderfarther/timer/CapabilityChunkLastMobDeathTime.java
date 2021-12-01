package com.mactso.harderfarther.timer;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CapabilityChunkLastMobDeathTime {

	public static final Capability<IChunkLastMobDeathTime> LASTMOBDEATHTIME = CapabilityManager
			.get(new CapabilityToken<>() {
			});;

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(IChunkLastMobDeathTime.class);
	}

}
