package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attachments.AvailableSpellsAttachment;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

public record ClientSpellsUpdatePacket(Map<String, Integer> spells) implements CustomPacketPayload {

    public static final Type<ClientSpellsUpdatePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "client_spells_update")
    );

    public static final StreamCodec<FriendlyByteBuf, ClientSpellsUpdatePacket> STREAM_CODEC =
            StreamCodec.of(
                    ClientSpellsUpdatePacket::encode,
                    ClientSpellsUpdatePacket::decode
            );

    private static void encode(FriendlyByteBuf buf, ClientSpellsUpdatePacket packet) {
        buf.writeVarInt(packet.spells().size());
        packet.spells().forEach((id, level) -> {
            buf.writeUtf(id);
            buf.writeVarInt(level);
        });
    }

    private static ClientSpellsUpdatePacket decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        Map<String, Integer> spells = new HashMap<>();

        for (int i = 0; i < size; i++) {
            String id = buf.readUtf();
            int level = buf.readVarInt();
            spells.put(id, level);
        }
        return new ClientSpellsUpdatePacket(spells);
    }

    @Override
    public Type<ClientSpellsUpdatePacket> type() {
        return TYPE;
    }

    public static void handle(ClientSpellsUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            AvailableSpellsAttachment attachment = player.getData(YpsAttachments.SPELL_LIST.get());
            attachment.clearSpells();
            packet.spells().forEach((spellId, level) -> {
                if (level >= 0) {
                    attachment.setSpellLevel(spellId, level);
                }
            });
        });
    }
}
