//package com.mactso.harderfarther.item;
//
//import java.util.Random;
//
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//
//public class BurnishingStone extends Item {
//
//	public BurnishingStone(Properties prop)  {
//		super(prop);
//	}
//	
//@Override
//public ItemStack finishUsingItem(ItemStack iStack, Level level, LivingEntity le) {
//		if (level.isClientSide)
//		return iStack;
//		Item i = ModItems.BURNISHING_STONE;
//		if (le instanceof Player p) {
//			Random rand = level.getRandom();
//			Inventory inv = p.getInventory();
//			ItemStack is = inv.getItem(Inventory.SLOT_OFFHAND);
//			if (!is.isDamageableItem()) return iStack;
//			if (is.getDamageValue() < is.getMaxDamage()) {
//				int newDamage = is.getDamageValue()-(10+rand.nextInt(9));
//				if (newDamage < 0) {
//					newDamage = 0;
//				}
//				is.setDamageValue(newDamage);
//			}
//
//		}
//		return super.finishUsingItem(iStack, level, le);		
//	}
//}	
//	
//
//
