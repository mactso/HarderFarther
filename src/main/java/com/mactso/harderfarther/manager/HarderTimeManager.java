package com.mactso.harderfarther.manager;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.events.FogColorsEventHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.GravelBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public class HarderTimeManager {

	static float pitch = 0.67f; // slower and longer

	static List<SoundEvent> spookySounds = Arrays.asList(SoundEvents.AMBIENT_CAVE, SoundEvents.WITCH_AMBIENT,
			SoundEvents.SOUL_ESCAPE, SoundEvents.ZOMBIE_AMBIENT, SoundEvents.SOUL_SAND_STEP,
			SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD);

	private static void doClientParticles(Player cp, Random rand,
			SimpleParticleType p1,
			SimpleParticleType p2,
			SimpleParticleType p3,
			SoundEvent soundEvent) 
	{
		BlockPos pos = cp.blockPosition();
		Vec3 lookv = cp.getForward();
		if (rand.nextInt(20) == 1) {
			lookv = lookv.reverse();
		}
		BlockPos pPos = new BlockPos(pos.getX()+lookv.x*7,pos.getY()+lookv.y*2,pos.getZ()+lookv.z*7);
		for (int k = 0; k < 5; ++k) {

			int xv = (rand.nextInt(7) - 4) * 3;
			int yv = (rand.nextInt(5) - 2) * 2;
			int zv = (rand.nextInt(7) - 4) * 3;

			BlockPos temp = pPos.east(xv).above(yv).north(zv);
			for (int j = 0; j < 2; ++j) {

				double x = (double) temp.getX() + rand.nextDouble() * (double) 0.1F;
				double y = (double) temp.getY() + rand.nextDouble();
				double z = (double) temp.getZ() + rand.nextDouble();

				cp.level.addParticle(p1,  x, y, z, xv/3, yv/2, zv/2);
				cp.level.addParticle(p2,  x, y, z, xv/3, yv/2, zv/2);
				cp.level.addParticle(p3,  x, y, z, xv/3, yv/2, zv/2);
			}

			cp.level.playSound(cp, pPos, soundEvent, SoundSource.AMBIENT, 0.95f, pitch);

		}
	}

	private static void doNiceAtmosphere(Player cp) {

		Random rand = cp.level.getRandom();

		
		float timeDifficulty = FogColorsEventHandler.getServerTimeDifficulty();

		if (timeDifficulty > 0) {
			return;
		}

		if (!cp.level.canSeeSky(cp.blockPosition())) {
			if (rand.nextInt(2400) == 43) {
				cp.level.playSound(cp, cp.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.AMBIENT, 0.95f, pitch);
			}	
			return;
		}
		

		int chance = 23;
		if (cp.level.isNight())
			chance -= -23;
		if (rand.nextInt(2400) > (chance))
			return;

		doClientParticles(cp, rand,
				ParticleTypes.GLOW,
				ParticleTypes.SPORE_BLOSSOM_AIR,
				ParticleTypes.BUBBLE,
				SoundEvents.AMETHYST_BLOCK_CHIME);
	}

	private static void doRandomScaryThings(ServerPlayer sp) {

		ServerLevel sl = sp.getLevel();
		Random rand = sl.getRandom();

		float timeDifficulty = getTimeDifficulty(sl, sp);
		if (rand.nextFloat() > timeDifficulty)
			return;

		int chance = 37;
		if (sl.isNight())
			chance += 19;

		if (rand.nextInt(1800) > (chance))
			return;
		
		BlockPos pos = sp.blockPosition();
		Vec3 lookv = sp.getForward();
		if (rand.nextInt(20) == 1) {
			lookv = lookv.reverse();
		}
		BlockPos pPos = new BlockPos(1+pos.getX()+(lookv.x*7),3+ pos.getY()+(lookv.y*3),1+pos.getZ()+(lookv.z*7));
		sl.sendParticles(sp, ParticleTypes.SOUL, false, pPos.getX(), pPos.getY(), pPos.getZ(), 1, 0, 0.08d, 0, 1);

		for (int k = 0; k < 3; ++k) {

			int xv = (rand.nextInt(6) - 2) * 3;
			int yv = (rand.nextInt(5) - 2) * 2;
			int zv = (rand.nextInt(6) - 2) * 3;

			BlockPos temp = pPos.east(xv).above(yv).north(zv);
			BlockState bs = sl.getBlockState(temp);
			Block b = bs.getBlock();
			FluidState fs = sl.getFluidState(temp); 
//			if (fs.is(FluidTags.WATER)) {
//				sl.setBlock(temp, BlockState.MUD, 3);   // FOR 1.19.1
//			sl.setBlock(temp, BlockState.BLUE, 3);   // FOR 1.19.1

			//			}
			if (!bs.isAir()) {
				if ((b instanceof TallGrassBlock) ) {
					sl.setBlock(temp.below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
					if (rand.nextInt(3)==1) {
						sl.setBlock(temp, Blocks.CRIMSON_ROOTS.defaultBlockState(), 3);
					} else {
						sl.setBlock(temp, Blocks.WARPED_ROOTS.defaultBlockState(), 3);
					}
				} else if (b instanceof CropBlock) {
					int r = rand.nextInt(4);
					if (r<=1) {
						sl.setBlock(temp, Blocks.WARPED_ROOTS.defaultBlockState(), 3);
					} else if (r==2){
						sl.setBlock(temp, Blocks.CRIMSON_ROOTS.defaultBlockState(), 3);
					} else {
						sl.setBlock(temp, Blocks.DEAD_BUSH.defaultBlockState(), 3);
						sl.setBlock(temp.below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
					}
				} else if (b instanceof FlowerBlock) {
					int r = rand.nextInt(6);
					if (r==0) {
						sl.setBlock(temp, Blocks.WARPED_FUNGUS.defaultBlockState(), 3);
					} else if (r<=2){
						sl.setBlock(temp, Blocks.CRIMSON_FUNGUS.defaultBlockState(), 3);
					} else {
						sl.setBlock(temp, Blocks.TALL_GRASS.defaultBlockState(), 3);
					}
				} else if (b instanceof LeavesBlock) {
					sl.setBlock(temp, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);;
				} else if (b instanceof GrassBlock) {
					sl.setBlock(temp, Blocks.COARSE_DIRT.defaultBlockState(), 3);
					sl.setBlock(temp.above(), Blocks.FIRE.defaultBlockState(), 131);
				} else if ((rand.nextInt(8)==1) && (bs.isFlammable(sl, pPos, null))) {
					sl.setBlock(temp, Blocks.FIRE.defaultBlockState(), 131);
				}   else if (b instanceof SnowLayerBlock) {
					sl.setBlock(temp, Blocks.ICE.defaultBlockState(), 3);
					sl.setBlock(temp.above(), Blocks.SOUL_FIRE.defaultBlockState(), 131);
				} else if (BlockTags.BASE_STONE_OVERWORLD.contains(bs.getBlock())) {
					if (!sl.canSeeSky(pos)) {
						sl.setBlock(temp, Blocks.GRAVEL.defaultBlockState(),3);
						sl.playSound(null, temp, SoundEvents.TURTLE_EGG_CRACK, SoundSource.AMBIENT, 0.11f, pitch);
					}
				} else if (b instanceof GravelBlock) {
					sl.setBlock(temp.above(), Blocks.GRAVEL.defaultBlockState(),3);
					sl.playSound(null, temp.above(), SoundEvents.SILVERFISH_STEP, SoundSource.AMBIENT, 0.11f, pitch);
				} 
			}

		}
	}

	public static void doScarySpookyThings(Player p) {
		if (p.level.isClientSide) {
			doNiceAtmosphere(p);
			doSpookyAtmosphere(p);
			return;
		} 
		doRandomScaryThings((ServerPlayer) p);
	}

	// clientside
	private static void doSpookyAtmosphere(Player cp) {

		Random rand = cp.level.getRandom();

		float timeDifficulty = FogColorsEventHandler.getServerTimeDifficulty();
		if (rand.nextFloat() > timeDifficulty)
			return;

		int chance = 7;
		if (cp.level.isNight())
			chance += 9;
		
		if (rand.nextInt(3000) > (chance))
			return;
		int i = rand.nextInt(spookySounds.size());
		cp.level.playSound(cp, cp.blockPosition(), spookySounds.get(i), SoundSource.AMBIENT, 0.12f, pitch);
		if (rand.nextInt(100) == 42) {
			cp.level.playSound(cp, cp.blockPosition(), SoundEvents.GOAT_SCREAMING_PREPARE_RAM, SoundSource.AMBIENT, 0.12f, pitch);
		}
		if (rand.nextInt(100) < 5) 
			cp.level.playSound(cp, cp.blockPosition(), SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, 0.23f, 0.66f);
		doClientParticles(cp, rand,
				ParticleTypes.LARGE_SMOKE,
				ParticleTypes.SMALL_FLAME,
				ParticleTypes.SOUL,
				SoundEvents.SOUL_ESCAPE);

	}

	// must be server side.  chunk inhabited time is 0 on client side.
	public static float getTimeDifficulty(ServerLevel level, LivingEntity entity) {
		if (!MyConfig.isMakeHarderOverTime())
			return 0;

		long startHarderTime = (long) (MyConfig.getMaxHarderTimeMinutes() *.66f);
		long inhabitedMinutes = level.getChunk(entity.blockPosition()).getInhabitedTime() / 1200; // 60 sec * 20

		if (inhabitedMinutes < startHarderTime)
			return 0;

		long minutes = inhabitedMinutes - startHarderTime;
		float timeDifficulty = Math.min(1.0f, (float) minutes / startHarderTime);
		timeDifficulty = (float) Math.min(0.33, timeDifficulty);
		return timeDifficulty;
	}
	
}
