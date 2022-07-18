package com.mactso.harderfarther.events;

import java.util.List;
import java.util.ListIterator;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.block.GrimGateBlock;
import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.block.properties.GrimGateType;
import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.network.GrimClientSongPacket;
import com.mactso.harderfarther.network.Network;
import com.mactso.harderfarther.sounds.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class BlockEvents {

	static int grimBonusDistSqr = MyConfig.getGrimCitadelBonusDistanceSq();
	static int PROTECTED_DISTANCE = 999; // (about 33 blocks in all directions from heart)
	static int MIN_CANCEL_BLOCKPLACE_DISTANCE = 1200; // (about 33 blocks in all directions from heart)
	static int MAX_CANCEL_BLOCKPLACE_DISTANCE = 1500; // (about 33 blocks in all directions from heart)
	
	@SubscribeEvent
	public static void onBreakBlock(BreakEvent event) {
		// server side only event.
		ServerPlayer sp = (ServerPlayer) event.getPlayer();
		ServerLevel serverLevel = (ServerLevel) sp.level;
		BlockPos pos = event.getPos();
		BlockState bs = serverLevel.getBlockState(pos);
		Block b = bs.getBlock();
		if (b == ModBlocks.GRIM_GATE) {
			doBrokenGrimGate(sp, serverLevel, pos, bs);
		} else if (b == ModBlocks.GRIM_HEART) {
			Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_LAKE_DESTINY), sp);
		}

		if (sp.isCreative())
			return;
		
		if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(pos) <= PROTECTED_DISTANCE) {
			if (GrimCitadelManager.getProtectedBlocks().contains(serverLevel.getBlockState(pos).getBlock())
					&& event.isCancelable()) {
				event.setCanceled(true);
			}
		}
	}

	private static void doBrokenGrimGate(ServerPlayer sp, ServerLevel serverLevel, BlockPos pos, BlockState bs) {
		if (bs.getValue(GrimGateBlock.TYPE) == GrimGateType.FLOOR) {
			GrimCitadelManager.makeSeveralHolesInFloor(serverLevel, pos);
		} else if (bs.getValue(GrimGateBlock.TYPE) == GrimGateType.DOOR) {
			Network.sendToClient(new GrimClientSongPacket(ModSounds.NUM_LABYRINTH_LOST_DREAMS), sp);
		}
	}

	@SubscribeEvent
	public static void onBlockPlacementEvent(EntityPlaceEvent event) {

		if (!(event.getEntity() instanceof Player))
			return;

		if (GrimCitadelManager.isInGrimProtectedArea(event.getPos())) {
			event.setCanceled(true);
			updateHands((ServerPlayer) event.getEntity());
		}

	}

	/**
	 * fix client side view of the hotbar for non creative
	 * This makes it so items don't look like they poofed.
	 */
	private static void updateHands(ServerPlayer player) {
		if (player.connection == null)
			return;
		ItemStack itemstack = player.getInventory().getSelected();
		if (!itemstack.isEmpty())
			slotChanged(player, 36 + player.getInventory().selected, itemstack);
		itemstack = player.getInventory().offhand.get(0);
		if (!itemstack.isEmpty())
			slotChanged(player, 45, itemstack);
	}

	private static void slotChanged(ServerPlayer player, int index, ItemStack itemstack) {
		InventoryMenu menu = player.inventoryMenu;
		player.connection.send(
				new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), index, itemstack));
	}

	@SubscribeEvent
	public static void onBucket(FillBucketEvent event) {

		HitResult target = event.getTarget();

		if (target.getType() == HitResult.Type.MISS)
			return;

		if (target.getType() == HitResult.Type.ENTITY)
			return;

		if (target.getType() == HitResult.Type.BLOCK) {
			Player player = event.getPlayer();
			Level world = player.level;
			BlockHitResult blockray = (BlockHitResult) target;
			BlockPos blockpos = blockray.getBlockPos();

			Fluid fluid = null;
			ItemStack stack = event.getEmptyBucket();
			Item item = stack.getItem();
			if (item instanceof BucketItem) {
				BucketItem bucket = (BucketItem) item;
				fluid = bucket.getFluid();
			} else {
				// not a bucket (not sure this will happen), so guess
				FluidState state = world.getFluidState(blockpos);
				if (state.getType() != Fluids.EMPTY)
					fluid = Fluids.EMPTY;
			}
			if (fluid != Fluids.EMPTY) {
				boolean next = true;
				if (fluid != null) {
					BlockState state = world.getBlockState(blockpos);
					Block block = state.getBlock();
					if (block instanceof LiquidBlockContainer) {
						LiquidBlockContainer lc = (LiquidBlockContainer) block;
						if (lc.canPlaceLiquid(world, blockpos, state, fluid))
							next = false;
					}
				}
				if (next)
					blockpos = blockpos.relative(blockray.getDirection());
			}

			if (GrimCitadelManager.getClosestGrimCitadelDistanceSq(blockpos) <= PROTECTED_DISTANCE) {
				if (event.isCancelable())
					event.setCanceled(true);

			}
		}
	}

//	@SubscribeEvent
//	public static void onCreateFluidSourceEvent (CreateFluidSourceEvent event)
//	{	
//
//		if (GrimCitadelManager.isInGrimProtectedArea(event.getPos())) {
//			addToKillWaterPosList(event.getPos());
//		}		
//	}
//
//	public static void addToKillWaterPosList(BlockPos newKillWaterPos) {
//		killWaterPos.add(newKillWaterPos);
//	}
//	
//	public static void killIllegalFluidBlocks(Level l) {
//		for (int i = 0; i < killWaterPos.size();i++ ) {
//			l.setBlock(killWaterPos.get(i), Blocks.AIR.defaultBlockState(), 3);
//		}
//		killWaterPos.clear();
//	}

	@SubscribeEvent
	public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
		Level level = event.getWorld();
		List<BlockPos> list = event.getAffectedBlocks();
		Vec3 vPos = event.getExplosion().getPosition();
		if (GrimCitadelManager
				.getClosestGrimCitadelDistanceSq(new BlockPos(vPos.x, vPos.y, vPos.z)) <= PROTECTED_DISTANCE) {
			for (ListIterator<BlockPos> iter = list.listIterator(list.size()); iter.hasPrevious();) {
				BlockPos pos = iter.previous();
				if (GrimCitadelManager.getProtectedBlocks().contains(level.getBlockState(pos).getBlock())) {
					iter.remove();
				}
			}
		}
	}

}