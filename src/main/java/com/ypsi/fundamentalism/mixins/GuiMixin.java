package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
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

@Mixin(Gui.class)
public class GuiMixin {
    @Unique
    private static final ResourceLocation REINFORCED_HEART_OUTLINE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/reinforced_heart.png");
    @Unique
    private static final ResourceLocation REINFORCED_HEART_OUTLINE_WHITE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/reinforced_heart_white.png");
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
            boolean isBlinking = gameTime < blinkEndTime;
            boolean shouldBlinkWhite = isBlinking && ((gameTime % 5L) < 3L);

            ResourceLocation texture = shouldBlinkWhite ? REINFORCED_HEART_OUTLINE_WHITE : REINFORCED_HEART_OUTLINE;
            guiGraphics.blit(texture, x, y, 0, 0, 9, 9, 9, 9);
        }
    }

}
