// 16.1 harder farther
package com.mactso.harderfarther;




import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("harderfarther")
public class Main {

	    public static final String MODID = "harderfarther"; 
	    
	    public Main()
	    {
  			System.out.println("harderfarther: Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
//	      ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
	    }


		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
				System.out.println("harderfarther: Registering Handlers");
//				MinecraftForge.EVENT_BUS.register(new SpawnerBreakEvent ());
//				MinecraftForge.EVENT_BUS.register(new SpawnerSpawnEvent());
//				MinecraftForge.EVENT_BUS.register(new MyEntityPlaceEvent());
		}   
		
		

}
