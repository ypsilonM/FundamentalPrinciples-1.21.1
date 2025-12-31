package com.ypsi.fundamentalism.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
    private final PlayerModel<Player> modelNormal;
    private final PlayerModel<Player> modelSlim;
    private final EntityModelSet modelSet;
    private final Minecraft minecraft;

    public ReinforcementLayer(RenderLayerParent<Player, PlayerModel<Player>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.modelSet = modelSet;
        this.minecraft = Minecraft.getInstance();
        this.modelNormal = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER), false);
        this.modelSlim = new PlayerModel<>(modelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Player player, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        boolean hasEffect = player.hasEffect(ModEffects.REINFORCEMENT_EFFECT);

        if (hasEffect) {
            if (minecraft.level == null) return;

            int color1 = increaseSaturation(Utils.packRGB(getElementalColor(player)), 1f);
            int color = rgbToArgb(color1, 0.3f);
            PlayerModel<Player> currentModel = determineModel(player);

            this.getParentModel().copyPropertiesTo(currentModel);
            currentModel.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            currentModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            ResourceLocation whiteTexture = ResourceLocation.parse("minecraft:textures/block/white_concrete.png");

            float gameTime = minecraft.level.getGameTime() + partialTicks;

            renderPartWithScale(poseStack, currentModel.head, buffer,gameTime,
                    1.15f, whiteTexture, color, currentModel);

            renderPartWithScale(poseStack, currentModel.rightArm, buffer,gameTime,
                    1.2f, whiteTexture, color, currentModel);

            renderPartWithScale(poseStack, currentModel.leftArm, buffer,gameTime,
                    1.2f, whiteTexture, color, currentModel);

            renderPartWithScale(poseStack, currentModel.rightLeg, buffer,gameTime,
                    1.2f, whiteTexture, color, currentModel);

            renderPartWithScale(poseStack, currentModel.leftLeg, buffer,gameTime,
                    1.2f, whiteTexture, color, currentModel);

            renderPartWithScale(poseStack, currentModel.body, buffer,gameTime,
                    1.1f, whiteTexture, color, currentModel);
        }
    }
    private void renderPartWithScale(PoseStack poseStack, ModelPart part, MultiBufferSource buffer,
                                     float gameTime, float baseScale,
                                     ResourceLocation texture, int color, PlayerModel<Player> model) {
        poseStack.pushPose();

        float centerX = part.x / 16.0f;
        float centerY = part.y / 16.0f;
        float centerZ = part.z / 16.0f;
        boolean isLeg = part == model.rightLeg || part == model.leftLeg;
        boolean isArm = part == model.rightArm || part == model.leftArm;

        if (isLeg) {
            centerY += 0.6f;
        } else if (isArm) {
            centerY += 0.5f;
        } else if (part == model.head) {
            centerY -= 0.2f;
        }
//        float pulse = (float) Math.sin(gameTime * 0.1f) * 0.5f + 0.5f;
        float pulse = 0.01f;
        float scale;
        poseStack.translate(centerX, centerY, centerZ);

        if(part == model.body){
            scale = baseScale + 0.2f * pulse;
            poseStack.scale(baseScale, baseScale-.1f, scale+(scale*0.1f));
        }else{
            if(part == model.head){
                scale = baseScale + 0.05f * pulse;
                float nScale = Math.clamp(scale, baseScale, scale);
                poseStack.scale(nScale, nScale, nScale);
            }else {
                scale = baseScale + 0.2f * pulse;
                poseStack.scale(scale, baseScale-0.1f+(scale*0.01f), scale);
            }
        }
        poseStack.translate(-centerX, -centerY, -centerZ);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        part.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color);

        poseStack.popPose();
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
        // hsb[1] es la saturación
        hsb[1] = Math.min(1.0f, hsb[1] * (1 + saturationFactor));

        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) & 0xFFFFFF;
    }
    public static Vector3f getElementalColor(Player player){
        final double EPSILON = 0.001;  // 0.1%

        Map<SchoolType, Double> schoolPowers = new LinkedHashMap<>();
        for(SchoolType school : SchoolRegistry.REGISTRY){
            double power = school.getPowerFor(player);
            double rounded = Math.round(power * 1000.0) / 1000.0;
            schoolPowers.put(school, rounded);
        }
        double maxValue = schoolPowers.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        if(maxValue <= 0){
            return Utils.deconstructRGB(0x8FEDF2);
        }
        long count = schoolPowers.values().stream()
                .filter(power -> Math.abs(power - maxValue) < EPSILON)
                .count();
        if(count != 1){
            return Utils.deconstructRGB(0x8FEDF2);
        }
        return schoolPowers.entrySet().stream()
                .filter(entry -> Math.abs(entry.getValue() - maxValue) < EPSILON)
                .map(Map.Entry::getKey)
                .findFirst()
                .map(SchoolType::getTargetingColor)
                .orElse(Utils.deconstructRGB(0x8FEDF2));
    }
}
