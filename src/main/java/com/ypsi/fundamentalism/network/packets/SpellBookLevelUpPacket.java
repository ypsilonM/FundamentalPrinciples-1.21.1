package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SpellBookLevelUpPacket(int playerId, int oldLevel, int newLevel) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpellBookLevelUpPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "spellbook_levelup")
            );

    public static final StreamCodec<ByteBuf, SpellBookLevelUpPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SpellBookLevelUpPacket::playerId,
            ByteBufCodecs.VAR_INT,
            SpellBookLevelUpPacket::oldLevel,
            ByteBufCodecs.VAR_INT,
            SpellBookLevelUpPacket::newLevel,
            SpellBookLevelUpPacket::new
    );

    public static void handle(final SpellBookLevelUpPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Player player = (Player) mc.level.getEntity(data.playerId);
                if (player != null && player.equals(mc.player)) {
                    player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}