package com.ypsi.fundamentalism.spells.ice;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsEntity;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChainsSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "chains");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(45)
            .build();

    public ChainsSpell() {
        this.manaCostPerLevel = 15;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 10 * 20;
        this.baseManaCost = 65;
    }

    public int getDuration(int spellLevel, LivingEntity caster) {
        return getCastTime(spellLevel)+(80*spellLevel);
    }

    @Override
    public int getCastTime(int spellLevel) { return castTime; }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 50, .35f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData castData = playerMagicData.getAdditionalCastData();
        if (castData instanceof TargetEntityCastData castTargetingData) {
            LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
            if (target != null && !target.getType().is(ModTags.CANT_ROOT)) {
                Vec3 spawn = target.position();
                ChainsEntity chainEntity = new ChainsEntity(level, entity);

                chainEntity.setDuration(getDuration(spellLevel, entity));
                target.addEffect(new MobEffectInstance(
                        ModEffects.CHAINED_EFFECT, getDuration(spellLevel, entity),
                        0, false, true
                ));
                target.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN, getDuration(spellLevel, entity),
                        3, false, true
                ));
                target.addEffect(new MobEffectInstance(
                        MobEffects.WEAKNESS, getDuration(spellLevel, entity),
                        5, false, true
                ));

                chainEntity.setTarget(target);
                chainEntity.moveTo(spawn);
                level.addFreshEntity(chainEntity);
                target.stopRiding();
                target.startRiding(chainEntity, true);

            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

}
