package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.MagicUnclearableEffect;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.effect.UnclearableEffect;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;


@EventBusSubscriber
public class ChainedEffect extends MagicUnclearableEffect {

    public ChainedEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {

        if (livingEntity instanceof IMagicEntity iMagicEntity && iMagicEntity.isCasting())
            iMagicEntity.cancelCast();

        if (livingEntity instanceof ServerPlayer player)
            Utils.serverSideCancelCast(player);

        return super.applyEffectTick(livingEntity, amplifier);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void cancelUse (LivingEntityUseItemEvent.Start event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.CHAINED_EFFECT))
                event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void cancelTeleport (EntityTeleportEvent event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.CHAINED_EFFECT))
                event.setCanceled(true);
        }
    }


}
