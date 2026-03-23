package com.ypsi.fundamentalism.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.entity.spells.proiectumProjectile.ProiectumRenderer;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskRenderer;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.gui.SpellLevelsScreen;
import com.ypsi.fundamentalism.gui.TierWheelOverlay;
import com.ypsi.fundamentalism.keybind.KeyState;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.ToggleReinforcementPacket;
import com.ypsi.fundamentalism.render.ChargeSpellVisuals;
import com.ypsi.fundamentalism.render.ReinforcementLayer;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
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
import static com.ypsi.fundamentalism.util.Util.getMaxExPerLevel;


public class ClientEvents {
    @EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, value = Dist.CLIENT)
    public static class Registration {
        //Eventos MOD

//        @SubscribeEvent
//        public static void onSpellBookLevelUp(SpellBookLevelUpEvent event) {
//            Minecraft mc = Minecraft.getInstance();
//            if (mc.player != null && mc.player.equals(event.getPlayer())) {
//                mc.player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1, 1);
//            }
//        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiLayersEvent event) {
            event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "levels_wheel"), TierWheelOverlay.instance);

            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "exhaustion_bottle"),
                    (guiGraphics, partialTick) -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if (minecraft.player == null || minecraft.options.hideGui) return;
                        Player player = minecraft.player;
                        int exhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION);
                        int exhaustionLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
                        if (exhaustion <= 0 && exhaustionLvl == 0) return;
                        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                        int hotbarCenterX = screenWidth / 2;
                        int hotbarRightEdge = hotbarCenterX + 91;

                        int x = hotbarRightEdge + 20;
                        int y = screenHeight - 25;

                        renderBottleExhaustionBar(guiGraphics, x, y, exhaustion, exhaustionLvl, player);
                    }
            );
        }

        private static void renderBottleExhaustionBar(GuiGraphics gui, int x, int y, int exhaustion, int exhaustionLvl, Player player) {
            int maxEx = getMaxExPerLevel(exhaustionLvl, player);
            float progress = Math.min(1.0f, exhaustion / (float)maxEx);

            ResourceLocation EMPTY_BOTTLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    FundamentalPrinciples.MOD_ID, "textures/gui/empty_bottle.png"
            );

            ResourceLocation LIQUID_TEXTURE = switch (exhaustionLvl) {
              case 4 -> ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/exhaustion/lvl4.png");
              case 3 -> ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/exhaustion/lvl3.png");
              case 2 -> ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/exhaustion/lvl2.png");
              case 1 -> ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/exhaustion/lvl1.png");
              default -> ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/exhaustion/lvl0.png");
            };

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
            renderExhaustionCounter(gui, x, y, exhaustion, exhaustionLvl ,maxEx , size);
            renderExhaustionLevel(gui, x, y, exhaustionLvl, size);
        }
        private static void renderExhaustionCounter(GuiGraphics gui, int bottleX, int bottleY, int exhaustion, int exhaustionLvl, int maxEx,int size) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            String exhaustionText = String.valueOf(exhaustion);
            int exhaustionX = bottleX + size - 4;
            int exhaustionY = bottleY + 2;

            PoseStack poseStack = gui.pose();

            poseStack.pushPose();
            poseStack.scale(0.8f, 0.8f, 0.8f);

            int scaledExhaustionX = (int) (exhaustionX / 0.8f);
            int scaledExhaustionY = (int) (exhaustionY / 0.8f);

            int exhaustionColor = getExhaustionTextColor(exhaustionLvl);
            int shadowColor = getExhaustionShadowColor(exhaustionLvl);

            gui.drawString(font, exhaustionText, scaledExhaustionX + 1, scaledExhaustionY + 1, shadowColor, false);
            gui.drawString(font, exhaustionText, scaledExhaustionX, scaledExhaustionY, exhaustionColor, false);
            poseStack.popPose();
        }
        private static void renderExhaustionLevel(GuiGraphics gui, int bottleX, int bottleY, int exhaustionLvl, int size) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            String levelText = String.valueOf(exhaustionLvl);
            int levelX = bottleX - 2;
            int levelY = bottleY - size - 2;

            PoseStack poseStack = gui.pose();

            poseStack.pushPose();
            poseStack.scale(0.8f, 0.8f, 0.8f);

            int scaledLevelX = (int) (levelX / 0.8f);
            int scaledLevelY = (int) (levelY / 0.8f);

            int levelColor = getExhaustionTextColor(exhaustionLvl);
            int shadowColor = getExhaustionShadowColor(exhaustionLvl);

            gui.drawString(font, levelText, scaledLevelX + 1, scaledLevelY + 1, shadowColor, false);
            gui.drawString(font, levelText, scaledLevelX, scaledLevelY, levelColor, false);

            poseStack.popPose();
        }

        private static int getExhaustionTextColor(int exhaustionLevel) {
            return switch (exhaustionLevel){
                case 4 -> 0xFF3366; // Rojo vibrante
                case 3 -> 0xCC33CC; // Púrpura brillante
                case 2 -> 0x3366FF; // Azul brillante
                case 1 -> 0x3399FF; // Azul cielo
                default -> 0x33CCCC; // Turquesa
            };
        }
        private static int getExhaustionShadowColor(int exhaustionLevel) {
            return switch (exhaustionLevel){
                case 4 -> 0x660022; // Sombra rojo más oscuro
                case 3 -> 0x660066; // Sombra púrpura más oscuro
                case 2 -> 0x001188; // Sombra azul más oscuro
                case 1 -> 0x004488; // Sombra azul medio más oscuro
                default -> 0x004444; // Sombra verde azulado más oscuro
            };
        }
        private static int getLiquidTextureV(float progress) {
            if (progress < 0.25f) return 0;
            if (progress < 0.5f) return 16;
            if (progress < 0.75f) return 32;
            return 48;
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(ThornRenderer.MODEL_LAYER_LOCATION, ThornRenderer::createBodyLayer);
            event.registerLayerDefinition(SacredDiskRenderer.MODEL_LAYER_LOCATION, SacredDiskRenderer::createDiskLayer);
            event.registerLayerDefinition(ProiectumRenderer.MODEL_LAYER_LOCATION, ProiectumRenderer::createBodyLayer);
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

            private static int useKeyId = Integer.MIN_VALUE;
            public static boolean isUseKeyDown;
            public static boolean hasReleasedSinceCasting;

            private static final ArrayList<KeyState> KEY_STATES = new ArrayList<>();
            private static final KeyState SELECTION = register(ModKeyBinds.SELECTION_KEY.get());
            private static final KeyState REINFORCE = register(ModKeyBinds.REINFORCE_KEY.get());
            private static final KeyState CATEGORIES = register(ModKeyBinds.SPELL_CATEGORIES.get());

            @SubscribeEvent
            public static void onKeyInput(InputEvent.Key event) {
                updateAllKeyStates();
                handleInputEvent(event.getKey(), event.getAction());
            }
            @SubscribeEvent
            public static void onMouseInput(InputEvent.MouseButton.Pre event) {
                updateAllKeyStates();
                handleInputEvent(event.getButton(), event.getAction());
            }
            private static void updateAllKeyStates() {
                for (KeyState k : KEY_STATES) {
                    k.update();
                }
            }

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
                if(CATEGORIES.wasPressed()){
                    if (minecraft.screen == null) {
                        minecraft.setScreen(new SpellLevelsScreen());
                    }
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
