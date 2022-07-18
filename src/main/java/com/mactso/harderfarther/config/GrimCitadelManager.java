package com.mactso.harderfarther.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.annotation.Nullable;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.GrimGateBlock;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.block.properties.GrimGateType;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncAllGCWithClientPacket;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.Vec3;

public class GrimCitadelManager {
	private static long checkTimer = 0;
	private static int grimBonusDistSqr = 0;

	private static int currentCitadelIndex = -1;

	public static List<BlockPos> realGCList = new ArrayList<BlockPos>();

	private static List<Block> protectedBlocks = Arrays.asList(Blocks.NETHERRACK, Blocks.BLACKSTONE, Blocks.BASALT,
			Blocks.POLISHED_BASALT, Blocks.CRIMSON_PLANKS, Blocks.POLISHED_BLACKSTONE_BRICKS ,Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE, Blocks.TINTED_GLASS, Blocks.CHEST,
			Blocks.ANCIENT_DEBRIS);
	
	private static List<Block> floorBlocks = Arrays.asList(Blocks.BASALT, Blocks.CRIMSON_PLANKS, Blocks.NETHERRACK);
	private static int FLOOR_BLOCKS_TOP = 0;
	private static int FLOOR_BLOCKS_MIDDLE= 1;
	private static int FLOOR_BLOCKS_BOTTOM = 2;

	private static List<Block> wallBlocks = Arrays.asList(Blocks.POLISHED_BASALT, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, Blocks.NETHERRACK);
	private static int WALLTRIM = 2;

	private static BlockState GRIM_GATE_FLOOR = ModBlocks.GRIM_GATE.defaultBlockState();
	private static BlockState GRIM_GATE_DOOR = ModBlocks.GRIM_GATE.defaultBlockState().setValue(GrimGateBlock.TYPE, GrimGateType.DOOR);
	

	private static BlockState AIR = Blocks.AIR.defaultBlockState();
	private static BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();	
	private static BlockState BASALT = Blocks.BASALT.defaultBlockState();	
	private static BlockState POLISHEDBASALT = Blocks.POLISHED_BASALT.defaultBlockState();
	private static BlockState POLISHED_BLACKSTONE_BRICKS = Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState();
	private static BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
	private static BlockState GILDED_BLACKSTONE = Blocks.GILDED_BLACKSTONE.defaultBlockState();
	private static BlockState WINDOW = Blocks.TINTED_GLASS.defaultBlockState();
	private static BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
	private static BlockState FIRE = Blocks.FIRE.defaultBlockState();
	private static BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.defaultBlockState();
	private static File grimFile;
	private static UUID ITEM_SPEED_UUID = UUID.fromString("4ce59996-ed35-11ec-8ea0-0242ac120002");
	private static void addCorners(ServerLevel level, BlockPos bottomPos, int offset) {
		BlockState bs = POLISHEDBASALT;

		addOneCorner(level, bottomPos.north(getGrimRadius() + 2).west(getGrimRadius() + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-getGrimRadius() - 1).west(getGrimRadius() + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-getGrimRadius() - 1).west(-getGrimRadius() - 1), offset, bs);
		addOneCorner(level, bottomPos.north(getGrimRadius() + 2).west(-getGrimRadius() - 1), offset, bs);
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
	private static void addOptionalNewHearts(ServerLevel level, @Nullable BlockPos pos) {

		while (realGCList.size() < MyConfig.getGrimCitadelsCount()) {
			double randomRadian = level.getRandom().nextFloat() * (Math.PI * 2F);
			double xVec = Math.cos(randomRadian);
			double zVec = Math.sin(randomRadian);

			BlockPos ssPos = level.getSharedSpawnPos();
			int distSq = getRandomGrimCitadelDistanceSq(level, ssPos);
			if (distSq == 0) {
				if (pos != null) {
					distSq = (int) ssPos.distSqr(pos);
				} else {
					distSq = MyConfig.getGrimCitadelBonusDistanceSq() * 2;
				}
			}
			int dist = (int) Math.sqrt(distSq);
			dist = dist + MyConfig.getGrimCitadelBonusDistance();
			BlockPos newHeartPos = new BlockPos(ssPos.getX() + (dist * xVec), -1, ssPos.getZ() + (dist * zVec));
			realGCList.add(newHeartPos);
			Utility.debugMsg(1, pos, "realGCList size:" + realGCList.size() + "Adding new HeartPos:" + newHeartPos);
		}
	}

	public static void buildAFloor(ServerLevel level, Random rand, MutableBlockPos floorPos, int fy, int height,
			boolean roof) {
		int updateFlag = 0;
		if (roof) {
			updateFlag = 3;
		}
		updateFlag = 3;
		int posX = floorPos.getX();
		int posZ = floorPos.getZ();
		for (int fx = -getGrimRadius(); fx <= getGrimRadius(); fx++) {
			for (int fz = -getGrimRadius(); fz <= getGrimRadius(); fz++) {
				floorPos.setX(posX + fx);
				floorPos.setZ(posZ + fz);
				int r = rand.nextInt(height);
				if (r > (fy / 4)) {
					level.setBlock(floorPos, floorBlocks.get(FLOOR_BLOCKS_TOP).defaultBlockState(),updateFlag);
				} else if (r > (2 * fy / 4)) {
					level.setBlock(floorPos, floorBlocks.get(FLOOR_BLOCKS_MIDDLE).defaultBlockState(), updateFlag);
				} else {
					level.setBlock(floorPos, floorBlocks.get(FLOOR_BLOCKS_BOTTOM).defaultBlockState(), updateFlag);
				}
			}
		}

		int fx = getValidRandomFloorOffset(rand);
		int fz = getValidRandomFloorOffset(rand);

		floorPos.setX(posX + fx);
		floorPos.setZ(posZ + fz);
		if (rand.nextInt(10) < 2) {
			level.setBlock(floorPos, CAVE_AIR, 3);
		} else {
			level.setBlock(floorPos, GRIM_GATE_FLOOR, 3);
		}
		level.setBlock(floorPos.above(), CAVE_AIR, 3);
		floorPos.setX(posX);
		floorPos.setZ(posZ);

		populateFloor(level, floorPos, fy);

	}

	private static void buildBalcony(ServerLevel level, BlockPos tempPos) {
		MutableBlockPos mPos = new MutableBlockPos();
		int posX = tempPos.getX();
		int posY = tempPos.getY();
		int posZ = tempPos.getZ();

		mPos.setX(posX);
		mPos.setY(posY);
		mPos.setZ(posZ);

		level.setBlock(mPos, POLISHEDBASALT, 0);
		mPos.setY(posY + 1);
		level.setBlock(mPos, NETHERRACK, 0);
		mPos.setY(posY + 2);
		level.setBlock(mPos, FIRE, 3);
	}

	private static void buildCitadelFloors(ServerLevel level, int bottom, int top, Random rand, BlockPos bottomPos) {
		MutableBlockPos floorPos = new MutableBlockPos();
		floorPos.set(bottomPos);

		boolean roof = true;

		// go from top to bottom.
		for (int fy = (top+2 - bottom); fy >= 0; fy--) {
			if (isGrimCitadelFloorHeight(fy)) {
				floorPos.setX(bottomPos.getX());
				floorPos.setY(bottomPos.getY() + fy + 1);
				floorPos.setZ(bottomPos.getZ());
				buildAFloor(level, rand, floorPos, fy, top - bottom, roof);

				if (roof)
					roof = false;

				if (fy > 8) {
					buildFloorBalcony(level, bottomPos, fy, rand.nextInt(6));
				}
			} else if (fy < bottom+8) {
				floorPos.setY(bottomPos.getY() + fy);
				clearAFloor(level, rand, floorPos, fy, top - bottom, roof);
			}
			buildOutsideWall(level, bottomPos, fy, top - bottom);
			buildCore(level, bottomPos, fy);

		}
	}

	private static void buildCitadelFoundation(ServerLevel level, BlockPos pos) {

		MutableBlockPos mPos = new MutableBlockPos();
		mPos.setY(pos.getY());
		int posX = pos.getX();
		int posY = pos.getY();
		int posZ = pos.getZ();

		for (int fx = -getGrimRadius()- 3; fx < getGrimRadius()+ 3 + 1; fx++) {
			mPos.setX(fx + posX);
			for (int fz = -getGrimRadius() - 3; fz < getGrimRadius() + 3 + 1; fz++) {
				mPos.setZ(fz + posZ);
				int ground = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mPos.getX(), mPos.getZ());
				int groundw = level.getHeight(Heightmap.Types.OCEAN_FLOOR, mPos.getX(), mPos.getZ());
				if (groundw < ground)
					ground = groundw;
				for (int fy = posY - 1; fy >= ground; fy--) {
					mPos.setY(fy);
					level.setBlock(mPos, BASALT, 0);
				}
			}
		}

	}
	private static void buildCore(ServerLevel level, BlockPos pos, int fy) {

		BlockState bs = GILDED_BLACKSTONE;
		float roll = level.getRandom().nextFloat();

		if (roll > 0.95f) {
			bs = POLISHEDBASALT;
		} else if (roll > 0.85f) {
			bs = BLACKSTONE;
		}
		int ew = (level.getRandom().nextInt(getGrimRadius() /2)) - 1;
		int ns = (level.getRandom().nextInt(getGrimRadius() /2)) - 1;
		level.setBlock(pos.east(ew).north(ns).above(fy), bs, 0);
		if (level.getRandom().nextFloat() > 0.66) {
			level.setBlock(pos.east(ew).north(ns).above(fy + 1), NETHERRACK, 0);
			if (level.getRandom().nextFloat() > 0.33f) {
				level.setBlock(pos.east(ew).north(ns).above(fy + 2), BROWN_MUSHROOM, 3);
			}
		}

	}

	private static void buildFloorBalcony(ServerLevel level, BlockPos bottomPos, int fy, int side) {

		BlockPos tempPos;
		Random rand = level.getRandom();
		if (side > 3)
			return;

		int balconyRadius = getGrimRadius() + 2;
		switch (side) {
		default:
		case 0:
			tempPos = bottomPos.north(balconyRadius).above(fy).east(rand.nextInt(5) - 2);
			break;
		case 1:
			tempPos = bottomPos.south(balconyRadius).above(fy).east(rand.nextInt(5) - 2);
			break;
		case 2:
			tempPos = bottomPos.east(balconyRadius).above(fy).north(rand.nextInt(5) - 2);
			break;
		case 3:
			tempPos = bottomPos.west(balconyRadius).above(fy).north(rand.nextInt(5) - 2);
			break;
		}
		buildBalcony(level, tempPos);

	}

	private static void buildOutsideWall(ServerLevel level, BlockPos pos, int fy, int height) {
		Random rand = level.getRandom();
		BlockState bs1;
		BlockState bs2;
		float percent;
		if (fy < height / 2) {
			percent = (float) fy / (float) (height / 2);
			bs1 = wallBlocks.get(0).defaultBlockState();
			bs2 = wallBlocks.get(1).defaultBlockState();
		} else {
			percent = (float) (fy - (height / 2)) / (float) (height / 2);
			bs1 = wallBlocks.get(1).defaultBlockState();
			bs2 = wallBlocks.get(2).defaultBlockState();
		}

		MutableBlockPos mPos = new MutableBlockPos();
		mPos.setY(pos.getY() + fy);
		int posX = pos.getX();
		int posZ = pos.getZ();
		int wallRadius = getGrimRadius() + 1;
		for (int fx = -wallRadius; fx <= wallRadius; fx++) {
			for (int fz = -wallRadius; fz <= wallRadius; fz++) {
				boolean corner = false;
				boolean centered = false;
				boolean eastwest = false;
				boolean northsouth = false;
				if ((fx == 0) ) {
					centered = true;			// TODO need to clean this area up.
					eastwest = true;
				}
				if ((fz == 0)) {
					centered = true;
					northsouth = true;
				}
				if ((Math.abs(fx) == wallRadius) && (Math.abs(fz) == wallRadius)) {
					corner = true;
				}
				if ((Math.abs(fx) == wallRadius) || (Math.abs(fz) == wallRadius)) {
					mPos.setX(posX + fx);
					mPos.setZ(posZ + fz);
					if (corner) {
						level.setBlock(mPos, wallBlocks.get(WALLTRIM).defaultBlockState(), 3);
					} else if ((((fy==31)&&(centered)) || (rand.nextInt(15) < 1)) && ((fy + 1) % 4 == 0)) {
						buildHeartLevelWindows(level, mPos, eastwest,northsouth);
					} else if (rand.nextFloat() < percent) {
						level.setBlock(mPos, bs2, 0);
					} else {
						level.setBlock(mPos, bs1, 0);
					}
				}
			}
		}
	}

	private static void buildHeartLevelWindows(ServerLevel level, MutableBlockPos mPos, boolean eastwest, boolean northsouth) {
		level.setBlock(mPos, WINDOW, 0);
		level.setBlock(mPos.above(), WINDOW, 3);

		if (eastwest) {
			level.setBlock(mPos.east(), WINDOW, 0);
			level.setBlock(mPos.east().above(), WINDOW, 3);
			level.setBlock(mPos.west(), WINDOW, 0);
			level.setBlock(mPos.west().above(), WINDOW, 3);

		} else if (northsouth) {
			level.setBlock(mPos.north(), WINDOW, 0);
			level.setBlock(mPos.north().above(), WINDOW, 3);
			level.setBlock(mPos.south(), WINDOW, 0);
			level.setBlock(mPos.south().above(), WINDOW, 3);			
		}
		
	}

	private static void buildRoofBalconies(ServerLevel level, BlockPos roofPos) {
		int balconyRadius = getGrimRadius() + 2;
		buildBalcony(level, roofPos.north(balconyRadius));
		buildBalcony(level, roofPos.south(balconyRadius));
		buildBalcony(level, roofPos.east(balconyRadius));
		buildBalcony(level, roofPos.west(balconyRadius));
	}

	public static void checkCleanUpCitadels(ServerLevel level) {

		if (level.dimension() != Level.OVERWORLD) {
			return;
		}

		if (level.isClientSide)
			return;
		
		if (!MyConfig.isUseGrimCitadels())
			return;
		
		long gameTime = level.getGameTime();
		if (checkTimer == 0) {  // delay creating grim citadels for 1 minute when game started.
			checkTimer = gameTime+900;
		}
		
		if (checkTimer > gameTime)
			return;
		

		long ntt = level.getServer().getNextTickTime();
        long i = Util.getMillis() - ntt;
        if (i > 250L) {
        	Utility.debugMsg(1, "Server Slow - Skipped Checking Citadels");
        	checkTimer += 15;
        	return;
        }	

		addOptionalNewHearts(level, null);

		// Iterator<BlockPos> i = realGCList.iterator();

		if (realGCList.isEmpty()) {
			return;
		}

		
		// Check one possible grim citadel 

		currentCitadelIndex++;
		if (currentCitadelIndex >= realGCList.size()) {
			currentCitadelIndex = 0;
		}

		BlockPos pos = realGCList.get(currentCitadelIndex);
    	Utility.debugMsg(1, pos, "Does Grim Citadel #"+ currentCitadelIndex +" exist?");

    	ChunkAccess chunk = level.getChunk(pos);
		checkTimer = gameTime + 600;

		Set<BlockPos> ePosSet = chunk.getBlockEntitiesPos();
		boolean foundHeart = false;
		for (BlockPos ePos : ePosSet) {
			if ((ePos.getX() == pos.getX()) && ePos.getZ() == pos.getZ()) {
				if (level.getBlockState(ePos).getBlock() == ModBlocks.GRIM_HEART) {
					foundHeart = true;
			    	Utility.debugMsg(1, pos, "Aye, Grim Citadel #"+ currentCitadelIndex +" still exists.");
					break;
				}
			}
		}
		if (chunk.getInhabitedTime() < 600) { // heart not created yet
			if (!foundHeart) {
				Utility.debugMsg(1, pos, "Creating New Grim Citadel.");
				int bottom = getCitadelBottom(level, pos);
				makeGrimCitadel(level, bottom, pos);
				BlockPos heartPos = new BlockPos(pos.getX(), bottom + 31, pos.getZ());
				level.setBlock(heartPos, ModBlocks.GRIM_HEART.defaultBlockState(), 3);
				realGCList.set(currentCitadelIndex, heartPos);
				save();
				updateGCLocationsToClients(level);
			}
		} else { // heart destroyed / gone / taken.
			if (!foundHeart) {
				realGCList.remove(currentCitadelIndex);
				addOptionalNewHearts(level, pos);
				save();
				updateGCLocationsToClients(level);
				currentCitadelIndex -= 1;
			}
		} 
		


	}

	public static void clear() {
		grimFile = null;
		realGCList.clear();
		checkTimer = 0;
		Main.lem.doResetTimers();
		Main.lem.shutdown();

	}

	public static void clearAFloor(ServerLevel level, Random rand, MutableBlockPos airPos, int fy, int height,
			boolean first) {
		int posX = airPos.getX();
		int posZ = airPos.getZ();

		// bigger to destroy obstacles around tower.
		for (int fx = -getGrimRadius() - 1; fx <= getGrimRadius()+ 1; fx++) {
			for (int fz = -getGrimRadius() - 1; fz <= getGrimRadius()+ 1; fz++) {
				airPos.setX(posX + fx);
				airPos.setZ(posZ + fz);
				level.setBlock(airPos, Blocks.CAVE_AIR.defaultBlockState(), 0);
			}
		}
		airPos.setX(posX);
		airPos.setZ(posZ);
	}

	private static void createHeartLoot(ServerLevel level, BlockPos pos) {
		level.setBlockAndUpdate(pos.below(), Blocks.GRAY_SHULKER_BOX.defaultBlockState());
		RandomizableContainerBlockEntity.setLootTable(level, level.random, pos.below(),
				BuiltInLootTables.NETHER_BRIDGE);
		ItemStack itemStackToDrop;
		ShulkerBoxBlockEntity s = (ShulkerBoxBlockEntity) level.getBlockEntity(pos.below());
		double bootsSpeed = 0.04D + level.getRandom().nextDouble() * 0.06D;
		AttributeModifier am = new AttributeModifier(ITEM_SPEED_UUID, "hfspeed", bootsSpeed,
				AttributeModifier.Operation.ADDITION);
		itemStackToDrop = new ItemStack(Items.DIAMOND_BOOTS, (int) 1);
		itemStackToDrop.addAttributeModifier(Attributes.MOVEMENT_SPEED, am, EquipmentSlot.FEET);

		s.setItem(1, itemStackToDrop);
	}

	private static void decorateCitadel(ServerLevel level, BlockPos bottomPos, int top, int bottom) {

		doBuildDoor(level, bottom, top, level.getRandom(), bottomPos.above());
		addCorners(level, bottomPos, -1);
		addCorners(level, bottomPos, top - bottom - 1);
		buildRoofBalconies(level, bottomPos.above(top - bottom ));
		decorateRoof(level, bottom, bottomPos, top);


	}

	private static void decorateRoof(ServerLevel level, int bottom, BlockPos bottomPos, int top) {
		level.setBlockAndUpdate(bottomPos.above(top - bottom + 3), Blocks.GLOWSTONE.defaultBlockState());
		level.setBlock(bottomPos.above(top - bottom + 1), Blocks.ANCIENT_DEBRIS.defaultBlockState(), 0);
		level.setBlock(bottomPos.above(top - bottom + 2), Blocks.LAVA.defaultBlockState(), 0);
	}

	private static void doBuildDoor(ServerLevel level, int bottom, int top, Random rand, BlockPos bottomPos) {
		int side = level.getRandom().nextInt(4);
		int grimRadius = getGrimRadius();
		if (side == 0) {
			doBuildDoorColumn(level, bottomPos.south(grimRadius+1).east(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius+1),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius+1).west(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius+2).east(),AIR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius+2),AIR);
			doBuildDoorColumn(level, bottomPos.south(grimRadius+2).west(),AIR);

		} else		if (side == 1) {
			doBuildDoorColumn(level, bottomPos.north(grimRadius+1).east(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius+1),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius+1).west(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius+2).east(),AIR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius+2),AIR);
			doBuildDoorColumn(level, bottomPos.north(grimRadius+2).west(),AIR);
		} else		if (side == 2) {
			doBuildDoorColumn(level, bottomPos.east(grimRadius+1).north(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius+1),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius+1).south(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius+2).north(),AIR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius+2),AIR);
			doBuildDoorColumn(level, bottomPos.east(grimRadius+2).south(),AIR);
		} else		if (side == 3) {
			doBuildDoorColumn(level, bottomPos.west(grimRadius+1).north(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius+1),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius+1).south(),GRIM_GATE_DOOR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius+2).north(),AIR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius+2),AIR);
			doBuildDoorColumn(level, bottomPos.west(grimRadius+2).south(),AIR);
		}
	}

	private static void doBuildDoorColumn(ServerLevel level, BlockPos doorColPos, BlockState blockState) {
		level.setBlock(doorColPos, blockState, 0);
		level.setBlock(doorColPos.above(1), blockState, 0);
		level.setBlock(doorColPos.above(2), blockState, 3);
	}

	private static int getCitadelBottom(ServerLevel level, BlockPos pos) {
		int bottom = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
		if (bottom > level.getMaxBuildHeight() - 63)
			bottom = level.getMaxBuildHeight() - 63;
		if (bottom < level.getMinBuildHeight())
			bottom = level.getMinBuildHeight();
		return bottom;
	}

	public static String getCitadelListAsString() {
		return realGCList.toString();
	}


	private static int getCitadelTop(ServerLevel level, BlockPos pos) {
		return getCitadelBottom(level, pos) + 62;
	}

	public static int getClosestGrimCitadelDistanceSq(BlockPos pos) {
		int closestSq = Integer.MAX_VALUE;

		for (BlockPos b : realGCList) {
			closestSq = Math.min((int) b.distSqr(pos), closestSq);
		}

		return closestSq;
	}

	public static BlockPos getClosestGrimCitadelPos(BlockPos pos) {
		BlockPos grimPos = pos;
		int closestSqr = Integer.MAX_VALUE;
		for (BlockPos b : realGCList) {
			int nextSqr = (int) b.distSqr(pos);
			if (closestSqr > nextSqr) {
				closestSqr = nextSqr;
				grimPos = b;
			}
		}
		if (grimPos == pos) {
			return null;
		}
		return grimPos;
	}

	public static Vec3 getDirectionGrimCitadel(BlockPos pos) {
		int closestSq = Integer.MAX_VALUE;
		BlockPos cPos = pos;
		for (BlockPos b : realGCList) {
			closestSq = Math.min((int) b.distSqr(pos), closestSq);
			cPos = b;
		}
		if (closestSq != Integer.MAX_VALUE) {
			Vec3 v = new Vec3(cPos.getX() - pos.getX(), cPos.getY() - pos.getY(), cPos.getZ() - pos.getZ());
			return v.normalize();
		}
		return null;
	}

	public static int getFarthestGrimCitadelDistanceSq(BlockPos pos) {
		int farthestSq = 0;

		for (BlockPos b : realGCList) {
			farthestSq = Math.max((int) b.distSqr(pos), farthestSq);
		}

		return farthestSq;
	}

	public static List<Block> getFloorBlocks() {
		return floorBlocks;
	}

	public static int getGrimRadius() {
		return MyConfig.getGrimCitadelsRadius();
	}

	public static List<Block> getProtectedBlocks() {
		return protectedBlocks;
	}

	public static int getRandomGrimCitadelDistanceSq(ServerLevel level, BlockPos pos) {
		int r = level.getRandom().nextInt(realGCList.size());
		int distSq = (int) pos.distSqr(realGCList.get(r));
		return distSq;
	}

	public static int getValidRandomFloorOffset(Random rand) {
		int r = rand.nextInt(getGrimRadius() - 2)+2;
		if (rand.nextBoolean()) {
			return r;
		}
			return -r;
	}

	public static boolean isGrimCitadelFloorHeight(int fy) {
		return fy % 4 == 0;
	}

	public static boolean isInGrimProtectedArea(BlockPos eventPos) {
		if (grimBonusDistSqr == 0)
			grimBonusDistSqr = MyConfig.getGrimCitadelBonusDistanceSq();

		if (getClosestGrimCitadelDistanceSq(eventPos) > grimBonusDistSqr)
			return false;
		BlockPos grimPos = getClosestGrimCitadelPos(eventPos);
		int protectedDistance = GrimCitadelManager.getGrimRadius() + 21;
		if (grimPos != null) {
			int xAbs = Math.abs(eventPos.getX() - grimPos.getX());
			int zAbs = Math.abs(eventPos.getZ() - grimPos.getZ());
			int yOffset = eventPos.getY() - grimPos.getY();
			if (yOffset < protectedDistance) {
				yOffset = protectedDistance;
			}
			// check grim tower protected airspace
			if ((xAbs <= protectedDistance) && (zAbs <= protectedDistance) && (eventPos.getY() > grimPos.getY() + -8)) {
				if ((xAbs > getGrimRadius()) || (zAbs > getGrimRadius())) {
					return true;
				}
			}
		}

		return false;
	}

	// File Section: Read and write grimcitadel data.
	public static void load(MinecraftServer server) {

//	       File file = new File(
//	               "C:\\Users\\pankaj\\Desktop\\test.txt");
//	    
//	           // Note:  Double backquote is to avoid compiler
//	           // interpret words
//	           // like \test as \t (ie. as a escape sequence)
//	    
//	           // Creating an object of BufferedReader class

		File file1 = server.getWorldPath(LevelResource.ROOT).toFile();
		grimFile = new File(file1, "data/grimcitadels.dat");
		if (grimFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(grimFile));
				readData(br);
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Iterator<BlockPos> iter = MyConfig.getGrimCitadelsBlockPosList().iterator();
			while (iter.hasNext()) {
				BlockPos pos = iter.next();
				realGCList.add(pos);
			}
		}
	}

	private static void makeGrimCitadel(ServerLevel level, int bottom, BlockPos pos) {
		Utility.debugMsg(1, pos, "Creating a GrimCitadel at : " + pos);

		Random rand = level.getRandom();
		BlockPos bottomPos = new BlockPos(pos.getX(), bottom, pos.getZ());
		int top = getCitadelTop(level, pos);

		buildCitadelFoundation(level, bottomPos);

		buildCitadelFloors(level, bottom, top, rand, bottomPos);

		decorateCitadel(level, bottomPos, top, bottom);
	}

	// note: this may try air outside tower.
	public static int makeOneHoleInGrimCitadelFloor(Level level, BlockPos pos, int i, Random rand) {
		int x = rand.nextInt(getGrimRadius() * 4 + 1) - getGrimRadius() * 2;
		int z = rand.nextInt(getGrimRadius() * 4 + 1) - getGrimRadius() * 2;
		System.out.println (x+", "+ pos.getY()+", "+z);
		BlockPos fPos = new BlockPos(pos.getX()+x,pos.getY(),pos.getZ()+z);
		Block b = level.getBlockState(fPos).getBlock();
		if (GrimCitadelManager.getFloorBlocks().contains(level.getBlockState(fPos).getBlock())) {
			level.setBlock(fPos, Blocks.CAVE_AIR.defaultBlockState(), 3);
			i += 1;
		}
		return i;
	}


	// Note: This doesn't know where grim heart is.  It's random around a position.
	// But it only changes grim citadel floor blocks.
	public static void makeSeveralHolesInFloor(Level level, BlockPos pos) {
		int i = 0;
		int passes = 0;
		Random rand = level.getRandom();
		while(i < GrimCitadelManager.getGrimRadius() && (passes < getGrimRadius() * 2 + 1)) {
			// TODO also need to pass pos in here. its looking at 3,131,4 not at tower.
			i = makeOneHoleInGrimCitadelFloor(level, pos, i, rand);
			passes++; // ensure exit if it doesn't find floor blocks..
		}
	}

	private static void populateFloor(ServerLevel level, BlockPos pos, int fy) {
		BlockPos savePos = pos.above(+1);
		int fx = getValidRandomFloorOffset(level.random);
		int fz = getValidRandomFloorOffset(level.random);
		boolean livingfloor = false;

		if (fy % 12 == 0) {
			if (level.getBlockState(savePos.south(fx).east(fz).below()).getBlock() != ModBlocks.GRIM_GATE) {
				level.setBlock(savePos.south(fx).east(fz), Blocks.CHEST.defaultBlockState(), 3);
				RandomizableContainerBlockEntity.setLootTable(level, level.random, savePos.south(fx).east(fz),
						BuiltInLootTables.NETHER_BRIDGE);
			}
			livingfloor = true;
		}

		if (!livingfloor) {
			populateUndeadFloor(level, savePos);
		} else {
			populateLivingFloor(level, savePos);
		}

	}

	private static void populateLivingFloor(ServerLevel level, BlockPos savePos) {
		boolean isPersistant = true;
		boolean isBaby = true;
		Utility.populateEntityType(EntityType.PIGLIN_BRUTE, level, savePos, 3, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.HOGLIN, level, savePos, 2, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.BLAZE, level, savePos, 3, -1, isPersistant, isBaby);
	}



	private static void populateUndeadFloor(ServerLevel level, BlockPos savePos) {
		boolean isPersistant = true;
		boolean isBaby = true;
		Utility.populateEntityType(EntityType.ZOMBIFIED_PIGLIN, level, savePos, 4, 0, isPersistant, isBaby);
		Utility.populateEntityType(EntityType.BLAZE, level, savePos, 3, -1, isPersistant, isBaby);
		if (!Utility.populateEntityType(EntityType.WITHER_SKELETON, level, savePos, 5, -1, isPersistant, isBaby)) {
			Utility.populateEntityType(EntityType.ZOGLIN, level, savePos, 2, 0, isPersistant, isBaby);
		}
	}

	public static void readData(BufferedReader br) {
		String line;
		int linecount = 0;
		realGCList.clear();

		try {
			while ((line = br.readLine()) != null) {
				linecount++;
				StringTokenizer st = new StringTokenizer(line, ",");
				try {
					int x = Integer.parseInt(st.nextToken().trim());
					int y = Integer.parseInt(st.nextToken().trim());
					int z = Integer.parseInt(st.nextToken().trim());
					realGCList.add(new BlockPos(x, y, z));
				} catch (Exception e) {
					if (!(line.isEmpty())) {
						Utility.debugMsg(0, "grimcitadels.data line " + linecount + " is malformed.");
					} else if (MyConfig.getDebugLevel() > 0) {
						Utility.debugMsg(0,
								"Harder Farther: Warning blank line at " + linecount + "th line of grimcitadels.dat");
					}
				}
			}
		} catch (Exception e) {
			Utility.debugMsg(0, "grimcitadels.dat not found in sudirectory saves/world/data.");
			// e.printStackTrace();
		}

	}
	
	public static void removeHeart(ServerLevel level, BlockPos pos) {

		Iterator<BlockPos> iter = realGCList.iterator();
		while (iter.hasNext()) {
			BlockPos gPos = iter.next();
			if ((gPos.getX() == pos.getX()) && gPos.getZ() == pos.getZ()) {
				iter.remove();
				addOptionalNewHearts(level, gPos);
				save();
				updateGCLocationsToClients(level);
				createHeartLoot(level, pos);
				break;
			}
		}
	}

	public static void save() {
		if (grimFile == null)
			return;
		try {
			FileOutputStream fos = new FileOutputStream(grimFile);
			writeData(fos);
			fos.close();
			Utility.debugMsg(1, "grimfile saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Used when a player logs in.
	public static void sendAllGCPosToClient(ServerPlayer sp) {
		Network.sendToClient(new SyncAllGCWithClientPacket(realGCList), sp);
	}

	private static void updateGCLocationsToClients(ServerLevel level) {
		List<BlockPos> gcL = GrimCitadelManager.realGCList;

		List<ServerPlayer> allPlayers = level.getServer().getPlayerList().getPlayers();
		Iterator<ServerPlayer> apI = allPlayers.iterator();
		// v = new SAGCP(s,gcl)
		SyncAllGCWithClientPacket msg = new SyncAllGCWithClientPacket(gcL);
		while (apI.hasNext()) { // sends to all players online.
			Network.sendToClient(msg, apI.next());
		}
	}
	
	public static void writeData(FileOutputStream fos) {
		PrintStream p = null;
		p = new PrintStream(fos, true);
		Iterator<BlockPos> iter = realGCList.iterator();
		while (iter.hasNext()) {
			BlockPos pos = iter.next();
			p.println(pos.getX() + "," + pos.getY() + "," + pos.getZ());
		}
	}

}
