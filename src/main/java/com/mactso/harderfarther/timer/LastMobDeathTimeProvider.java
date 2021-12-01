package com.mactso.harderfarther.timer;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;


	public class LastMobDeathTimeProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
	{
		IChunkLastMobDeathTime storage;

		public LastMobDeathTimeProvider() {
			storage = new ChunkLastMobDeathTime();
		}

		
		@SuppressWarnings("unchecked")
		@Override
		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == CapabilityChunkLastMobDeathTime.LASTMOBDEATHTIME)
				return (LazyOptional<T>) LazyOptional.of(() -> storage);
			return LazyOptional.empty();
		}

		@Override
		public CompoundTag serializeNBT() {
			CompoundTag ret = new CompoundTag();
			ret.putLong("lastMobDeathTime", storage.getLastKillTime());
			return ret;
		}

		@Override
		public void deserializeNBT(CompoundTag nbt) {
			long time = nbt.getLong("lastMobDeathTime");
			storage.setLastKillTime(time);
		}
	}

