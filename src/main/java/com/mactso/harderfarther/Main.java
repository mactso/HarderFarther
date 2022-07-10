// 16.2+ harder farther
package com.mactso.harderfarther;

import com.mactso.harderfarther.block.ModBlocks;
import com.mactso.harderfarther.blockentities.ModBlockEntities;
import com.mactso.harderfarther.config.GrimCitadelManager;
import com.mactso.harderfarther.config.HarderFartherCommands;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.events.BlockEvents;
import com.mactso.harderfarther.events.ChunkEvent;
import com.mactso.harderfarther.events.ExperienceDropEventHandler;
import com.mactso.harderfarther.events.FogColorsEventHandler;
import com.mactso.harderfarther.events.LivingEventMovementHandler;
import com.mactso.harderfarther.events.MonsterDropEventHandler;
import com.mactso.harderfarther.events.PlayerInteractionEventHandler;
import com.mactso.harderfarther.events.PlayerLoginEventHandler;
import com.mactso.harderfarther.events.SpawnEventHandler;
import com.mactso.harderfarther.item.ModItems;
import com.mactso.harderfarther.network.Register;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint.DisplayTest;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.api.distmarker.Dist;


@Mod("harderfarther")
public class Main {

	    public static final String MODID = "harderfarther"; 
	    public static LivingEventMovementHandler lem;
	    
	    public Main()
	    {

	    	System.out.println(MODID + ": Registering Mod.");
	  		FMLJavaModLoadingContext.get().getModEventBus().register(this);
 	        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,MyConfig.COMMON_SPEC );
	    	System.out.println(MODID + ": Registering Mod.");
	        ModLoadingContext.get().registerExtensionPoint(DisplayTest.class,
	        		() -> new DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

	    }

		@OnlyIn(Dist.CLIENT)
	    @SubscribeEvent
	    public void setupClient(final FMLClientSetupEvent event) {
	    	
			MinecraftForge.EVENT_BUS.register(new FogColorsEventHandler());
			ModBlocks.setRenderLayer();
			
	    }
	    
	    @SubscribeEvent
	    public void setupCommon(final FMLCommonSetupEvent event)
	    {
	        Register.initPackets();
	    }

		@SubscribeEvent 
		public void preInit (final FMLCommonSetupEvent event) {
				System.out.println(MODID + ": Registering Handlers");
//				MinecraftForge.EVENT_BUS.register(new SpawnerBreakEvent ());
				MinecraftForge.EVENT_BUS.register(new SpawnEventHandler());
				MinecraftForge.EVENT_BUS.register(new MonsterDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ExperienceDropEventHandler());
				MinecraftForge.EVENT_BUS.register(new ChunkEvent());
				MinecraftForge.EVENT_BUS.register(new PlayerLoginEventHandler());
//				MinecraftForge.EVENT_BUS.register(new PlayerInteractionEventHandler());
				lem = new LivingEventMovementHandler();
				MinecraftForge.EVENT_BUS.register(lem);
				MinecraftForge.EVENT_BUS.register(new BlockEvents());
 		}  
		
		
		
		@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	    public static class ModEvents
	    {

		    @SubscribeEvent
		    public static void onItemsRegistry(final RegistryEvent.Register<Item> event)
		    {
		        ModItems.register(event.getRegistry());
		    }
		    
		    @SubscribeEvent
		    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event)
		    {
		        ModBlocks.register(event.getRegistry());
		    }
		    
		    @SubscribeEvent
		    public static void onBlockEntitiesRegistry(final RegistryEvent.Register<BlockEntityType<?>> event)
		    {
		        ModBlockEntities.register(event.getRegistry());
		    }
		    
	    }	
		
		@Mod.EventBusSubscriber(bus = Bus.FORGE)
		public static class ForgeEvents
		{
			@SubscribeEvent
			public static void onServerAbout(ServerAboutToStartEvent event)
			{
				GrimCitadelManager.load(event.getServer());
			}

			@SubscribeEvent
			public static void onServerStopping(ServerStoppingEvent event)
			{
				GrimCitadelManager.clear();
				Utility.debugMsg(0, MODID + "Cleanup Successful");
			}

			@SubscribeEvent 		
			public static void onCommandsRegistry(final RegisterCommandsEvent event) {
				System.out.println("Happy Trails: Registering Command Dispatcher");
				HarderFartherCommands.register(event.getDispatcher());			
			}

		}
		
		
	
		

}
