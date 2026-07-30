package com.ypsi.fundamentalism.mixins.clientMixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManaBarOverlay.class)
public class ManaBarMixin {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"
            ),remap = false
    )
    private int changeManaTextColor(GuiGraphics instance, Font font, String text, int x, int y, int color) {
        var player = Minecraft.getInstance().player;
        int exhaustion = FatigueManager.getFatigueLevel(player);

        int newColor = Util.getExhaustionColor(exhaustion);

        return instance.drawString(font, text, x, y, newColor);
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"
            ), remap = false
    )
    private void redirectFillBlit(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        var player = Minecraft.getInstance().player;
        int exhaustion = FatigueManager.getFatigueLevel(player);

        float[] tint = Util.getExhaustionColors(Util.getExhaustionColor(exhaustion));
        RenderSystem.setShaderColor(tint[0]/255, tint[1]/255, tint[2]/255, 1.0f);
        instance.blit(atlasLocation, x, y, uOffset, vOffset, uWidth, vHeight);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
