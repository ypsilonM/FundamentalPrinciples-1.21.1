package com.ypsi.fundamentalism.entity.spells.pull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class PullRenderer extends EntityRenderer<PullProjectile> {
    public PullRenderer(EntityRendererProvider.Context context){
        super(context);
    }
    //private static final ResourceLocation CENTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull.png");
    public static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull1.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull2.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull3.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull4.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull5.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull6.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull7.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull8.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/pull/pull9.png")
    };
    @Override
    public void render(PullProjectile entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);

        float entityScale = entity.getBbWidth() * .05f;
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

        poseStack.translate(0, 0, 0);

        float rotationAngle = (entity.tickCount + partialTicks) * -15f; // Negativo para sentido horario
        poseStack.mulPose(Axis.ZP.rotationDegrees(rotationAngle));

        poseStack.scale(.5f * entityScale, .5f * entityScale, .5f * entityScale);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();

        ResourceLocation currentTexture = getTextureLocation(entity.tickCount);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(currentTexture));

        consumer.addVertex(poseMatrix, -8, -8, 0).setColor(255, 255, 255, 255).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
        consumer.addVertex(poseMatrix, -8, 8, 0).setColor(255, 255, 255, 255).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
        consumer.addVertex(poseMatrix, 8, 8, 0).setColor(255, 255, 255, 255).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
        consumer.addVertex(poseMatrix, 8, -8, 0).setColor(255, 255, 255, 255).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(pose, 0, 1, 0);
        poseStack.popPose();

        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }
    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 1f;
        return TEXTURES[(int) (offset / ticksPerFrame) % TEXTURES.length];
    }

    @Override
    public ResourceLocation getTextureLocation(PullProjectile pEntity) {
        return getTextureLocation(pEntity.getAge());
    }
}
