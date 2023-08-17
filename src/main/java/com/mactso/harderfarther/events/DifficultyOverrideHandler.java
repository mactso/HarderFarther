package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.config.DimensionDifficultyOverridesConfig;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Main.MODID)
public class DifficultyOverrideHandler {

    public static void registerEvent() {
        DifficultyCalculator.registerOverrideEvent(
                (currentDifficulty, world, outposts, minBoostDistance, maxBoostDistance) -> {

                    if(DimensionDifficultyOverridesConfig.isTheOverworldOverridden()) {
                        if (world.dimension() == Level.OVERWORLD) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getOverworldDifficulty()/100;
                        }
                    }

                    if(DimensionDifficultyOverridesConfig.isTheNetherOverridden()) {
                        if (world.dimension() == Level.NETHER) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getNetherDifficulty()/100;
                        }
                    }


                    if(DimensionDifficultyOverridesConfig.isTheEndOverridden()) {
                        if (world.dimension() == Level.END) {
                            currentDifficulty[0] = DimensionDifficultyOverridesConfig.getEndDifficulty()/100;
                        }
                    }

                });
    }

}
