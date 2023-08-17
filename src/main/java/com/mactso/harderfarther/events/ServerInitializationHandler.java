package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.BiomeConfig;
import com.mactso.harderfarther.config.Platform;
import com.mactso.harderfarther.manager.GrimCitadelManager;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class ServerInitializationHandler
{

    private static ArrayList<String> structureList = new ArrayList<>();
    private static ArrayList<String> entityTypeList = new ArrayList<>();
    private static ArrayList<String> biomeList = new ArrayList<>();
    private static ArrayList<String> oreList = new ArrayList<>();


    // Use the lowest priority to account for nonsense from e.g. MCreator.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerAboutToStart(ServerAboutToStartEvent event)
    {

        GrimCitadelManager.load(event.getServer());






        //Dumping registries for convenience (registryAcess Code for biome snippet from Glitchfiend from terrablender
        RegistryAccess registryAccess = event.getServer().registryAccess();

        Registry<Structure> structures = registryAccess.registryOrThrow(Registries.STRUCTURE);
        Registry<EntityType> entitieTypes = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
        Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);
        Registry<PlacedFeature> ores = registryAccess.registryOrThrow(Registries.PLACED_FEATURE);
        Registry<Block> blocks = registryAccess.registryOrThrow(Registries.BLOCK);

        structures.registryKeySet().forEach(structureFeatureKey -> {
            structureList.add(structureFeatureKey.location().toString());
        });

        entitieTypes.registryKeySet().forEach(entityTypeKey -> {
            entityTypeList.add(entityTypeKey.location().toString());
        });

        biomes.registryKeySet().forEach(biomeKey -> {
            biomeList.add(biomeKey.location().toString());
        });

        ores.registryKeySet().forEach(placedFeatureKey -> {
            if(ores.get(placedFeatureKey).feature().value().feature() instanceof OreFeature){
                Block block = ((OreConfiguration)ores.get(placedFeatureKey).feature().value().config()).targetStates.get(0).state.getBlock();
                String blockId = blocks.getKey(block).toString();
                if(!oreList.contains(blockId)) {
                    oreList.add(blockId);
                }
            }
        });

        saveConfig();

        BiomeConfig.setDynamicBiomeRegistry(biomes);

    }

    private static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("ores", oreList.toString());
        properties.put("structures", structureList.toString());
        properties.put("biomes", biomeList.toString());
        properties.put("entity_types", entityTypeList.toString());

        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            properties.store(stream, "A fairly useful list of ores/structures/biomes/entities");
        } catch (final IOException e) {
            Main.LOGGER.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
        }
    }


    private static File getConfigFile() {
        final File configDir = Platform.configDirectory().toFile();

        if (!configDir.exists()) {
            Main.LOGGER.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
        }

        return new File(configDir, "Registry_Dump.properties");
    }
}
