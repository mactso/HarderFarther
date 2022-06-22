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
	private static int grimBonusDistance = MyConfig.getGrimCitadelBonusDistance()
			* MyConfig.getGrimCitadelBonusDistance();
	private static float sliderRedPercent = 1.0f;
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

		if (range > grimBonusDistance) {
			if (sliderRedPercent == 1.0f)
				return;
			if ( (colorTick != gametick)) {
				colorTick = gametick;
				sliderRedPercent = adjustSlider(sliderRedPercent, 1.0f);
			}
			slideFogColor(event, sliderRedPercent);
			return;
		}

 		float percent = (range / grimBonusDistance) / 2;

		if (percent < 0.05f)
			percent = 0.05f;

		if (colorTick != gametick) {
			colorTick = gametick;
			sliderRedPercent = adjustSlider(sliderRedPercent, percent);
		}

		slideFogColor(event, sliderRedPercent);
	}

	private void slideFogColor(FogColors event, float slider) {
		float g = event.getGreen();
		event.setGreen(g * slider);
		float b = event.getBlue();
		event.setBlue(b * slider);
	}

	
	@SubscribeEvent
	public void handleFogRender(RenderFogEvent event) {

		if (event.getMode() == FogMode.FOG_TERRAIN) {
			Minecraft m = Minecraft.getInstance();
			LocalPlayer p = m.player;
			long gametick = p.level.getGameTime();
			
			float range = GrimCitadelManager.getClosestGrimCitadelDistanceSq(p.blockPosition());
			if (range > grimBonusDistance) {
				
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

			float percent = (range / grimBonusDistance);

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
}
