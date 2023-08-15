package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.BiomeConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


//Code snippet from Glitchfiend from terrablender
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InitializationHandler
{
    // Use the lowest priority to account for nonsense from e.g. MCreator.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event)
    {

        RegistryAccess registryAccess = event.getServer().registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registryOrThrow(Registries.BIOME);

        BiomeConfig.setDynamicBiomeRegistry(biomeRegistry);

    }
}
