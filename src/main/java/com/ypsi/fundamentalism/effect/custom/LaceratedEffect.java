package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

@EventBusSubscriber
public class LaceratedEffect extends MagicMobEffect {

    public LaceratedEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void laceratedHeal(LivingHealEvent event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.LACERATED_EFFECT)) {

                if(!entity.hasEffect(MobEffectRegistry.BLIGHT))
                    event.setAmount(event.getAmount() * 0.6f);

                event.setAmount(event.getAmount() * 0.8f);
            }
        }
    }
}
