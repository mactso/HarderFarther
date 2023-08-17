package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncFogToClientsPacket;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class PlayerLoginEventHandler {
    @SuppressWarnings("resource")
	@SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent event)
    {
    	if (event.getEntity().level().isClientSide) return;
    	Player sp = event.getEntity();
    	if ( sp == null ) return;
    	if (!(sp instanceof ServerPlayer)) return;
    	if (MyConfig.isUseGrimCitadels()) {
    			GrimCitadelManager.sendAllGCPosToClient((ServerPlayer) sp );
    			SyncFogToClientsPacket msg = new SyncFogToClientsPacket(
    					MyConfig.getGrimFogRedPercent(),
    					MyConfig.getGrimFogGreenPercent(),
    					MyConfig.getGrimFogBluePercent());
   				Network.sendToClient(msg, (ServerPlayer)sp);
    	}
    	
    }
}
