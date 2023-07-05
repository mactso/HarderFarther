package com.mactso.harderfarther.block;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks
{
	public static final Block GRIM_HEART = 
			new GrimHeartBlock(BlockBehaviour
					.Properties.of().mapColor(MapColor.COLOR_PURPLE).lightLevel((state) -> { return 7;}).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY), ParticleTypes.FLAME
					);
	
	public static final Block GRIM_GATE = 
			new GrimGateBlock(BlockBehaviour
					.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).instabreak().noOcclusion().lightLevel((state) -> { return 3;}).sound(SoundType.GLASS)
					);
	
//	   public static final Block WALL_TORCH = register("wall_torch", new WallTorchBlock(BlockBehaviour
//			   .Properties.of(Material.DECORATION).noCollission().instabreak().lightLevel((p_152607_) -> {
//		      return 14;   }).sound(SoundType.WOOD).dropsLike(TORCH), ParticleTypes.FLAME));
	public static final Block DEAD_BRANCHES = new LeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isSuffocating(ModBlocks::never).isViewBlocking(ModBlocks::never));

	
	public static void register(IForgeRegistry<Block> forgeRegistry	)
	{

		
		forgeRegistry.register("grim_heart",GRIM_HEART);
		forgeRegistry.register("grim_gate",GRIM_GATE);
		forgeRegistry.register("dead_branches",DEAD_BRANCHES);


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