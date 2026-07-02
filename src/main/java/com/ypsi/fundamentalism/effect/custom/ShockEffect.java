package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.MagicUnclearableEffect;
import com.ypsi.fundamentalism.effect.ModEffects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber
public class ShockEffect extends MagicUnclearableEffect {
    public ShockEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    @SubscribeEvent
    public static void onShockTick(EntityTickEvent.Pre event){
        Entity entity = event.getEntity();
        if(entity instanceof LivingEntity livingEntity){
            if(livingEntity.hasEffect(ModEffects.SHOCK_EFFECT)) {
                livingEntity.setSpeed(0);
                livingEntity.setDeltaMovement(Vec3.ZERO);
            }
        }
    }
}
