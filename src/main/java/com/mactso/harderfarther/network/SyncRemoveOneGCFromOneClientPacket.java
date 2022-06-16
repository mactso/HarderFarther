//package com.mactso.harderfarther.network;
//
//import java.util.List;
//import java.util.function.Supplier;
//
//import com.mactso.harderfarther.config.MyConfig;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.network.NetworkEvent;
//
//public class SyncRemoveOneGCFromOneClientPacket {
//	BlockPos pos;
//
//	public SyncRemoveOneGCFromOneClientPacket (BlockPos pos)
//	{
//		this.pos = pos;
//	}
//	
//	public static void processSyncRemoveOneGCFromOneClientPacket(SyncRemoveOneGCFromOneClientPacket message, Supplier<NetworkEvent.Context> ctx)
//	{
//		ctx.get().enqueueWork( () -> 
//			{
//				// TODO lupin.
//				List<BlockPos> l = MyConfig.getGrimCitadelsBlockPosList();
//				int lIdx = l.indexOf(message.pos);
//				l.remove(lIdx);
//				MyConfig.setGrimCitadelsBlockPosList(l);
//			}
//		);
//		ctx.get().setPacketHandled(true);
//	}
//
//	public static SyncRemoveOneGCFromOneClientPacket readPacketData(FriendlyByteBuf buf)
//	{
//		BlockPos incPos = buf.readBlockPos();
//		return new SyncRemoveOneGCFromOneClientPacket(incPos);
//	}
//
//	public static void writePacketData(SyncRemoveOneGCFromOneClientPacket msg, FriendlyByteBuf buf)
//	{
//		msg.encode(buf);
//	}
//	
//	public void encode(FriendlyByteBuf buf)
//	{
//		buf.writeBlockPos(this.pos);
//	}
//}
