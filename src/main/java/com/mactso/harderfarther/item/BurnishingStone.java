package com.mactso.harderfarther.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BurnishingStone extends Item {

	public BurnishingStone(Properties prop) {
		super(prop);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stackIn, Level level, LivingEntity le) {
		ItemStack itemstack = super.finishUsingItem(stackIn, level, le);
		if (le instanceof ServerPlayer sp) {
			ItemStack stack;
			if (stackIn == sp.getMainHandItem()) {
				stack = sp.getOffhandItem();
			} else {
				stack = sp.getMainHandItem();
			}
			if ((stack.isDamageableItem()) && (stack.isDamaged())) {
				int repairAmount = level.getRandom().nextInt(stack.getMaxDamage() / 10) + 3;
				int newDamageValue = Math.min(stack.getMaxDamage(), stack.getDamageValue() + repairAmount);
				stack.setDamageValue(newDamageValue);
				stackIn.setCount(stackIn.getCount() - 1);
				level.playSound((Player) null, le.getX(), le.getY(), le.getZ(), SoundEvents.GRINDSTONE_USE,
						SoundSource.PLAYERS, 1.0F, 1.0F);
				le.playSound(SoundEvents.GRINDSTONE_USE, 1.0F, 1.0F);
				((Player) sp).getCooldowns().addCooldown(this, 40);

			}
		}
		return stackIn;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand iHand) {
		if (!level.isClientSide()) {
			ItemStack bstack;
			ItemStack stack;

			if (iHand == InteractionHand.MAIN_HAND) {
				bstack = player.getMainHandItem();
				stack = player.getOffhandItem();
			} else {
				bstack = player.getOffhandItem();
				stack = player.getMainHandItem();
			}
			if ((stack.isDamageableItem()) && (stack.isDamaged())) {
				int repairAmount = level.getRandom().nextInt(stack.getMaxDamage() / 20) + stack.getMaxDamage() / 20;
				int newDamageValue = Math.max(0, stack.getDamageValue() - repairAmount);
				stack.setDamageValue(newDamageValue);
				bstack.setCount(bstack.getCount() - 1);
				level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.GRINDSTONE_USE,
						SoundSource.PLAYERS, 1.0F, 1.0F);
				player.playSound(SoundEvents.GRINDSTONE_USE, 1.0F, 1.0F);
				player.getCooldowns().addCooldown(this, 60);
			}
		}
		return super.use(level, player, iHand);
	}

	@Override
	public boolean isEnchantable(ItemStack p_41456_) {
		return false;
	}

}
