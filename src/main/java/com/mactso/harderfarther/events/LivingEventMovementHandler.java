package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;;

@Mod.EventBusSubscriber()
public class LivingEventMovementHandler {

	private int duration = 80; // four seconds.
	long bonusGrimDistSq1 = 0;
	long bonusGrimDistSq2 = 0;
	long bonusGrimDistSq4 = 0;
	long bonusGrimDistSq8 = 0;
	long bonusGrimDistSq16 = 0;
	long bonusGrimDistSq32 = 0;
	long bonusGrimDistSq64 = 0;
	long bonusGrimDistSq128 = 0;
	long bonusGrimDistSq256 = 0;
	
	@SubscribeEvent
	public void livingEntityHandler(LivingUpdateEvent event) {

		
		LivingEntity le = event.getEntityLiving();
		Level level = le.level;

		if (level.isClientSide()) {
			return;
		}


		
		long gametime = level.getGameTime();
		int amplitude1 = 0;
		int amplitude2 = 0;
		boolean killFlight = false;

		if (gametime % 10 != le.getId() % 10) 
			return;

		BlockPos pos = le.blockPosition();

		double closestGrimDistSq = GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos);
		int x=3;

		if (bonusGrimDistSq1 == 0) {
			bonusGrimDistSq1 = MyConfig.getGrimCitadelBonusDistanceSq();
			bonusGrimDistSq2 = bonusGrimDistSq1 / 2;
			bonusGrimDistSq4 = bonusGrimDistSq1 / 4;
			bonusGrimDistSq8 = bonusGrimDistSq1 / 8;
			bonusGrimDistSq16 = bonusGrimDistSq1 / 16;
			bonusGrimDistSq32 = bonusGrimDistSq1 / 32;
			bonusGrimDistSq64 = bonusGrimDistSq1 / 64;
			bonusGrimDistSq128 = bonusGrimDistSq1 / 128;
			bonusGrimDistSq256 = bonusGrimDistSq1 / 256;
		}

		
		if ((closestGrimDistSq > bonusGrimDistSq1)) {
			return;
		}
		if ((closestGrimDistSq >= bonusGrimDistSq256) && (closestGrimDistSq <= bonusGrimDistSq4)) {
			amplitude1 = 0;
			amplitude2 = 1;
		} else if (closestGrimDistSq < bonusGrimDistSq256) {
			amplitude1 = 0;
			amplitude2 = 4;
			killFlight = true;
		}

		if (closestGrimDistSq < (double) bonusGrimDistSq256) {
			amplitude1 = 0;
			amplitude2 = 4;
			killFlight = true;
		}
		int d = 6;
		if (le instanceof ServerPlayer) {
			// System.out.println ("c:"+closestGrimDistSq + " bonus:" + (double) (bonusGrimDistSq256) + " amplitude2:" + amplitude2);

			ServerPlayer p = (ServerPlayer) le;
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.MOVEMENT_SLOWDOWN, duration);
			Utility.updateEffect((LivingEntity) p, amplitude1, MobEffects.WEAKNESS, duration);
			Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.DIG_SLOWDOWN, duration);
			if ((killFlight) && (p.isFallFlying()) && (p.flyingSpeed > 0)) {
				Utility.updateEffect((LivingEntity) p, 3, MobEffects.POISON, duration);
				Utility.updateEffect((LivingEntity) p, 5, MobEffects.HUNGER, duration);
//				Can't seem to slow movement or change deltamovement vector.
				// wrong event/  Maybe PlayerTickUpdate?
//				Vec3 v = p.getDeltaMovement();
//				System.out.println(" FlyingSpeedp: " + v);
//				Vec3 v2 = new Vec3(v.x/2, v.y/2, v.z/2);
//				System.out.println(" FlyingSpeed.5: " + v2);
//				p.setDeltaMovement(v2);
//				System.out.println("delta =" + p.getDeltaMovement());

//				Utility.updateEffect((LivingEntity) p, 5, MobEffects.SLOW_FALLING, duration);

				//				Utility.updateEffect((LivingEntity) p, amplitude2, MobEffects.BLINDNESS, duration);
			}
		} else if (le instanceof Animal) {
			if (level.getRandom().nextInt(400) == 51) {
				Utility.updateEffect((LivingEntity) le, amplitude1, MobEffects.POISON, duration);
				if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.COARSE_DIRT) {
					System.out.println ("gravel");
					level.setBlock(le.blockPosition().below(), Blocks.GRAVEL.defaultBlockState(), 3);
				}
				if (level.getBlockState(le.blockPosition().below()).getBlock() == Blocks.GRASS_BLOCK) {
					System.out.println ("coarse");
					level.setBlock(le.blockPosition().below(), Blocks.COARSE_DIRT.defaultBlockState(), 3);
				}
			}
			if (level.getRandom().nextInt(9000) == 51) {
				System.out.println ("fire");
				level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);

			}
		}

	}
}
