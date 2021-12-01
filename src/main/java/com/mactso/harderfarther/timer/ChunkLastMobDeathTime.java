package com.mactso.harderfarther.timer;

public class ChunkLastMobDeathTime implements IChunkLastMobDeathTime
{

	private long lastKillTime;

	@Override
	public long getLastKillTime() {
		return lastKillTime;
	}


	@Override
	public void setLastKillTime(long gametime) {
		lastKillTime = gametime;
		
	}

}
