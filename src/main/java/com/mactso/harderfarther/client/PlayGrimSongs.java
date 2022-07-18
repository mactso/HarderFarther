package com.mactso.harderfarther.client;

import java.util.Random;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayGrimSongs {
	boolean blockmusic = false;
	private static Minecraft mc = null;
	private static MusicManager musicTicker = null;
	private static final Random rand = new Random();
	// this is kludgy since I'm hard stopping any currently playing song.
	// there *is* an official way of doing this.
	private static long clientPsuedoTicks = 0;
	private static long grimSongDelayTicks = 0;

	@SubscribeEvent
	public static void playSong(SoundEvent song) {
		playSong(song, 3600, 4800);
	}

	public static void playSong(SoundEvent song, int minDelay, int maxDelay) {

		doInit();

		clientPsuedoTicks = Util.getMillis() / 50;
		if (grimSongDelayTicks < clientPsuedoTicks) {
			grimSongDelayTicks = clientPsuedoTicks + (600); // ignore calls within 30 seconds.  
			// TODO: need forcestart=true/false parm
		}
		musicTicker.stopPlaying();
		boolean replaceCurrentMusic = true;
		Music m = new Music(song, minDelay, maxDelay, replaceCurrentMusic);
		musicTicker.startPlaying(m);
	}

	private static void doInit() {
		if (mc == null) {
			rand.setSeed(Util.getMillis()); // TODO is this safe?
			mc = Minecraft.getInstance(); // TODO - can I init this once safely?
			// TODO - can I init music manager once safely?
		}
		if (musicTicker == null) {
			musicTicker = mc.getMusicManager();
		}
	}
}
