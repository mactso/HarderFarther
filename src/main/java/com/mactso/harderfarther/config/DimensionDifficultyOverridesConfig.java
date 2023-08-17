package com.mactso.harderfarther.config;

import com.mactso.harderfarther.Main;
import net.minecraft.util.Tuple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class DimensionDifficultyOverridesConfig {

    private static int size;

    private static ArrayList<String> dimensionOverridesAsString = new ArrayList<>();
    private static ArrayList<Tuple<Boolean, Float>> dimensionOverrides = new ArrayList<>();

    private static boolean isTheOverworldOverridden;
    private static boolean isTheNetherOverridden;

    private static boolean isTheEndOverridden;

    private static float overworldDifficulty;
    private static float netherDifficulty;

    private static float endDifficulty;

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

        dimensionOverridesAsString.add(properties.computeIfAbsent("the_overworld", (a) -> "false:20").toString());
        dimensionOverridesAsString.add(properties.computeIfAbsent("the_nether", (a) -> "false:60").toString());
        dimensionOverridesAsString.add(properties.computeIfAbsent("the_end", (a) -> "false:100").toString());


        computeConfigValues();
        saveConfig();

    }

    private static File getConfigFile() {
        final File configDir = Platform.configDirectory().toFile();

        if (!configDir.exists()) {
            Main.LOGGER.warn("[Harder Farther] Could not access configuration directory: " + configDir.getAbsolutePath());
        }

        return new File(configDir, "Dimension_Overrides.properties");
    }

    private static void computeConfigValues() {

        for(int x = 0; x< dimensionOverridesAsString.size(); x++) {
            boolean isDimensionOverriden = Boolean.parseBoolean(dimensionOverridesAsString.get(x).split(":",2)[0]);
            float difficulty = Float.parseFloat(dimensionOverridesAsString.get(x).split(":", 2)[1]);
            dimensionOverrides.add(new Tuple<>(isDimensionOverriden, difficulty));
        }

        isTheOverworldOverridden = dimensionOverrides.get(0).getA().booleanValue();
        isTheNetherOverridden = dimensionOverrides.get(1).getA().booleanValue();
        isTheEndOverridden = dimensionOverrides.get(2).getA().booleanValue();

        overworldDifficulty = dimensionOverrides.get(0).getB();
        netherDifficulty = dimensionOverrides.get(1).getB();
        endDifficulty = dimensionOverrides.get(2).getB();


    }

    public static void saveConfig() {
        final File configFile = getConfigFile();
        final Properties properties = new Properties();

        properties.put("the_overworld", dimensionOverridesAsString.get(0));
        properties.put("the_nether", dimensionOverridesAsString.get(1));
        properties.put("the_end", dimensionOverridesAsString.get(2));



        try (FileOutputStream stream = new FileOutputStream(configFile)) {
            properties.store(stream, "Override the difficulty calculation for a dimension with a constant. It can also go beyond the 100 limit for boosting health and damage. speed and knockback Resistance maintain the 100 limit.");
        } catch (final IOException e) {
            Main.LOGGER.warn("[HarderFarther] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
        }
    }



    public static int getSize(){
        return size;
    }

    public static boolean isTheOverworldOverridden(){
        return isTheOverworldOverridden;
    }
    public static boolean isTheNetherOverridden(){
        return isTheNetherOverridden;
    }

    public static boolean isTheEndOverridden(){
        return isTheEndOverridden;
    }

    public static float getOverworldDifficulty(){
        return overworldDifficulty;
    }
    public static float getNetherDifficulty(){
        return netherDifficulty;
    }

    public static float getEndDifficulty(){
        return endDifficulty;
    }

}
