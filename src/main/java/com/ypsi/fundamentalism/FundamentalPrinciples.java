package com.ypsi.fundamentalism;

import com.ypsi.fundamentalism.advancements.triggers.YpTriggers;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.block.YpsBlocks;
import com.ypsi.fundamentalism.block.YpsEntityBlocks;
import com.ypsi.fundamentalism.block.custom.DomainBlockEntityRenderer;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.datagen.RecipeSerializers;
import com.ypsi.fundamentalism.datagen.providers.ModLootProvider;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.mobs.cherry_bird.CherryBirdRenderer;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerRenderer;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpRenderer;
import com.ypsi.fundamentalism.entity.mobs.runear.RunearRenderer;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusRenderer;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsRenderer;
import com.ypsi.fundamentalism.entity.spells.domain.DomainRenderer;
import com.ypsi.fundamentalism.entity.spells.pull.PullRenderer;
import com.ypsi.fundamentalism.entity.spells.holy_lightning.HolyLightningRenderer;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskRenderer;
import com.ypsi.fundamentalism.entity.spells.sol.SolRenderer;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.item.ModCreativeModTabs;
import com.ypsi.fundamentalism.item.ModFluids;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.*;
import com.ypsi.fundamentalism.network.commands.ExhaustionCommand;
import com.ypsi.fundamentalism.network.commands.ShowToastCommand;
import com.ypsi.fundamentalism.network.commands.SpellCategoriesCommand;
import com.ypsi.fundamentalism.network.commands.SpellbookLevelCommand;
import com.ypsi.fundamentalism.particle.*;
import com.ypsi.fundamentalism.sound.ModSounds;
import com.ypsi.fundamentalism.spells.ModSpells;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import io.redspace.ironsspellbooks.fluids.SimpleClientFluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
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
        YpsEntityBlocks.register(modEventBus);

        ModFluids.register(modEventBus);

        ModLootProvider.register(modEventBus);

        YpsDataComponents.register(modEventBus);
        ModSounds.register(modEventBus);

        ModEffects.register(modEventBus);
        ModParticles.register(modEventBus);
        //ModNetwork.register(modEventBus);
        ModEntities.register(modEventBus);

        YpsAttributes.register(modEventBus);
        YpsAttachments.register(modEventBus);
        YpTriggers.register(modEventBus);

        YpsSchoolRegistry.register(modEventBus);
        ModSpells.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        RecipeSerializers.registrar(modEventBus);

        modContainer.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            clientReg(modEventBus);
        }


    }
    private void clientReg(IEventBus modEventBus) {
        ModKeyBinds.register(modEventBus);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ExhaustionCommand.registerSet(event.getDispatcher());
        SpellCategoriesCommand.register(event.getDispatcher());
        SpellbookLevelCommand.register(event.getDispatcher());

        //ShowToastCommand.register(event.getDispatcher());

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
//        LOGGER.info("HELLO FROM COMMON SETUP");
//
//        if (ServerConfig.logDirtBlock)
//            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
//
//        LOGGER.info(ServerConfig.magicNumberIntroduction + ServerConfig.magicNumber);
//
//        ServerConfig.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.COMBAT){
//            event.accept(ModItems.ORB);
//            event.accept(ModItems.PURE_ORB);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }


    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.IMP.get(), ImpRenderer::new);
            event.registerEntityRenderer(ModEntities.HEMOMANCER.get(), HemomancerRenderer::new);
            event.registerEntityRenderer(ModEntities.VENEMERUS.get(), VenemerusRenderer::new);
            event.registerEntityRenderer(ModEntities.RUNEAR.get(), RunearRenderer::new);
            event.registerEntityRenderer(ModEntities.CHERRY_BIRD.get(), CherryBirdRenderer::new);

            event.registerEntityRenderer(ModEntities.HOLY_LIGHTNING_PROJECTILE.get(), HolyLightningRenderer::new);
            event.registerEntityRenderer(ModEntities.CHAINS.get(), ChainsRenderer::new);

            event.registerEntityRenderer(ModEntities.PULL_PROJECTILE.get(), PullRenderer::new);
            event.registerEntityRenderer(ModEntities.SOL_PROJECTILE.get(), SolRenderer::new);
            event.registerEntityRenderer(ModEntities.THORN_PROJECTILE.get(), ThornRenderer::new);
            event.registerEntityRenderer(ModEntities.SACRED_DISK.get(), SacredDiskRenderer::new);

            event.registerEntityRenderer(ModEntities.DOMAIN_ENTITY.get(), DomainRenderer::new);

            event.registerBlockEntityRenderer(
                    YpsEntityBlocks.DOMAIN_BLOCK_ENTITY.get(),
                    DomainBlockEntityRenderer::new
            );


        }

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticles.REINFORCEMENT_PARTICLE.get(), ReinforceParticles.Provider::new);
            event.registerSpriteSet(ModParticles.CONSTELLATION_PARTICLE.get(), ConstellationParticle.Provider::new);

            event.registerSpriteSet(ModParticles.MINDFUL_PARTICLE.get(), MindfulParticle.Provider::new);
            event.registerSpriteSet(ModParticles.REGRESSION_PARTICLE.get(), RegressionParticle.Provider::new);
            //event.registerSpriteSet(ModParticles.SOL_PARTICLE.get(), SolAppearanceParticle.Provider::new);
        }
        @SubscribeEvent
        public static void registerClient(RegisterClientExtensionsEvent event){
            event.registerFluidType(new SimpleClientFluidType(
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "block/arcane_mixture")),
                    ModFluids.ARCANE_MIXTURE_TYPE);
            event.registerFluidType(new SimpleClientFluidType(
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "block/pitcher_extract")),
                    ModFluids.PITCHER_EXTRACT_TYPE);
        }



    }



}
