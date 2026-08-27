package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Principles;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ChargeBoostPacket(boolean released) implements CustomPacketPayload {

    public static final Type<ChargeBoostPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "charge_boost")
    );
    public static final StreamCodec<FriendlyByteBuf, ChargeBoostPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeBoolean(packet.released());
                    },
                    buf -> new ChargeBoostPacket(buf.readBoolean())
            );

    @Override
    public Type<ChargeBoostPacket> type() {
        return TYPE;
    }

    public static void handle(ChargeBoostPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {

            if (context.player() instanceof ServerPlayer player) {
                final String BOOST_ACTIVE_KEY = "boostActive";

                if (packet.released()) {
                    player.getPersistentData().putBoolean(BOOST_ACTIVE_KEY, false);
                } else {
                    player.getPersistentData().putBoolean(BOOST_ACTIVE_KEY, true);
                }
            }


        });
    }
}
