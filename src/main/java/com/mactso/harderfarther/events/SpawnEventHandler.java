package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnEventHandler {
	private static int debugThreadIdentifier = 0;
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCheckSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
    	
 
    	if (!(MyConfig.isMakeMonstersHarderFarther())) return;
    	
    	if (!(event.getWorld() instanceof ServerWorld)) {
    		return;
    	}
    	
    	ServerWorld serverWorld = (ServerWorld) event.getWorld();
    	LivingEntity entity = event.getEntityLiving();
        
		EntityType<?> type = entity.getType();
		if (type.getClassification().getPeacefulCreature()) {
			return;
		}
 
		MyConfig.setaDebugLevel(2);
		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println(Main.MODID + " : Hostile Spawn Event.("+event.getX()+" "+event.getY()+ " "+event.getZ()+ ")  " + entity.getType().toString());
		}
    	// no spawns closer to worldspawn than safe distance
    	if (Math.abs(event.getX()) < MyConfig.getSafeDistance()) {
    		if (Math.abs(event.getZ()) < MyConfig.getSafeDistance()) {
    			event.setResult(Result.DENY);
    			return ;
    		}
    	}
    	int deb7 = 4;
    	float distanceModifier = calcDistanceModifier(event, serverWorld);
    	float pctModifier = 1.0f + (distanceModifier/100);

    	

    	
    	if (MyConfig.isHpMaxModified()) {
        	float baseMaxHealth = entity.getMaxHealth();
        	float totalMaxHealth = entity.getMaxHealth();
        	float healthModifier = (totalMaxHealth * distanceModifier) / 100.0f;
        	float maxHealthMultiplier = calcMaxHealthMultiplier(entity);
            totalMaxHealth = baseMaxHealth + (maxHealthMultiplier * healthModifier);    
            float maxHealthModifier = (((totalMaxHealth/baseMaxHealth) - 1.0f));
    		entity.getAttribute(Attributes.MAX_HEALTH).func_233767_b_(new AttributeModifier("maxhealthboost", maxHealthModifier, Operation.MULTIPLY_TOTAL));
    		float maxHealth = entity.getMaxHealth();
            entity.setHealth(entity.getMaxHealth());
    		if (MyConfig.getaDebugLevel() > 1) {
    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost HP from "+baseMaxHealth+" to "+ totalMaxHealth + ".");
    		}

    	}
    	

    	if (MyConfig.isSpeedModified()) {
            float baseSpeed = (float) entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
            float newSpeed = baseSpeed * pctModifier;
            int debug7 = 3;
            entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);    		
    		if (MyConfig.getaDebugLevel() > 1) {
    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost speed from "+baseSpeed+" to "+ newSpeed+ ".");
    		}

    	}

        if (MyConfig.isAtkDmgModified()) {
            float baseAttackDamage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            float newAttackDamage = baseAttackDamage * pctModifier;
            entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newAttackDamage);        	
 
            if (MyConfig.getaDebugLevel() > 1) {
    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost attack damage from "+baseAttackDamage+" to "+ newAttackDamage+ ".");
    		}

        }


        if(MyConfig.isKnockBackModified()) {
            float baseKnockBackResistance = (float) entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
            float newKnockBackResistance = ((1.0f + baseKnockBackResistance) * pctModifier) - 1.0f;
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(newKnockBackResistance);
            if (MyConfig.getaDebugLevel() > 1) {
    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost KB Resist from "+baseKnockBackResistance+" to "+ newKnockBackResistance+ ".");
    		}
        }


        int debug = 4;
        
    }



	private float calcDistanceModifier(LivingSpawnEvent.CheckSpawn event, ServerWorld serverWorld) {
		float distanceFromSpawn = (float) Math.abs(serverWorld.getWorldInfo().getSpawnX()-event.getX()) / 1000.00f;
		
		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = (float) Math.abs(serverWorld.getWorldInfo().getSpawnZ()-event.getZ()) / 1000.00f;    		
        	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
        		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
        	}
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
        	Vector3d spawnVec = new Vector3d (serverWorld.getWorldInfo().getSpawnX(),serverWorld.getWorldInfo().getSpawnY(),serverWorld.getWorldInfo().getSpawnZ());
        	Vector3d eventVec = new Vector3d (event.getX(),event.getY(),event.getZ());
        	distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec)) ;
    	} 

    	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance();
    	}
    	
    	int safe = MyConfig.getSafeDistance();
    	int mdist = MyConfig.getModifierMaxDistance();
    	int mval = MyConfig.getModifierValue();
    	float maxModifierDistance = MyConfig.getModifierMaxDistance();
    	float modifierValue = MyConfig.getModifierValue();
    	float pctDistanceToMax = distanceFromSpawn/maxModifierDistance ;
    	float distanceModifier = modifierValue * pctDistanceToMax;

    	double spawnHeight = event.getY();
    	int minSafeAltitude = MyConfig.getMinimumSafeAltitude();
    	int maxSafeAltitude = MyConfig.getMaximumSafeAltitude();
    	
		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}

		return distanceModifier;
	}
    
    
    
	private float calcMaxHealthMultiplier(LivingEntity entity) {
		float maxHealthMultiplier = 1.0f;

		// give some mobs more bonus hit points.
		if (entity instanceof ZombieEntity) {
			maxHealthMultiplier = 2.0f;
		} else if (entity instanceof CaveSpiderEntity) {
			// no mod
		} else if (entity instanceof SpiderEntity) {
			maxHealthMultiplier = 2.1f;
		} else if (entity instanceof CreeperEntity) {
			// no mod
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			// no mod
		} else if (entity instanceof SkeletonEntity) {
			maxHealthMultiplier = 1.8f;
		}
		return maxHealthMultiplier;
	}
    
}
