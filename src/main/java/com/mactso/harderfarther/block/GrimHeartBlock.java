package com.mactso.harderfarther.block;

import com.mactso.harderfarther.blockentities.GrimHeartBlockEntity;
import com.mactso.harderfarther.config.GrimCitadelManager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrimHeartBlock extends BaseEntityBlock {

	protected static final VoxelShape SHAPE = Block.box(7.0D, 7.0D, 7.0D, 10.0D, 10.0D, 10.0D);

	public GrimHeartBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GrimHeartBlockEntity(pos, state);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_60584_) {
		return PushReaction.DESTROY;
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
		return 171;
	}

	@Override
	public void onRemove(BlockState oldbs, Level level, BlockPos pos, BlockState newbs, boolean moving) {
		if (level instanceof ServerLevel) {
			GrimCitadelManager.removeHeart((ServerLevel) level, pos);
		}
		super.onRemove(oldbs, level, pos, newbs, moving);
	}
}