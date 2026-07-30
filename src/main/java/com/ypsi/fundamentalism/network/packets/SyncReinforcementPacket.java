package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Principles;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.render.animation.AnimationHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;


public record SyncReinforcementPacket(int playerId, boolean hasEffect) implements CustomPacketPayload {
    public static final Type<SyncReinforcementPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sync_reinforcement")
    );

    public static final StreamCodec<FriendlyByteBuf, SyncReinforcementPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.playerId());
                        buf.writeBoolean(packet.hasEffect());
                    },
                    buf -> new SyncReinforcementPacket(buf.readInt(), buf.readBoolean())
            );

    @Override
    public Type<SyncReinforcementPacket> type() {
        return TYPE;
    }

    public static void handle(SyncReinforcementPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            var entity = level.getEntity(packet.playerId());

            if (entity instanceof Player targetPlayer) {
                if (packet.hasEffect()) {
                    targetPlayer.forceAddEffect(
                            new MobEffectInstance(
                                    ModEffects.REINFORCEMENT_EFFECT,
                                    -1,
                                    getAmp(PrinciplesProgressionManager.getCategoryLevel(targetPlayer, Principles.AUGERE)),
                                    true, true, true
                            ),
                            null
                    );
                } else {
                    targetPlayer.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                }
                SpellAnimations.SELF_CAST_ANIMATION.getForPlayer()
                        .ifPresent(resourceLocation -> AnimationHelper.animatePlayerStart(targetPlayer, resourceLocation));
            }
        });
    }

    private static int getAmp(int augereLvl) {
        return switch (augereLvl) {
            case 3,4,5,6 -> 0;
            case 7,8,9,10 -> 1;
            case 11,12,13,14,15 -> 2;
            case 16,17,18,19 -> 3;
            case 20 -> 4;
            default -> 0;
        };
    }
}
