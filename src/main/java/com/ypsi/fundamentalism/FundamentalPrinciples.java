package com.ypsi.fundamentalism;
import com.ypsi.fundamentalism.attributes.ModAttributes;
import com.ypsi.fundamentalism.block.ModBlocks;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.imp.ImpRenderer;
import com.ypsi.fundamentalism.item.ModCreativeModTabs;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.ModNetwork;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FundamentalPrinciples.MOD_ID)
public class FundamentalPrinciples {
    public static final String MOD_ID = "ypfundamentals";

    private static final Logger LOGGER = LogUtils.getLogger();

    public FundamentalPrinciples(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEffects.register(modEventBus);
        ModKeyBinds.register(modEventBus);

        ModNetwork.register(modEventBus);

        ModEntities.register(modEventBus);

        ModAttributes.register(modEventBus);
//        ModDataComponents.register(modEventBus);

        // Register the item to a creative tab

        modEventBus.addListener(this::addCreative);
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.COMBAT){
            event.accept(ModItems.ORB);
            event.accept(ModItems.PURE_ORB);
        }
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES){
            event.accept(ModBlocks.MANA_BLOCK);
        }
        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS){
            event.accept(ModBlocks.MANA_ORE);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.IMP.get(), ImpRenderer::new);
        }
    }

}
