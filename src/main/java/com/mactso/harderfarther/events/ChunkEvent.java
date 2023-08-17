package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.timer.LastMobDeathTimeProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class ChunkEvent {

    @SubscribeEvent
    public static void onChunk(AttachCapabilitiesEvent<LevelChunk> event)
    {
    	
    	event.addCapability(new ResourceLocation(Main.MODID, "lastmobdeath_capability"), new LastMobDeathTimeProvider());
    }
    
    
}

