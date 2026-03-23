package com.ypsi.fundamentalism.spells.blood;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.effect.custom.BloodstreamEffect;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;

public class BloodstreamSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(60)
            .build();


    public BloodstreamSpell(){
        this.manaCostPerLevel = 30;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 10;
        this.castTime = 0;
        this.baseManaCost = 60;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(
                ModEffects.BLOODSTREAM_EFFECT,
                (int) (getSpellPower(spellLevel, entity) * 20),
                spellLevel - 1,
                false,
                false,
                true)
        );

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1)),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentAttackSpeed(spellLevel, caster), 0), Component.translatable("attribute.name.generic.attack_speed")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentSpeedBoost(spellLevel, caster), 0), Component.translatable("attribute.name.generic.movement_speed")),
                Component.translatable("attribute.modifier.plus.1", Utils.stringTruncation(getPercentJumpPower(spellLevel, caster), 0), Component.translatable("attribute.name.generic.jump_strength"))
        );
    }

    private float getPercentAttackSpeed(int spellLevel, LivingEntity entity) {
        return spellLevel * BloodstreamEffect.ATTACK_SPEED_PER_LEVEL * 100;
    }

    private float getPercentJumpPower(int spellLevel, LivingEntity entity) {
        return spellLevel * BloodstreamEffect.JUMP_PER_LEVEL * 100;
    }

    private float getPercentSpeedBoost(int spellLevel, LivingEntity entity) {
        return spellLevel * BloodstreamEffect.SPEED_PER_LEVEL * 100;
    }

}
