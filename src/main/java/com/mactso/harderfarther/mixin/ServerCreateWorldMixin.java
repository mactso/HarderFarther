package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.PrimaryConfig;
import com.mactso.harderfarther.mixinInterfaces.IExtendedBiomeSourceHF;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class ServerCreateWorldMixin {

    @Inject(at = @At(value = "TAIL"), method = "createLevels", cancellable = false)
    private void harderfarther$onServerCreateWorlds(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {

        for(ServerLevel serverWorld : this.getAllLevels()) {
            ((IExtendedBiomeSourceHF)serverWorld.getChunkSource().getGenerator().getBiomeSource()).setWorld(serverWorld);
            ((IExtendedBiomeSourceHF)serverWorld.getChunkSource().getGenerator().getBiomeSource()).setInit(true);
            if(PrimaryConfig.getDebugLevel() > 0) {
                Utility.debugMsg(1, "World " + serverWorld.dimension().location() + " initialized");
            }
        }

    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getDataStorage()Lnet/minecraft/world/level/storage/DimensionDataStorage;"), method = "createLevels", cancellable = false)
    private void harderfarther$onServerCreateOverworld(ChunkProgressListener worldGenerationProgressListener, CallbackInfo ci) {

        for(ServerLevel serverWorld : this.getAllLevels()) {
            ((IExtendedBiomeSourceHF)serverWorld.getChunkSource().getGenerator().getBiomeSource()).setWorld(serverWorld);
        }

    }

    @Shadow
    public Iterable<ServerLevel> getAllLevels(){
        return null;
    }

}
