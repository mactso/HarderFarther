package com.mactso.harderfarther.item;

import com.mactso.harderfarther.block.ModBlocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {
	
// public static final Item BLESSED_APPLE = new Item(new Item.Properties().food(new FoodProperties.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
//	public static final Item STRANGE_POTION = new Item(new Item.Properties().food(new Food.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
	public static final Item LIFE_HEART = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).fireResistant().rarity(Rarity.EPIC)).setRegistryName("life_heart");
	public static final Item DEAD_BRANCHES = new BlockItem(ModBlocks.DEAD_BRANCHES, (new Item.Properties()).tab(CreativeModeTab.TAB_DECORATIONS)).setRegistryName("dead_branches");
	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		forgeRegistry.registerAll(DEAD_BRANCHES,LIFE_HEART);
	}

}
