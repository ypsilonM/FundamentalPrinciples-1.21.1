package com.ypsi.fundamentalism.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ReinforcementLayer extends RenderLayer<Player, PlayerModel<Player>> {
    private static final ResourceLocation REINFORCEMENT_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/mana_b.png");

    private final PlayerModel<Player> modelNormal;
    private final PlayerModel<Player> modelSlim;

    public ReinforcementLayer(RenderLayerParent<Player, PlayerModel<Player>> renderer, EntityModelSet modelSet) {
        super(renderer);

        this.modelNormal = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), false);
        this.modelSlim = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);

    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Player player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        if (player.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
            PlayerModel<Player> currentModel = determineModel(player);

            int color1 = increaseSaturation(Utils.packRGB(Util.getElementalColor(player)), 1f);

            float gameTime = player.tickCount + partialTicks;
            float pulse = (float) Math.sin(gameTime * 0.15f) * 0.075f + 0.325f;
            int color = rgbToArgb(color1, pulse);

            this.getParentModel().copyPropertiesTo(currentModel);
            currentModel.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            currentModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            VertexConsumer consumer = buffer.getBuffer(
                    RenderType.entityTranslucentEmissive(REINFORCEMENT_TEXTURE)
            );
            poseStack.pushPose();
            float scalePulse = (float) Math.sin(gameTime * 0.15f) * 0.02f + 1.05f;
            poseStack.scale(scalePulse, scalePulse, scalePulse);

            currentModel.renderToBuffer(poseStack, consumer, LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY, color);

            poseStack.popPose();
        }
    }

    private PlayerModel<Player> determineModel(Player player) {
        try {
            Field slimField = PlayerModel.class.getDeclaredField("slim");
            slimField.setAccessible(true);
            boolean isSlim = slimField.getBoolean(this.getParentModel());
            return isSlim ? modelSlim : modelNormal;
        } catch (Exception e) {
            String rendererStr = this.getParentModel().toString().toLowerCase();
            if (rendererStr.contains("slim")) {
                return modelSlim;
            }
            return modelNormal;
        }
    }

    public static int rgbToArgb(int rgb, float alpha) {
        int alphaByte = (int)(alpha * 255) << 24;
        return alphaByte | (rgb & 0xFFFFFF);
    }

    public static int increaseSaturation(int rgb, float saturationFactor) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        hsb[1] = Math.min(1.0f, hsb[1] * (1 + saturationFactor));
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0xFFFFFF;
    }
}
