package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.mixinInterfaces.IExtendedSearchTree;
import com.mojang.datafixers.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.world.level.biome.Climate;

@Mixin(Climate.RTree.class)
public class SearchTreeMixin<T>  implements IExtendedSearchTree<T> {

    private List<Pair<Climate.ParameterPoint, T>> originalBiomePairs;

    @Inject(at = @At(value = "RETURN"), method = "create", cancellable = true)
    private static <T> void onCreate(List<Pair<Climate.ParameterPoint, T>> entries, CallbackInfoReturnable<Climate.RTree<T>> cir) {
        Climate.RTree searchTree = cir.getReturnValue();
        ((IExtendedSearchTree)(Object)searchTree).setOriginalList(entries);
        cir.setReturnValue(searchTree);
    }


    @Override
    public List<Pair<Climate.ParameterPoint, T>> getOriginalList() {
        return this.originalBiomePairs;
    }

    @Override
    public void setOriginalList(List entries) {
        this.originalBiomePairs = entries;
    }

}
