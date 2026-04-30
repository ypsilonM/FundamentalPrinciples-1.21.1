package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.attachments.customAtt.PrinciplesLevelsAttachment;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record SyncCategoryLevelsPacket(Map<String, Integer> levels, Map<String, Integer> experience) implements CustomPacketPayload {
    public static final Type<SyncCategoryLevelsPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sync_spell_categories")
    );

    public static final StreamCodec<FriendlyByteBuf, SyncCategoryLevelsPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeMap(packet.levels, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
                buf.writeMap(packet.experience, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
            },
            buf -> new SyncCategoryLevelsPacket(
                    buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt),
                    buf.readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt)
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void sendToPlayer(ServerPlayer player) {
        PrinciplesLevelsAttachment categoryLevels = PrinciplesProgressionManager.getCategoryLevels(player);
        SyncCategoryLevelsPacket packet = new SyncCategoryLevelsPacket(
                new HashMap<>(categoryLevels.getCategoryLevels()),
                new HashMap<>(categoryLevels.getCategoryExperience())
        );
        player.connection.send(new ClientboundCustomPayloadPacket(packet));
    }

    public static void handle(SyncCategoryLevelsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow() == PacketFlow.CLIENTBOUND) {
                ClientCategoryLevelsData.setLevels(packet.levels, packet.experience);
            }
        });
    }
}
