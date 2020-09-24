package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.timer.LastMobDeathTimeProvider;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkEvent {

    @SubscribeEvent
    public void onChunk(AttachCapabilitiesEvent<Chunk> event)
    {
    	event.addCapability(new ResourceLocation(Main.MODID, "lastmobdeath_capability"), new LastMobDeathTimeProvider(event.getObject()));

    }
}

