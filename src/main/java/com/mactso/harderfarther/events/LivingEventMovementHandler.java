package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
	long zombieTimer = 0;
	long witherSkeletonTimer = 0;
	long creeperTimer = 0;	

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
			return;
		}

		ServerLevel serverLevel = (ServerLevel) level;
	
		if ((closestGrimDistSq >= gDist05) && (closestGrimDistSq <= gDist50)) {
			amplitude1 = 0;
			amplitude2 = 1;
		} else if (closestGrimDistSq < gDist05) {
			amplitude1 = 0;
			amplitude2 = 4;
			if (le.hasEffect(MobEffects.SLOW_FALLING)) {
				le.removeEffect(MobEffects.SLOW_FALLING);
			}
		}

		int d = 6;

		if (le instanceof ServerPlayer) {
			ServerPlayer p = (ServerPlayer) le;
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.MOVEMENT_SLOWDOWN, duration);
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.WEAKNESS, duration);
			Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.DIG_SLOWDOWN, duration);
			if (isTooCloseToFly(closestGrimDistSq)) {
				slowFlyingMotion(le);
				if (p.isFallFlying()) {
					Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.POISON, duration);
				}
			}
		} else if ((le instanceof Animal) && (MyConfig.isGrimHarmPassiveCreatures())) {
			if (level.getRandom().nextInt(400) == 51) {
				Utility.updateEffect((LivingEntity) le, amplitude1, MobEffects.POISON, duration);
				if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.COARSE_DIRT) {
					level.setBlock(le.blockPosition().below(), Blocks.GRAVEL.defaultBlockState(), 3);
				}
				if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
					level.setBlock(le.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
				}
			}
			if (level.getRandom().nextInt(9000) == 51) {
				level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
			}
		} else if (le instanceof AbstractSkeleton) {
			if (skeletonTimer < gameTime) {
				skeletonTimer = gameTime + 120;
				Block b = serverLevel.getBlockState(pos).getBlock();
				Utility.updateEffect(le, 0, MobEffects.DAMAGE_BOOST, 480);
				Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 480);
				if (le instanceof WitherSkeleton) {
					Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 480);
				} else {
					if (serverLevel.getMaxLocalRawBrightness(pos) < 9){
						if (witherSkeletonTimer < gameTime) {
							witherSkeletonTimer = gameTime + 1800;
							GrimCitadelManager.populateEntityType(EntityType.WITHER_SKELETON, serverLevel, le.blockPosition(), 1, 0);
						}
					}
				}
			}
		} else if (le instanceof Zombie) {
			if (zombieTimer < gameTime) {
				zombieTimer = gameTime + 240;
				Utility.updateEffect(le, 1, MobEffects.REGENERATION, duration);
				Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 240);
			}
		} else if (le instanceof Creeper) {
			if (creeperTimer < gameTime) {
				creeperTimer = gameTime + 240;
				Utility.updateEffect(le, 0, MobEffects.INVISIBILITY, 960);
			}
		} else if (le instanceof Phantom) {
			if (phantomTimer < gameTime) {
				phantomTimer = gameTime + 80;
				Utility.updateEffect(le, 0, MobEffects.REGENERATION, duration);
				Utility.updateEffect(le, 0, MobEffects.FIRE_RESISTANCE, 320);
				if (serverLevel.getRandom().nextInt(6)== 1 ) {
					PrimedTnt tnt = EntityType.TNT.spawn(serverLevel, null, null, null, pos, MobSpawnType.NATURAL,
							true, true);
					tnt.setFuse(80);
				}
			}
			
		}
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
					slowedVec = vec.multiply(0.15,-1,0.15);
				} else {
					slowedVec = vec.multiply(0.15,1.001,0.15);
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
