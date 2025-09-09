package com.ypsi.fundamentalism.effect.custom;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class ShapelessMotherEffect extends MagicMobEffect {

    public ShapelessMotherEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

}
