package com.ypsi.fundamentalism.network;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleReinforcementPacket() implements CustomPacketPayload {

    public static final Type<ToggleReinforcementPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "toggle_reinforcement")
    );

    public static final StreamCodec<FriendlyByteBuf, ToggleReinforcementPacket> STREAM_CODEC =
            StreamCodec.unit(new ToggleReinforcementPacket());

    @Override
    public Type<ToggleReinforcementPacket> type() {
        return TYPE;
    }

    public static void handle(ToggleReinforcementPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                boolean hasEffect = player.hasEffect(ModEffects.REINFORCEMENT_EFFECT);

                if (!hasEffect) {
                    if (hasEnoughMana(player)) {
                        player.addEffect(new MobEffectInstance(
                                ModEffects.REINFORCEMENT_EFFECT,
                                Integer.MAX_VALUE,
                                0,
                                false,
                                false,
                                true
                        ));
                    }
                } else {
                    player.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                }

            }
        });
    }

    private static boolean hasEnoughMana(ServerPlayer player) {
        var magicData = MagicData.getPlayerMagicData(player);
        return magicData != null && magicData.getMana() >= player.getAttributeValue(AttributeRegistry.MAX_MANA) * 0.10;
    }



}
