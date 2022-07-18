package com.mactso.harderfarther.block;

import com.mactso.harderfarther.block.properties.GrimGateType;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class GrimGateBlock extends HalfTransparentBlock {
	public static final EnumProperty<GrimGateType> TYPE = EnumProperty.create("type", GrimGateType.class);

	@Override
	public void neighborChanged(BlockState myBlockState, Level level, BlockPos myPos, Block neighborOldBlock,
			BlockPos neighborPos, boolean pushing) {
		Block neighborNewBlock = level.getBlockState(neighborPos).getBlock();
		if ((neighborNewBlock == Blocks.AIR) && (neighborOldBlock == ModBlocks.GRIM_GATE)) {
			level.destroyBlock(myPos, false);
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(TYPE);
	}

	public GrimGateBlock(Properties prop) {
		super(prop);
        registerDefaultState(getStateDefinition().any().setValue(TYPE, GrimGateType.FLOOR));
	}
}
