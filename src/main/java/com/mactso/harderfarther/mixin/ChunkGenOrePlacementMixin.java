package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.config.OreConfig;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.utility.Utility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.phys.Vec3;

@Mixin(OreFeature.class)
public class ChunkGenOrePlacementMixin {

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionOres = new ArrayList<>();


    @Inject(at = @At(value = "HEAD"), method = "place", cancellable = true)
    private void onGenerate(FeaturePlaceContext<OreConfiguration> context, CallbackInfoReturnable<Boolean> cir) {

        //return false if generation is not allowed.
        //return true if generation is allowed
        //context contains ore to be placed among other things like world & blockpos & config
        //config is the block


        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    OreConfig.getDifficultySections().forEach(section -> {
                        difficultySectionNumbers.add(section.getA());
                        difficultySectionOres.add(section.getB());
                    });


                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization






        ServerLevel world = context.level().getLevel();

        if(world.dimension() == Level.OVERWORLD){

            BlockPos pos = context.origin();
            String block = context.config().targetStates.get(0).state.getBlock().toString().substring(6);
            block = block.substring(0, block.length()-1);


            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(world, new Vec3(pos.getX(), pos.getY(), pos.getZ())) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });


            //default to alllow all ores if list is empty. - .isEmpty doesn't work as it seems initialized with empty strings.
            if(difficultySectionOres.get(choosenAreaIndex[0]).get(0).equals("")){
                return;
            }

            if(!difficultySectionOres.get(choosenAreaIndex[0]).contains(block)){
                if(PrimaryConfig.getDebugLevel() > 0){
                    Utility.debugMsg(1, "Harder Farther cancled ore: " + block);
                }
                cir.setReturnValue(false);
            }


        }

    }

}
