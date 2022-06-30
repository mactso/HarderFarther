package com.mactso.harderfarther.events;

import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.GrimCitadelManager;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class BlockEvents
{
//	List<Integer> list = Collections.EMPTY_LIST;
	static List<Block> protectedBlocks = Arrays.asList(Blocks.NETHERRACK,Blocks.BLACKSTONE,Blocks.BASALT,Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE,Blocks.TINTED_GLASS,Blocks.CHEST,Blocks.ANCIENT_DEBRIS);

//	List<Block> protectedBlocks = new ArrayList<Block>() {Blocks.NETHERRACK,Blocks.BLACKSTONE,Blocks.BASALT,Blocks.POLISHED_BASALT, Blocks.BLACKSTONE, Blocks.GILDED_BLACKSTONE,Blocks.TINTED_GLASS};
	static int PROTECTED_DISTANCE = 999; // (about 33 blocks in all directions from heart)
	@SubscribeEvent
	public static void onBreakBlock(BreakEvent event)
	{
		Player player = event.getPlayer();
		if (player.isCreative()) 
			return;
		Level level = player.level;
		if (level.isClientSide)
			return;
		BlockPos pos = event.getPos();
		if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos) <= PROTECTED_DISTANCE) {
			if (protectedBlocks.contains(level.getBlockState(pos).getBlock()) && event.isCancelable()) {	
					event.setCanceled(true);
			}
		}
	}


	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event)
	{
		Level level = event.getWorld();
		List<BlockPos> list = event.getAffectedBlocks();
		Vec3 vPos = event.getExplosion().getPosition();
		if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(new BlockPos (vPos.x,vPos.y,vPos.z)) <= PROTECTED_DISTANCE) {
		for (ListIterator<BlockPos> iter = list.listIterator(list.size()); iter.hasPrevious(); )
		{
			BlockPos pos = iter.previous();
			if (protectedBlocks.contains(level.getBlockState(pos).getBlock())) {
				iter.remove();
			}
		}
		}
	}

}