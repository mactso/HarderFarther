package com.mactso.harderfarther.config;

import com.mactso.harderfarther.Main;
import net.minecraft.util.Tuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class StructureConfig {

    private static int size;

    private static ArrayList<String> difficultySectionAsString = new ArrayList<>();
    private static ArrayList<Tuple<Float, List<String>>> difficultySections = new ArrayList<>();

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

        difficultySectionAsString.add(properties.computeIfAbsent("Section_1", (a) -> ">0:\"\"").toString());

        size = properties.size();

        if(size > 1){
            for(int x = 1; x < size; x++){
                difficultySectionAsString.add(properties.getProperty("Section_" + (x+1)).toString());
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

        return new File(configDir, "Structures.properties");
    }

    private static void computeConfigValues() {

        for(int x=0; x<size; x++) {
            float section = Float.parseFloat(difficultySectionAsString.get(x).substring(1).split(":",2)[0]);
            List<String> biomes = List.of(difficultySectionAsString.get(x).split(":", 2)[1].replace("\"", "").split(","));
            difficultySections.add(new Tuple<>(section, biomes));
        }


    }

    public static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("Section_1", difficultySectionAsString.get(0));

        int x = 1;
        if(difficultySectionAsString.size() > 1){
            while(x<difficultySectionAsString.size()){
                properties.put("Section_" + (x+1), difficultySectionAsString.get(x));
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

}
