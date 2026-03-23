package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.particle.ModParticles;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.*;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ReinforcementEffect extends MagicMobEffect {

    private static final int CUSTOM_COLOR = 0xFFFFA500;

    public ReinforcementEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return ModParticles.REINFORCEMENT_PARTICLE.get();
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        if(pLivingEntity instanceof Player player){
            player.setGlowingTag(true);
        }
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        if(pLivingEntity instanceof Player player){
            player.setGlowingTag(false);
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 15 == 0;
    }


    @Override
    public int getColor() {
        return CUSTOM_COLOR;
    }



}
