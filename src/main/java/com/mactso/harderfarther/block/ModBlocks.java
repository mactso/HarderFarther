package com.mactso.harderfarther.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlimeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.api.distmarker.Dist;

public class ModBlocks
{
	public static final Block GRIM_HEART = new GrimHeartBlock(BlockBehaviour.Properties.of(Material.CLAY).instabreak().sound(SoundType.METAL)).setRegistryName("grim_heart");
	public static final Block GRIM_GATE_BLOCK = new GrimGateBlock(BlockBehaviour.Properties.of(Material.CLAY).instabreak().noOcclusion().sound(SoundType.METAL)).setRegistryName("grim_gate_block");

	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{
		// BlockBehaviour.Properties.of(Material.CLAY, MaterialColor.GRASS).friction(0.8F).sound(SoundType.SLIME_BLOCK).noOcclusion()))
		forgeRegistry.register(GRIM_HEART);
		forgeRegistry.register(GRIM_GATE_BLOCK);

	}
	
	@OnlyIn(Dist.CLIENT)
	public static void setRenderLayer()
	{
		ItemBlockRenderTypes.setRenderLayer(GRIM_GATE_BLOCK, RenderType.translucent());
	
	}
}