package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.MagicUnclearableEffect;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.util.Principles;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

@EventBusSubscriber
public class SpatialDisruption extends MagicUnclearableEffect {

    public SpatialDisruption(MobEffectCategory category, int color) {
        super(category, color);
    }

    @SubscribeEvent
    public static void onSpacialDSpell(SpellPreCastEvent event){
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity.hasEffect(ModEffects.SPATIAL_DISRUPTION)) {
            if (SpellCategoriesGenerator.isInPrinciple(event.getSpellId(), Principles.APPARITIO)) {
                event.setCanceled(true);
                if (livingEntity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.translatable(
                            "ui.ypfundamentals.saeptum_apparitio_failure").withStyle(ChatFormatting.WHITE), true);
                }
            }
            if (event.getSpellId().equals(SpellRegistry.EVASION_SPELL.get().getSpellId())) {
                event.setCanceled(true);
                if (livingEntity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.translatable(
                            "ui.ypfundamentals.saeptum_apparitio_failure").withStyle(ChatFormatting.WHITE), true);
                }
            }
        }
    }
    @SubscribeEvent
    public static void cancelTeleportSpacialD(EntityTeleportEvent event){
        if(event.getEntity() instanceof LivingEntity entity){
            if(entity.hasEffect(ModEffects.SPATIAL_DISRUPTION)) {
                event.setCanceled(true);
                if (entity instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.translatable(
                            "ui.ypfundamentals.saeptum_teleport_failure").withStyle(ChatFormatting.WHITE), true);
                }
            }
        }
    }
}
