package com.mactso.harderfarther.network;

import com.mactso.harderfarther.client.GrimSongManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class GrimClientSongPacket {
	private int song;
	
	public GrimClientSongPacket (int song )
	{
		this.song = song;
	}
	
	public static void processPacket(GrimClientSongPacket message, Context ctx)
	{
		ctx.enqueueWork( () -> 
			{
				GrimSongManager.startSong(message.song);
			}
		);
		ctx.setPacketHandled(true);
	}
	
	public static GrimClientSongPacket readPacketData(FriendlyByteBuf buf)
	{
		int song = buf.readInt();
		return new GrimClientSongPacket(song);
	}
	
	public static void writePacketData(GrimClientSongPacket msg, FriendlyByteBuf buf)
	{
		msg.encode(buf);
	}
	
	public void encode(FriendlyByteBuf buf)
	{
			
		buf.writeInt(this.song);

	}
}
