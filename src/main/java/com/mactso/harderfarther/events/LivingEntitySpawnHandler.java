package com.mactso.harderfarther.events;


import com.mactso.harderfarther.api.DifficultyCalculator;
import com.mactso.harderfarther.config.MobConfig;
import com.mactso.harderfarther.mixinInterfaces.IExtendedServerWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class LivingEntitySpawnHandler {


    //Should always run on server Side
    @SubscribeEvent
    public void onLivingEntitySpawn(MobSpawnEvent.SpawnPlacementCheck spawnEvent){


        String string = spawnEvent.getEntityType().toString();
        MobSpawnType spawnReason = spawnEvent.getSpawnType();

        ServerLevel world = spawnEvent.getLevel().getLevel();


        if( (spawnReason == MobSpawnType.NATURAL || spawnReason == MobSpawnType.CHUNK_GENERATION )){



            //Start of main logic
            if ( !((IExtendedServerWorld)world).areListInitialized() ) {

                MobConfig.getDifficultySections().forEach(section -> {
                    ((IExtendedServerWorld)world).getDifficultySectionNumbers().add(section.getA());
                    ((IExtendedServerWorld)world).getDifficultySectionMobs().add(section.getB());
                });


                ((IExtendedServerWorld)world).setListInitialized();
            }
            //end of listInitialization








            //Start of overworld logic
            if (world.dimension() == Level.OVERWORLD) {

                BlockPos pos = spawnEvent.getPos();
                String entityIdentifier = spawnEvent.getEntityType().toString().substring(7);
                entityIdentifier = entityIdentifier.replace(".", ":");


                float difficulty = DifficultyCalculator.getDistanceDifficultyHere(world, new Vec3(pos.getX(), pos.getY(), pos.getZ())) * 100;

                int[] choosenAreaIndex = {-1};
                ((IExtendedServerWorld)world).getDifficultySectionNumbers().forEach(difficultySectionNumber -> {
                    if (difficulty >= difficultySectionNumber) choosenAreaIndex[0]++;
                });


                //default to alllow all mobs if list is empty. - .isEmpty doesn't work as it seems initialized with empty strings.
                if (((IExtendedServerWorld)world).getDifficultySectionMobs().get(choosenAreaIndex[0]).get(0).equals("")) {
                    return;  //do nothing
                }

                if ( !((IExtendedServerWorld)world).getDifficultySectionMobs().get(choosenAreaIndex[0]).contains(entityIdentifier) ) {
                    spawnEvent.setCanceled(true);
                }
            }
            //End of Overworld logic

            //End of main logic
        }

    }


}
