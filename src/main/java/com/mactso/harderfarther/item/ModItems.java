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
// public static final Item BLESSED_APPLE = new Item(new Item.Properties().food(new FoodProperties.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
//	public static final Item STRANGE_POTION = new Item(new Item.Properties().food(new Food.Builder().hunger(3).setAlwaysEdible().saturation(0.3F).fastToEat().effect(() -> new EffectInstance(Effects.ABSORPTION, 400, 4), 1.0F).build()).group(ItemGroup.FOOD).maxStackSize(16)).setRegistryName("blessed_apple");
	public static final Item LIFE_HEART = new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC).fireResistant().rarity(Rarity.EPIC));
	public static ItemStack LIFE_HEART_STACK = null;

	public static final Item DEAD_BRANCHES = new BlockItem(ModBlocks.DEAD_BRANCHES, (new Item.Properties()).tab(CreativeModeTab.TAB_DECORATIONS));
	
	public static void register(IForgeRegistry<Item> forgeRegistry)
	{

		forgeRegistry.register("dead_branches", DEAD_BRANCHES);
		forgeRegistry.register("life_heart", LIFE_HEART);
		LIFE_HEART_STACK = new ItemStack(LIFE_HEART, 1);
		Utility.setLore(LIFE_HEART_STACK,
				Component.Serializer.toJson(Component.translatable("item.harderfarther.life_heart.lore")));
	}

}
