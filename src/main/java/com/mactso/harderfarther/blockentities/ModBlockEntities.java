package com.mactso.harderfarther.blockentities;

import com.mactso.harderfarther.block.ModBlocks;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlockEntities
{

	public static final BlockEntityType<GrimHeartBlockEntity> GRIM_HEART = BlockEntityType.Builder.of(GrimHeartBlockEntity::new, ModBlocks.GRIM_HEART).build(null);

	public static void register(IForgeRegistry<BlockEntityType<?>> forgeRegistry)
	{
		int x= 3;
		forgeRegistry.register("grim_heart",GRIM_HEART);
	}
}
