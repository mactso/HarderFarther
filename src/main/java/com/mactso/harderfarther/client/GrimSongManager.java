package com.mactso.harderfarther.client;

import com.mactso.harderfarther.sounds.ModSounds;

public class GrimSongManager {
	
	
	public static void startSong(int song) {
		if (song == ModSounds.NUM_DUSTY_MEMORIES) {
			PlayGrimSongs.playSong(ModSounds.DUSTY_MEMORIES);
		} else if (song == ModSounds.NUM_LABYRINTH_LOST_DREAMS) {
			PlayGrimSongs.playSong(ModSounds.LABYRINTH_LOST_DREAMS);
		} else if (song == ModSounds.NUM_LAKE_DESTINY) {
			PlayGrimSongs.playSong(ModSounds.LAKE_DESTINY);
		}
	}

}
