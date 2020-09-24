package com.mactso.harderfarther.timer;

public class ChunkLastMobDeathTime implements IChunkLastMobDeathTime
{
	Object object;
	private long lastKillTime;

	public ChunkLastMobDeathTime(Object object) {
		this.object = object;
	}


	@Override
	public long getLastKillTime() {
		return lastKillTime;
	}


	@Override
	public void setLastKillTime(long gametime) {
		lastKillTime = gametime;
		
	}




}
