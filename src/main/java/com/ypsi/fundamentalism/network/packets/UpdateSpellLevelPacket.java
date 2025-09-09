package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.util.SpellAttributeUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public class UpdateSpellLevelPacket implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<UpdateSpellLevelPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "update_spell_level"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSpellLevelPacket> STREAM_CODEC =
            CustomPacketPayload.codec(UpdateSpellLevelPacket::write, UpdateSpellLevelPacket::new);

    private final String spellId;
    private final int levelModifier;

    public UpdateSpellLevelPacket(String spellId, int levelModifier) {
        this.spellId = spellId;
        this.levelModifier = levelModifier;
    }

    public UpdateSpellLevelPacket(FriendlyByteBuf buf) {
        this.spellId = buf.readUtf();
        this.levelModifier = buf.readInt();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(spellId);
        buf.writeInt(levelModifier);
    }

    public static void handle(UpdateSpellLevelPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                // Ejecutar en el servidor
                SpellAttributeUtils.modifySpellLevelIfExists(serverPlayer, packet.spellId, packet.levelModifier);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
