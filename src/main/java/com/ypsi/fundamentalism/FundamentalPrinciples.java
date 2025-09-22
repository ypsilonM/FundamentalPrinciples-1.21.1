package com.ypsi.fundamentalism;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.block.YpsBlocks;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerRenderer;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpRenderer;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusRenderer;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsRenderer;
import com.ypsi.fundamentalism.entity.spells.pull.PullRenderer;
import com.ypsi.fundamentalism.entity.spells.holy_lightning.HolyLightningRenderer;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskRenderer;
import com.ypsi.fundamentalism.entity.spells.sol.SolRenderer;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.item.ModCreativeModTabs;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.ModNetwork;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.particle.ReinforceParticles;
import com.ypsi.fundamentalism.render.ReinforcementLayer;
import com.ypsi.fundamentalism.spells.ModSpells;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(FundamentalPrinciples.MOD_ID)
public class FundamentalPrinciples {
    public static final String MOD_ID = "ypfundamentals";

    public static final Logger LOGGER = LogUtils.getLogger();

    public FundamentalPrinciples(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        YpsBlocks.register(modEventBus);
        ModEffects.register(modEventBus);
        ModParticles.register(modEventBus);

        ModKeyBinds.register(modEventBus);
        ModNetwork.register(modEventBus);

        ModEntities.register(modEventBus);

        YpsAttributes.register(modEventBus);
        YpsAttachments.register(modEventBus);

        ModSpells.register(modEventBus);
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
//            event.accept(ModItems.ORB);
//            event.accept(ModItems.PURE_ORB);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents
    {

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.IMP.get(), ImpRenderer::new);
            event.registerEntityRenderer(ModEntities.HEMOMANCER.get(), HemomancerRenderer::new);
            event.registerEntityRenderer(ModEntities.VENEMERUS.get(), VenemerusRenderer::new);

            event.registerEntityRenderer(ModEntities.HOLY_LIGHTNING_PROJECTILE.get(), HolyLightningRenderer::new);
            event.registerEntityRenderer(ModEntities.CHAINS.get(), ChainsRenderer::new);

            event.registerEntityRenderer(ModEntities.PULL_PROJECTILE.get(), PullRenderer::new);
            event.registerEntityRenderer(ModEntities.SOL_PROJECTILE.get(), SolRenderer::new);
            event.registerEntityRenderer(ModEntities.THORN_PROJECTILE.get(), ThornRenderer::new);
            event.registerEntityRenderer(ModEntities.SACRED_DISK.get(), SacredDiskRenderer::new);
        }
        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event){
            event.registerSpriteSet(ModParticles.REINFORCEMENT_PARTICLE.get(), ReinforceParticles.Provider::new);
        }


    }



}
