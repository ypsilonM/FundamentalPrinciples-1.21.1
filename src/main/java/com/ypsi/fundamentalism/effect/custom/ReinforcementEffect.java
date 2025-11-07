package com.ypsi.fundamentalism.effect.custom;

import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionLevelPacket;
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

import static com.ypsi.fundamentalism.event.ModEvents.getMaxExPerLevel;

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


    @Override
    public int getColor() {
        return CUSTOM_COLOR;
    }


}
