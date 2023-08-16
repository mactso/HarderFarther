package com.mactso.harderfarther.mixin;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {


    @Shadow
    public abstract Item getItem();


    @Shadow
    public abstract int getDamageValue();

    @Shadow
    public abstract void setDamageValue(int damage);

    @Shadow
    public abstract int getMaxDamage();

    //Sets a max value configured by end-user(default 6) to durability damage on armor so that armor doesn't get absolutely destroyed by higher damaging entities. Zombies in normal minecraft on normal difficulty do 3 damage for reference. Wither skeletons do 8 for reference.
    @Inject(at = @At(value = "RETURN"), method = "hurt", cancellable = true)
    private void harderfarther$SetItemMaxDamage(int amount, RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> cir){

        if(this.getItem() instanceof ArmorItem) {
            if(MyConfig.getDebugLevel() > 0) {
                Utility.debugMsg(1, "Armor piece " + this.getItem().getDescriptionId() + " took " + amount + " damage(before possible reduction)!");
            }
            if(amount >= MyConfig.getMaximumArmorDamage()) {
                int i = this.getDamageValue() - amount + MyConfig.getMaximumArmorDamage();
                this.setDamageValue(i);
                cir.setReturnValue(i >= this.getMaxDamage());
                //For reference, an iron chest-plate has 240 durability, so 6 damage would allow 40 hits before it breaks.
            }
        }

    }

}
