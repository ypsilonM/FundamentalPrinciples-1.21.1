package com.ypsi.fundamentalism.spells.fundamentalism;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.domain.DomainEntity;
import com.ypsi.fundamentalism.spells.Animations;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;

public class SaeptumSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "saeptum");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(YpsSchoolRegistry.FUNDAMENTAL_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(60*5)
            .build();

    public SaeptumSpell() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = (80);
        this.baseManaCost = 600;
    }

    @Override
    public int getCastTime(int spellLevel) {
        return this.castTime;
    }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.LONG; }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        Vector3f maxColor;
        SchoolType schoolType;
        if(entity instanceof Player player) {
             maxColor = Util.getElementalColor(player);
             schoolType = Util.getElementalSchool(player);
        }else{
            maxColor = Utils.deconstructRGB(0x30ABFF);
            schoolType = YpsSchoolRegistry.FUNDAMENTALISM.get();
        }

        float spellPower = getSpellPower(entity, schoolType);
        DomainEntity domainEntity = new DomainEntity(level, maxColor, schoolType, refinementLevel(entity, spellPower));
        domainEntity.setPos(entity.position().add(0, - domainEntity.getBoundingBox().getYsize() * 0.5f, 0));
        domainEntity.setOwner(entity);
        level.addFreshEntity(domainEntity);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
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
        return Animations.SAEPTUM;
    }

    public float getSpellPower(@Nullable Entity sourceEntity, SchoolType schoolType) {
        return Util.getAdaptativeSpellPower(sourceEntity, schoolType);
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.PORTAL_TRIGGER);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.END_PORTAL_SPAWN);
    }

    @Override
    public boolean stopSoundOnCancel() {
        return true;
    }


    public int refinementLevel(LivingEntity caster, float spellPower){
        float refinementMultiplier = Util.getRefinementMultiplier(caster, spellPower);
        return (int) (spellPower*refinementMultiplier);
    }
}
