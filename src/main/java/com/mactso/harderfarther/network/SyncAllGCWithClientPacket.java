package com.mactso.harderfarther.network;

import java.util.ArrayList;
import java.util.List;

import com.mactso.harderfarther.manager.GrimCitadelManager;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class SyncAllGCWithClientPacket {
	
	private List<BlockPos> GCLocations;
	
	public SyncAllGCWithClientPacket ( List<BlockPos> gl)
	{
		this.GCLocations = gl;
	}
	
	public static void processPacket(SyncAllGCWithClientPacket message, Context ctx)
	{
		ctx.enqueueWork( () -> 
			{
				GrimCitadelManager.realGCList = message.GCLocations;
			}
		);
		ctx.setPacketHandled(true);
	}

	public static SyncAllGCWithClientPacket readPacketData(FriendlyByteBuf buf)
	{
		int numGCLocations = buf.readVarInt();
		List<BlockPos> readGCLocations = new ArrayList<>(numGCLocations);			
		for(int i=0; i<numGCLocations;i++) {
			readGCLocations.add(buf.readBlockPos());
		}
		return new SyncAllGCWithClientPacket(readGCLocations);
	}

	public static void writePacketData(SyncAllGCWithClientPacket msg, FriendlyByteBuf buf)
	{
		msg.encode(buf);
	}
	
	public void encode(FriendlyByteBuf buf)
	{
			
		buf.writeVarInt(GCLocations.size());
		for ( BlockPos b : GCLocations) {
			buf.writeBlockPos(b);
		}

	}
}

