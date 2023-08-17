package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BiomeSource.class)
public class BiomeSourceMixin implements IExtendedBiomeSourceHF {

    private ServerLevel serverWorld; //Not dirty; Be careful with this. It can cause major issues if used incorrectly.
    private boolean init;

    private Vec3 overworldSpawn;

    @Override
    public void setWorld(ServerLevel dirtyWorld) {
        this.serverWorld = dirtyWorld;
    }

    @Override
    public ServerLevel getWorld() {
        return this.serverWorld;
    }

    public void setInit(boolean i){
        if(i){
            this.init = i;
        }
    }

    public boolean getInit(){
        return this.init;
    }


}
