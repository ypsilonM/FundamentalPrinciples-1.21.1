package com.ypsi.fundamentalism.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;

import java.util.Set;

public class UnclearableEffect extends MobEffect implements IMobEffectExtension {

    public UnclearableEffect(MobEffectCategory category, int color){
        super(category, color);
    }

    @Override
    public void fillEffectCures(Set<EffectCure> cures, MobEffectInstance effectInstance) {

    }

}
