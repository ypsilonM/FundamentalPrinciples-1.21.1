package com.ypsi.fundamentalism.particle;

import com.ypsi.fundamentalism.effect.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.ypsi.fundamentalism.render.ReinforcementLayer.getElementalColor;
import static com.ypsi.fundamentalism.render.ReinforcementLayer.rgbToArgb;

public class ReinforceParticles extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int totalFrames = 4;

    protected ReinforceParticles(ClientLevel level, double x, double y, double z, SpriteSet spriteSet,
                                 double xSpeed, double ySpeed, double zSpeed, int color) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);

        this.sprites = spriteSet;
        this.lifetime = 20;
        this.quadSize = 0.15f;
        this.friction = 0.8f;
        this.setSpriteFromAge(spriteSet);

        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        this.setColor(red, green, blue);
        this.setAlpha(alpha);

        this.gravity = -0.001f;

        this.xd *= 0.1;
        this.yd *= 1.2;
        this.zd *= 0.1;
    }

    @Override
    public void tick() {
        super.tick();
        int currentFrame = (int) ((float) this.age / this.lifetime * this.totalFrames);
        currentFrame = Math.min(currentFrame, this.totalFrames - 1);
        this.setSprite(sprites.get(currentFrame, this.totalFrames));
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
            int color = getColorForNearestPlayer(clientLevel, pX, pY, pZ);
//            int color = rgbToArgb(0x65f0eb, 0.6f);
            return new ReinforceParticles(clientLevel, pX, pY, pZ, this.spriteSet,pxSpeed, pySpeed, pzSpeed, color);
        }
        private int getColorForNearestPlayer(ClientLevel level, double x, double y, double z) {
            List<AbstractClientPlayer> players = level.players();

            Player nearestPlayerWithEffect = null;
            double nearestDistance = Double.MAX_VALUE;
            final double MAX_DISTANCE = 10.0 * 10.0;
            for (Player player : players) {
                if (player.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                    double distance = player.distanceToSqr(x, y, z);
                    if (distance < nearestDistance && distance <= MAX_DISTANCE) {
                        nearestDistance = distance;
                        nearestPlayerWithEffect = player;
                    }
                }
            }
            if (nearestPlayerWithEffect != null) {
                return rgbToArgb(getElementalColor(nearestPlayerWithEffect), 0.6f);
            }
            return 0x4DB3E6FF;
        }
    }
}
