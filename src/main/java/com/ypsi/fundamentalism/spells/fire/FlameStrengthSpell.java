package com.ypsi.fundamentalism.spells.fire;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.effect.custom.FlamePoweredEffect;
import com.ypsi.fundamentalism.spells.SpellAnimatorClass;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import java.util.List;

import static com.ypsi.fundamentalism.spells.SpellAnimatorClass.ANIMATIONS;
import static com.ypsi.fundamentalism.spells.SpellAnimatorClass.FLAME_ANIMATION;

@AutoSpellConfig
public class FlameStrengthSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "grantstrength");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getSpellPower(spellLevel, caster) * 20, 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(3)
            .setCooldownSeconds(54)
            .build();

    public FlameStrengthSpell(){
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 10;
        this.castTime = 20;
        this.baseManaCost = 100;
    }

    @Override
    public CastType getCastType() { return CastType.LONG; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.addEffect(new MobEffectInstance(
                ModEffects.FLAME_GRANT_STRENGTH,
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
        return SpellAnimations.CHARGE_ANIMATION;
    }

}
