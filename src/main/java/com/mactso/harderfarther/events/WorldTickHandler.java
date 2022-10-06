package com.mactso.harderfarther.events;

import java.util.Iterator;
import java.util.List;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class WorldTickHandler {

	// assumes this event only raised for server worlds. TODO verify.
	@SubscribeEvent
	public static void onWorldTickEvent(LevelTickEvent event) {

		if (event.phase == Phase.START)
			return;

		// this is always serverlevel
		if (event.level instanceof ServerLevel level) {
			Utility.debugMsg(2, "onWorldTick");
			GrimCitadelManager.checkCleanUpCitadels(level);
			
			long gametime = event.level.getGameTime();

			List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
			Iterator<ServerPlayer> apI = allPlayers.iterator();
			
			SyncFogToClientsPacket msg = new SyncFogToClientsPacket(
					MyConfig.getGrimFogRedPercent(),
					MyConfig.getGrimFogGreenPercent(),
					MyConfig.getGrimFogBluePercent());
			while (apI.hasNext()) { // sends to all players online.
				ServerPlayer sp = apI.next();
				if (gametime % 100 == sp.getId() % 100) {
					Network.sendToClient(msg, sp);
				}
			}
		}
	}

}
