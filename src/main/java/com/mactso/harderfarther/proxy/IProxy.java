package com.mactso.harderfarther.proxy;

import java.util.List;

import net.minecraft.core.BlockPos;

public interface IProxy
{	
	public void setClientGrimCitadelListValues(List<BlockPos> grimList);
	public void setClientFogColors( int r, int g, int b );
	public void setClientGrimSong(int song);
}
