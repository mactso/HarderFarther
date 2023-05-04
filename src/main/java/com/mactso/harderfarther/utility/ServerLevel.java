package com.mactso.harderfarther.utility;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat.LevelSave;

public class ServerLevel extends ServerWorld {

	public ServerLevel(MinecraftServer p_i241885_1_, Executor p_i241885_2_, LevelSave p_i241885_3_,
			IServerWorldInfo p_i241885_4_, RegistryKey<World> p_i241885_5_, DimensionType p_i241885_6_,
			IChunkStatusListener p_i241885_7_, ChunkGenerator p_i241885_8_, boolean p_i241885_9_, long p_i241885_10_,
			List<ISpecialSpawner> p_i241885_12_, boolean p_i241885_13_) {
		super(p_i241885_1_, p_i241885_2_, p_i241885_3_, p_i241885_4_, p_i241885_5_, p_i241885_6_, p_i241885_7_, p_i241885_8_,
				p_i241885_9_, p_i241885_10_, p_i241885_12_, p_i241885_13_);
		// TODO Auto-generated constructor stub
	}

}
