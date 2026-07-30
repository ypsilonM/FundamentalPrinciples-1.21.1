package com.ypsi.fundamentalism.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.ClientConfig;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskRenderer;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornRenderer;
import com.ypsi.fundamentalism.gui.PrincipleLevelUpToast;
import com.ypsi.fundamentalism.gui.PrinciplesScreen;
import com.ypsi.fundamentalism.gui.TierWheelOverlay;
import com.ypsi.fundamentalism.item.ModFluids;
import com.ypsi.fundamentalism.keybind.KeyState;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.ClientToastPacket;
import com.ypsi.fundamentalism.network.packets.ToggleReinforcementPacket;
import com.ypsi.fundamentalism.render.ChargeSpellVisuals;
import com.ypsi.fundamentalism.render.ReinforcementLayer;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.fluids.SimpleClientFluidType;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.player.ClientSpellCastHelper;
import io.redspace.ironsspellbooks.render.animation.AnimationHelper;
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
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosTooltip;

import java.util.ArrayList;

import static com.ypsi.fundamentalism.util.Util.getExhaustionColor;
import static com.ypsi.fundamentalism.util.Util.getMaxFatigue;


public class ClientEvents {
    @EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, value = Dist.CLIENT)
    public static class Registration {
        //Eventos MOD

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event){
            event.enqueueWork(() -> {
                ClientToastPacket.toastAction = (player, packet) -> {
                    Minecraft.getInstance().getToasts().addToast(
                            new PrincipleLevelUpToast(packet.category(), packet.newLevel())
                    );
                };
            });
        }



        @SubscribeEvent
        public static void registerOverlays(RegisterGuiLayersEvent event) {
            event.registerAboveAll(
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "levels_wheel"),
                    TierWheelOverlay.instance
            );

            event.registerAbove(
                    VanillaGuiLayers.HOTBAR,
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "exhaustion_bottle"),
                    (guiGraphics, partialTick) -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        if (minecraft.player == null || minecraft.options.hideGui) return;
                        Player player = minecraft.player;
                        int exhaustion = FatigueManager.getFatigueAmount(player);
                        int exhaustionLvl = FatigueManager.getFatigueLevel(player);
                        if (exhaustion <= 0 && exhaustionLvl == 0) return;
                        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                        int hotbarCenterX = screenWidth / 2;
                        int hotbarRightEdge = hotbarCenterX + 91;

                        int offsetX = ClientConfig.XOFFSET.get();
                        int offsetY = ClientConfig.YOFFSET.get();

                        int x = hotbarRightEdge + 10 + offsetX;
                        int y = screenHeight - 20 + offsetY;


                        renderBottleExhaustionBar(guiGraphics, x, y, exhaustion, exhaustionLvl, player);
                    }
            );
        }


        private static void renderBottleExhaustionBar(GuiGraphics gui, int x, int y, int exhaustion, int exhaustionLvl, Player player) {
            int maxEx = getMaxFatigue(exhaustionLvl, player);
            renderExhaustionCounter(gui, x, y, exhaustion, exhaustionLvl ,maxEx );
//            renderExhaustionLevel(gui, x, y, exhaustionLvl, BOTTLE_HEIGHT, BOTTLE_WIDTH);

        }
        private static void renderExhaustionCounter(GuiGraphics gui, int bottleX, int bottleY, int exhaustion, int exhaustionLvl, int maxEx) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            String exhaustionText = "["+(exhaustion);
            exhaustionText+="/"+maxEx+"]";
            int exhaustionX = bottleX ;
            int exhaustionY = bottleY + 2;

            PoseStack poseStack = gui.pose();

            poseStack.pushPose();

            int exhaustionColor = getExhaustionColor(exhaustionLvl);

            gui.drawString(font, exhaustionText, (exhaustionX), (exhaustionY), exhaustionColor, true);
            poseStack.popPose();
        }
        private static void renderExhaustionLevel(GuiGraphics gui, int bottleX, int bottleY, int exhaustionLvl, int height, int width) {
            Minecraft minecraft = Minecraft.getInstance();
            Font font = minecraft.font;

            String levelText = String.valueOf(exhaustionLvl);
            int levelX = bottleX + (width/2)-2;
            int levelY = bottleY - height + 4;

            PoseStack poseStack = gui.pose();

            poseStack.pushPose();
            int scaledLevelX = (int) (levelX);
            int scaledLevelY = (int) (levelY);

            int levelColor = getExhaustionColor(exhaustionLvl);

            gui.drawString(font, levelText, scaledLevelX, scaledLevelY, levelColor, false);

            poseStack.popPose();
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
                    SpellAnimations.SELF_CAST_ANIMATION.getForPlayer()
                            .ifPresent(resourceLocation -> AnimationHelper.animatePlayerStart(minecraft.player, resourceLocation));
                    PacketDistributor.sendToServer(new ToggleReinforcementPacket());

                }
                if(CATEGORIES.wasPressed()){
                    if (minecraft.screen == null) {
                        minecraft.player.playSound(SoundEvents.BOOK_PAGE_TURN, 1f, 1f);
                        minecraft.setScreen(new PrinciplesScreen());
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
