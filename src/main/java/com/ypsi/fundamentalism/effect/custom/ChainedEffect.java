package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.effect.UnclearableEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;


@EventBusSubscriber
public class ChainedEffect extends UnclearableEffect {

    public ChainedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @SubscribeEvent
    public static void cancelUse (LivingEntityUseItemEvent.Start event){
        if(event.getEntity() instanceof Player player){
            if(player.hasEffect(ModEffects.CHAINED_EFFECT))
                event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void cancelTeleport (EntityTeleportEvent event){
        if(event.getEntity() instanceof Player player){
            if(player.hasEffect(ModEffects.CHAINED_EFFECT))
                event.setCanceled(true);
        }
    }

}
