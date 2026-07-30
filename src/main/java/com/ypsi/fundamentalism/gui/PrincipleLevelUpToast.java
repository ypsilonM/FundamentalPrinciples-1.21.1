package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import oshi.jna.platform.mac.SystemB;

@OnlyIn(Dist.CLIENT)
public class PrincipleLevelUpToast implements Toast {

    private final Component title;
    private final Component subtitle;
    private long createdTime;
    private boolean hasPlayedSound;
    private String category;

    private static final ResourceLocation TOAST_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/toast.png");

    public PrincipleLevelUpToast(String category, int newLevel) {
        this.category = category;
        this.title = Component.literal(PrinciplesProgressionManager.getShortCategoryDisplayName(category)  + " perfected ");
        this.subtitle = Component.literal("Level " + newLevel + " achieved");
        this.createdTime = System.currentTimeMillis();
    }


    @Override
    public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceStarted) {
        if (!hasPlayedSound) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
            hasPlayedSound = true;
        }

        int w = this.width();
        int h = this.height();
        guiGraphics.blit(TOAST_TEXTURE, 0, 0, 0, 0, w, h, w, h);

        String symbol = PrinciplesProgressionManager.getCategorySymbol(category);
        Font font = toastComponent.getMinecraft().font;

        int iconCenterX = 8 + 16 / 2;
        int iconCenterY = 8 + 16 / 2;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(iconCenterX, iconCenterY, 0);
        pose.scale(2, 2, 1.0f);
        int halfWidth = font.width(symbol) / 2;
        int halfHeight = font.lineHeight / 2;
        guiGraphics.drawString(font, symbol, -halfWidth, -halfHeight, 0xFFB800);
        pose.popPose();

        guiGraphics.drawString(font, this.title, 30, 7, 0xFFAA00);
        guiGraphics.drawString(font, this.subtitle, 30, 18, 0xFFCE4D);

        long elapsed = System.currentTimeMillis() - createdTime;
        return elapsed >= 3000 ? Visibility.HIDE : Visibility.SHOW;
    }

    @Override
    public Object getToken() {
        return Toast.super.getToken();
    }

    @Override
    public int width() {
        return Toast.super.width();
    }

    @Override
    public int height() {
        return Toast.super.height();
    }

    @Override
    public int slotCount() {
        return Toast.super.slotCount();
    }
}
