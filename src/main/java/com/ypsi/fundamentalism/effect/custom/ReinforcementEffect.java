package com.ypsi.fundamentalism.effect.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ReinforcementEffect extends MobEffect {
    private static final int CUSTOM_COLOR = 0xFFFFA500;

    public ReinforcementEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        return true;
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public int getColor() {
        return CUSTOM_COLOR;
    }




}
