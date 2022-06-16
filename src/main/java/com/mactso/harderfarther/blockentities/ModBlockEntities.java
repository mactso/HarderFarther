package com.mactso.harderfarther.blockentities;

import com.mactso.harderfarther.block.ModBlocks;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlockEntities
{
	public static final BlockEntityType<GrimHeartBlockEntity> GRIM_HEART = create("grim_heart", BlockEntityType.Builder.of(GrimHeartBlockEntity::new, ModBlocks.GRIM_HEART).build(null));

	public static <T extends BlockEntity> BlockEntityType<T> create(String key, BlockEntityType<T> type)
	{
		type.setRegistryName(key);
		return type;
	}

	public static void register(IForgeRegistry<BlockEntityType<?>> forgeRegistry)
	{
		forgeRegistry.register(GRIM_HEART);
	}
}
