package com.mactso.harderfarther.config;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.feature.BasaltColumnsFeature;
import net.minecraft.world.level.levelgen.feature.BasaltPillarFeature;
import net.minecraft.world.level.levelgen.Heightmap;

public class GrimCitadelManager {
	private static long checkTimer = 0;
	private static long builtSpikeTimer;
	private static BlockState BASALT = Blocks.BASALT.defaultBlockState();
	private static BlockState POLISHEDBASALT = Blocks.POLISHED_BASALT.defaultBlockState();
	private static BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
	private static BlockState BLACKSTONESLAB = Blocks.BLACKSTONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE,
			SlabType.TOP);
	private static BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
	private static BlockState FIRE = Blocks.FIRE.defaultBlockState();

	private static void makeGrimCitadel(ServerLevel level, int bottom, int top, BlockPos pos) {
		Block b = Blocks.BASALT;
		Random rand = level.getRandom();
		BlockPos bottomPos = new BlockPos(pos.getX(), bottom, pos.getZ());

		for (int fy = 0; fy < top - bottom; fy++) {
			if (fy % 4 == 0) {
				buildAFloor(level, rand, bottomPos, fy, top - bottom);
			}
			buildOutsideWall(level, bottomPos, fy, top - bottom);
			buildCore(level, bottomPos, fy);
		}
		decorateCitadel(level,bottomPos, top, bottom);
		level.setBlock(bottomPos.north(3), Blocks.AIR.defaultBlockState(),0);
		level.setBlock(bottomPos.north(3).above(1), Blocks.AIR.defaultBlockState(),0);
		level.setBlock(bottomPos.north(3).above(2), Blocks.AIR.defaultBlockState(),0);

		level.setBlockAndUpdate(bottomPos.above(top - bottom + 2), Blocks.GLOWSTONE.defaultBlockState());
		level.setBlock(bottomPos.above(top - bottom + 1), Blocks.LAVA.defaultBlockState(), 0);

		builtSpikeTimer = level.getGameTime() + 21;
	}

	private static void decorateCitadel(ServerLevel level, BlockPos bottomPos, int top, int bottom) {
		addCorners(level, bottomPos, -1);
		addCorners(level, bottomPos, top - bottom -2);

	}

	private static void addCorners(ServerLevel level, BlockPos bottomPos, int offset) {
		BlockState bs = POLISHEDBASALT;
		addOneCorner(level, bottomPos.north(4).west(4), offset, bs);
		addOneCorner(level, bottomPos.north(-3).west(4), offset, bs);
		addOneCorner(level, bottomPos.north(-3).west(-3), offset, bs);
		addOneCorner(level, bottomPos.north(4).west(-3), offset, bs);
		
	}

	private static void addOneCorner(ServerLevel level, BlockPos pos, int offset, BlockState bs) {
		MutableBlockPos mPos = new MutableBlockPos();

		int posX = pos.getX();
		int posY = pos.getY() + offset;
		int posZ = pos.getZ();
		
		for (int fx = 0; fx < 2; fx++) {
			for (int fz = 0; fz < 2; fz++) {
				for (int fy = 0; fy < 3; fy++) {
					mPos.setX(posX + fx);
					mPos.setY(posY + fy);
					mPos.setZ(posZ + fz);
					level.setBlock(mPos, bs, 0);					
				}
			}
		}
		
	}

	private static void buildOutsideWall(ServerLevel level, BlockPos pos, int fy, int height) {
		Random rand = level.getRandom();
		BlockState bs1;
		BlockState bs2;
		float percent;
		if (fy < height / 2) {
			percent = (float) fy / (float) (height / 2);
			bs1 = BASALT;
			bs2 = POLISHEDBASALT;
		} else {
			percent = (float) (fy - (height / 2)) / (float) (height / 2);
			bs1 = POLISHEDBASALT;
			bs2 = NETHERRACK;
		}

		MutableBlockPos mPos = new MutableBlockPos();
		mPos.setY(pos.getY() + fy);
		int posX = pos.getX();
		int posZ = pos.getZ();
		for (int fx = -3; fx <= 3; fx++) {
			for (int fz = -3; fz <= 3; fz++) {
				boolean corner = false;
				if ((Math.abs(fx) == 3) && (Math.abs(fz) == 3)) {
					corner = true;
				}
				if ((Math.abs(fx) == 3) || (Math.abs(fz) == 3)) {
					mPos.setX(posX + fx);
					mPos.setZ(posZ + fz);
					if ((!corner) && (rand.nextInt(15) < 1) && ((fy + 2) % 4 == 0)) {

						level.setBlock(mPos, BLACKSTONESLAB, 0);
					} else if (rand.nextFloat() < percent) {
						level.setBlock(mPos, bs2, 0);
					} else {
						level.setBlock(mPos, bs1, 0);
					}
				}
			}
		}
	}

	private static void buildCore(ServerLevel level, BlockPos pos, int fy) {

		BlockState bs = BLACKSTONE;
		Random rand = level.getRandom();
		if (rand.nextFloat() > 0.75) {
			bs = POLISHEDBASALT;
		}
		int ew = (level.getRandom().nextInt(3)) - 1;
		int ns = (level.getRandom().nextInt(3)) - 1;
		level.setBlock(pos.east(ew).north(ns).above(fy), bs, 0);
		if (rand.nextFloat() > 0.66) {
			level.setBlock(pos.east(ew).north(ns).above(fy + 1), Blocks.NETHERRACK.defaultBlockState(), 0);
			if (level.getRandom().nextFloat() > 0.33) {
				level.setBlock(pos.east(ew).north(ns).above(fy + 2), Blocks.FIRE.defaultBlockState(), 0);
			}
		}
	}

	public static void buildAFloor(ServerLevel level, Random rand, BlockPos pos, int fy, int height) {
		MutableBlockPos mPos = new MutableBlockPos();
		mPos.setY(pos.getY() + fy);
		int posX = pos.getX();
		int posZ = pos.getZ();

		for (int fx = -2; fx <= 2; fx++) {
			for (int fz = -2; fz <= 2; fz++) {
				mPos.setX(posX + fx);
				mPos.setZ(posZ + fz);
				int r = rand.nextInt(height);
				if (r > (fy / 3)) {
					level.setBlock(mPos, BASALT, 0);
				} else if (r > 2 * (fy / 3)) {
					level.setBlock(mPos, POLISHEDBASALT, 0);
				} else {
					level.setBlock(mPos, NETHERRACK, 0);
				}
				level.setBlock(mPos.above(1), Blocks.AIR.defaultBlockState(), 0);
				level.setBlock(mPos.above(2), Blocks.AIR.defaultBlockState(), 0);
				level.setBlock(mPos.above(3), Blocks.AIR.defaultBlockState(), 0);

			}
		}
		int fx = rand.nextInt(5)-2;
		int fz = rand.nextInt(5)-2;
		mPos.setX(posX + fx);
		mPos.setZ(posZ + fz);
		level.setBlock(mPos, Blocks.AIR.defaultBlockState(), 0);
	}

	public static void checkCleanUpCitadels(ServerLevel level) {
		if (checkTimer > level.getGameTime())
			return;

		if (builtSpikeTimer > level.getGameTime())
			return;

		Iterator<BlockPos> i = MyConfig.getGrimCitadelsBlockPosList().iterator();
		while (i.hasNext()) {
			BlockPos pos = i.next();
			ChunkAccess chunk = level.getChunk(pos);
			BlockState bs = chunk.getBlockState(pos);
			if (chunk.getInhabitedTime() < 600) {
				if (bs.getBlock() != Blocks.SHROOMLIGHT) {
					makeGrimCitadel(level, getCitadelBottom(level, pos), getCitadelTop(level, pos), pos);
					level.setBlockAndUpdate(pos, Blocks.SHROOMLIGHT.defaultBlockState());
					break;
				}
			} else {
				if (bs.getBlock() != Blocks.SHROOMLIGHT) {
					i.remove();
				}
			}
		}
		checkTimer = level.getGameTime() + 200; // TODO change back to 2000
	}

	private static int getCitadelBottom(ServerLevel level, BlockPos pos) {
		int bottom = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
		if (bottom > pos.getY() - 30)
			bottom = pos.getY() - 30;
		if (bottom < level.getMinBuildHeight())
			bottom = level.getMinBuildHeight();
		return bottom;
	}

	private static int getCitadelTop(ServerLevel level, BlockPos pos) {
		int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
		if (top < pos.getY() + 30)
			top = pos.getY() + 30;
		if (top > level.getMaxBuildHeight())
			top = level.getMaxBuildHeight();
		return top;
	}

}
