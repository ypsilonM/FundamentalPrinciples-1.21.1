package com.ypsi.fundamentalism.effect.custom;

import com.mojang.blaze3d.shaders.Effect;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class FlamePoweredEffect extends MagicMobEffect {
    public FlamePoweredEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public boolean applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(!pLivingEntity.level().isClientSide){

            if(pLivingEntity.hasEffect(MobEffects.POISON))
                pLivingEntity.removeEffect(MobEffects.POISON);
            if(pLivingEntity.hasEffect(MobEffects.WITHER))
                pLivingEntity.removeEffect(MobEffects.WITHER);
            if(pLivingEntity.isFreezing())
                pLivingEntity.setTicksFrozen(0);

            float damageApplied = 3.0f * (pAmplifier);
            float radius = 2.0f * (pAmplifier+1);
            var entities = pLivingEntity.level().getEntities(pLivingEntity, pLivingEntity.getBoundingBox().inflate(radius));
            MagicManager.spawnParticles(pLivingEntity.level(), new BlastwaveParticleOptions(SchoolRegistry.FIRE.get().getTargetingColor(), radius), pLivingEntity.getX(), pLivingEntity.getBoundingBox().getCenter().y, pLivingEntity.getZ(), 1, 0, 0, 0, 0, true);
            pLivingEntity.level().playSound(null, pLivingEntity.blockPosition(), SoundRegistry.FIRE_CAST.get(), SoundSource.PLAYERS, 3, Utils.random.nextIntBetweenInclusive(8, 12) * .1f);
            for (Entity entity : entities) {
                    if (entity instanceof LivingEntity target && !target.isAlliedTo(pLivingEntity)) {
                        MagicManager.spawnParticles(pLivingEntity.level(), ParticleHelper.EMBERS, target.getX(), target.getY() + 0.25, target.getZ(), 20, 0.03, 0.4, 0.03, 0.4, true);
                        DamageSources.applyDamage(target, damageApplied, ModSpells.FLAME_GRANT_STRENGTH.get().getDamageSource(pLivingEntity));
                    }
                }
            }
        return super.applyEffectTick(pLivingEntity, pAmplifier);
    }
    @SubscribeEvent
    public static void setOnFire(LivingDamageEvent.Pre event){
        var entity = event.getEntity();
        if (!entity.level().isClientSide) {
            if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && attacker.hasEffect(ModEffects.FLAME_GRANT_STRENGTH)){
                LivingEntity target = event.getEntity();
                int amplifier = attacker.getEffect(ModEffects.FLAME_GRANT_STRENGTH).getAmplifier()+1;
                target.igniteForSeconds(2 + amplifier);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration%40==0;
    }

}
