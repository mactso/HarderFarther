package com.mactso.harderfarther.timer;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;


	public class LastMobDeathTimeProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundNBT>
	{
		IChunkLastMobDeathTime storage;

		public LastMobDeathTimeProvider(Chunk chunk) {
			storage = new ChunkLastMobDeathTime(chunk);
		}

		public LastMobDeathTimeProvider (ServerPlayerEntity serverPlayerEntity) {
			storage = new ChunkLastMobDeathTime(serverPlayerEntity);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME)
				return (LazyOptional<T>) LazyOptional.of(() -> storage);
			return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT() {
			CompoundNBT ret = new CompoundNBT();
			ret.putLong("lastMobDeathTime", storage.getLastKillTime());
			return ret;
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			int time = nbt.getInt("lastMobDeathTime");
			storage.setLastKillTime(time);
		}
	}

