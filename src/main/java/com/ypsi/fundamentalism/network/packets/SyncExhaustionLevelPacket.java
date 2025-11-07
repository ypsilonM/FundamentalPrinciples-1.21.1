package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.network.packets.data.ClientExhaustionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncExhaustionLevelPacket(int current) implements CustomPacketPayload {

    public static final Type<SyncExhaustionLevelPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sync_level")
    );

    public static final StreamCodec<FriendlyByteBuf, SyncExhaustionLevelPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> buf.writeInt(packet.current),
                    buf -> new SyncExhaustionLevelPacket(buf.readInt())
            );


    @Override
    public Type<SyncExhaustionLevelPacket> type() {
        return TYPE;
    }

    public static void handle(SyncExhaustionLevelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientExhaustionData.setLevelExhaustion(packet.current);
        });
    }

    public static void sendToPlayer(ServerPlayer player, int current) {
        player.connection.send(new SyncExhaustionLevelPacket(current));
    }
}
