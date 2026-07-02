package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.particle.ModParticles;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.particles.*;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

public class ReinforcementEffect extends MagicMobEffect {

    public static final float BASE_SPELL_RESISTANCE = 0.10f;
    public static final float SPELL_POWER_REDUCTION = -0.30f;
    public static final float ARMOUR_TOUGHNESS = 2f;

//    public static final float BASE_SPELL_RESISTANCE = 0.20f;
//    public static final float SPELL_POWER = -0.20f;
//    public static final float ARMOUR_TOUGHNESS = 4f;


    private static final int CUSTOM_COLOR = 0xFFFFA500;

    public ReinforcementEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void addAttributeModifiers(AttributeMap attributeMap, int amplifier) {
        super.addAttributeModifiers(attributeMap, amplifier);
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
