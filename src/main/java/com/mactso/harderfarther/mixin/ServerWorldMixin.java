package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedServerWorld;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;

@Mixin(ServerLevel.class)
public class ServerWorldMixin implements IExtendedServerWorld {

    private boolean areListInitialized = false;

    private ArrayList<Float> difficultySectionNumbers = new ArrayList<>();
    private ArrayList<List<String>> difficultySectionMobs = new ArrayList<>();


    @Override
    public boolean areListInitialized() {
        return this.areListInitialized;
    }

    @Override
    public void setListInitialized() {
        this.areListInitialized = true;
    }

    @Override
    public ArrayList<Float> getDifficultySectionNumbers() {
        return this.difficultySectionNumbers;
    }

    @Override
    public ArrayList<List<String>> getDifficultySectionMobs() {
        return this.difficultySectionMobs;
    }
}
