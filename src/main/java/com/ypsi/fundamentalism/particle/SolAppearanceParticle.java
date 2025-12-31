package com.ypsi.fundamentalism.particle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class SolAppearanceParticle extends Particle {
    private final SpriteSet sprites;
    private RenderType renderType;

    public SolAppearanceParticle(ClientLevel level, double x, double y, double z,
                                 SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.gravity = 0.0F;
        this.lifetime = 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float ageRatio = ((float)this.age + partialTicks) / (float)this.lifetime;
        float alpha = 0.05F + 0.5F * Mth.sin(ageRatio * (float)Math.PI);

        // Obtener la textura actual del SpriteSet
        TextureAtlasSprite sprite = this.sprites.get(this.age, this.lifetime);

        // Crear RenderType con la textura actual
        this.renderType = RenderType.entityTranslucent(sprite.atlasLocation());

        PoseStack poseStack = new PoseStack();
        poseStack.mulPose(renderInfo.rotation());
        poseStack.mulPose(Axis.XP.rotationDegrees(150.0F * ageRatio - 60.0F));
        poseStack.scale(2.0F, -2.0F, -2.0F);
        poseStack.translate(0.0F, -0.5F, 0.0F);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(this.renderType);

        renderQuad(poseStack.last(), vertexConsumer, alpha, sprite);

        bufferSource.endBatch();
    }

    private void renderQuad(PoseStack.Pose pose, VertexConsumer consumer, float alpha,
                            TextureAtlasSprite sprite) {
        Matrix4f matrix = pose.pose();
        float half = 0.5F;
        int light = LightTexture.FULL_BRIGHT;

        // Usar UVs del sprite (importante!)
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        consumer.addVertex(matrix, -half, -half, 0)
                .setColor(1, 1, 1, alpha).setUv(u1, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, -half, half, 0)
                .setColor(1, 1, 1, alpha).setUv(u1, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, half, half, 0)
                .setColor(1, 1, 1, alpha).setUv(u0, v0)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(pose, 0, 0, 1);
        consumer.addVertex(matrix, half, -half, 0)
                .setColor(1, 1, 1, alpha).setUv(u0, v1)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light)
                .setNormal(pose, 0, 0, 1);
    }

    @Override
    public AABB getRenderBoundingBox(float partialTicks) {
        return AABB.INFINITE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites; // SpriteSet contiene las texturas del JSON

        public Provider(SpriteSet sprites) {
            this.sprites = sprites; // Se pasa automáticamente
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new SolAppearanceParticle(level, x, y, z, this.sprites);
        }
    }
}