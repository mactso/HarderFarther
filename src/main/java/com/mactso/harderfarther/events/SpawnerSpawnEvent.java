package com.mactso.harderfarther.events;

import java.beans.EventSetDescriptor;

import com.mactso.harderfarther.Main;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class SpawnerSpawnEvent {
	private static int debugThreadIdentifier = 0;
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCheckSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
    	
    	System.out.println(Main.MODID + " : Spawn Event");

//    	if (event.getSpawnReason() != SpawnReason.SPAWNER) {
//    		return;
//    	}

    	if (!(event.getWorld() instanceof ServerWorld)) {
    		return;
    	}
    	
    	ServerWorld serverWorld = (ServerWorld) event.getWorld();
    	LivingEntity entity = event.getEntityLiving();
        
		EntityType<?> type = entity.getType();
		if (type.getClassification().getPeacefulCreature()) {
			return;
		}
 
    	System.out.println(Main.MODID + " : Hostile Spawn Event.  " + entity.getType().toString());

    	
    	Vector3d spawnVec = new Vector3d (serverWorld.getWorldInfo().getSpawnX(),serverWorld.getWorldInfo().getSpawnY(),serverWorld.getWorldInfo().getSpawnZ());
    	Vector3d eventVec = new Vector3d (event.getX(),event.getY(),event.getZ());
    	float distanceModifier = (float) (eventVec.distanceTo(spawnVec) / 1000.0) ;
    	if (distanceModifier > 30) distanceModifier = 30;

    	float healthModifier = distanceModifier;
    	float maxHealth = entity.getMaxHealth();
        float totalMaxHealth = maxHealth + healthModifier;
        float maxHealthModifier = healthModifier/maxHealth;
		entity.getAttribute(Attributes.MAX_HEALTH).func_233767_b_(new AttributeModifier("maxhealthboost", maxHealthModifier, Operation.MULTIPLY_TOTAL));
        entity.setHealth(entity.getMaxHealth());
        float dHealth = entity.getMaxHealth();
    	float pctModifier = 1.0f + (distanceModifier/100);

        float speed = (float) entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        speed = speed * pctModifier;
        entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(speed);

        AttributeModifierManager a = entity.getAttributeManager();
//        if (a.func_233790_b_(Attributes.FLYING_SPEED)) {
//        	System.out.println(" mob has flying ");
//            float flyingSpeed = (float) entity.getAttribute(Attributes.FLYING_SPEED).getValue();
//            flyingSpeed = flyingSpeed * pctModifier;
//            entity.getAttribute(Attributes.FLYING_SPEED).setBaseValue(flyingSpeed);
//        }
//int debug = 8;
        float attackDamage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        attackDamage = attackDamage * pctModifier;
        entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(attackDamage);

        float knockBackResistance = (float) entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
        knockBackResistance = attackDamage * pctModifier;
        entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(knockBackResistance);
        
        int deb = 3;
        
    }
    
}
