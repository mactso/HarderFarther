package com.mactso.harderfarther.timer;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class CapabilityChunkLastMobDeathTime {




	    @CapabilityInject(IChunkLastMobDeathTime.class)
	    public static Capability<IChunkLastMobDeathTime> LASTMOBDEATHTIME = null;

	    public static void register()
	    {
	        CapabilityManager.INSTANCE.register(IChunkLastMobDeathTime.class, new IStorage<IChunkLastMobDeathTime>()
	        {
	            @Override
	            public INBT writeNBT(Capability<IChunkLastMobDeathTime> capability, IChunkLastMobDeathTime instance, Direction side)
	            {
	                return LongNBT.valueOf(instance.getLastKillTime());
	            }

	            @Override
	            public void readNBT(Capability<IChunkLastMobDeathTime> capability, IChunkLastMobDeathTime instance, Direction side, INBT nbt)
	            {
	                if (!(instance instanceof ChunkLastMobDeathTime))
	                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
	                ((ChunkLastMobDeathTime)instance).setLastKillTime(((IntNBT)nbt).getInt());
	            }
	        },
	        () -> new ChunkLastMobDeathTime(null));
	    }
	}

