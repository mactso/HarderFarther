package com.mactso.harderfarther.client;

import java.util.Optional;
import java.util.Random;

import org.jetbrains.annotations.NotNull;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

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
		playSong(song, 9600, 18200);
	}

	public static void playSong(SoundEvent song, int minDelay, int maxDelay) {

		doInit();

		Holder<SoundEvent> songHolder = ForgeRegistries.SOUND_EVENTS.getHolder(song).get();
		
		clientPsuedoTicks = Util.getMillis() / 50;
		if (grimSongDelayTicks < clientPsuedoTicks) {
			grimSongDelayTicks = clientPsuedoTicks + (1200); // ignore calls within 60 seconds.  
			// TODO: need force start=true/false parm
		}
		musicTicker.stopPlaying();

		// TODO make replaceCurrentMusic part of the network packet.
		boolean replaceCurrentMusic = true;
		Music m = new Music(songHolder, minDelay, maxDelay, replaceCurrentMusic);
		musicTicker.startPlaying(m);
	}

	private static void doInit() {
		if (mc == null) {
			rand.setSeed(Util.getMillis()); 
			mc = Minecraft.getInstance(); 
		}
		if (musicTicker == null) {
			musicTicker = mc.getMusicManager();
		}
	}
}
