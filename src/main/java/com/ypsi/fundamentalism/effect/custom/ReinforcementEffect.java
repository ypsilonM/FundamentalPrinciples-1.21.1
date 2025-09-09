package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.effect.guiding_bolt.GuidingBoltManager;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class ReinforcementEffect extends MagicMobEffect {
    private static final int CUSTOM_COLOR = 0xFFFFA500;

    public ReinforcementEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int amplifier) {
        return true;
    }

    @SubscribeEvent
    public static void onPlayerReforcedHurt(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                MagicData magicData = MagicData.getPlayerMagicData(event.getEntity());

                float originalDamage = event.getNewDamage();
                float currentMana = magicData.getMana();

                double maxMana = serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                double spellPower = serverPlayer.getAttributeValue(AttributeRegistry.SPELL_POWER);

                float mitigatedDamage = (float)(Math.sqrt((maxMana/100))*spellPower);
                float modifiedDamage = originalDamage;

                float manaToConsume = (float)(maxMana*0.1);

                if(currentMana>=(maxMana*.1)) {
                    if (originalDamage < mitigatedDamage) {
                        modifiedDamage = 0.0f;
                        manaToConsume/=2;
                        magicData.addMana(-manaToConsume);
                    } else {
                        modifiedDamage = originalDamage - mitigatedDamage;
                        magicData.addMana(-manaToConsume);
                    }
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            0.5F,
                            0.4F
                    );

                    PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData));
                }
                event.setNewDamage(modifiedDamage);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void onEffectAdded(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectAdded(pLivingEntity, pAmplifier);
        pLivingEntity.setGlowingTag(true);
    }

    @Override
    public void onEffectRemoved(LivingEntity pLivingEntity, int pAmplifier) {
        super.onEffectRemoved(pLivingEntity, pAmplifier);
        pLivingEntity.setGlowingTag(false);
    }

    @Override
    public int getColor() {
        return CUSTOM_COLOR;
    }


}
