package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerLoginEventHandler {
    @SuppressWarnings("resource")
	@SubscribeEvent
    public void onLogin(PlayerLoggedInEvent event)
    {
    	if (event.getEntity().level().isClientSide) return;
    	Player sp = event.getEntity();
    	if ( sp == null ) return;
    	if (!(sp instanceof ServerPlayer)) return;
    	if (PrimaryConfig.isUseGrimCitadels()) {
    			GrimCitadelManager.sendAllGCPosToClient((ServerPlayer) sp );
    			SyncFogToClientsPacket msg = new SyncFogToClientsPacket(
    					PrimaryConfig.getGrimFogRedPercent(),
    					PrimaryConfig.getGrimFogGreenPercent(),
    					PrimaryConfig.getGrimFogBluePercent());
   				Network.sendToClient(msg, (ServerPlayer)sp);
    	}
    	
    }
}
