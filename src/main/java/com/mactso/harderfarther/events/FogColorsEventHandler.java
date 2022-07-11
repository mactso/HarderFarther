package com.mactso.harderfarther.events;

import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer.FogMode;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FogColorsEventHandler {
	private static int grimBonusDistanceSq = MyConfig.getGrimCitadelBonusDistanceSq();
	private static float sliderColorPercent = 1.0f;
	private long colorTick = 0;
	private static float sliderFogPercent = 1.0f;
	private long fogTick = 0;
	private static float sliderStartFogPercent = 1.0f;

	@SubscribeEvent
	public void onFogColorCheck(FogColors event) {

		
		Minecraft m = Minecraft.getInstance();
		LocalPlayer p = m.player;
		long gametick = p.level.getGameTime();

		float range = GrimCitadelManager.getClosestGrimCitadelDistanceSq(p.blockPosition());

		if ((range > grimBonusDistanceSq) || (!MyConfig.isGrimCitadels())) {
			if (sliderColorPercent == 1.0f)
				return;
			if ( (colorTick != gametick)) {
				colorTick = gametick;
				sliderColorPercent = adjustSlider(sliderColorPercent, 1.0f);
			}
			slideFogColor(event, sliderColorPercent);
			return;
		}

 		float percent = (range / grimBonusDistanceSq) / 2;

		if (percent < 0.05f)
			percent = 0.05f;

		if (colorTick != gametick) {
			colorTick = gametick;
			sliderColorPercent = adjustSlider(sliderColorPercent, percent);
		}

		slideFogColor(event, sliderColorPercent);
	}

	
	private float adjustSlider(float slider, float target) {
		final double slideAmount = 0.005f;
		if (slider > target) {
			slider -= slideAmount;
		} else if (slider < target) {
			slider += slideAmount;
		} else {
			slider = target;
		}
		return slider;
	}
	
	
	private void slideFogColor(FogColors event, float slider) {
		float redSlider = slider;
		if (redSlider < MyConfig.getGrimFogRedPercent()) {
			redSlider = (float) MyConfig.getGrimFogRedPercent();
		}
		float greenSlider = slider;
		if (greenSlider < MyConfig.getGrimFogGreenPercent()) {
			greenSlider = (float) MyConfig.getGrimFogGreenPercent();
		}
		float blueSlider = slider;
		if (blueSlider < MyConfig.getGrimFogBluePercent()) {
			blueSlider = (float) MyConfig.getGrimFogBluePercent();
		}
		float r = event.getRed();
		event.setRed(r * redSlider);
		float g = event.getGreen();
		event.setGreen(g * greenSlider);
		float b = event.getBlue();
		event.setBlue(b * blueSlider);
	}

	
	
	// Density of Fog- not Color
	@SubscribeEvent
	public void handleFogRender(RenderFogEvent event) {

		if (event.getMode() == FogMode.FOG_TERRAIN) {
			Minecraft m = Minecraft.getInstance();
			LocalPlayer p = m.player;
			long gametick = p.level.getGameTime();
			
			float range = GrimCitadelManager.getClosestGrimCitadelDistanceSq(p.blockPosition());
			if (range > grimBonusDistanceSq) {
				
				if ((sliderFogPercent == 1.0f) && (sliderStartFogPercent == 1.0f))
					return;
				
				if ( (fogTick != gametick)) {
					fogTick = gametick;
					sliderStartFogPercent = adjustSlider(sliderStartFogPercent, 1.0f);
					sliderFogPercent = adjustSlider(sliderFogPercent, 1.0f);
				}
				
				adjustFogDistance(event, sliderStartFogPercent, sliderFogPercent);
				return;
			}	

			float percent = (range / grimBonusDistanceSq);

			if (percent < 0.01f) {
				percent = 0.01f + ((0.01f - percent) * 100);
			}

			if (percent < 0.61f) {
				percent = (0.61f + percent) / 2;
			}

			if (percent > 0.999999f) {
				percent = 0.999999f;
			}

			sliderStartFogPercent = adjustSlider(sliderStartFogPercent, 0.10f);
			sliderFogPercent = adjustSlider(sliderFogPercent, percent);

			adjustFogDistance(event, sliderStartFogPercent, sliderFogPercent);

		}

	}

	private void adjustFogDistance(RenderFogEvent event, float startFogPercent, float fogPercent) {
		
		float f1 = RenderSystem.getShaderFogStart();
		float f2 = RenderSystem.getShaderFogEnd();

		f1 = (f1 * startFogPercent) * fogPercent;
		f2 *= fogPercent;

		RenderSystem.setShaderFogStart(f1);
		RenderSystem.setShaderFogEnd(f2);
		
	}


}
