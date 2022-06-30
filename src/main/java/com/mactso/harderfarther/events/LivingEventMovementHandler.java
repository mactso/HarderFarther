package com.mactso.harderfarther.events;

import java.util.Arrays;
import java.util.List;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {

	private int duration = 80; // four seconds.
	int gDist100 = 0; // default: 2000 meters (100%)
	int gDist70 = 0; // default: 1414 meters (70%)
	int gDist50 = 0; // default: 1000 meters (50%)
	int gDist30 = 0; // default: 707 meters (30%)
	int gDist25 = 0; // default: 500 meters (25%)
	int gDist16 = 0; // default: 352 meters (16%)
	int gDist12 = 0; // default: 250 meters (12%)
	int gDist09 = 0; // default: 176 meters (9%)
	int gDist05 = 0; // default: 125 meters (5%)
	boolean clientSynced = false;
	long phantomTimer = 0;
	long invisTimer = 0;
	long skeletonTimer = 0;
	long spiderTimer = 0;
	long zombieTimer = 0;
	long witherSkeletonTimer = 0;
	long creeperTimer = 0;
	static List<SoundEvent> gSoundEvents = Arrays.asList(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY,SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT,SoundEvents.LAVA_AMBIENT, SoundEvents.ZOMBIE_VILLAGER_STEP,SoundEvents.HOGLIN_ANGRY,SoundEvents.BLAZE_AMBIENT,SoundEvents.HOGLIN_AMBIENT,SoundEvents.AMBIENT_NETHER_WASTES_MOOD,SoundEvents.FIRE_AMBIENT, SoundEvents.WITHER_SKELETON_STEP);
	static List<SoundEvent> gAmbientSoundEvents = Arrays.asList(SoundEvents.AMBIENT_CAVE, SoundEvents.WITCH_AMBIENT, SoundEvents.WOLF_HOWL, SoundEvents.AMBIENT_CAVE, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD);
	static List<Block> gHungerBlocks = Arrays.asList(Blocks.WATER, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.SAND, Blocks.NETHERRACK);

	@SubscribeEvent
	public void livingEntityHandler(LivingUpdateEvent event) {
		LivingEntity le = event.getEntityLiving();
		BlockPos pos = le.blockPosition();
		Level level = le.level;
		long gameTime = level.getGameTime();
		initializeDistanceConstants();
		double closestGrimDistSq = GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos);

		if ((closestGrimDistSq > gDist100)) { // note also MAXINTEGER in here.
			return;
		}

		if (isTooCloseToFly(closestGrimDistSq)) {
			slowFlyingMotion(le);
		}

		if (gameTime % 10 != le.getId() % 10)
			return;

		// note: Synced when a player logs in and when hearts destroyed.

		int amplitude1 = 0;
		int amplitude2 = 0;

		if (level.isClientSide()) {
			if (le instanceof Player pe) {
				playOptionalSoundCues(pos, level, closestGrimDistSq, pe);
			}
			return;
		}

		ServerLevel serverLevel = (ServerLevel) level;

		if ((closestGrimDistSq >= gDist05) && (closestGrimDistSq <= gDist50)) {
			amplitude1 = 0;
			amplitude2 = 0;
		} else if (closestGrimDistSq < gDist05) {
			amplitude1 = 0;
			amplitude2 = 1;
			if (le.hasEffect(MobEffects.SLOW_FALLING)) {
				le.removeEffect(MobEffects.SLOW_FALLING);
			}
		}

		int d = 6;

		if (le instanceof ServerPlayer) {
			ServerPlayer p = (ServerPlayer) le;
			Block b = level.getBlockState(pos).getBlock();
			Block bb = level.getBlockState(pos.below()).getBlock();
			if (gHungerBlocks.contains(b) || gHungerBlocks.contains(bb)) {
				Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.HUNGER, duration);
			}
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.MOVEMENT_SLOWDOWN, duration);
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.WEAKNESS, duration);
			Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.DIG_SLOWDOWN, duration);
			if (isTooCloseToFly(closestGrimDistSq)) {
				slowFlyingMotion(le);
				if (p.isFallFlying()) {
					Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.POISON, duration);
				}
			}
			
			if ((closestGrimDistSq > gDist09) && (level.getRandom().nextInt(36000) == 42)) {
				BlockPos phantomPos = new BlockPos (pos.getX(),level.getHeightmapPos(Types.MOTION_BLOCKING, pos).getY()+12,pos.getZ());
				GrimCitadelManager.populateEntityType(EntityType.PHANTOM, serverLevel,
						phantomPos, 1, 0);
			}
		} else if ((le instanceof Animal) && (MyConfig.isGrimHarmPassiveCreatures())) {
			doGrimEffectsAnimals(le, pos, level);
		} else if (le instanceof AbstractSkeleton) {
			doGrimEffectsSkeletons(le, pos, gameTime, serverLevel);
		} else if (le instanceof Zombie) {
			doGrimEffectsZombies(le, gameTime);
		} else if (le instanceof Creeper) {
			doGrimEffectsCreepers(le, gameTime);
		} else if (le instanceof Phantom) {
			doGrimEffectsPhantoms(le, pos, gameTime, serverLevel);
		} else if (le instanceof Spider) {
			doGrimEffectsSpiders(le, pos, gameTime, serverLevel);
		}
		doSpreadDeadBranches(le, pos, level);

	}

	private void playOptionalSoundCues(BlockPos pos, Level level, double closestGrimDistSq, Player pe) {
		BlockPos grPos;

		float volume = 1.0f;
		float pitch = 0.67f;
		boolean playDirectionalSound = true;
		boolean playambientsound = true;
		if (closestGrimDistSq < gDist100) {
			volume = 0.35f;
		} 
		if (closestGrimDistSq < gDist70) {
			volume = 0.45f;
		} 
		if (closestGrimDistSq < gDist50) {
			volume = 0.55f;
		} 
		if (closestGrimDistSq < gDist30) {
			volume = 0.65f;
		} 
		if (closestGrimDistSq < gDist25) {
			volume = 0.7f;
		} 
		if (closestGrimDistSq < gDist05) {
			playDirectionalSound = false;
		}
		if (playDirectionalSound) {
			playDirectionalSoundCue(pos, level, pe, volume, pitch, playDirectionalSound, playambientsound);
		}
	}

	private void playDirectionalSoundCue(BlockPos pos, Level level, Player pe, float volume, float pitch,
			boolean playDirectionalSound, boolean playambientsound) {
		BlockPos grPos;
		Vec3 v = GrimCitadelManager.getDirectionGrimCitadel(pos);
		if (v != null) {
			v = v.scale(15);
			grPos = new BlockPos(pos.getX() + v.x, pos.getY() + v.y, pos.getZ() + v.z);
		} else {
			grPos = pos;
		}
		int i = level.getRandom().nextInt(gAmbientSoundEvents.size());
		int bonusChance = 0;
		if (level.isNight()) {
			bonusChance = 17;
		}
		if ((playambientsound) && (level.getRandom().nextInt(1200) <= 13 + bonusChance)) {
			level.playSound(pe, pos, gAmbientSoundEvents.get(i), SoundSource.AMBIENT, 0.20f, pitch);
		}
		i = level.getRandom().nextInt(gSoundEvents.size());
		bonusChance = 23;
		if ((playDirectionalSound) && (level.getRandom().nextInt(1200) <= 31 + bonusChance)) {
			level.playSound(pe, grPos, gSoundEvents.get(i), SoundSource.AMBIENT, volume, pitch);
		}
	}

	private void doGrimEffectsPhantoms(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
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

	private void doGrimEffectsCreepers(LivingEntity le, long gameTime) {
		if (creeperTimer < gameTime) {
			creeperTimer = gameTime + 240;
			Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 960);
		}
	}

	private void doGrimEffectsZombies(LivingEntity le, long gameTime) {
		if (zombieTimer < gameTime) {
			zombieTimer = gameTime + 240;
			Utility.updateEffect(le, 1, MobEffects.REGENERATION, duration);
			Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 240);
		}
	}

	private void doGrimEffectsSkeletons(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (skeletonTimer < gameTime) {
			skeletonTimer = gameTime + 120;
			Block b = serverLevel.getBlockState(pos).getBlock();
			Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
			Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 480);
			if (le instanceof WitherSkeleton) {
				Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 480);
			} else {
				if (serverLevel.getMaxLocalRawBrightness(pos) < 9) {
					if (witherSkeletonTimer < gameTime) {
						witherSkeletonTimer = gameTime + 1800;
						GrimCitadelManager.populateEntityType(EntityType.WITHER_SKELETON, serverLevel,
								le.blockPosition(), 1, 0);
					}
				}
			}
		}
	}

	private void doGrimEffectsSpiders(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (spiderTimer < gameTime) {
			spiderTimer = gameTime + 60;
			Block b = serverLevel.getBlockState(pos).getBlock();
			Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
			Utility.updateEffect(le, 0, MobEffects.ABSORPTION, 480);
		}
	}
	
	private void doGrimEffectsAnimals(LivingEntity le, BlockPos pos, Level level) {
		if (level.getRandom().nextInt(400) < 3) {
			if (le.getHealth() > 2) {
				Utility.updateEffect((LivingEntity) le, 0, MobEffects.POISON, 10);
			}
			if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.COARSE_DIRT) {
				level.setBlock(le.blockPosition().below(), Blocks.NETHERRACK.defaultBlockState(), 3);
			}
			if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
				level.setBlock(le.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
			}
		}
		if (level.getRandom().nextInt(9000) == 51) {
			BlockPos firePos = level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos.north(2));
			level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
		}
//		if (le instanceof Pig) {
//			if (level.getRandom().nextInt(4800) == 42) {
//				 e = LightningBolt.spawn(level, null, null, null, savePos.north(2).west(2), MobSpawnType.NATURAL,
//						true, true);
//			}
//		}
	}

	private void doSpreadDeadBranches(LivingEntity le, BlockPos pos, Level level) {
		if (level.getBrightness(LightLayer.SKY, pos) > 10) {
			BlockPos deadBranchPos = level.getHeightmapPos(Types.MOTION_BLOCKING, pos);
			Block b = level.getBlockState(deadBranchPos.below()).getBlock();
			if (b instanceof LeavesBlock) {
				if (b == ModBlocks.DEAD_BRANCHES) {
					BlockPos workPos = deadBranchPos;
					for (int i = 0;  i <= 3; i++) {
						workPos = doSpreadOneDeadBranch(level, deadBranchPos);
					}
				} else {
					level.setBlock(deadBranchPos, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);
				}
			}
		}
	}

	private BlockPos doSpreadOneDeadBranch(Level level, BlockPos pos) {
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
			int r = 1 + level.getRandom().nextInt(2) ;
			workPos = pos.below(r);
		}
		b = level.getBlockState(workPos).getBlock();
		if ((b instanceof LeavesBlock) && (b != ModBlocks.DEAD_BRANCHES)) {
			level.setBlock(workPos, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);
		}
		return workPos;
	}

	private boolean isTooCloseToFly(double closestGrimDistSq) {
		return closestGrimDistSq < gDist09;
	}

	private void slowFlyingMotion(LivingEntity le) {

		if ((le instanceof Player) && (le.isFallFlying())) {
			Player cp = (Player) le;
			Vec3 vec = cp.getDeltaMovement();
			Vec3 slowedVec;
			if (vec.y > 0) {
				slowedVec = vec.multiply(0.17, -0.75, 0.17);
			} else {
				slowedVec = vec.multiply(0.17, 1.001, 0.17);
			}
			cp.setDeltaMovement(slowedVec);
		}
	}

	private void initializeDistanceConstants() {
		if (gDist100 == 0) {
			gDist100 = MyConfig.getGrimCitadelBonusDistanceSq();
			gDist70 = gDist100 / 2;
			gDist50 = gDist100 / 4;
			gDist30 = gDist100 / 8;
			gDist25 = gDist100 / 16;
			gDist16 = gDist100 / 32;
			gDist12 = gDist100 / 64;
			gDist09 = gDist100 / 128;
			gDist05 = gDist100 / 256;
		}
	}

	public void shutdown() {
		phantomTimer = 0;
		invisTimer = 0;
		skeletonTimer = 0;
		zombieTimer = 0;
		witherSkeletonTimer = 0;
		creeperTimer = 0;
	}
}
