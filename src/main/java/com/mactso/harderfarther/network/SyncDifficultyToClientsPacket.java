package com.mactso.harderfarther.network;

import com.mactso.harderfarther.events.FogColorsEventHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;

public class SyncDifficultyToClientsPacket  {
		private float hardDifficulty;
		private float grimDifficulty;
		private float timeDifficulty;
	
		public SyncDifficultyToClientsPacket ( float hard, float grim, float time)
		{
			this.hardDifficulty = hard;
			this.grimDifficulty = grim;
			this.timeDifficulty = time;
		}

		public static void processPacket(SyncDifficultyToClientsPacket message, Context ctx)
		{
			ctx.enqueueWork( () -> 
				{
					FogColorsEventHandler.setLocalDifficulty(message.hardDifficulty, message.grimDifficulty, message.timeDifficulty);
				}
			);
			ctx.setPacketHandled(true);
		}
		
		public static SyncDifficultyToClientsPacket readPacketData(FriendlyByteBuf buf) {
			float hard = buf.readFloat();
			float grim = buf.readFloat();
			float time = buf.readFloat();
			return new SyncDifficultyToClientsPacket(hard, grim, time);
		}
		
		public static void writePacketData(SyncDifficultyToClientsPacket msg, FriendlyByteBuf buf)
		{
			msg.encode(buf);
		}
		
		public void encode(FriendlyByteBuf buf)
		{
			buf.writeFloat(this.hardDifficulty);
			buf.writeFloat(this.grimDifficulty);
			buf.writeFloat(this.timeDifficulty);

		}
}
