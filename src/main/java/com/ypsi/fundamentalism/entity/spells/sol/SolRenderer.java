package com.ypsi.fundamentalism.entity.spells.sol;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.pull.PullProjectile;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.icicle.IcicleRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class SolRenderer extends EntityRenderer<SolProjectile> {
    public SolRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public static final ResourceLocation[] TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png"),
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/sol/sol.png")
    };

    @Override
    public void render(SolProjectile entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        poseStack.pushPose();

        // Centrar en la entidad
        poseStack.translate(0, entity.getBbHeight() / 2, 0);

        // Rotación animada en 3 ejes para efecto 3D
        float time = entity.tickCount + partialTicks;
        float rotationSpeed = 3f;
        poseStack.mulPose(Axis.YP.rotationDegrees(time * rotationSpeed));
        poseStack.mulPose(Axis.XP.rotationDegrees(time * rotationSpeed * 0.7f));
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * rotationSpeed * 0.3f));

        // Escalar según el tamaño de la entidad
        float scale = entity.getBbWidth();
        poseStack.scale(scale, scale, scale);

        // Textura animada
        ResourceLocation texture = getTextureLocation((int)time);

        // Renderizar las 6 caras del cubo
        renderCube(poseStack, bufferSource, texture, LightTexture.FULL_BRIGHT);

        poseStack.popPose();
    }

    private void renderCube(PoseStack poseStack, MultiBufferSource bufferSource,
                            ResourceLocation texture, int packedLight) {

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMatrix = pose.pose();

        // Tamaño del cubo (1x1x1)
        final float SIZE = 0.5f;
        final float MIN = -SIZE/2;
        final float MAX = SIZE/2;
        final int ALPHA = 255;
        final int COLOR = 0xFFFFFF; // Blanco

        // --- CARA 1: FRONTAL (Z negativo) ---
        addVertex(consumer, poseMatrix, MIN, MIN, MIN, 0, 0, -1, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MAX, MIN, 0, 0, -1, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MIN, 0, 0, -1, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MIN, MIN, 0, 0, -1, 1, 1, COLOR, ALPHA, packedLight);

        // --- CARA 2: TRASERA (Z positivo) ---
        addVertex(consumer, poseMatrix, MAX, MIN, MAX, 0, 0, 1, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MAX, 0, 0, 1, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MAX, MAX, 0, 0, 1, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MIN, MAX, 0, 0, 1, 1, 1, COLOR, ALPHA, packedLight);

        // --- CARA 3: DERECHA (X positivo) ---
        addVertex(consumer, poseMatrix, MAX, MIN, MIN, 1, 0, 0, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MIN, 1, 0, 0, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MAX, 1, 0, 0, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MIN, MAX, 1, 0, 0, 1, 1, COLOR, ALPHA, packedLight);

        // --- CARA 4: IZQUIERDA (X negativo) ---
        addVertex(consumer, poseMatrix, MIN, MIN, MAX, -1, 0, 0, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MAX, MAX, -1, 0, 0, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MAX, MIN, -1, 0, 0, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MIN, MIN, -1, 0, 0, 1, 1, COLOR, ALPHA, packedLight);

        // --- CARA 5: SUPERIOR (Y positivo) ---
        addVertex(consumer, poseMatrix, MIN, MAX, MIN, 0, 1, 0, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MAX, MAX, 0, 1, 0, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MAX, 0, 1, 0, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MAX, MIN, 0, 1, 0, 1, 1, COLOR, ALPHA, packedLight);

        // --- CARA 6: INFERIOR (Y negativo) ---
        addVertex(consumer, poseMatrix, MIN, MIN, MAX, 0, -1, 0, 0, 1, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MIN, MIN, MIN, 0, -1, 0, 0, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MIN, MIN, 0, -1, 0, 1, 0, COLOR, ALPHA, packedLight);
        addVertex(consumer, poseMatrix, MAX, MIN, MAX, 0, -1, 0, 1, 1, COLOR, ALPHA, packedLight);
    }

    private void addVertex(VertexConsumer consumer, Matrix4f poseMatrix,
                           float x, float y, float z,
                           float nx, float ny, float nz,
                           float u, float v, int color, int alpha, int packedLight) {

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        consumer.addVertex(poseMatrix, x, y, z)
                .setColor(r, g, b, alpha)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(nx, ny, nz); // Corregido: solo los valores normales
    }

    @Override
    public ResourceLocation getTextureLocation(SolProjectile entity) {
        return getTextureLocation(entity.getAge());
    }

    public static ResourceLocation getTextureLocation(int offset) {
        float ticksPerFrame = 3f;
        int frame = (int)(offset / ticksPerFrame) % TEXTURES.length;
        return TEXTURES[frame];
    }
}
