package com.mactso.harderfarther.events;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.item.ModItems;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class HandleTabSetup {

	// TODO - rename this to HandleTabSetup next time in here.
	@SubscribeEvent
	public static void handleTabSetup (BuildCreativeModeTabContentsEvent event)
    {

		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.BURNISHING_STONE);
			event.accept(ModItems.LIFE_HEART);
		} else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
			event.accept(ModItems.DEAD_BRANCHES);
		}
    }
}

