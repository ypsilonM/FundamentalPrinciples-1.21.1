package com.ypsi.fundamentalism.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class ConstellationParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int totalFrames = 12;
    private final float initialSize;
    private final float targetSize;
    private final boolean useParticle1Set;

    protected ConstellationParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                                    double xSpeed, double ySpeed, double zSpeed, int color, boolean useParticle1Set) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.sprites = spriteSet;
        this.useParticle1Set = useParticle1Set;
        this.lifetime = 40;
        this.initialSize = 1.0f;
        this.targetSize = 2.0f;
        this.quadSize = initialSize;
        this.friction = 0.8f;

        this.setSpriteFromAge(spriteSet);

        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        this.setColor(red, green, blue);
        this.setAlpha(alpha);

        this.gravity = 0f;

        this.xd *= 0.1;
        this.yd *= 1.2;
        this.zd *= 0.1;
    }

    @Override
    public void tick() {
        super.tick();
        int currentFrame = (int) ((float) this.age / this.lifetime * this.totalFrames);
        currentFrame = Math.min(currentFrame, this.totalFrames - 1);

//        this.setSprite(sprites.get(currentFrame, this.totalFrames));
        int spriteIndex;
        if (useParticle1Set) {
            spriteIndex = currentFrame;
        } else {
            spriteIndex = currentFrame+this.totalFrames+1;
        }

        this.setSprite(sprites.get(spriteIndex, this.totalFrames * 2));

        float progress = (float) this.age / this.lifetime;
        this.alpha = 1.0f - progress;
    }
    @Override
    public float getQuadSize(float partialTick) {
        float progress = (float) (this.age + partialTick) / this.lifetime;
        return Mth.lerp(progress, initialSize, targetSize);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType>{
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                                 double pX, double pY, double pZ, double pxSpeed, double pySpeed, double pzSpeed) {
            int color = 0xFF65F0EB;
            boolean useParticle1Set = clientLevel.random.nextBoolean();
            return new ConstellationParticle(clientLevel, pX, pY, pZ, this.spriteSet, pxSpeed, pySpeed, pzSpeed, color, useParticle1Set);
        }
    }
}
