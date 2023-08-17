package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.manager.GrimCitadelManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class PlayerTeleportHandler {

	@SubscribeEvent
	public static void onLivingUpdate(EntityTeleportEvent event) {
		if (event.getEntity() instanceof Player p) {
			if (p.isCreative()) {
				return;
			}
			if (GrimCitadelManager.isInsideGrimCitadelRadius(BlockPos.containing(event.getTarget()))) {
				event.setCanceled(true);
			}
			
		}
	}

}
