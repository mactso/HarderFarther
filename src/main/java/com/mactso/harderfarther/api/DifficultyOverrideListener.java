package com.mactso.harderfarther.api;


import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;


public interface DifficultyOverrideListener {

    void interact(float currentDifficulty[], ServerLevel world, Vec3[] outposts, int minBoostDistance, int maxBoostDistance);

}