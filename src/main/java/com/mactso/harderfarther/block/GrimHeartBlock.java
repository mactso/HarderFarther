package com.mactso.harderfarther.block;

import java.util.Random;

import com.mactso.harderfarther.blockentities.GrimHeartBlockEntity;
import com.mactso.harderfarther.client.PlayGrimSongs;
import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.sounds.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
	protected final ParticleOptions particle;
	protected static final VoxelShape SHAPE = Block.box(7.0D, 7.0D, 7.0D, 10.0D, 10.0D, 10.0D);

	
	public GrimHeartBlock(Properties properties, ParticleOptions particleChoice) {
		super(properties);
	    this.particle = particleChoice;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}
	
	// TODO: This is only called once when the block is set.
	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return 7;
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
			level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE , SoundSource.AMBIENT, 4.0f, 0.8f);
			// start flute music SoundEvent.
			GrimCitadelManager.removeHeart((ServerLevel) level, pos);
//			level.playSound(null, pos, ModSounds.LAKE_DESTINY, SoundSource.MUSIC, 2.0f, 1.0f);

		} else {
			if (level.isClientSide()) {
//				PlayGrimSongs.stopCurrentSong();
				PlayGrimSongs.playSong(ModSounds.LAKE_DESTINY);
			}			
		}
		super.onRemove(oldbs, level, pos, newbs, moving);
	}
	
		// this is client side.
	   public void animateTick(BlockState bs, Level level, BlockPos pos, Random rand) {
		      double d0 = (double)pos.getX() + 0.5D;
		      double d1 = (double)pos.getY() + 0.5D;
		      double d2 = (double)pos.getZ() + 0.5D;
		      double vx = (rand.nextDouble()-0.5)/64;
		      double vz = (rand.nextDouble()-0.5)/64;
		      double vy = (rand.nextDouble()-0.65)/64;
		      if (rand.nextInt(3) == 1) {
			      level.addParticle(ParticleTypes.FALLING_OBSIDIAN_TEAR, d0, d1, d2, vx, vy, vz);
		      }
		      vx = (rand.nextDouble()-0.5)/32;
		      vz = (rand.nextDouble()-0.5)/32;
		      vy = (rand.nextDouble()-0.55)/32;
    		  level.addParticle(this.particle, d0, d1, d2, vx, vy, vz);
	   }
}