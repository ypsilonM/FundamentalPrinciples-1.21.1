package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;


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
                                    0,
                                    true, true, true
                            ),
                            null
                    );
                } else {
                    targetPlayer.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                }
            }
        });
    }
}
