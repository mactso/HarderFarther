package com.mactso.harderfarther.item;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {

	public static final Item LIFE_HEART = new Item(new Item.Properties().fireResistant().rarity(Rarity.EPIC));
	public static ItemStack LIFE_HEART_STACK = null;
	public static final Item DEAD_BRANCHES = new BlockItem(ModBlocks.DEAD_BRANCHES, (new Item.Properties()));
	public static final Item BURNISHING_STONE = new BurnishingStone(new Item.Properties().fireResistant().rarity(Rarity.RARE));
	public static ItemStack BURNISHING_STONE_STACK = null;
	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{

		forgeRegistry.register("dead_branches", DEAD_BRANCHES);
		forgeRegistry.register("life_heart", LIFE_HEART);
		forgeRegistry.register("burnishing_stone",BURNISHING_STONE);

		LIFE_HEART_STACK = new ItemStack(LIFE_HEART, 1);
		Utility.setLore(LIFE_HEART_STACK,
				Component.Serializer.toJson(Component.translatable("item.harderfarther.life_heart.lore")));

		BURNISHING_STONE_STACK = new ItemStack(BURNISHING_STONE, 1);
		Utility.setLore(BURNISHING_STONE_STACK,
				Component.Serializer.toJson(Component.translatable("item.harderfarther.burnishing_stone.lore")));
	}

}
