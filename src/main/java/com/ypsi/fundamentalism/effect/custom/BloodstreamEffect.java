package com.ypsi.fundamentalism.effect.custom;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BloodstreamEffect extends MagicMobEffect {
    private static int particleCounter = 0;
//    private static int healCounter = 0;

    public static final float ATTACK_SPEED_PER_LEVEL = 0.10f;
    public static final float JUMP_PER_LEVEL = 0.15f;
    public static final float SPEED_PER_LEVEL = 0.20f;

    public BloodstreamEffect(MobEffectCategory pCategory, int pColor) { super(pCategory, pColor); }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
    }

    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity.level().isClientSide) {
            spawnParticles(pLivingEntity);
        }
//        if(pLivingEntity instanceof Player player) {
//            heal(player, pAmplifier);
//        }

        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }
    private void spawnParticles(LivingEntity entity) {
        if(particleCounter==20) {
            for(int i=0;i<=6;i++) {
                entity.level().addParticle(
                        ParticleRegistry.BLOOD_PARTICLE.get(),
                        entity.getRandomX(0.5),
                        entity.getRandomY(),
                        entity.getRandomZ(0.5),
                        0, 0.1, 0
                );
            }
            particleCounter=0;
        }else {
            particleCounter++;
        }
    }
//    private void heal(Player entity, int amplifier) {
//        if (healCounter == 20-(amplifier*5)) {
//            MagicData magicData = MagicData.getPlayerMagicData(entity);
//            if (entity.getHealth() < entity.getMaxHealth() && magicData.getMana() > entity.getAttributeValue(AttributeRegistry.MAX_MANA)*.75) {
//                entity.heal(1);
//                magicData.addMana(-40);
//            }
//            healCounter = 0;
//        }else{
//            healCounter++;
//        }
//    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
