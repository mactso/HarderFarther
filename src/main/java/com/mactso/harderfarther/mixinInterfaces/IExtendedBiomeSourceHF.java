package com.mactso.harderfarther.mixinInterfaces;

import net.minecraft.server.level.ServerLevel;

public interface IExtendedBiomeSourceHF {

    void setWorld(ServerLevel dirtyWorld);

    ServerLevel getWorld();

    void setInit(boolean i);

    boolean getInit();


}
