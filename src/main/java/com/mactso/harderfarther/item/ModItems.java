package com.mactso.harderfarther.item;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {
	
	public static final Item BLESSED_APPLE = new Item(new Item.Properties().food(new Food.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
//	public static final Item STRANGE_POTION = new Item(new Item.Properties().food(new Food.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");

	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		forgeRegistry.registerAll(BLESSED_APPLE);
	}

}
