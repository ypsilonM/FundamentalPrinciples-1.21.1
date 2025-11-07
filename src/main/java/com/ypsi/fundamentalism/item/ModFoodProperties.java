package com.ypsi.fundamentalism.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {

    public static final FoodProperties MANA_FRUIT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(1.2f)
            .effect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1), 1.0F)
            .effect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1), 1.0F)
            .alwaysEdible()
            .build();

    public static final FoodProperties TONIC = new FoodProperties.Builder()
            .build();
}
