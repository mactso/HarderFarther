package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.timer.LastMobDeathTimeProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkEvent {

    @SubscribeEvent
    public void onChunk(AttachCapabilitiesEvent<LevelChunk> event)
    {
    	
    	event.addCapability(new ResourceLocation(Main.MODID, "lastmobdeath_capability"), new LastMobDeathTimeProvider());

    }
}

