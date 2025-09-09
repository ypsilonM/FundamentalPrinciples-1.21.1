package com.ypsi.fundamentalism.entity.spells.sol;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.pull.PullProjectile;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

public class SolRenderer extends EntityRenderer<SolProjectile> {
    public SolRenderer(EntityRendererProvider.Context context){
        super(context);
    }

    public static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol1.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol2.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol3.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol4.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol5.png")
    };
    private static final ResourceLocation CENTER_TEXTURE = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png");

    @Override
    public void render(SolProjectile entity, float pEntityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBbHeight() / 2, 0);

        ResourceLocation currentTexture = getTextureLocation(entity.tickCount);

//        float entityScale = entity.getBbWidth() * .5f;
//
//        poseStack.scale(entityScale, entityScale, entityScale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(90f));
//
//        poseStack.translate(5, 0, 0);
        float halfWidth = entity.getBbWidth() / 2f;
        float halfHeight = entity.getBbHeight() / 2f;

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(currentTexture));

        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();

        consumer.addVertex(poseMatrix, 0, -halfHeight, -halfWidth).setColor(255, 255, 255, 240).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, halfHeight, -halfWidth).setColor(255, 255, 255, 240).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, halfHeight, halfWidth).setColor(255, 255, 255, 240).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        consumer.addVertex(poseMatrix, 0, -halfHeight, halfWidth).setColor(255, 255, 255, 240).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f);
        poseStack.popPose();
        poseStack.pushPose();

        poseStack.translate(0, entity.getBoundingBox().getYsize() / 2, 0);
        RandomSource randomSource = RandomSource.create(432L);

        poseStack.popPose();

        super.render(entity, pEntityYaw, partialTicks, poseStack, bufferSource, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SolProjectile pEntity) {
        return getTextureLocation(pEntity.getAge());
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 3f;
        return TEXTURES[(int) (offset / ticksPerFrame) % TEXTURES.length];
    }
}
