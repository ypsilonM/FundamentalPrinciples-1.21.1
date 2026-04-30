package com.ypsi.fundamentalism.spells.fire;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.Animations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class IgniteSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "ignite");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", Utils.stringTruncation(getFireDamage(spellLevel, caster), 2)),
                Component.translatable("ui.ypfundamentals.fire_time", Utils.timeFromTicks((int)getFireTime(spellLevel, caster)*20, 2))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(6)
            .setCooldownSeconds(8)
            .build();

    public IgniteSpell(){
        this.manaCostPerLevel = 8;
        this.baseSpellPower = 12;
        this.spellPowerPerLevel = 2;
        this.castTime = 0;
        this.baseManaCost = 15;
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 20, .6f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData()
                instanceof TargetEntityCastData targetEntityCastData) {
            var target = targetEntityCastData.getTarget((ServerLevel) level);
            if (target != null) {
                if(target.isOnFire() || target.fireImmune()){
                    DamageSources.applyDamage(
                            target,
                            getFireDamage(spellLevel, caster),
                            getDamageSource(caster)
                    );
                }else {
                    target.igniteForSeconds((int)getFireTime(spellLevel, caster));
                }
            }
        }
        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    private float getFireDamage(int spellLevel, LivingEntity caster) {
        return getSpellPower(spellLevel, caster) * 0.8f;
    }
    public float getFireTime(int spellLevel, LivingEntity caster) {
        return 1f + getSpellPower(spellLevel, caster) * .5f + spellLevel;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
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
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.FIRECHARGE_USE);
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return Animations.IGNITE_CAST;
    }
}
