package com.mactso.harderfarther.events;

import java.util.Arrays;
import java.util.List;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {

	static List<SoundEvent> gSoundEvents = Arrays.asList(SoundEvents.ZOMBIFIED_PIGLIN_ANGRY,
			SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT, SoundEvents.LAVA_AMBIENT, SoundEvents.ZOMBIE_VILLAGER_STEP,
			SoundEvents.HOGLIN_ANGRY, SoundEvents.BLAZE_AMBIENT, SoundEvents.HOGLIN_AMBIENT,
			SoundEvents.AMBIENT_NETHER_WASTES_MOOD, SoundEvents.FIRE_AMBIENT, SoundEvents.WITHER_SKELETON_STEP);
	static List<SoundEvent> gAmbientSoundEvents = Arrays.asList(SoundEvents.AMBIENT_CAVE, SoundEvents.WITCH_AMBIENT,
			SoundEvents.WOLF_HOWL, SoundEvents.AMBIENT_CAVE, SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD);
	static List<Block> gHungerBlocks = Arrays.asList(Blocks.WATER, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.SAND,
			Blocks.NETHERRACK);

	private int duration = 80; // four seconds.

	int gcDist100 = 0; // default: 2000 meters (100%)
	int gcDist70 = 0; // default: 1414 meters (70%)
	int gcDist50 = 0; // default: 1000 meters (50%)
	int gcDist30 = 0; // default: 707 meters (30%)
	int gcDist25 = 0; // default: 500 meters (25%)
	int gcDist16 = 0; // default: 352 meters (16%)
	int gcDist12 = 0; // default: 250 meters (12%)
	int gcDist09 = 0; // default: 176 meters (9%)
	int gcDist05 = 0; // default: 125 meters (5%)

	boolean clientSynced = false;

	long pigTimer = 0;
	long fishTimer = 0;

	long villagerTimer = 0;
	long phantomTimer = 0;
	long invisTimer = 0;
	long skeletonTimer = 0;
	long spiderTimer = 0;
	long spiderWebTimer = 0;
	long zoglinTimer = 0;
	long zombifiedPiglinTimer = 0;
	long zombieTimer = 0;
	long witherSkeletonTimer = 0;
	long creeperTimer = 0;

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {

		

		// everything below here is grim citadel code
		if (!MyConfig.isUseGrimCitadels()) {
			return;
		}
		initializeDistanceConstants();

		LivingEntity le = event.getEntityLiving();
		BlockPos pos = le.blockPosition();
		Level level = le.level;


		long gameTime = level.getGameTime();
		double closestGrimDistSq = GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos);

		if ((closestGrimDistSq > gcDist100)) { // note also MAXINTEGER in here.
			return;
		}

		if (isTooCloseToFly(closestGrimDistSq)) {
			slowFlyingMotion(le);
		}

		if (gameTime % 10 != le.getId() % 10)
			return;

		Utility.debugMsg(2, pos, "Living Event " + event.getEntity().getType().getRegistryName().toString());

		// note: Synced when a player logs in and when hearts destroyed.

		int amplitude1 = 0;
		int amplitude2 = 0;

		if (level.isClientSide()) {
			if (le instanceof Player pe) {
				playOptionalSoundCues(pos, level, closestGrimDistSq, pe);
			}
			// TODO *might* be able to play Dusty directly here.
			return;
		}

		ServerLevel serverLevel = (ServerLevel) level;

		if (le instanceof ServerPlayer sp) {
			if ((level.getRandom().nextInt(144000) == 4242) && (closestGrimDistSq > gcDist09)) {
				int song = level.getRandom().nextInt(ModSounds.NUM_SONGS) + 1;
				// note client has a song spam timer.
				Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_DUSTY_MEMORIES), sp);
			}
		}

		if ((closestGrimDistSq >= gcDist16) && (closestGrimDistSq <= gcDist50)) {
			amplitude1 = 0;
			amplitude2 = 0;
		} else if ((closestGrimDistSq >= gcDist05) && (closestGrimDistSq < gcDist16)) {
			amplitude1 = 0;
			amplitude2 = 1;
			if (le.hasEffect(MobEffects.SLOW_FALLING)) {
				le.removeEffect(MobEffects.SLOW_FALLING);
			}
		} else if (closestGrimDistSq < gcDist05) {
			amplitude1 = 0;
			amplitude2 = 0;
			if (le.hasEffect(MobEffects.SLOW_FALLING)) {
				le.removeEffect(MobEffects.SLOW_FALLING);
			}
		}

		doGrimEffects(le, pos, level, gameTime, closestGrimDistSq, amplitude1, amplitude2, serverLevel);

		doSpreadDeadBranches(le, pos, level);

	}

	private void doGrimEffectPigs(Pig pig, BlockPos pos, long gameTime, ServerLevel serverLevel) {
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
					Utility.populateEntityType(EntityType.ZOMBIFIED_PIGLIN, serverLevel, pos, 1, 0, true, pig.isBaby()); // TODO
																															// test.
				}
			}
		}
	}

	private void doGrimEffects(LivingEntity le, BlockPos pos, Level level, long gameTime, double closestGrimDistSq,
			int amplitude1, int amplitude2, ServerLevel serverLevel) {
		if (le instanceof ServerPlayer se) {
			doGrimEffectsPlayer(se, pos, level, closestGrimDistSq, amplitude1, amplitude2, serverLevel);
		} else if (le instanceof Villager ve) {
			doGrimEffectVillagers(ve, pos, gameTime, serverLevel);
		} else if (le instanceof WaterAnimal we) {
			doGrimEffectsWaterAnimal(we, pos, gameTime, serverLevel);
		} else if (le instanceof Animal ae) {
			doGrimEffectsAnimals(ae, pos, gameTime, serverLevel);
		} else if (le instanceof Enemy) {
			doGrimEffectsMonsters(le, pos, gameTime, serverLevel);
		}
	}

	private void doGrimEffectsAnimals(Animal ae, BlockPos pos, long gameTime, ServerLevel level) {

		if (!(MyConfig.isGrimEffectAnimals()))
			return;

		if (level.getRandom().nextInt(400) < 9) {
			if (ae.getHealth() > 3) {
				Utility.updateEffect((LivingEntity) ae, 0, MobEffects.POISON, 10);
				Block b = level.getBlockState(ae.blockPosition().below()).getBlock();
				if (!GrimCitadelManager.getFloorBlocks().contains(b)) {
					level.setBlock(ae.blockPosition().below(), Blocks.GRAVEL.defaultBlockState(), 3);
				}
			}
			doGrimGroundTransform(ae, level);
		}
		if (level.getRandom().nextInt(9000) == 51) {
			BlockPos firePos = level.getHeightmapPos(Types.MOTION_BLOCKING_NO_LEAVES, pos.north(2));
			level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 3);
		}
		if (ae instanceof Pig) {
			doGrimEffectPigs((Pig) ae, pos, gameTime, level);
		}
	}

	private void doGrimEffectsMobCreepers(LivingEntity le, long gameTime, ServerLevel serverLevel) {
		if (creeperTimer < gameTime) {
			creeperTimer = gameTime + 240;
			Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 960);
		}
	}

	private void doGrimEffectsMobPhantoms(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
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

	private void doGrimEffectsMobSkeletons(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
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

	private void doGrimEffectsMobSpiders(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (spiderTimer < gameTime) {
			spiderTimer = gameTime + 80;
			Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
			Utility.updateEffect(le, 0, MobEffects.ABSORPTION, 480);
		}
		if (spiderWebTimer < gameTime) {
			spiderWebTimer = gameTime + 1200; // 1 per two minutes.
			if (isNotNearWebs(pos, serverLevel)) {
				le.level.setBlock(pos, Blocks.COBWEB.defaultBlockState(), 3);
			}
		}
	}

	private void doGrimEffectsMobZoglins(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zoglinTimer < gameTime) {
				zoglinTimer = gameTime + 500;
				doGrimGrassTransform(pos, serverLevel);
				doGrimGroundTransform(le, serverLevel);
			} else {
				zoglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	private void doGrimEffectsMobZombies(LivingEntity le, long gameTime, ServerLevel serverLevel) {
		if (zombieTimer < gameTime) {
			zombieTimer = gameTime + 240;
			Utility.updateEffect(le, 1, MobEffects.REGENERATION, duration);
			Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 720);
		}
	}

	private void doGrimEffectsMobZombifiedPiglin(LivingEntity le, BlockPos pos, long gameTime,
			ServerLevel serverLevel) {
		if (Utility.isOutside(pos, serverLevel)) {
			if (zombifiedPiglinTimer < gameTime) {
				zombifiedPiglinTimer = gameTime + 600;
				doGrimGrassTransform(pos, serverLevel);
				doGrimGroundTransform(le, serverLevel);
				if (serverLevel.getLevel().getRandom().nextInt(10000) == 42) {
					ZombifiedPiglin ze = (ZombifiedPiglin) le;
					ze.setAggressive(true);
				}
			} else {
				zombifiedPiglinTimer--; // when lots of zoglin speed up timer
			}
		}
	}

	private void doGrimEffectsMonsters(LivingEntity le, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (le instanceof AbstractSkeleton) {
			doGrimEffectsMobSkeletons(le, pos, gameTime, serverLevel);
		} else if (le instanceof Zombie) {
			doGrimEffectsMobZombies(le, gameTime, serverLevel);
		} else if (le instanceof Creeper) {
			doGrimEffectsMobCreepers(le, gameTime, serverLevel);
		} else if (le instanceof Phantom) {
			doGrimEffectsMobPhantoms(le, pos, gameTime, serverLevel);
		} else if (le instanceof Spider) {
			doGrimEffectsMobSpiders(le, pos, gameTime, serverLevel);
		} else if (le instanceof Zoglin) {
			doGrimEffectsMobZoglins(le, pos, gameTime, serverLevel);
		} else if (le instanceof ZombifiedPiglin) {
			doGrimEffectsMobZombifiedPiglin(le, pos, gameTime, serverLevel);
		}
	}

	private void doGrimEffectsPlayer(ServerPlayer le, BlockPos pos, Level level, double closestGrimDistSq,
			int amplitude1, int amplitude2, ServerLevel serverLevel) {

		ServerPlayer p = (ServerPlayer) le;
		Block b = level.getBlockState(pos).getBlock();
		Block bBelow = level.getBlockState(pos.below()).getBlock();
		if (gHungerBlocks.contains(b) || gHungerBlocks.contains(bBelow)) {
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.HUNGER, duration);
		}
		doGrimPlayerCurse(closestGrimDistSq, amplitude1, amplitude2, p);
		if (isTooCloseToFly(closestGrimDistSq)) {
			slowFlyingMotion(le);
			if (p.isFallFlying()) {
				Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.POISON, duration);
			}
		}
		if ((closestGrimDistSq > gcDist09) && (level.getRandom().nextInt(36000) == 42)) {
			BlockPos phantomPos = new BlockPos(pos.getX(),
					level.getHeightmapPos(Types.MOTION_BLOCKING, pos).getY() + 12, pos.getZ());
			Utility.populateEntityType(EntityType.PHANTOM, serverLevel, phantomPos, 1, 0);
		}
	}

	private void doGrimEffectsWaterAnimal(WaterAnimal we, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		// May later break this into different kinds of water animals or fish.
		doGrimEffectWaterAnimal(we, pos, gameTime, serverLevel);
	}

	private void doGrimEffectVillagers(Villager ve, BlockPos pos, long gameTime, ServerLevel serverLevel) {
		if (MyConfig.isGrimEffectVillagers()) {
			if (villagerTimer < gameTime) {
				villagerTimer = gameTime + 2400;
				Utility.populateEntityType(EntityType.WITCH, serverLevel, pos, 1, 0);
				Utility.updateEffect(ve, 9, MobEffects.WITHER, 240);
			}
		}
	}

	private void doGrimEffectWaterAnimal(WaterAnimal we, BlockPos pos, long gameTime, ServerLevel serverLevel) {

		Biome b = we.level.getBiome(pos);
		if (b == null)
			return;
		BiomeCategory bc = b.getBiomeCategory();
		if (bc == null)
			return;
		if (bc != BiomeCategory.OCEAN)
			return;

		if (fishTimer < gameTime) {
			fishTimer = gameTime + 600;
			List<Guardian> listG = serverLevel.getEntitiesOfClass(Guardian.class,
					new AABB(pos.north(16).west(16).above(8), pos.south(16).east(16).below(8)));
			if (listG.size() > 5)
				return;
			float pitch = 0.7f;
			serverLevel.playSound(null, pos, SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.AMBIENT, 2.20f, pitch);
			Utility.populateEntityType(EntityType.GUARDIAN, serverLevel, pos, 1, 0);
		}
	}

	private void doGrimGrassTransform(BlockPos pos, ServerLevel serverLevel) {
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

	private void doGrimGroundTransform(LivingEntity le, ServerLevel level) {
		if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.COARSE_DIRT) {
			level.setBlock(le.blockPosition().below(), Blocks.NETHERRACK.defaultBlockState(), 3);
		}
		if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
			level.setBlock(le.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
		}
	}

	private void doGrimPlayerCurse(double closestGrimDistSq, int amplitude1, int amplitude2, ServerPlayer p) {
		int curseDistSq = MyConfig.getGrimCitadelPlayerCurseDistanceSq();
		if (closestGrimDistSq < curseDistSq) {
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.WEAKNESS, duration);
			if (closestGrimDistSq > gcDist05)
				Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.MOVEMENT_SLOWDOWN, duration);
			if (closestGrimDistSq < curseDistSq / 2)
				Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.DIG_SLOWDOWN, duration);
		}
	}

	public void doResetTimers() {
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

	private void doSpreadDeadBranches(LivingEntity le, BlockPos pos, Level level) {
		Utility.debugMsg(2, pos, "doSpreadDeadBranches");
		if (level.getBrightness(LightLayer.SKY, pos) > 10) {
			BlockPos deadBranchPos = level.getHeightmapPos(Types.MOTION_BLOCKING, pos);
			Block b = level.getBlockState(deadBranchPos.below()).getBlock();
			if (b instanceof LeavesBlock || b == Blocks.NETHER_WART_BLOCK) {
				if (b == ModBlocks.DEAD_BRANCHES || b == Blocks.NETHER_WART_BLOCK) {
					BlockPos workPos = deadBranchPos;
					for (int i = 0; i <= 3; i++) {
						workPos = doSpreadOneDeadBranch(level, deadBranchPos);
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
			int r = 1 + level.getRandom().nextInt(2);
			workPos = pos.below(r);
		}
		b = level.getBlockState(workPos).getBlock();
		if ((b instanceof LeavesBlock) && (b != ModBlocks.DEAD_BRANCHES)) {
			level.setBlock(workPos, ModBlocks.DEAD_BRANCHES.defaultBlockState(), 3);
		}
		return workPos;
	}

	private void initializeDistanceConstants() {
		if (gcDist100 == 0) {
			gcDist100 = MyConfig.getGrimCitadelBonusDistanceSq();
			gcDist70 = gcDist100 / 2;
			gcDist50 = gcDist100 / 4;
			gcDist30 = gcDist100 / 8;
			gcDist25 = gcDist100 / 16;
			gcDist16 = gcDist100 / 32;
			gcDist12 = gcDist100 / 64;
			gcDist09 = gcDist100 / 128;
			gcDist05 = gcDist100 / 256;
		}
	}

	// this only runs once per minute;
	private boolean isNotNearWebs(BlockPos pos, ServerLevel serverLevel) {

		if (serverLevel.getBlockState(pos).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.above()).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.below()).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.north()).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.south()).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.east()).getBlock() == Blocks.COBWEB)
			return true;
		if (serverLevel.getBlockState(pos.west()).getBlock() == Blocks.COBWEB)
			return true;

		return false;
	}

	private boolean isTooCloseToFly(double closestGrimDistSq) {
		return closestGrimDistSq < gcDist09;
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

	private void playOptionalSoundCues(BlockPos pos, Level level, double closestGrimDistSq, Player pe) {

		float volume = 1.0f;
		float pitch = 0.67f;
		boolean playDirectionalSound = true;
		boolean playambientsound = true;
		if (closestGrimDistSq < gcDist100) {
			volume = 0.35f;
		}
		if (closestGrimDistSq < gcDist70) {
			volume = 0.45f;
		}
		if (closestGrimDistSq < gcDist50) {
			volume = 0.55f;
		}
		if (closestGrimDistSq < gcDist30) {
			volume = 0.65f;
		}
		if (closestGrimDistSq < gcDist25) {
			volume = 0.7f;
		}
		if (closestGrimDistSq < gcDist05) {
			playDirectionalSound = false;
		}
		if (playDirectionalSound) {
			playDirectionalSoundCue(pos, level, pe, volume, pitch, playDirectionalSound, playambientsound);
		}
	}

	public void shutdown() {
		doResetTimers();
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
}
