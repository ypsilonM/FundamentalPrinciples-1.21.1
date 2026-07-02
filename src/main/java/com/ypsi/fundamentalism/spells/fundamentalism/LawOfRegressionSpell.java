package com.ypsi.fundamentalism.spells.fundamentalism;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.Animations;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LawOfRegressionSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "law_of_regression");


    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.healing", Utils.stringTruncation(spellLevel, 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(YpsSchoolRegistry.FUNDAMENTAL_RESOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(5)
            .build();

    public LawOfRegressionSpell() {
        this.manaCostPerLevel = -10;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = (20) * 10;
        this.baseManaCost = 80;
    }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.CONTINUOUS; }

//    @Override
//    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
//        Utils.preCastTargetHelper(level, entity, playerMagicData, this, 2, .15f, false);
//        return true;
//    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float healAmount =  spellLevel;
//        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData castTargetingData
//            && spellLevel == this.getMaxLevel()) {
//
//            var target = castTargetingData.getTarget((ServerLevel) level);
//            if (target != null) {
//
//                if(target.getType().is(EntityTypeTags.UNDEAD)){
//                    DamageSources.applyDamage(target, healAmount, this.getDamageSource(entity));
//                }else {
//                    NeoForge.EVENT_BUS.post(new SpellHealEvent(entity, target, healAmount, getSchoolType()));
//                    target.heal(healAmount);
//                    target.removeEffectsCuredBy(EffectCures.HONEY);
//                }
//                if (!level.isClientSide) {
//
//                    ServerLevel serverLevel = (ServerLevel) level;
//                    serverLevel.sendParticles(
//                            new DustParticleOptions(new Vector3f(0.98f, 0.81f, 0.22f), 0.8f),
//                            target.getX(),
//                            target.getY() + target.getBbHeight() / 2,
//                            target.getZ(),
//                            10, 0.1, 0.0, 0.1, 0.01
//                    );
//
//                }
//            }
//        }else{
            NeoForge.EVENT_BUS.post(new SpellHealEvent(entity, entity, healAmount, getSchoolType()));
            entity.heal(healAmount);
            entity.removeEffectsCuredBy(EffectCures.HONEY);

            if(!level.isClientSide){

                ServerLevel serverLevel = (ServerLevel) level;
                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(0.98f,0.81f,0.22f),0.8f),
                        entity.getX(),
                        entity.getY()+ entity.getBbHeight()/2,
                        entity.getZ(),
                        10,0.1,0.0, 0.1,0.01
                );

            }


    }

    @Override
    public boolean allowLooting() {
        return false;
    }

    @Override
    public boolean requiresLearning() {
        return true;
    }

    @Override
    public boolean allowCrafting() { return false; }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return Animations.REMEDIUM;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.CANDLE_EXTINGUISH);
    }

    @Override
    public float getSpellPower(int spellLevel, @Nullable Entity sourceEntity) {
        return super.getSpellPower(spellLevel, sourceEntity);
    }

    @Override
    public void playSound(Optional<SoundEvent> sound, Entity entity) {
        super.playSound(sound, entity);
    }
}