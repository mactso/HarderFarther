package com.mactso.harderfarther.config;

import com.mactso.harderfarther.Main;
import net.minecraft.core.Registry;
import net.minecraft.util.Tuple;

import net.minecraft.world.level.biome.Biome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class BiomeConfig {

    private static int size;

    private static ArrayList<String> difficultySectionsAsString = new ArrayList<>();
    private static ArrayList<Tuple<Float, List<String>>> difficultySections = new ArrayList<>();

    private static ArrayList<String> difficultySectionBiomeReplacementsAsString = new ArrayList<>();
    private static ArrayList<Map<String, String>> difficultySectionBiomeReplacements = new ArrayList<>();

    private static Registry<Biome> biomeRegistry;

    public static void initConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        if (configFile.exists()) {
            try (FileInputStream stream = new FileInputStream(configFile)) {
                properties.load(stream);
            } catch (final IOException e) {
                Main.LOGGER.warn("[HarderFarther] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
            }
        }
        size = properties.size();

        difficultySectionsAsString.add(properties.computeIfAbsent("Section_1", (a) -> ">0:\"\"").toString());
        difficultySectionBiomeReplacementsAsString.add(properties.computeIfAbsent("Section_1_replacements", (a) -> "\"\"").toString());

        if(size > 2){
            for(int x = 1; x < size/2; x++){
                difficultySectionsAsString.add(properties.getProperty("Section_" + (x+1)).toString());
                difficultySectionBiomeReplacementsAsString.add(properties.getProperty("Section_" + (x+1) + "_replacements").toString());
            }
        }

        computeConfigValues();
        saveConfig();

    }

    private static File getConfigFile() {
        final File configDir = Platform.configDirectory().toFile();

        if (!configDir.exists()) {
            Main.LOGGER.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
        }

        return new File(configDir, "Biomes.properties");
    }

    private static void computeConfigValues() {

        for(int x=0; x<size/2; x++) {
            float section = Float.parseFloat(difficultySectionsAsString.get(x).substring(1).split(":",2)[0]);
            List<String> biomes = List.of(difficultySectionsAsString.get(x).split(":", 2)[1].replace("\"", "").split(","));
            difficultySections.add(new Tuple<>(section, biomes));

            Map map = new HashMap<>();
            String[] replacements = difficultySectionBiomeReplacementsAsString.get(x).replace("\"", "").split(",");
            for (String replacement:replacements) {
                //Make sure entry isn't empty
                if(!replacement.equals("")) {
                    map.put(replacement.split(">")[0], replacement.split(">")[1]);
                }
            }
            difficultySectionBiomeReplacements.add(map);
        }


    }

    public static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("Section_1", difficultySectionsAsString.get(0));
        properties.put("Section_1_replacements", difficultySectionBiomeReplacementsAsString.get(0));

        int x = 1;
        if(difficultySectionsAsString.size() > 1){
            while(x< difficultySectionsAsString.size()){
                properties.put("Section_" + (x+1), difficultySectionsAsString.get(x));
                properties.put("Section_" + (x+1) + "_replacements", difficultySectionBiomeReplacementsAsString.get(x));
                x++;
            }
        }

        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            properties.store(stream, "A list of biomes allowed in every difficulty section, empty meaning all");
        } catch (final IOException e) {
            Main.LOGGER.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
        }
    }



    public static int getSize(){
        return size;
    }

    public static ArrayList<Tuple<Float, List<String>>> getDifficultySections(){
        return difficultySections;
    }
    public static ArrayList<Map<String, String>> getDifficultySectionBiomeReplacements(){
        return difficultySectionBiomeReplacements;
    }

    public static void setDynamicBiomeRegistry(Registry biomes){
        biomeRegistry = biomes;
    }

    public static Registry<Biome> getDynamicBiomeRegistry(){
        return biomeRegistry;
    }

}
