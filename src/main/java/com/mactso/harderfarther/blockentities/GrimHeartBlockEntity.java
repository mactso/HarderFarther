package com.mactso.harderfarther.blockentities;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GrimHeartBlockEntity extends BlockEntity {
	static Set<BlockPos> GrimHeartEntityPositions; 
	public GrimHeartBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GRIM_HEART, pos, state);
	}
}
