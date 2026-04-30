package com.ypsi.fundamentalism.mixins.clientMixins;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(Gui.class)
public class LifeGuiMixin {

    @Unique
    private static final ResourceLocation REINFORCED_HEART =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/reinforced_h.png");

    @Unique
    private static long lastHealth = 0;
    @Unique
    private static long blinkEndTime = 0;


    @Inject(method = "*(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIZZZ)V", at = @At("TAIL"))
    private void onRenderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean halfHeart, boolean blinking, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player != null && player.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
            long gameTime = minecraft.level.getGameTime();

            int currentHealth = (int) player.getHealth();
            if (currentHealth != lastHealth) {
                blinkEndTime = gameTime + 10L;
                lastHealth = currentHealth;
            }
            int color = Utils.packRGB(Util.getElementalColor(player));

            boolean isBlinking = gameTime < blinkEndTime;
            boolean shouldBlinkWhite = isBlinking && ((gameTime % 5L) < 3L);

            int finalColor = shouldBlinkWhite ? mixColors(color, 0xFFFFFF, 0.8f) : color;

            float r = ((finalColor >> 16) & 0xFF) / 255.0f;
            float g = ((finalColor >> 8) & 0xFF) / 255.0f;
            float b = (finalColor & 0xFF) / 255.0f;

            guiGraphics.setColor(r, g, b, 1.0f);
            guiGraphics.blit(REINFORCED_HEART, x, y, 0, 0, 9, 9, 9, 9);
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        }
    }
    private static int mixColors(int colorA, int colorB, float ratio) {
        int rA = (colorA >> 16) & 0xFF;
        int gA = (colorA >> 8) & 0xFF;
        int bA = colorA & 0xFF;

        int rB = (colorB >> 16) & 0xFF;
        int gB = (colorB >> 8) & 0xFF;
        int bB = colorB & 0xFF;

        int r = (int)(rA * (1 - ratio) + rB * ratio);
        int g = (int)(gA * (1 - ratio) + gB * ratio);
        int b = (int)(bA * (1 - ratio) + bB * ratio);

        return (r << 16) | (g << 8) | b;
    }


}
