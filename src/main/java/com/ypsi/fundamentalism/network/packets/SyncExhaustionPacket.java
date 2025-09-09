package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncExhaustionPacket(int current) implements CustomPacketPayload {

    public static final Type<SyncExhaustionPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sync_exhaustion")
    );

    // StreamCodec correcto para records
    public static final StreamCodec<FriendlyByteBuf, SyncExhaustionPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.current), // Sin paréntesis
                    buf -> new SyncExhaustionPacket(buf.readInt())
            );


    @Override
    public Type<SyncExhaustionPacket> type() {
        return TYPE;
    }

    public static void handle(SyncExhaustionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientExhaustionData.setCurrentExhaustion(packet.current); // Sin paréntesis
        });
    }

    public static void sendToPlayer(ServerPlayer player, int current) {
        player.connection.send(new SyncExhaustionPacket(current));
    }
}
