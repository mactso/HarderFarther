package com.mactso.harderfarther.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	public static final Block GRIM_HEART = new GrimHeartBlock(BlockBehaviour.Properties.of(Material.CLAY).instabreak().sound(SoundType.METAL)).setRegistryName("grim_heart");
	public static final Block GRIM_GATE = new GrimGateBlock(BlockBehaviour.Properties.of(Material.GLASS).instabreak().noOcclusion().sound(SoundType.GLASS)).setRegistryName("grim_gate");
	public static final Block DEAD_BRANCHES = new LeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never)).setRegistryName("dead_branches");

	
	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{
		// BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion()))

		forgeRegistry.register(GRIM_HEART);
		forgeRegistry.register(GRIM_GATE);
		forgeRegistry.register(DEAD_BRANCHES);


	}


   private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_) {
      return (boolean)false;
   }
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayer()
	{
		ItemBlockRenderTypes.setRenderLayer(GRIM_GATE, RenderType.translucent());
	
	}
}