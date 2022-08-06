package com.mactso.harderfarther.network;

import java.util.function.Supplier;

import com.mactso.harderfarther.events.FogColorsEventHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class SyncFogToClientsPacket  {
		private double red;
		private double green;
		private double blue;
	
		public SyncFogToClientsPacket ( double r, double g, double b)
		{
			this.red = r;
			this.green = g;
			this.blue = b;
		}

		public static void processPacket(SyncFogToClientsPacket message, Supplier<NetworkEvent.Context> ctx)
		{
			ctx.get().enqueueWork( () -> 
				{
					FogColorsEventHandler.setFogRGB(message.red, message.green, message.blue);
				}
			);
			ctx.get().setPacketHandled(true);
		}
		
		public static SyncFogToClientsPacket readPacketData(FriendlyByteBuf buf) {
			double red = buf.readDouble();
			double green = buf.readDouble();
			double blue = buf.readDouble();
			return new SyncFogToClientsPacket(red, green, blue);
		}
		
		public static void writePacketData(SyncFogToClientsPacket msg, FriendlyByteBuf buf)
		{
			msg.encode(buf);
		}
		
		public void encode(FriendlyByteBuf buf)
		{
				
			buf.writeDouble(this.red);
			buf.writeDouble(this.green);
			buf.writeDouble(this.blue);

		}
}
