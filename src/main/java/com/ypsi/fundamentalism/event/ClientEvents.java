package com.ypsi.fundamentalism.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskRenderer;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.gui.TierWheelOverlay;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.ClientExhaustionData;
import com.ypsi.fundamentalism.network.packets.ToggleReinforcementPacket;
import com.ypsi.fundamentalism.render.ChargeSpellVisuals;
import com.ypsi.fundamentalism.render.ReinforcementLayer;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.player.KeyState;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.ArrayList;


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
                        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                        int hotbarCenterX = screenWidth / 2;
                        int hotbarRightEdge = hotbarCenterX + 91;

                        int x = hotbarRightEdge + 20;
                        int y = screenHeight - 25;

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

            gui.blit(EMPTY_BOTTLE_TEXTURE, x, y - size, 0, 0, size, size, size, size);

            if (progress > 0) {
                int fillHeight = (int) (size * progress);
                int textureV = getLiquidTextureV(progress);

                gui.blit(LIQUID_TEXTURE,
                        x, y - fillHeight,
                        0, textureV + (size - fillHeight),
                        size, fillHeight,
                        size, size);
            }
            renderExhaustionCounter(gui, x, y, exhaustion, size);
        }
        private static void renderExhaustionCounter(GuiGraphics gui, int bottleX, int bottleY, int exhaustion, int size) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            int counterX = bottleX - 2;
            int counterY = bottleY - size - 2;
            String text = String.valueOf(exhaustion);

            PoseStack poseStack = gui.pose();
            poseStack.pushPose();
            poseStack.scale(0.7f, 0.7f, 1.0f);

            float inverseScale = 1 / 0.7f;
            int scaledX = (int) (counterX * inverseScale);
            int scaledY = (int) (counterY * inverseScale);

            int textColor = getExhaustionTextColor(exhaustion);

            gui.drawString(font, text, scaledX + 1, scaledY + 1, 0x000000, false);
            gui.drawString(font, text, scaledX, scaledY, textColor, false);

            poseStack.popPose();
        }

        private static int getLiquidTextureV(float progress) {
            if (progress < 0.25f) return 0;     // Green
            if (progress < 0.5f) return 16;     // Yellow
            if (progress < 0.75f) return 32;    // Orange
            return 48;                          // Red
        }
        private static int getExhaustionTextColor(int exhaustion) {
            if (exhaustion < 25) return 0x00FF00; // Green
            if (exhaustion < 50) return 0xFFFF00; // Yellow
            if (exhaustion < 75) return 0xFFA500; // Orange
            return 0xFF0000; // Red
        }


        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ThornRenderer.MODEL_LAYER_LOCATION, ThornRenderer::createBodyLayer);
            event.registerLayerDefinition(SacredDiskRenderer.MODEL_LAYER_LOCATION, SacredDiskRenderer::createDiskLayer);
        }


        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.AddLayers event) {
            addLayerToPlayerSkin(event, PlayerSkin.Model.SLIM);
            addLayerToPlayerSkin(event, PlayerSkin.Model.WIDE);
        }
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.AddLayers event) {
            addReinforcementLayerToRenderer(event, PlayerSkin.Model.WIDE);
            addReinforcementLayerToRenderer(event, PlayerSkin.Model.SLIM);
        }

        private static void addReinforcementLayerToRenderer(EntityRenderersEvent.AddLayers event, PlayerSkin.Model modelType) {
            EntityRenderer<? extends Player> renderer = event.getSkin(modelType);
            if (renderer instanceof LivingEntityRenderer) {
                @SuppressWarnings("unchecked")
                LivingEntityRenderer<Player, PlayerModel<Player>> playerRenderer =
                        (LivingEntityRenderer<Player, PlayerModel<Player>>) renderer;

                playerRenderer.addLayer(new ReinforcementLayer(playerRenderer, event.getEntityModels()));
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
