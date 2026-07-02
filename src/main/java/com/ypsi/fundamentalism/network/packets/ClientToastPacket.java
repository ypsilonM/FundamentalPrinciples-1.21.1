package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.gui.PrincipleLevelUpToast;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientToastPacket(int playerId, String category, int newLevel) implements CustomPacketPayload {
    public static final Type<ClientToastPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "toast_packet")
    );

    public static final StreamCodec<FriendlyByteBuf, ClientToastPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.playerId());
                        buf.writeUtf(packet.category);
                        buf.writeInt(packet.newLevel);
                    },
                    buf -> new ClientToastPacket(
                            buf.readInt(),
                            buf.readUtf(),
                            buf.readInt()
                    )
            );

    public static void handle(ClientToastPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            var entity = level.getEntity(packet.playerId());

            if (entity instanceof Player player && player.level().isClientSide) {
                Minecraft.getInstance().getToasts().addToast(new PrincipleLevelUpToast(packet.category(), packet.newLevel()));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}