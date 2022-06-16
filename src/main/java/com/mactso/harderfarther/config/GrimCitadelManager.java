package com.mactso.harderfarther.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.network.SyncAllGCWithClientPacket;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class GrimCitadelManager {
	private static long checkTimer = 0;
	private static long builtSpikeTimer;
	private static int currentCitadelIndex = -1;

	public static List<BlockPos> realGCList = new ArrayList<BlockPos>();

	private static int grimRadius = 4;
	private static BlockState BASALT = Blocks.BASALT.defaultBlockState();
	private static BlockState POLISHEDBASALT = Blocks.POLISHED_BASALT.defaultBlockState();
	private static BlockState BLACKSTONE = Blocks.BLACKSTONE.defaultBlockState();
	private static BlockState GILDED_BLACKSTONE = Blocks.GILDED_BLACKSTONE.defaultBlockState();
	private static BlockState WINDOW = Blocks.TINTED_GLASS.defaultBlockState();
	private static BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
	private static BlockState FIRE = Blocks.FIRE.defaultBlockState();
	private static BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.defaultBlockState();

	private static File grimFile;
	private static UUID ITEM_SPEED_UUID = UUID.fromString("4ce59996-ed35-11ec-8ea0-0242ac120002");

	private static void makeGrimCitadel(ServerLevel level, int bottom, BlockPos pos) {
		Block b = Blocks.BASALT;
		Random rand = level.getRandom();
		BlockPos bottomPos = new BlockPos(pos.getX(), bottom, pos.getZ());
		int top = getCitadelTop(level, pos);

		buildCitadelFoundation(level, bottomPos);

		buildCitadelFloors(level, bottom, top, rand, bottomPos);

		decorateCitadel(level, bottomPos, top, bottom);

		level.setBlockAndUpdate(bottomPos.above(top - bottom + 2), Blocks.GLOWSTONE.defaultBlockState());
		level.setBlock(bottomPos.above(top - bottom), Blocks.ANCIENT_DEBRIS.defaultBlockState(), 0);
		level.setBlock(bottomPos.above(top - bottom + 1), Blocks.LAVA.defaultBlockState(), 0);

		builtSpikeTimer = level.getGameTime() + 21;

	}

	private static void buildCitadelFloors(ServerLevel level, int bottom, int top, Random rand, BlockPos bottomPos) {
		MutableBlockPos floorPos = new MutableBlockPos();
		floorPos.set(bottomPos);

		boolean first = true;
		for (int fy = fy = top - bottom; fy >= 0; fy--) {
			if (fy % 4 == 0) {
				floorPos.setY(bottomPos.getY() + fy);

				buildAFloor(level, rand, floorPos, fy, top - bottom, first);

				if (first)
					first = false;

				if (fy > 8) {
					buildFloorBalcony(level, bottomPos, fy, rand.nextInt(6));
				}
			}
			buildOutsideWall(level, bottomPos, fy, top - bottom);
			buildCore(level, bottomPos, fy);

		}
	}

	private static void buildDoor(ServerLevel level, int bottom, int top, Random rand, BlockPos bottomPos) {
		if (level.getRandom().nextBoolean() == true) {
			level.setBlock(bottomPos.south(grimRadius + 1), Blocks.AIR.defaultBlockState(), 0);
			level.setBlock(bottomPos.south(grimRadius + 1).above(1), Blocks.AIR.defaultBlockState(), 0);
			level.setBlock(bottomPos.south(grimRadius + 1).above(2), Blocks.AIR.defaultBlockState(), 0);
		} else {
			level.setBlock(bottomPos.north(grimRadius + 1), Blocks.AIR.defaultBlockState(), 0);
			level.setBlock(bottomPos.north(grimRadius + 1).above(1), Blocks.AIR.defaultBlockState(), 0);
			level.setBlock(bottomPos.north(grimRadius + 1).above(2), Blocks.AIR.defaultBlockState(), 0);
		}
	}

	private static void decorateCitadel(ServerLevel level, BlockPos bottomPos, int top, int bottom) {

		buildDoor(level, bottom, top, level.getRandom(), bottomPos);
		addCorners(level, bottomPos, -1);
		addCorners(level, bottomPos, top - bottom - 2);
		buildRoofBalconies(level, bottomPos.above(top - bottom - 1));

	}

	private static void buildFloorBalcony(ServerLevel level, BlockPos bottomPos, int fy, int side) {

		BlockPos tempPos;
		Random rand = level.getRandom();
		if (side > 3)
			return;

		int balconyRadius = grimRadius + 2;
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

	private static void buildRoofBalconies(ServerLevel level, BlockPos roofPos) {
		int balconyRadius = grimRadius + 2;
		buildBalcony(level, roofPos.north(balconyRadius));
		buildBalcony(level, roofPos.south(balconyRadius));
		buildBalcony(level, roofPos.east(balconyRadius));
		buildBalcony(level, roofPos.west(balconyRadius));
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

	private static void addCorners(ServerLevel level, BlockPos bottomPos, int offset) {
		BlockState bs = POLISHEDBASALT;

		addOneCorner(level, bottomPos.north(grimRadius + 2).west(grimRadius + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-grimRadius - 1).west(grimRadius + 2), offset, bs);
		addOneCorner(level, bottomPos.north(-grimRadius - 1).west(-grimRadius - 1), offset, bs);
		addOneCorner(level, bottomPos.north(grimRadius + 2).west(-grimRadius - 1), offset, bs);
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
		int wallRadius = grimRadius + 1;
		for (int fx = -wallRadius; fx <= wallRadius; fx++) {
			for (int fz = -wallRadius; fz <= wallRadius; fz++) {
				boolean corner = false;
				if ((Math.abs(fx) == wallRadius) && (Math.abs(fz) == wallRadius)) {
					corner = true;
				}
				if ((Math.abs(fx) == wallRadius) || (Math.abs(fz) == wallRadius)) {
					mPos.setX(posX + fx);
					mPos.setZ(posZ + fz);
					if (corner) {
						level.setBlock(mPos, NETHERRACK, 0);
					} else if ((rand.nextInt(15) < 1) && ((fy + 2) % 4 == 0)) {
						level.setBlock(mPos, WINDOW, 0);
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

		BlockState bs = GILDED_BLACKSTONE;
		float roll = level.getRandom().nextFloat();

		if (roll > 0.95f) {
			bs = POLISHEDBASALT;
		} else if (roll > 0.85f) {
			bs = BLACKSTONE;
		}
		int ew = (level.getRandom().nextInt(grimRadius >> 1)) - 1;
		int ns = (level.getRandom().nextInt(grimRadius >> 1)) - 1;
		level.setBlock(pos.east(ew).north(ns).above(fy), bs, 0);
		if (level.getRandom().nextFloat() > 0.66) {
			level.setBlock(pos.east(ew).north(ns).above(fy + 1), NETHERRACK, 0);
			if (level.getRandom().nextFloat() > 0.33f) {
				level.setBlock(pos.east(ew).north(ns).above(fy + 2), BROWN_MUSHROOM, 3);
			}
		}

	}

	public static void buildAFloor(ServerLevel level, Random rand, MutableBlockPos floorPos, int fy, int height,
			boolean first) {
		int updateFlag = 0;
		if (first) {
			updateFlag = 3;
		}

		int posX = floorPos.getX();
		int posZ = floorPos.getZ();

		for (int fx = -grimRadius; fx <= grimRadius; fx++) {
			for (int fz = -grimRadius; fz <= grimRadius; fz++) {
				floorPos.setX(posX + fx);
				floorPos.setZ(posZ + fz);
				int r = rand.nextInt(height);
				if (r > (fy / 3)) {
					level.setBlock(floorPos, BASALT, 0);
				} else if (r > 2 * (fy / 3)) {
					level.setBlock(floorPos, POLISHEDBASALT, updateFlag);
				} else {
					level.setBlock(floorPos, NETHERRACK, updateFlag);
				}
			}
		}

		int fx = getValidRandomFloorSpot(rand);
		int fz = getValidRandomFloorSpot(rand);

		floorPos.setX(posX + fx);
		floorPos.setZ(posZ + fz);
		if (rand.nextInt(10) == 0) {
			level.setBlock(floorPos, Blocks.AIR.defaultBlockState(), 3);
		} else {
			level.setBlock(floorPos, Blocks.HONEY_BLOCK.defaultBlockState(), 3);
		}

		floorPos.setX(posX);
		floorPos.setZ(posZ);

		populateFloor(level, floorPos, fy);

	}

	private static int getValidRandomFloorSpot(Random rand) {
		int fx = rand.nextInt(grimRadius * 2 - 2) - (grimRadius);
		if (fx >= -1)
			fx += 3; // ignore 3 squares in middle.
		return fx;
	}

	private static void populateFloor(ServerLevel level, BlockPos pos, int fy) {
		BlockPos savePos = pos.above(+1);
		int fx = getValidRandomFloorSpot(level.random);
		int fz = getValidRandomFloorSpot(level.random);

		if (fy % 12 == 0) {
			level.setBlock(savePos.south(fx).east(fz), Blocks.CHEST.defaultBlockState(), 3);
			RandomizableContainerBlockEntity.setLootTable(level, level.random, savePos.south(fx).east(fz),
					BuiltInLootTables.NETHER_BRIDGE);
		}
		int numZP = level.random.nextInt(4);
		for (int i = 0; i < numZP; i++) {
			Mob e = EntityType.ZOMBIFIED_PIGLIN.spawn(level, null, null, null, savePos.north(2).west(2),
					MobSpawnType.NATURAL, true, true);
			e.setPersistenceRequired();
		}
		numZP = level.random.nextInt(2);
		for (int i = 0; i < numZP; i++) {
			Mob e = EntityType.HOGLIN.spawn(level, null, null, null, savePos.north(2).west(2), MobSpawnType.NATURAL,
					true, true);
			e.setPersistenceRequired();
		}
		numZP = level.random.nextInt(4) - 2;
		for (int i = 0; i < numZP; i++) {
			Mob e = EntityType.WITHER_SKELETON.spawn(level, null, null, null, savePos.north(2).west(2),
					MobSpawnType.NATURAL, true, true);
			e.setPersistenceRequired();
		}

	}

	public static int getGrimCitadelDistance(BlockPos pos) {
		int closest = Integer.MAX_VALUE;

		for (BlockPos b : realGCList) {
			closest = Math.min((int) b.distSqr(pos), closest);
		}
		return closest;
	}

	public static int getGrimRange(BlockPos pos) {
		Iterator<BlockPos> iter = realGCList.iterator();

		while (iter.hasNext()) {
			BlockPos grimPos = iter.next();
			if (grimPos.distManhattan(pos) <= getGrimCitadelDistance(grimPos)) {
				return grimPos.distManhattan(pos);
			}
		}

		return Integer.MAX_VALUE;

	}

	public static void checkCleanUpCitadels(ServerLevel level) {

		if (level.isClientSide)
			return;

		if (checkTimer > level.getGameTime())
			return;

		if (builtSpikeTimer > level.getGameTime())
			return;

		Iterator<BlockPos> i = realGCList.iterator();

		if (realGCList.isEmpty()) {
			return;
		}

		List<BlockPos> list = realGCList;
		int y = list.size();
		currentCitadelIndex++;
		if (currentCitadelIndex >= realGCList.size()) {
			currentCitadelIndex = 0;
		}

		BlockPos pos = realGCList.get(currentCitadelIndex);
		ChunkAccess chunk = level.getChunk(pos);
		Set<BlockPos> ePosSet = chunk.getBlockEntitiesPos();
		boolean foundHeart = false;
		for (BlockPos ePos : ePosSet) {
			if ((ePos.getX() == pos.getX()) && ePos.getZ() == pos.getZ()) {
				if (level.getBlockState(ePos).getBlock() == ModBlocks.GRIM_HEART) {
					foundHeart = true;
					System.out.println("foundHeart"); // TODO remove
					break;
				}
			}
		}
		int x = 3;
		if (chunk.getInhabitedTime() < 600) { // heart not created yet
			if (!foundHeart) {
				Utility.debugMsg(1, pos, "Creating New Citadel.");
				int bottom = getCitadelBottom(level, pos);
				makeGrimCitadel(level, bottom, pos);
				BlockPos heartPos = new BlockPos(pos.getX(), bottom + 30, pos.getZ());
				level.setBlock(heartPos, ModBlocks.GRIM_HEART.defaultBlockState(), 3);
				realGCList.set(currentCitadelIndex, heartPos);
				save();
				updateGCLocationsToClients(level);
			}
		} else { // heart destroyed / gone / taken.
			if (!foundHeart) {
				realGCList.remove(currentCitadelIndex);
				save();
				updateGCLocationsToClients(level);
				currentCitadelIndex -= 1;
			}
		}

		checkTimer = level.getGameTime() + 600; // TODO change back to 2000
	}

	public static void removeHeart(ServerLevel level, BlockPos pos) {

		Iterator<BlockPos> iter = realGCList.iterator();

		while (iter.hasNext()) {
			BlockPos gPos = iter.next();
			if ((gPos.getX() == pos.getX()) && gPos.getZ() == pos.getZ()) {
				iter.remove();
				break;
			}
		}
		save();
		updateGCLocationsToClients(level);
		level.setBlockAndUpdate(pos, Blocks.GRAY_SHULKER_BOX.defaultBlockState());
		ItemStack itemStackToDrop;
		AttributeModifier am = new AttributeModifier(ITEM_SPEED_UUID, "hfspeed", 0.1d,
				AttributeModifier.Operation.ADDITION);
		itemStackToDrop = new ItemStack(Items.DIAMOND_BOOTS, (int) 1);
		itemStackToDrop.addAttributeModifier(Attributes.MOVEMENT_SPEED, am, EquipmentSlot.FEET);
		ItemEntity ie = new ItemEntity(level, pos.getX(), pos.getY()+1, pos.getZ(), itemStackToDrop);
		level.addFreshEntity(ie);
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

	private static void buildCitadelFoundation(ServerLevel level, BlockPos pos) {

		MutableBlockPos mPos = new MutableBlockPos();
		mPos.setY(pos.getY());
		int posX = pos.getX();
		int posY = pos.getY();
		int posZ = pos.getZ();

		for (int fx = -grimRadius - 3; fx < grimRadius + 3 + 1; fx++) {
			mPos.setX(fx + posX);
			for (int fz = -grimRadius - 3; fz < grimRadius + 3 + 1; fz++) {
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

	private static int getCitadelBottom(ServerLevel level, BlockPos pos) {
		int bottom = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ());
		if (bottom > level.getMaxBuildHeight() - 63)
			bottom = level.getMaxBuildHeight() - 63;
		if (bottom < level.getMinBuildHeight())
			bottom = level.getMinBuildHeight();
		return bottom;
	}

	private static int getCitadelTop(ServerLevel level, BlockPos pos) {
		return getCitadelBottom(level, pos) + 60;
	}

	// Network Section
	//
	// utility: sends actual message to one client to remove one GC from hand.

	// Used when a player logs in.
	public static void sendAllGCPosToClient(ServerPlayer sp) {
		Network.sendToClient(new SyncAllGCWithClientPacket(realGCList), sp);
	}

	// File Section: Read and write grimcitadel data.
	public static void load(MinecraftServer server) {

		File file1 = server.getWorldPath(LevelResource.ROOT).toFile();
		grimFile = new File(file1, "data/grimcitadels.dat");
		if (grimFile.exists()) {
			try {
				FileInputStream fis = new FileInputStream(grimFile);
				readData(fis);
				fis.close();
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

	public static void clear() {
		grimFile = null;
	}

//	public static void initData (FileOutputStream fos) {
//		PrintStream p = null;
//		p = new PrintStream(fos,true);
//		Iterator<BlockPos> iter = MyConfig.getGrimCitadelsBlockPosList().iterator();
//		while(iter.hasNext()) {
//			BlockPos pos = iter.next();
//			realGCList.add(pos);
//			p.println(pos.getX()+","+pos.getY()+","+pos.getZ());
//		}
//	}

	public static void save() {
		if (grimFile == null)
			return;
		try {
			FileOutputStream fos = new FileOutputStream(grimFile);
			writeData(fos);
			fos.close();
			System.out.println("grimfile saved.");
		} catch (IOException e) {
			e.printStackTrace();
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

	public static void readData(FileInputStream fis) {
		String line;
		int linecount = 0;

		try (InputStreamReader input = new InputStreamReader(
				new FileInputStream("config/spawnbalanceutility/BiomeMobWeight.csv"))) {
			BufferedReader br = new BufferedReader(input);
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
						System.out.println("grimCitadel line " + linecount + " is malformed.");
					} else if (MyConfig.getDebugLevel() > 0) {
						System.out.println(
								"GrimCitadel Warning blank line at " + linecount + "th line of BiomeMobWeight.csv.");
					}
				}
			}
			input.close();
		} catch (Exception e) {
			System.out.println("BiomeMobWeight.csv not found in subdirectory SpawnBalanceUtility");
			// e.printStackTrace();
		}

	}

}
