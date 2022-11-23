package com.mactso.harderfarther.utility;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.manager.GrimCitadelManager;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.AABB;

public class Glooms {

	static List<Block> gloomHungerBlocks = Arrays.asList(Blocks.WATER, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.SAND,
			Blocks.NETHERRACK);

	public final static int HARD = 0;
	public final static int GRIM = 1;
	public final static int TIME = 2;

	private final static int AMP_1 = 0;
	static long pigTimer = 0;
	static long fishTimer = 0;

	static long villagerTimer = 0;
	static long phantomTimer = 0;
	static long invisTimer = 0;
	static long skeletonTimer = 0;
	static long spiderTimer = 0;
	static long spiderWebTimer = 0;
	static long zoglinTimer = 0;
	static long zombifiedPiglinTimer = 0;
	static long zombieTimer = 0;
	static long witherSkeletonTimer = 0;
	static long creeperTimer = 0;

	public static void doGloomPigs(Pig pig, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (MyConfig.isGrimEffectPigs()) {
			
			if (pigTimer < gameTime) {
				pigTimer = gameTime + 1800;
				float pitch = 0.8f;
				int roll = serverLevel.getRandom().nextInt(100);
				if (roll < 10) {
					Utility.updateEffect(pig, 3, MobEffects.WITHER, 120);
					serverLevel.playSound(null, pos, SoundEvents.PIGLIN_ADMIRING_ITEM, SoundSource.AMBIENT, 2.20f,
							pitch);
					Utility.populateEntityType(EntityType.PIGLIN, serverLevel, pos, 1, 0, pig.isBaby());
				} else if (roll < 80) {
					Utility.updateEffect(pig, 3, MobEffects.WITHER, 120);
					serverLevel.playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.AMBIENT, 2.20f,
							pitch);
					Utility.populateEntityType(EntityType.HOGLIN, serverLevel, pos, 1, 0, pig.isBaby());
				} else {
					serverLevel.playSound(null, pos, SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.AMBIENT, 4.20f,
							pitch);
					Utility.populateEntityType(EntityType.ZOMBIFIED_PIGLIN, serverLevel, pos, 1, 0, true, pig.isBaby()); 
																															
				}
			}
		}
	}

	public static void doGloomDeadBranches(LivingEntity le, BlockPos pos, Level level) {
		Utility.debugMsg(2, pos, "doSpreadDeadBranches");
		if (MyConfig.isGrimEffectTrees()) {
			if (level.getBrightness(LightLayer.SKY, pos) > 10) {
				BlockPos deadBranchPos = level.getHeightmapPos(Types.MOTION_BLOCKING, pos);
				Block b = level.getBlockState(deadBranchPos.below()).getBlock();
				if (b instanceof LeavesBlock || b == Blocks.NETHER_WART_BLOCK) {
					if (b == ModBlocks.DEAD_BRANCHES || b == Blocks.NETHER_WART_BLOCK) {
						for (int i = 0; i <= 3; i++) {
							deadBranchPos = doSpreadOneDeadBranch(level, deadBranchPos);
						}
					} else {
						if (level.getRandom().nextInt(100) == 42) {
							level.setBlock(deadBranchPos, Blocks.NETHER_WART_BLOCK.defaultBlockState(), 3);
						} else {
							level.setBlock(deadBranchPos, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);
						}
					}
				}
			}
		}
	}

	private static BlockPos doSpreadOneDeadBranch(Level level, BlockPos pos) {
		Block b;
		BlockPos workPos = pos;
		int i = level.getRandom().nextInt(7);
		switch (i) {
		case 0:
			workPos = pos.north();
			break;
		case 1:
			workPos = pos.south();
			break;
		case 2:
			workPos = pos.east();
			break;
		case 3:
			workPos = pos.west();
			break;
		default:
			int r = 1 + level.getRandom().nextInt(2);
			workPos = pos.below(r);
		}
		b = level.getBlockState(workPos).getBlock();
		if ((b instanceof LeavesBlock) && (b != ModBlocks.DEAD_BRANCHES)) {
			level.setBlock(workPos, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);
		}
		return workPos;
	}

	public static void doGloomWaterAnimals(WaterAnimal we, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		// May later break this into different kinds of water animals or fish.
		doGloomWaterAnimal(we, pos, gameTime, serverLevel);
	}

	private static void doGloomWaterAnimal(WaterAnimal we, BlockPos pos, long gameTime, ServerLevel serverLevel) {

		if (fishTimer < gameTime) {
			if (!isDeepWaterUnderSky(we))
				return;

			fishTimer = gameTime + 600;
			List<Guardian> listG = serverLevel.getEntitiesOfClass(Guardian.class,
					new AABB(pos.north(32).west(32).above(8), pos.south(32).east(32).below(8)));
			if (listG.size() > 5)
				return;
			float pitch = 0.7f;
			Utility.populateEntityType(EntityType.GUARDIAN, serverLevel, pos, 1, 0);
		}
	}

	private static boolean isDeepWaterUnderSky(WaterAnimal we) {
		BlockPos pos = we.blockPosition();

		if (!we.getLevel().canSeeSkyFromBelowWater(pos))
			return false;
		Block bAbove = we.level.getBlockState(pos.above(12)).getBlock();
		Block bBelow = we.level.getBlockState(pos.below(12)).getBlock();
		if ((bAbove == Blocks.WATER) || (bBelow == Blocks.WATER)) {
			return true;
		}

		return false;
	}

	public static void doGlooms(ServerLevel serverLevel, long gameTime, float difficulty, LivingEntity le,
			int gloomType) {
		BlockPos pos = le.blockPosition();

		if (le instanceof ServerPlayer sp) {
			int amplitude = getEffectAmplitudeByDifficulty(difficulty);
			doGloomPlayer(sp, pos, serverLevel, difficulty, gloomType, amplitude);
		} else if (le instanceof Villager ve) {
			doGloomVillagers(ve, pos, gameTime, serverLevel);
		} else if (le instanceof WaterAnimal we) {
			Glooms.doGloomWaterAnimals(we, pos, gameTime, serverLevel);
		} else if (le instanceof Animal ae) {
			doGloomAnimals(ae, pos, gameTime, serverLevel);
		} else if (le instanceof Enemy) {
			doGloomMonsters(le, pos, gameTime, serverLevel);
		}

		Glooms.doGloomDeadBranches(le, pos, serverLevel);
	}

	private static int getEffectAmplitudeByDifficulty(float difficulty) {
		if (difficulty > Utility.Pct95)
			return 0;
		if (difficulty > Utility.Pct84)
			return 1;
		return 0;
	}

	public static void doGloomAnimals(Animal ae, BlockPos pos, long gameTime, ServerLevel level) {

		if (!(MyConfig.isGrimEffectAnimals()))
			return;

		if (level.getRandom().nextInt(400) < 9) {
			if (ae.getHealth() > 3) {
				Utility.updateEffect((LivingEntity) ae, 0, MobEffects.POISON, 10);
				BlockState bs = level.getBlockState(ae.blockPosition().below());
				if ((!isStoneOrWall(bs)) && (!GrimCitadelManager.getFloorBlocks().contains(bs.getBlock()))) {
					level.setBlock(ae.blockPosition().below(), Blocks.GRAVEL.defaultBlockState(), 3);
				}
			}
			doGloomGroundTransform(ae, level);
		}
		if (level.getRandom().nextInt(9000) == 51) {
			BlockPos firePos = level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos.north(2));
			level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
		}
		if (ae instanceof Pig) {
			Glooms.doGloomPigs((Pig) ae, pos, gameTime, level);
		}
	}

	
	
	public static boolean isStoneOrWall (BlockState bs) {
		if (bs.getBlock() == Blocks.COBBLESTONE) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_SLAB) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_WALL) return false;
		if (bs.getBlock() == Blocks.COBBLESTONE_STAIRS) return false;
		if (bs.getMaterial() == Material.STONE) return true;
		if (bs.getBlock() instanceof WallBlock) return true;
		return false;
	}
	
	
	
	public static void doGloomMobCreepers(LivingEntity le, long gameTime, ServerLevel serverLevel) {
		if (creeperTimer < gameTime) {
			creeperTimer = gameTime + 240;
			Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 960);
		}
	}

	public static void doGloomMobPhantoms(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (phantomTimer < gameTime) {
			phantomTimer = gameTime + 160;
			Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 640);
			if (serverLevel.getRandom().nextInt(6) == 1) {
				PrimedTnt tnt = EntityType.TNT.spawn(serverLevel, null, null, null, pos, MobSpawnType.NATURAL, true,
						true);
				tnt.setFuse(80);
			}
		}
	}

	public static void doGloomMobSkeletons(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (skeletonTimer < gameTime) {
			skeletonTimer = gameTime + 120;
//			Block b = serverLevel.getBlockState(pos).getBlock(); possible buff based on block standing on feature.
			Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
			Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 480);
			if (le instanceof WitherSkeleton) {
				Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 480);
			} else {
				if (serverLevel.getMaxLocalRawBrightness(pos) < 9) {
					if (witherSkeletonTimer < gameTime) {
						witherSkeletonTimer = gameTime + 1800;
						Utility.populateEntityType(EntityType.WITHER_SKELETON, serverLevel, le.blockPosition(), 1, 0);
					}
				}
			}
		}
	}

	public static void doGloomMobSpiders(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (spiderTimer < gameTime) {
			spiderTimer = gameTime + 80;
			Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
			Utility.updateEffect(le, 0, MobEffects.ABSORPTION, 480);
		}
		if (spiderWebTimer < gameTime) {
			spiderWebTimer = gameTime + 1200; // 1 per two minutes.
			if (Utility.isNotNearWebs(pos, serverLevel)) {
				le.level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
			}
		}
	}

	public static void doGloomMobZoglins(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zoglinTimer < gameTime) {
				zoglinTimer = gameTime + 500;
				doGloomGrassTransform(pos, serverLevel);
				doGloomGroundTransform(le, serverLevel);
			} else {
				zoglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	public static void doGloomMobZombies(LivingEntity le, long gameTime, ServerLevel serverLevel) {
		if (zombieTimer < gameTime) {
			zombieTimer = gameTime + 240;
			Utility.updateEffect(le, 1, MobEffects.REGENERATION, Utility.FOUR_SECONDS);
			Utility.updateEffect(le, AMP_1, MobEffects.FIRE_RESISTANCE, 720);
		}
	}

	public static void doGloomMobZombifiedPiglin(LivingEntity le, BlockPos pos, long gameTime,
			ServerLevel serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zombifiedPiglinTimer < gameTime) {
				zombifiedPiglinTimer = gameTime + 600;
				doGloomGrassTransform(pos, serverLevel);
				doGloomGroundTransform(le, serverLevel);
				if (serverLevel.getLevel().getRandom().nextInt(10000) == 42) {
					ZombifiedPiglin ze = (ZombifiedPiglin) le;
					ze.setAggressive(true);
				}
			} else {
				zombifiedPiglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	public static void doGloomMonsters(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (le instanceof AbstractSkeleton) {
			doGloomMobSkeletons(le, pos, gameTime, serverLevel);
		} else if (le instanceof Zombie) {
			doGloomMobZombies(le, gameTime, serverLevel);
		} else if (le instanceof Creeper) {
			doGloomMobCreepers(le, gameTime, serverLevel);
		} else if (le instanceof Phantom) {
			doGloomMobPhantoms(le, pos, gameTime, serverLevel);
		} else if (le instanceof Spider) {
			doGloomMobSpiders(le, pos, gameTime, serverLevel);
		} else if (le instanceof Zoglin) {
			doGloomMobZoglins(le, pos, gameTime, serverLevel);
		} else if (le instanceof ZombifiedPiglin) {
			doGloomMobZombifiedPiglin(le, pos, gameTime, serverLevel);
		}
	}

	public static void doGloomPlayer(ServerPlayer p, BlockPos pos, ServerLevel serverLevel, float difficulty,
			int gloomType, int amplitude) {

		Block b = serverLevel.getBlockState(pos).getBlock();
		Block bBelow = serverLevel.getBlockState(pos.below()).getBlock();
		if (gloomHungerBlocks.contains(b) || gloomHungerBlocks.contains(bBelow)) {
			Utility.updateEffect((LivingEntity) p, AMP_1, MobEffects.HUNGER, Utility.FOUR_SECONDS);
		}
		doGloomPlayerCurse(difficulty, gloomType, amplitude, p);
		if (GrimCitadelManager.isGCNear(difficulty)) {
			Utility.slowFlyingMotion(p);
			if (p.isFallFlying()) {
				Utility.updateEffect((LivingEntity) p, AMP_1, MobEffects.POISON, Utility.FOUR_SECONDS);
			}
		}
		if ((difficulty > Utility.Pct09) && (serverLevel.getRandom().nextInt(36000) == 42)) {
			BlockPos phantomPos = new BlockPos(pos.getX(),
					serverLevel.getHeightmapPos(Types.MOTION_BLOCKING, pos).getY() + 12, pos.getZ());
			Utility.populateEntityType(EntityType.PHANTOM, serverLevel, phantomPos, 1, 0);
		}
	}

	public static void doGloomVillagers(Villager ve, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (MyConfig.isGrimEffectVillagers()) {
			if (villagerTimer < gameTime) {
				villagerTimer = gameTime + 2400;
				Utility.populateEntityType(EntityType.WITCH, serverLevel, pos, 1, 0);
				Utility.updateEffect(ve, 9, MobEffects.WITHER, 240);
			}
		}
	}

	public static void doGloomGrassTransform(BlockPos pos, ServerLevel serverLevel) {
		BlockPos workPos = pos;
		if (serverLevel.getBlockState(pos.below()).getBlock() == Blocks.AIR) {
			workPos = pos.below();
		}
		if ((serverLevel.getBlockState(pos).getBlock() instanceof TallGrassBlock)
				|| (serverLevel.getBlockState(workPos).getBlock() instanceof DoublePlantBlock)) {

			Block b = Blocks.NETHER_SPROUTS;
			if (serverLevel.getBlockState(workPos).getBlock() == Blocks.TALL_GRASS) {
				b = Blocks.CRIMSON_ROOTS;
			} else if (serverLevel.getBlockState(workPos).getBlock() == Blocks.LARGE_FERN) {
				b = Blocks.WARPED_ROOTS;
			}
			serverLevel.setBlock(pos, b.defaultBlockState(), 3);
		}

	}

	public static void doGloomGroundTransform(LivingEntity le, ServerLevel level) {
		if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.COARSE_DIRT) {
			level.setBlock(le.blockPosition().below(), Blocks.NETHERRACK.defaultBlockState(), 3);
		}
		if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
			level.setBlock(le.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		}
	}

	public static void doGloomPlayerCurse(float difficulty, int gloomType, int amplitude, ServerPlayer sp) {

		Random rand = sp.getLevel().getRandom();
		boolean hasLifeHeart = sp.getInventory().contains(new ItemStack(ModItems.LIFE_HEART));

		if (hasLifeHeart) {
			if ((difficulty > Utility.Pct50) && (rand.nextInt(42) == 42)) {
				// System.out.println("regen");
				Utility.updateEffect((LivingEntity) sp, 0, MobEffects.REGENERATION, Utility.FOUR_SECONDS);
			}
		}
		doGrimPlayerCurses(difficulty, gloomType, amplitude, sp, hasLifeHeart);
		doTimePlayerCurses(difficulty, gloomType, amplitude, sp);
	}

	private static void doTimePlayerCurses(float difficulty, int gloomType, int amplitude, ServerPlayer sp) {
		if (gloomType == Glooms.TIME) {
			if (difficulty > Utility.Pct25)
				Utility.updateEffect((LivingEntity) sp, AMP_1, MobEffects.WEAKNESS, Utility.FOUR_SECONDS);
			if (difficulty > Utility.Pct75)
				Utility.updateEffect((LivingEntity) sp, amplitude, MobEffects.MOVEMENT_SLOWDOWN, Utility.FOUR_SECONDS);
		}
	}

	private static void doGrimPlayerCurses(float difficulty, int gloomType, int amplitude, ServerPlayer sp,
			boolean hasLifeHeart) {
		if (gloomType == Glooms.GRIM) {
			if (difficulty > Utility.Pct00)
				Utility.updateEffect((LivingEntity) sp, AMP_1, MobEffects.WEAKNESS, Utility.FOUR_SECONDS);
			if (hasLifeHeart) {
				if ((difficulty > Utility.Pct25) && (difficulty < Utility.Pct91))
					Utility.updateEffect((LivingEntity) sp, amplitude, MobEffects.MOVEMENT_SLOWDOWN,
							Utility.FOUR_SECONDS);
				if ((difficulty > Utility.Pct50) && (difficulty < Utility.Pct84))
					Utility.updateEffect((LivingEntity) sp, amplitude, MobEffects.DIG_SLOWDOWN, Utility.FOUR_SECONDS);

			} else {
				if (difficulty > Utility.Pct09)
					Utility.updateEffect((LivingEntity) sp, amplitude, MobEffects.MOVEMENT_SLOWDOWN,
							Utility.FOUR_SECONDS);
				if ((difficulty > Utility.Pct50) && (difficulty < Utility.Pct95))
					Utility.updateEffect((LivingEntity) sp, amplitude, MobEffects.DIG_SLOWDOWN, Utility.FOUR_SECONDS);
			}
		}
	}

	public static void doResetTimers() {
		pigTimer = 0;
		fishTimer = 0;

		villagerTimer = 0;
		phantomTimer = 0;
		invisTimer = 0;
		skeletonTimer = 0;
		spiderTimer = 0;
		spiderWebTimer = 0;
		zoglinTimer = 0;
		zombifiedPiglinTimer = 0;
		zombieTimer = 0;
		witherSkeletonTimer = 0;
		creeperTimer = 0;
	}

}
