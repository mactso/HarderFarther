package com.mactso.harderfarther.utility;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;

public class Level extends World{

	protected Level(ISpawnWorldInfo p_i241925_1_, RegistryKey<World> p_i241925_2_, DimensionType p_i241925_3_,
			Supplier<IProfiler> p_i241925_4_, boolean p_i241925_5_, boolean p_i241925_6_, long p_i241925_7_) {
		super(p_i241925_1_, p_i241925_2_, p_i241925_3_, p_i241925_4_, p_i241925_5_, p_i241925_6_, p_i241925_7_);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ITickList<Block> getBlockTicks() {
		return this.getBlockTicks();
	}

	@Override
	public ITickList<Fluid> getLiquidTicks() {
		return this.getLiquidTicks();
	}

	@Override
	public AbstractChunkProvider getChunkSource() {

		return this.getChunkSource();
	}



	@Override
	public DynamicRegistries registryAccess() {
		return this.registryAccess();
	}

	@Override
	public List<? extends PlayerEntity> players() {
		return this.players();
	}

	@Override
	public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return this.getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
	}

	@Override
	public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sendBlockUpdated(BlockPos p_184138_1_, BlockState p_184138_2_, BlockState p_184138_3_,
			int p_184138_4_) {
		this.sendBlockUpdated(p_184138_1_, p_184138_2_, p_184138_3_, p_184138_4_);
		
	}

	@Override
	public void playSound(PlayerEntity p_184148_1_, double p_184148_2_, double p_184148_4_, double p_184148_6_,
			SoundEvent p_184148_8_, SoundCategory p_184148_9_, float p_184148_10_, float p_184148_11_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playSound(PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_,
			SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
		this.playSound(p_217384_1_, p_217384_2_, p_217384_3_, p_217384_4_, p_217384_5_, p_217384_6_);
		
	}

	@Override
	public Entity getEntity(int p_73045_1_) {
		return this.getEntity(p_73045_1_);
	}

	@Override
	public MapData getMapData(String p_217406_1_) {
		return this.getMapData(p_217406_1_);
	}

	@Override
	public void setMapData(MapData p_217399_1_) {
		this.setMapData(p_217399_1_);
		
	}

	@Override
	public int getFreeMapId() {
		return this.getFreeMapId();
	}

	@Override
	public void destroyBlockProgress(int p_175715_1_, BlockPos p_175715_2_, int p_175715_3_) {
		this.destroyBlockProgress(p_175715_1_, p_175715_2_, p_175715_3_);;
		
	}

	@Override
	public Scoreboard getScoreboard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RecipeManager getRecipeManager() {
		return this.getRecipeManager();
	}

	@Override
	public ITagCollectionSupplier getTagManager() {
		return this.getTagManager();
	}

	@Override
	public void levelEvent(PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_) {
		this.levelEvent(p_217378_1_,   p_217378_2_, p_217378_3_, p_217378_4_);
		
	}

}
