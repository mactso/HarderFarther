// 16.2+ harder farther
package com.mactso.harderfarther;




import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.events.ChunkEvent;
import com.mactso.harderfarther.events.ExperienceDropEventHandler;
import com.mactso.harderfarther.events.MonsterDropEventHandler;
import com.mactso.harderfarther.events.SpawnEventHandler;
import com.mactso.harderfarther.item.ModItems;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.FMLNetworkConstants;


@Mod("harderfarther")
public class Main {

	    public static final String MODID = "harderfarther"; 
	    
	    public Main()
	    {
	    	System.out.println(MODID + ": Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
	    	System.out.println(MODID + ": Registering Mod.");
	        ModLoadingContext.get().registerExtensionPoint(DisplayTest.class,
	        		() -> new DisplayTest(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

	    }


		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
				System.out.println(MODID + ": Registering Handlers");
//				MinecraftForge.EVENT_BUS.register(new SpawnerBreakEvent ());
				MinecraftForge.EVENT_BUS.register(new SpawnEventHandler());
				MinecraftForge.EVENT_BUS.register(new MonsterDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ExperienceDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ChunkEvent());
		}   
		
		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

		    @SubscribeEvent
		    public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
		    {
		        ModItems.register(event.getRegistry());
		    }

	    }	
	

}
