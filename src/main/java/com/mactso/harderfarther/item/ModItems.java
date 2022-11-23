package com.mactso.harderfarther.item;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {

	public static final Item LIFE_HEART = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).fireResistant().rarity(Rarity.EPIC)).setRegistryName("life_heart");
	public static final ItemStack LIFE_HEART_STACK = new ItemStack(LIFE_HEART, 1);
	public static final Item DEAD_BRANCHES = new BlockItem(ModBlocks.DEAD_BRANCHES, (new Item.Properties()).tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName("dead_branches");
	public static final Item BURNISHING_STONE = new BurnishingStone(new Item.Properties().tab(CreativeModeTab.TAB_MISC).fireResistant().rarity(Rarity.RARE)).setRegistryName("burnishing_stone");
	public static final ItemStack BURNISHING_STONE_STACK = new ItemStack(BURNISHING_STONE, 1);
	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{

		forgeRegistry.register(DEAD_BRANCHES);
		forgeRegistry.register(LIFE_HEART);
		forgeRegistry.register(BURNISHING_STONE);
		Utility.setLore(LIFE_HEART_STACK,
				Component.Serializer.toJson(new TranslatableComponent("item.harderfarther.life_heart.lore")));
		Utility.setLore(BURNISHING_STONE_STACK,
				Component.Serializer.toJson(new TranslatableComponent("item.harderfarther.burnishing_stone.lore")));

	}

}
