package com.ypsi.fundamentalism.spells.blood;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;
@AutoSpellConfig
public class BloodMoonSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodmoon");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(60*5)
            .build();


    public BloodMoonSpell(){
        this.manaCostPerLevel = 200;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 5*20;
        this.baseManaCost = 200;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
//        Optional<EnhancedCelestialsLunarForecastWorldData> lunarData = EnhancedCelestials.lunarForecastWorldData(serverLevel);
//        if (lunarData.isEmpty()) {
//            return;
//        }
//        EnhancedCelestialsLunarForecastWorldData data = lunarData.get();
//        if (level.isRaining() && data.getDimensionSettings().requiresClearSkies()) {
//            return;
//        }
//        ResourceKey<LunarEvent> bloodMoonKey = ResourceKey.create(
//                EnhancedCelestialsRegistry.LUNAR_EVENT_KEY,
//                ResourceLocation.fromNamespaceAndPath("enhancedcelestials", "super_blood_moon") // ID del evento
//        );
//
//        if (!data.getForecast().isEmpty()) {
//            LunarEventInstance firstEvent = data.getForecast().get(0);
//            if (firstEvent.active(data.getCurrentDay())) {
//                data.removeEventInForecast(0);
//            }
//        }

//        data.setLunarEvent(bloodMoonKey);

//        entity.addEffect(new MobEffectInstance(
//                ModEffects.SHAPELESS_MOTHER,
//                (int) (20*60*7),
//                0,
//                false,
//                false,
//                true)
//        );
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }


}
