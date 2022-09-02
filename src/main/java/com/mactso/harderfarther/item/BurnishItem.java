//package com.mactso.harderfarther.item;
//
//import java.util.Random;
//
//import net.minecraft.core.NonNullList;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ChorusFruitItem;
//import net.minecraft.world.item.FlintAndSteelItem;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.Level;
//
//public class BurnishItem extends FlintAndSteelItem {
//
//	public BurnishItem(Properties p_40710_) {
//		super(p_40710_);
//		// 
//	}
//	
//	@Override
//	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity le) {
//		
//		if (level.isClientSide)
//			return stack;
//		Item i = Items.FLINT_AND_STEEL;
//		if (le instanceof Player p) {
//			Random rand = level.getRandom();
//			Inventory inv = p.getInventory();
//			ItemStack is = inv.getItem(Inventory.SLOT_OFFHAND);
//			if (!is.isDamageableItem()) return stack;
//			if (is.getDamageValue() < is.getMaxDamage()) {
//				int newDamage = is.getDamageValue()-(10+rand.nextInt(9));
//				if (newDamage < 0) {
//					newDamage = 0;
//				}
//				is.setDamageValue(newDamage);
//			}
//
//		}
//		return super.finishUsingItem(stack, level, le);
//	}
//
//}
