package com.ypsi.fundamentalism.spells.fundamentalism;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.Animations;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class LawOfRegressionSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "law_of_regression");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(YpsSchoolRegistry.FUNDAMENTAL_RESOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(5)
            .build();

    public LawOfRegressionSpell() {
        this.manaCostPerLevel = -10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 10;
        this.castTime = (20) * 4;
        this.baseManaCost = 40;
    }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.CONTINUOUS; }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {

        entity.heal(getSpellPower(spellLevel, entity));
        if(!level.isClientSide){
                ServerLevel serverLevel = (ServerLevel) level;
                serverLevel.sendParticles(
                        ParticleRegistry.CLEANSE_PARTICLE.get(),
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


}