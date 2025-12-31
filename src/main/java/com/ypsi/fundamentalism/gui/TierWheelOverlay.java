package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ypsi.fundamentalism.network.ModNetwork;
import com.ypsi.fundamentalism.network.packets.UpdateSpellLevelPacket;
import com.ypsi.fundamentalism.util.SpellAttributeUtils;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.SyncedSpellData;
import io.redspace.ironsspellbooks.gui.overlays.SpellSelection;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.network.gui.SelectSpellPacket;
import io.redspace.ironsspellbooks.player.ClientInputEvents;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import io.redspace.ironsspellbooks.util.TooltipsUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.Platform;

public class TierWheelOverlay implements LayeredDraw.Layer {
    public static TierWheelOverlay instance = new TierWheelOverlay();

    public final static ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(IronsSpellbooks.MODID, "textures/gui/icons.png");

    private final Vector4f lineColor = new Vector4f(1f, .85f, .7f, 1f);
    private final Vector4f radialButtonColor = new Vector4f(.04f, .03f, .01f, .6f);
    private final Vector4f highlightColor = new Vector4f(.8f, .7f, .55f, .7f);

    private final float ringInnerEdge = 20;
    private float ringOuterEdge = 80;
    private final float ringOuterEdgeMax = 80;
    private final float ringOuterEdgeMin = 65;

    public boolean active;
    private int wheelSelection;

    public SpellData selectedSpell = null;

    public void open() {
        selectedSpell = null;
        active = true;
        wheelSelection = -1;
        Minecraft.getInstance().mouseHandler.releaseMouse();
    }

    public void close() {
        active = false;
        if (wheelSelection >= 0 && selectedSpell != null) {
            Player player = Minecraft.getInstance().player;
            if(player!=null) {
                String spellId = selectedSpell.getSpell().getSpellId();
                String aaSpellId = spellId.replace(":", "/");

                int totalLevels = selectedSpell.getLevel();
                int levelSelected = wheelSelection + 1;
                int targetLevel = levelSelected - totalLevels;


                PacketDistributor.sendToServer(new UpdateSpellLevelPacket(aaSpellId, targetLevel));
                player.displayClientMessage(
                        Component.literal("Spell Level: " + levelSelected).withStyle(ChatFormatting.GOLD),
                        true
                );
                player.playSound(
                        SoundEvents.BOOK_PAGE_TURN,
                        1,
                        1.2F
                );
            }
        }
        Minecraft.getInstance().mouseHandler.grabMouse();
    }

    public void render(GuiGraphics guiHelper, DeltaTracker deltaTracker) {
        if (Minecraft.getInstance().options.hideGui || Minecraft.getInstance().player.isSpectator()) {
            return;
        }
        var screenWidth = guiHelper.guiWidth();
        var screenHeight = guiHelper.guiHeight();
        if (!active)
            return;

        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.screen != null || minecraft.mouseHandler.isMouseGrabbed()) {
            close();
            return;
        }

        var swsm = new SpellSelectionManager(player);
        selectedSpell = swsm.getSpellData(swsm.getSelectionIndex());
        int totalLevels = selectedSpell.getLevel();
        //Levels Enable
        SpellData[] sp = new SpellData[totalLevels];
        for(int i=0; i<totalLevels; i++){
            sp[i] = new SpellData(selectedSpell.getSpell(), i+1);
        }

        if (totalLevels <= 0) {
            close();
            return;
        }
        PoseStack poseStack = guiHelper.pose();
        poseStack.pushPose();

        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        Vec2 screenCenter = new Vec2(minecraft.getWindow().getScreenWidth() * .5f, minecraft.getWindow().getScreenHeight() * .5f);
        Vec2 mousePos = new Vec2((float) minecraft.mouseHandler.xpos(), (float) minecraft.mouseHandler.ypos());
        double radiansPerSpell = Math.toRadians(360 / (float) totalLevels);

        float mouseRotation = (Utils.getAngle(mousePos, screenCenter) + 1.570f + (float) radiansPerSpell * .5f) % 6.283f;

        wheelSelection = (int) Mth.clamp(mouseRotation / radiansPerSpell, 0, totalLevels - 1);
        if (mousePos.distanceToSqr(screenCenter) < ringOuterEdgeMin * ringOuterEdgeMin) {
            wheelSelection = Math.max(0, totalLevels-1);
        }

        guiHelper.fill(0, 0, screenWidth, screenHeight, 0);

        drawRadialBackgrounds(guiHelper, centerX, centerY, wheelSelection, totalLevels);
        drawDividingLines(guiHelper, centerX, centerY, totalLevels);


        //Text background
        var selectedSpell = sp[wheelSelection];
        var spellLevel = selectedSpell.getLevel();

        var font = Minecraft.getInstance().font;
        var info = selectedSpell.getSpell().getUniqueInfo(spellLevel, minecraft.player);

        int textHeight = Math.max(2, info.size()) * font.lineHeight + 5;
        int textCenterMargin = 5;
        int textTitleMargin = 5;

        var title = selectedSpell.getSpell().getDisplayName(minecraft.player).withStyle(Style.EMPTY.withUnderlined(true));
        var level = Component.translatable("ui.irons_spellbooks.level", Util.getPlainLevelComponenet(selectedSpell, player).withStyle(selectedSpell.getSpell().getRarity(spellLevel).getDisplayName().getStyle()));
        var mana = Component.translatable("ui.irons_spellbooks.mana_cost", selectedSpell.getSpell().getManaCost(spellLevel)).withStyle(ChatFormatting.AQUA);
//            selectedSpell.getUniqueInfo(minecraft.player).forEach((line) -> lines.add(line.withStyle(ChatFormatting.DARK_GREEN)));

        drawTextBackground(guiHelper, centerX, centerY, ringOuterEdge + textHeight - textTitleMargin - font.lineHeight, textCenterMargin, Math.max(2, info.size()) * font.lineHeight);
        guiHelper.drawString(font, title, (int) (centerX - font.width(title) / 2), (int) (centerY - (ringOuterEdge + textHeight)), 0xFFFFFF, true);
        guiHelper.drawString(font, level, (int) (centerX - font.width(level) - textCenterMargin), (int) (centerY - (ringOuterEdge + textHeight) + font.lineHeight + textTitleMargin), 0xFFFFFF, true);
        guiHelper.drawString(font, mana, (int) (centerX - font.width(mana) - textCenterMargin), (int) (centerY - (ringOuterEdge + textHeight) + font.lineHeight * 2 + textTitleMargin), 0xFFFFFF, true);

        for (int i = 0; i < info.size(); i++) {
            var line = info.get(i);
            guiHelper.drawString(font, line, (int) (centerX + textCenterMargin), (int) (centerY - (ringOuterEdgeMax + textHeight) + font.lineHeight * (i + 1) + textTitleMargin), 0x3be33b, true);
        }

        //Spell Icons
        float scale = Mth.lerp(totalLevels / 15f, 2, 1.25f) * .65f;
        double radius = 3 / scale * (ringInnerEdge + ringInnerEdge) * .5 * (.85f + .25f * (totalLevels / 15f));
        Vec2[] locations = new Vec2[totalLevels];
        for (int i = 0; i < locations.length; i++) {
            locations[i] = new Vec2((float) (Math.sin(radiansPerSpell * i) * radius), (float) (-Math.cos(radiansPerSpell * i) * radius));
        }
        for (int i = 0; i < locations.length; i++) {
            var spell = selectedSpell.getSpell();
            if (spell != null) {
                var texture = spell.getSpellIconResource();
                poseStack.pushPose();
                poseStack.translate(centerX, centerY, 0);
                poseStack.scale(scale, scale, scale);

                //Icon
                int iconWidth = 16 / 2;
                int borderWidth = 32 / 2;
                int cdWidth = 16 / 2;

                guiHelper.blit(texture, (int) locations[i].x - iconWidth, (int) locations[i].y - iconWidth, 0, 0, 16, 16, 16, 16);
                /*
                Border
                 */
                guiHelper.blit(TEXTURE, (int) locations[i].x - borderWidth, (int) locations[i].y - borderWidth, swsm.getSelectionIndex() == i ? 32 : 0, 106, 32, 32);
                /*
                Cooldown
                 */
                float f = ClientMagicData.getCooldownPercent(spell);
                if (f > 0) {
                    RenderSystem.enableBlend();
                    int pixels = (int) (16 * f + 1f);

                    guiHelper.blit(TEXTURE, (int) locations[i].x - cdWidth, (int) locations[i].y + cdWidth - pixels, 47, 87, 16, pixels);
                }
                poseStack.popPose();
            }
        }


        poseStack.popPose();
    }

    private void drawTextBackground(GuiGraphics guiHelper, float centerX, float centerY, float textYOffset, int textCenterMargin, int textHeight) {
        guiHelper.fill(0, 0, (int) (centerX * 2), (int) (centerY * 2), 0);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        centerY = centerY - textYOffset - 2;
        int heightMax = textHeight / 2 + 4;
        int heightMin = 0;
        int widthMax = 70;
        int widthMin = 0;

        widthMin = -1;
        widthMax = 1;

        final VertexConsumer vertexConsumer = guiHelper.bufferSource().getBuffer(RenderType.gui());
        Matrix4f m = guiHelper.pose().last().pose();
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMin, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMin, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);

        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        vertexConsumer.addVertex(m, centerX + widthMin, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMax + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), 0);
        vertexConsumer.addVertex(m, centerX + widthMax, centerY + heightMin + heightMax, 0f).setColor(radialButtonColor.x(), radialButtonColor.y(), radialButtonColor.z(), radialButtonColor.w());
        RenderSystem.disableBlend();
    }

    private void drawRadialBackgrounds(GuiGraphics guiGraphics, float centerX, float centerY, int selectedSpellIndex, int totalLevels) {
        float quarterCircle = Mth.HALF_PI;
        int totalSpellsAvailable = totalLevels;
        int segments;
        if (totalSpellsAvailable < 6) {
            segments = totalSpellsAvailable % 2 == 1 ? 15 : 12;
        } else {
            segments = totalSpellsAvailable * 2;
        }
        float radiansPerObject = 2 * Mth.PI / segments;
        float radiansPerSpell = 2 * Mth.PI / totalSpellsAvailable;
        ringOuterEdge = Math.max(ringOuterEdgeMin, ringOuterEdgeMax);
        for (int i = 0; i < segments; i++) {
            final float beginRadians = i * radiansPerObject - (quarterCircle + (radiansPerSpell / 2));
            final float endRadians = (i + 1) * radiansPerObject - (quarterCircle + (radiansPerSpell / 2));

            final float x1m1 = Mth.cos(beginRadians) * ringInnerEdge;
            final float x2m1 = Mth.cos(endRadians) * ringInnerEdge;
            final float y1m1 = Mth.sin(beginRadians) * ringInnerEdge;
            final float y2m1 = Mth.sin(endRadians) * ringInnerEdge;

            final float x1m2 = Mth.cos(beginRadians) * ringOuterEdge;
            final float x2m2 = Mth.cos(endRadians) * ringOuterEdge;
            final float y1m2 = Mth.sin(beginRadians) * ringOuterEdge;
            final float y2m2 = Mth.sin(endRadians) * ringOuterEdge;

            boolean isHighlighted = (i * totalSpellsAvailable) / segments == selectedSpellIndex;

            Vector4f color = radialButtonColor;
            if (isHighlighted) color = highlightColor;

            final VertexConsumer vertexConsumer = guiGraphics.bufferSource().getBuffer(RenderType.gui());
            final Matrix4f m = guiGraphics.pose().last().pose();

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m2, centerY + y2m2, 0).setColor(color.x(), color.y(), color.z(), 0);
            vertexConsumer.addVertex(m, centerX + x1m2, centerY + y1m2, 0).setColor(color.x(), color.y(), color.z(), 0);

            //Category line
            color = lineColor;
            float categoryLineWidth = 2;
            final float categoryLineOuterEdge = ringInnerEdge + categoryLineWidth;

            final float x1m3 = Mth.cos(beginRadians) * categoryLineOuterEdge;
            final float x2m3 = Mth.cos(endRadians) * categoryLineOuterEdge;
            final float y1m3 = Mth.sin(beginRadians) * categoryLineOuterEdge;
            final float y2m3 = Mth.sin(endRadians) * categoryLineOuterEdge;

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m3, centerY + y2m3, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x1m3, centerY + y1m3, 0).setColor(color.x(), color.y(), color.z(), color.w());

        }
    }

    private void drawDividingLines(GuiGraphics guiHelper, float centerX, float centerY, int totalLevels) {
        if (totalLevels <= 1)
            return;

        float quarterCircle = Mth.HALF_PI;
        float radiansPerSpell = 2 * Mth.PI / totalLevels;
        ringOuterEdge = Math.max(ringOuterEdgeMin, ringOuterEdgeMax);

        for (int i = 0; i < totalLevels; i++) {
            final float closeWidth = 8 * Mth.DEG_TO_RAD;
            final float farWidth = closeWidth / 4;
            final float beginCloseRadians = i * radiansPerSpell - (quarterCircle + (radiansPerSpell / 2)) - (closeWidth / 4);
            final float endCloseRadians = beginCloseRadians + closeWidth;
            final float beginFarRadians = i * radiansPerSpell - (quarterCircle + (radiansPerSpell / 2)) - (farWidth / 4);
            final float endFarRadians = beginCloseRadians + farWidth;

            final float x1m1 = Mth.cos(beginCloseRadians) * ringInnerEdge;
            final float x2m1 = Mth.cos(endCloseRadians) * ringInnerEdge;
            final float y1m1 = Mth.sin(beginCloseRadians) * ringInnerEdge;
            final float y2m1 = Mth.sin(endCloseRadians) * ringInnerEdge;

            final float x1m2 = Mth.cos(beginFarRadians) * ringOuterEdge * 1.4f;
            final float x2m2 = Mth.cos(endFarRadians) * ringOuterEdge * 1.4f;
            final float y1m2 = Mth.sin(beginFarRadians) * ringOuterEdge * 1.4f;
            final float y2m2 = Mth.sin(endFarRadians) * ringOuterEdge * 1.4f;

            Vector4f color = lineColor;
            final VertexConsumer vertexConsumer = guiHelper.bufferSource().getBuffer(RenderType.gui());
            Matrix4f m = guiHelper.pose().last().pose();

            vertexConsumer.addVertex(m, centerX + x1m1, centerY + y1m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m1, centerY + y2m1, 0).setColor(color.x(), color.y(), color.z(), color.w());
            vertexConsumer.addVertex(m, centerX + x2m2, centerY + y2m2, 0).setColor(color.x(), color.y(), color.z(), 0);
            vertexConsumer.addVertex(m, centerX + x1m2, centerY + y1m2, 0).setColor(color.x(), color.y(), color.z(), 0);
        }
    }

}
