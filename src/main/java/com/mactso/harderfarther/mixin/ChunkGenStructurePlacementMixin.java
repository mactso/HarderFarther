package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.config.StructureConfig;
import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.core.registries.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.Vec3;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGenStructurePlacementMixin {

    @Shadow public abstract BiomeSource getBiomeSource();

    //private boolean shouldgen;

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionStructure = new ArrayList<>();


    //private static boolean areListInitializedStatic = false;

    //private static ArrayList<Float> difficultySectionNumbersStatic = new ArrayList<>();
    //private static ArrayList<List<String>> difficultySectionStructureStatic = new ArrayList<>();

    /*@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/gen/structure/StructureSet;placement()Lnet/minecraft/structure/StructurePlacement;"), method = "Lnet/minecraft/world/gen/chunk/ChunkGenerator;m_mzeyuzcs(Lnet/minecraft/util/Holder;Lnet/minecraft/world/gen/RandomState;JIII)Z", cancellable = true)
    private void onGenerate(Holder<StructureSet> holder, RandomState randomState, long l, int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {

        //i is x
        //j is z

        if(!areListInitialized){

            OreConfig.getDifficultySections().forEach(section ->{
                difficultySectionNumbers.add(section.first);
                difficultySectionStructureSet.add(section.second);
            });


            areListInitialized = true;
        }
        //end of listInitialization





        if(((IExtendedBiomeSourceHF)this.getBiomeSource()).getInit()) {

            ServerWorld world = ((IExtendedBiomeSourceHF) this.getBiomeSource()).getDirtyWorld();

            String structureSet = holder.getKey().get().getValue().toString();
            System.out.println(structureSet);


            float difficulty = HarderFartherManager.getDistanceDifficultyHere(world, new Vec3d(i, 0, j)) * 100;

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });


            if(difficultySectionStructureSet.get(choosenAreaIndex[0]).get(0).equals("")){
                return;
            }

            if(!difficultySectionStructureSet.get(choosenAreaIndex[0]).contains(structureSet)){
                cir.setReturnValue(false);
            }

        }




    }*/

    /*
    The method I'm calling below uses a potential location for a structure and checks if placement is valid/already placed. If it is valid/exists, it returns the position/structure back.

    It's called by another method which uses a multiple k that checks distances outward separated by the structure's spacing. It checks this spacing by a product of k expanding outwards from where it's checking against.

    There's an outer loop used to get the distance x & an inner loop used to get the distance z. Both have booleans that check if one matches k or -k to prevent checking areas it's already checked.

    In the locate command. k is a range that goes up to 100. looping from 0 to 100. Each loop runs the logic above.

    StructureCheckResult.START_NOT_PRESENT = I don't know.
    StructureCheckResult.START_PRESENT = structure already placed
    StructureCheckResult.CHUNK_LOAD_NEEDED = structure not placed; The chunk gets loaded during the locate structure command though, so it becomes START_PRESENT after running the locate command
    */


    //This prevents non-generated structures being located on explorer maps or with the locate command. Something interesting is that cartographers will try to locate a structure as soon as they unlock a purchasable map. That structure will be used for the map if bought.
    /*@Inject(at = @At(value = "RETURN"), method = "m_gxxzcexz", cancellable = true)
    private static void harderfarther$RemovePotentialStructureResult(Set<Holder<StructureFeature>> set, WorldView world, StructureManager structureManager, boolean bl, StructurePlacement structurePlacement, ChunkPos chunkPos, CallbackInfoReturnable<@Nullable Pair<BlockPos, Holder<StructureFeature>>> cir){
        if(!areListInitializedStatic) {

            StructureConfig.getDifficultySections().forEach(section -> {
                difficultySectionNumbersStatic.add(section.getLeft());
                difficultySectionStructureStatic.add(section.getRight());
            });

            areListInitializedStatic = true;
        }
        //end of listInitialization









        ServerWorld worldReal = (ServerWorld)world;

        //This has to be used to get block pos. Getting it from chunkPos directly results in weirdness here.
        BlockPos blockPos = structurePlacement.getLocatePos(chunkPos);
        //System.out.println(blockPos + " is block position");
        int x = blockPos.getX();
        int z = blockPos.getZ();

        String structure = "";
        for(Holder<StructureFeature> holder : set){
            Registry<StructureFeature> registry = worldReal.getRegistryManager().get(Registry.STRUCTURE_WORLDGEN);
            StructureFeature structureFeature = holder.value();
            structure = (String)registry.getKey(structureFeature).map(Object::toString).orElseGet(structureFeature::toString);
        }
        structure = structure.substring(0, structure.length() - 1);
        structure = structure.split("/")[2].substring(1);

        //System.out.println(structure);

        //only overworld right now.
        if (worldReal.getRegistryKey() == World.OVERWORLD) {


            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(worldReal, new Vec3d(x, 0, z)) * 100;
            //System.out.println(difficulty + " is difficulty");

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbersStatic.forEach(difficultySectionNumber -> {
                //System.out.println("Difficulty section number is " + difficultySectionNumber);
                if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });
            //System.out.println(choosenAreaIndex[0] + " is Chosen index");


            if (difficultySectionStructureStatic.get(choosenAreaIndex[0]).get(0).equals("")) {
                return;
            }

            if (!difficultySectionStructureStatic.get(choosenAreaIndex[0]).contains(structure)) {
                if(PrimaryConfig.getDebugLevel() > 0){
                    Utility.debugMsg(1, "Canceled structure: " + structure);
                }
                cir.setReturnValue(null);
            }
        }


    }*/








    //This seems to be the best target for structures. The locate structure command respects it as well.
    @Inject(at = @At(value = "HEAD"), method = "tryGenerateStructure", cancellable = true)
    private void harderfarther$skipStructureGeneration(StructureSet.StructureSelectionEntry structureSelectionEntry, StructureManager structureManager, RegistryAccess registryManager, RandomState randomState, StructureTemplateManager structureTemplateManager, long l, ChunkAccess chunk, ChunkPos chunkPos, SectionPos chunkSectionPos, CallbackInfoReturnable<Boolean> cir){
        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    StructureConfig.getDifficultySections().forEach(section -> {
                        difficultySectionNumbers.add(section.getA());
                        difficultySectionStructure.add(section.getB());
                    });


                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization




        ServerLevel worldReal = ((IExtendedBiomeSourceHF)this.getBiomeSource()).getWorld();


        int x = chunkPos.getMiddleBlockX();
        int z = chunkPos.getMiddleBlockZ();


        String structure = "";
        Registry<Structure> registry = worldReal.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure structureFeature = structureSelectionEntry.structure().value();
        structure = (String)registry.getResourceKey(structureFeature).map(Object::toString).orElseGet(structureFeature::toString);
        structure = structure.substring(0, structure.length() - 1);
        structure = structure.split("/")[2].substring(1);

        //System.out.println(structure);

        //only overworld right now.
        if (worldReal.dimension() == Level.OVERWORLD) {


            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(worldReal, new Vec3(x, 0, z)) * 100;
            //System.out.println(difficulty + " is difficulty");

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                //System.out.println("Difficulty section number is " + difficultySectionNumber);
                if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });
            //System.out.println(choosenAreaIndex[0] + " is Chosen index");


            //Allows all structures if section is empty.
            if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                return;
            }

            if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                if(MyConfig.getDebugLevel() > 0){
                    Utility.debugMsg(1, "Canceled structure: " + structure + " located near x:" + x + ", z:" + z + " at difficulty: " + difficulty);
                }
                cir.setReturnValue(false);
            }
        }


    }


    //this cancels generation, but it will still locate with the locate command or explorer map. The structure is still technically there, but structure doesn't build itself
   /* @WrapWithCondition(at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V"), method = "generateFeatures")
    private boolean harderfarther$skipGenerationIfStructureDisabled(List instance, Consumer consumer){
        return this.shouldgen;
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/structure/StructureManager;getStructureStarts(Lnet/minecraft/util/math/ChunkSectionPos;Lnet/minecraft/world/gen/feature/StructureFeature;)Ljava/util/List;"), method = "generateFeatures")
    private void harderfarther$setShouldGenStructure(StructureWorldAccess world, Chunk chunk, StructureManager structureManager, CallbackInfo ci){

        //((IExtendedChunkRegion)world).setBiomeSource(this.getBiomeSource());

        if(!areListInitialized) {
            synchronized (this) {
                if (!areListInitialized) {

                    StructureConfig.getDifficultySections().forEach(section -> {
                        difficultySectionNumbers.add(section.getLeft());
                        difficultySectionStructure.add(section.getRight());
                    });


                    areListInitialized = true;
                }
            }
        }
        //end of listInitialization











        this.shouldgen = true;
        if (((IExtendedBiomeSourceHF) this.getBiomeSource()).getInit()) {

            ServerWorld worldReal = ((IExtendedBiomeSourceHF) this.getBiomeSource()).getWorld();

            ChunkPos chunkPos = chunk.getPos();
            int x = BiomeCoords.fromChunk(chunkPos.getCenterX());
            int z = BiomeCoords.fromChunk(chunkPos.getCenterZ());

            String structure = ((IExtendedChunkRegion) world).getGeneratingStructureResourceKey();
            structure = structure.substring(0, structure.length() - 1);
            structure = structure.split("/")[2].substring(1);

            //System.out.println(structure);


            if (worldReal.getRegistryKey() == World.OVERWORLD) {


                float difficulty = DifficultyCalculator.getDistanceDifficultyHere(worldReal, new Vec3d(x, 0, z)) * 100;

                int[] choosenAreaIndex = {-1};
                difficultySectionNumbers.forEach(difficultySectionNumber -> {
                    if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
                });


                if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                    return;
                }

                if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                    if(PrimaryConfig.getDebugLevel() > 0){
                        Utility.debugMsg(1, "Canceled structure: " + structure);
                    }
                    this.shouldgen = false;
                }
            }

        }



        //repeat logic for spawn before worlds are initialized.
        if(!((IExtendedBiomeSourceHF) this.getBiomeSource()).getInit()){

            String structure = ((IExtendedChunkRegion) world).getGeneratingStructureResourceKey();
            structure = structure.substring(0, structure.length() - 1);
            structure = structure.split("/")[2].substring(1);


            //Calculate distance difficulty
            float difficulty = DifficultyCalculator.getDistanceDifficultyHere(((IExtendedBiomeSourceHF)this.getBiomeSource()).getWorld(), new Vec3d(0, 0, 0));

            int[] choosenAreaIndex = {-1};
            difficultySectionNumbers.forEach(difficultySectionNumber -> {
                if(difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
            });



            if (difficultySectionStructure.get(choosenAreaIndex[0]).get(0).equals("")) {
                return;
            }

            if (!difficultySectionStructure.get(choosenAreaIndex[0]).contains(structure)) {
                if(PrimaryConfig.getDebugLevel() > 0){
                    Utility.debugMsg(1, "Harder Farther canceled structure: " + structure);
                }
                this.shouldgen = false;
            }

        }
    }



    @Shadow
    public BiomeSource getBiomeSource(){
        return null;
    }*/


}
