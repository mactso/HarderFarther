package com.mactso.harderfarther.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistry;


public class ModItems {
	
// public static final Item BLESSED_APPLE = new Item(new Item.Properties().food(new FoodProperties.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
//	public static final Item STRANGE_POTION = new Item(new Item.Properties().food(new Food.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");

	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{
		//forgeRegistry.registerAll(BLESSED_APPLE);
	}

}
