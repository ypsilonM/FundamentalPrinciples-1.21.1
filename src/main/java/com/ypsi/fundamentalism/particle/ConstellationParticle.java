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
    private final int spriteSetIndex;

    private static final int FRAMES_PER_SET = 12;
    private static final int TOTAL_SETS = 5;

    protected ConstellationParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                                    double xSpeed, double ySpeed, double zSpeed, int color, int spriteSetIndex) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.sprites = spriteSet;
        this.spriteSetIndex = Math.max(1, Math.min(spriteSetIndex, 5)); // Asegurar 1-5
        this.lifetime = 40;
        this.initialSize = 1.0f;
        this.targetSize = 2.0f;
        this.quadSize = initialSize;
        this.friction = 0.8f;

        this.setSpriteForAge(0);

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

    private void setSpriteForAge(int age) {
        int currentFrame = (int) ((float) age / this.lifetime * this.totalFrames);
        currentFrame = Math.min(currentFrame, this.totalFrames - 1);

        int spriteIndex;
        switch (this.spriteSetIndex) {
            case 1:
                spriteIndex = currentFrame;
                break;
            case 2:
                spriteIndex = currentFrame + FRAMES_PER_SET+1;
                break;
            case 3:
                spriteIndex = currentFrame + (FRAMES_PER_SET*2)+1;
                break;
            case 4:
                spriteIndex = currentFrame + (FRAMES_PER_SET*3)+1;
                break;
            case 5:
                spriteIndex = currentFrame + (FRAMES_PER_SET*4)+1;
                break;
            default:
                spriteIndex = currentFrame;
                break;
        }

        this.setSprite(sprites.get(spriteIndex, FRAMES_PER_SET * TOTAL_SETS));
    }

    @Override
    public void tick() {
        super.tick();
        setSpriteForAge(this.age);

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

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Provider(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientLevel,
                                                 double pX, double pY, double pZ,
                                                 double pxSpeed, double pySpeed, double pzSpeed) {
            int color = 0xFF65F0EB;
            int particleSet = clientLevel.random.nextInt(1, 6);
            return new ConstellationParticle(clientLevel, pX, pY, pZ, this.spriteSet,
                    pxSpeed, pySpeed, pzSpeed, color, particleSet);
        }
    }
}