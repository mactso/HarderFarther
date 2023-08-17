package com.mactso.harderfarther.mixinInterfaces;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.level.biome.Climate;

public interface IExtendedSearchTree<T> {

    List<Pair<Climate.ParameterPoint, T>> getOriginalList();

    void setOriginalList(List<Pair<Climate.ParameterPoint, T>> entries);


}
