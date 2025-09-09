package com.ypsi.fundamentalism.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.gui.TierWheelOverlay;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.ClientExhaustionData;
import com.ypsi.fundamentalism.network.packets.ToggleReinforcementPacket;
import com.ypsi.fundamentalism.render.ChargeSpellVisuals;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellBarOverlay;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.network.casting.CastPacket;
import io.redspace.ironsspellbooks.network.casting.QuickCastPacket;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.player.KeyMappings;
import io.redspace.ironsspellbooks.player.KeyState;
import io.redspace.ironsspellbooks.render.SpellTargetingLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


public class ClientEvents {
    @EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, value = Dist.CLIENT)
    public static class Registration {
        //Eventos MOD

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiLayersEvent event) {
            event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "levels_wheel"), TierWheelOverlay.instance);

            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "exhaustion_bottle"),
                    (guiGraphics, partialTick) -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if (minecraft.player == null || minecraft.options.hideGui) return;

                        int exhaustion = ClientExhaustionData.getCurrentExhaustion();
                        if (exhaustion <= 0) return;

                        // Calculate position to the LEFT of hotbar
                        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

                        // Hotbar is centered, so get its left edge position
                        int hotbarCenterX = screenWidth / 2;
                        int hotbarLeftEdge = hotbarCenterX - 91; // Hotbar width is 182px, so left edge is center - 91

                        // Position: fixed left position with spacing
                        int x = hotbarLeftEdge - 20; // 20px to the left of hotbar edge
                        int y = screenHeight - 25; // Fixed position at bottom (align with hotbar)

                        renderBottleExhaustionBar(guiGraphics, x, y, exhaustion);
                    }
            );
        }

        private static void renderBottleExhaustionBar(GuiGraphics gui, int x, int y, int exhaustion) {
            float progress = Math.min(1.0f, exhaustion / 100.0f);

            ResourceLocation EMPTY_BOTTLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    FundamentalPrinciples.MOD_ID, "textures/gui/empty_bottle.png"
            );
            ResourceLocation LIQUID_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    FundamentalPrinciples.MOD_ID, "textures/gui/liquid.png"
            );

            int size = 16;

            // Botella vacía - posición FIJA
            gui.blit(EMPTY_BOTTLE_TEXTURE, x, y - size, 0, 0, size, size, size, size);

            // Líquido - se llena hacia arriba pero la botella no se mueve
            if (progress > 0) {
                int fillHeight = (int) (size * progress);
                int textureV = getLiquidTextureV(progress);

                // Render liquid from BOTTOM UP at fixed position
                gui.blit(LIQUID_TEXTURE,
                        x, y - fillHeight,           // Posición fija, solo cambia el fill height
                        0, textureV + (size - fillHeight), // Crop texture from top
                        size, fillHeight,            // Solo renderizar la parte llena
                        size, size);
            }
        }

        private static int getLiquidTextureV(float progress) {
            // Select color row based on exhaustion level
            if (progress < 0.25f) return 0;     // Green
            if (progress < 0.5f) return 16;     // Yellow
            if (progress < 0.75f) return 32;    // Orange
            return 48;                          // Red
        }


        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ThornRenderer.MODEL_LAYER_LOCATION, ThornRenderer::createBodyLayer);
        }


        @SubscribeEvent
        public static void registerRenderers(final EntityRenderersEvent.AddLayers event) {
            addLayerToPlayerSkin(event, PlayerSkin.Model.SLIM);
            addLayerToPlayerSkin(event, PlayerSkin.Model.WIDE);
            for (EntityType type : event.getEntityTypes()) {
                var renderer = event.getRenderer(type);
                if (renderer instanceof LivingEntityRenderer livingRenderer) {
                    livingRenderer.addLayer(new SpellTargetingLayer.Vanilla<>(livingRenderer));
                }
            }
        }
        @SuppressWarnings({"rawtypes", "unchecked"})
        private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinName) {
            EntityRenderer<? extends Player> render = event.getSkin(skinName);
            if (render instanceof LivingEntityRenderer livingRenderer) {
                livingRenderer.addLayer(new ChargeSpellVisuals.Vanilla<>(livingRenderer));
            }
        }

        @EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID,  value = Dist.CLIENT)
        public static class Runtime {
            //Eventos FORGE

            private static int useKeyId = Integer.MIN_VALUE;
            public static boolean isUseKeyDown;
            public static boolean hasReleasedSinceCasting;

            private static final ArrayList<KeyState> KEY_STATES = new ArrayList<>();
            private static final KeyState SELECTION = register(ModKeyBinds.SELECTION_KEY.get());
            private static final KeyState REINFORCE = register(ModKeyBinds.REINFORCE_KEY.get());

            @SubscribeEvent
            public static void onKeyInput(InputEvent.Key event) {
                handleInputEvent(event.getKey(), event.getAction());
            }
            @SubscribeEvent
            public static void onMouseInput(InputEvent.MouseButton.Pre event) {handleInputEvent(event.getButton(), event.getAction());}

            private static void handleInputEvent(int button, int action) {
                var minecraft = Minecraft.getInstance();
                Player player = minecraft.player;
                if (player == null) {
                    return;
                }
               handleRightClickSuppression(button, action);

                if(REINFORCE.wasPressed()){
                    minecraft.player.playSound(SoundEvents.END_PORTAL_FRAME_FILL, 0.9f, 0.7f);
                    PacketDistributor.sendToServer(new ToggleReinforcementPacket());
                }
                if (SELECTION.wasPressed()) {
                    if (minecraft.screen == null) {
                        TierWheelOverlay.instance.open();
                    }
                }
                if (SELECTION.wasReleased()) {
                    if (minecraft.screen == null && TierWheelOverlay.instance.active) {
                        TierWheelOverlay.instance.close();
                    }
                }
                update();
            }
            private static void update() {
                for (KeyState k : KEY_STATES) {
                    k.update();
                }
            }
//            @SubscribeEvent
//            public static void onKeyPress(ClientTickEvent.Post event) {
//                Minecraft mc = Minecraft.getInstance();
//
//
//                if (ModKeyBinds.REINFORCE_KEY.get().consumeClick()) {
//
//                }
//            }

            private static KeyState register(KeyMapping key) {
                var k = new KeyState(key);
                KEY_STATES.add(k);
                return k;
            }

            private static void handleRightClickSuppression(int button, int action) {
                if (useKeyId == Integer.MIN_VALUE) {
                    useKeyId = Minecraft.getInstance().options.keyUse.getKey().getValue();
                }

                if (button == useKeyId) {
                    if (action == InputConstants.RELEASE) {
                        ClientSpellCastHelper.setSuppressRightClicks(false);
                        isUseKeyDown = false;
                        hasReleasedSinceCasting = true;
                    } else if (action == InputConstants.PRESS) {
                        isUseKeyDown = true;
                    }
                }
            }

        }


    }
}
