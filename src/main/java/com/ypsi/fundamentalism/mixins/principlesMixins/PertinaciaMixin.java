package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class PertinaciaMixin {

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private MobEffectInstance modifyEffectDuration(MobEffectInstance effectInstance) {

        if(!ServerConfig.ACTIVE_PERTINACIA.get() || !ServerConfig.PRINCIPLES_SYSTEM.get()) return effectInstance;

        LivingEntity entity = (LivingEntity) (Object) this;

        if (entity instanceof ServerPlayer player && effectInstance.getEffect().value() instanceof MagicMobEffect) {
            int originalDuration = effectInstance.getDuration();
            if(originalDuration == -1) return effectInstance;

            int pertinaciaLvl = PrinciplesProgressionManager.getCategoryLevel(player, Principles.PERTINACIA);

            int newDuration = switch (effectInstance.getEffect().value().getCategory()) {
                case BENEFICIAL -> (int) (originalDuration * Util.beneficialPertinaciaMultiplier(pertinaciaLvl)); //  140
                case HARMFUL    -> (int) (originalDuration * Util.harmfulPertinaciaMultiplier(pertinaciaLvl)); //  60
                default         -> originalDuration;
            };
            newDuration = Math.max(newDuration, 1);

            return new MobEffectInstance(
                    effectInstance.getEffect(),
                    newDuration,
                    effectInstance.getAmplifier(),
                    effectInstance.isAmbient(),
                    effectInstance.isVisible(),
                    effectInstance.showIcon()
            );
        }
        return effectInstance;
    }
}
