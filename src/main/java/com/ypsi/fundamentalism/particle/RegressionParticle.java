package com.ypsi.fundamentalism.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

import javax.annotation.Nullable;

public class RegressionParticle extends TextureSheetParticle {
    protected RegressionParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        super(level, x, y, z, 0, 1, 0);

        this.setSprite(spriteSet.get(0, 1));
        this.lifetime = 30;
        this.quadSize = 0.12f;
        this.hasPhysics = true;
        this.gravity = -0.05f;
        this.xd = 0;
        this.zd = 0;
        this.yd = 0;

        this.setColor(1f, 1f, 1f);
        this.setAlpha(1.0f);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - ((float) this.age / this.lifetime);
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
        public @Nullable Particle createParticle(SimpleParticleType type, ClientLevel level,
                                                 double x, double y, double z,
                                                 double xSpeed, double ySpeed, double zSpeed) {
            double offsetX = (level.random.nextDouble() - 0.5) * 1.2;
            double offsetY = level.random.nextDouble() * 0.4;
            double offsetZ = (level.random.nextDouble() - 0.5) * 1.2;

            return new RegressionParticle(level,
                    x + offsetX,
                    y + offsetY,
                    z + offsetZ,
                    this.spriteSet
            );
        }
    }
}
