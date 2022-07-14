package com.mactso.harderfarther.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GrimGateBlock extends HalfTransparentBlock {

	@Override
	public void neighborChanged(BlockState myBlockState, Level level, BlockPos myPos, Block neighborOldBlock,
			BlockPos neighborPos, boolean pushing) {
		Block neighborNewBlock = level.getBlockState(neighborPos).getBlock();
		if ((neighborNewBlock == Blocks.AIR) && (neighborOldBlock == ModBlocks.GRIM_GATE)) {
			level.destroyBlock(myPos,false);
		}
	}

	public GrimGateBlock(Properties p_53970_) {
		super(p_53970_);
	}
}
