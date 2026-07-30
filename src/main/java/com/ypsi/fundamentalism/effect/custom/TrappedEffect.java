package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.MagicUnclearableEffect;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;


@EventBusSubscriber
public class TrappedEffect extends MagicUnclearableEffect {
    public TrappedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @SubscribeEvent
    public static void onShockTick(EntityTickEvent.Pre event){
        Entity entity = event.getEntity();
        if(entity instanceof LivingEntity livingEntity){
            if(livingEntity.hasEffect(ModEffects.TRAPPED_EFFECT)) {
                livingEntity.setSpeed(0);
                livingEntity.setDeltaMovement(Vec3.ZERO);
            }
        }
    }
    @SubscribeEvent
    public static void onShockSpell(SpellPreCastEvent event){
        Entity entity = event.getEntity();
        if(entity instanceof LivingEntity livingEntity){
            if(livingEntity.hasEffect(ModEffects.TRAPPED_EFFECT)) {
                event.isCanceled();
            }
        }
    }
    @SubscribeEvent
    public static void cancelUseShock(LivingEntityUseItemEvent.Start event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.TRAPPED_EFFECT))
                event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void cancelTeleportShock(EntityTeleportEvent event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.TRAPPED_EFFECT))
                event.setCanceled(true);
        }
    }

}
