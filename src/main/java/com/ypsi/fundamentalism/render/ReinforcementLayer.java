package com.ypsi.fundamentalism.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
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
import java.lang.reflect.Field;

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

            int color = rgbToArgb(getElementalColor(player), 0.3f);
            PlayerModel<Player> currentModel = determineModel(player);

            this.getParentModel().copyPropertiesTo(currentModel);
            currentModel.prepareMobModel(player, limbSwing, limbSwingAmount, partialTicks);
            currentModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            ResourceLocation whiteTexture = ResourceLocation.parse("minecraft:textures/block/white_concrete.png");

            float gameTime = minecraft.level.getGameTime() + partialTicks;

            renderPartWithScale(poseStack, currentModel.head, buffer,gameTime,
                    1.1f, whiteTexture, color, currentModel);

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
            centerY += 0.6f; // Las piernas son más largas, centro más abajo
        } else if (isArm) {
            centerY += 0.5f; // Los brazos son largos, centro más abajo
        } else if (part == model.head) {
            centerY -= 0.3f; // La cabeza necesita ajuste vertical
        }

        float pulse = (float) Math.sin(gameTime * 0.1f) * 0.5f + 0.5f;
        float scale;
        poseStack.translate(centerX, centerY, centerZ);

        if(part == model.body){
            scale = baseScale + 0.2f * pulse;
            poseStack.scale(baseScale, baseScale, scale+(scale*0.1f));
        }else{
            if(part == model.head){
                scale = baseScale + 0.08f * pulse;
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
    public static int getElementalColor(Player player){
        double[] values = {
                player.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.LIGHTNING_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.NATURE_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.HOLY_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.ENDER_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.EVOCATION_SPELL_POWER),
                player.getAttributeValue(AttributeRegistry.ELDRITCH_SPELL_POWER)
        };
        int[] colors = {
                0xff7b08, // Fuego
                0x0f2bff, // Rayo
                0x12e7ff, // Hielo
                0x42ff3b, // Naturaleza
                0xffd900, // Sagrado
                0x9816fa, // Ender
                0xff1212, // Sangre
                0xbddbb2, // Evocación
                0x09000f  // Eldritch
        };
        int maxIndex = 0;
        double maxValue = values[0];
        boolean hasTie = false;
        for (int i = 1; i < values.length; i++) {
            if (values[i] > maxValue) {
                maxValue = values[i];
                maxIndex = i;
                hasTie = false;
            } else if (values[i] == maxValue) {
                hasTie = true;
            }
        }
        return hasTie ? 0x65f0eb : colors[maxIndex];
    }
}
