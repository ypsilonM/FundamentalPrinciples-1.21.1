package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.util.Pair;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.core.particles.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class ReinforcementEffect extends MagicMobEffect {

    private static final int CUSTOM_COLOR = 0xFFFFA500;

    public ReinforcementEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public ParticleOptions createParticleOptions(MobEffectInstance effect) {
        return ModParticles.REINFORCEMENT_PARTICLE.get();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 15 == 0;
    }

    @SubscribeEvent
    public static void onPlayerReinforcedHurt(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {

                MagicData magicData = MagicData.getPlayerMagicData(event.getEntity());

                float originalDamage = event.getNewDamage();
                float currentMana = magicData.getMana();

                double maxMana = serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                double baseSpellPower = serverPlayer.getAttributeBaseValue(AttributeRegistry.SPELL_POWER);
                double removeSpellBase = baseSpellPower*0.2;
                double spellPower = serverPlayer.getAttributeValue(AttributeRegistry.SPELL_POWER)+removeSpellBase;

                float mitigatedDamage = (float)(Math.sqrt((maxMana/100))*spellPower);

                float modifiedDamage = originalDamage;

                float manaToConsume = (float)(maxMana*0.05);

                if(currentMana>=(maxMana*.5)) {
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
                int currentExhaustion = serverPlayer.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
                serverPlayer.setData(YpsAttachments.CURRENT_EXHAUSTION,
                        Mth.clamp(currentExhaustion+2,0,100));
                SyncExhaustionPacket.sendToPlayer(serverPlayer,serverPlayer.getData(YpsAttachments.CURRENT_EXHAUSTION));

                event.setNewDamage(modifiedDamage);
            }
        }
    }

    @Override
    public int getColor() {
        return CUSTOM_COLOR;
    }


}
