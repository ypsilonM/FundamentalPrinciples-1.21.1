package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.PacketDistributor;
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
                        MobEffectInstance effectInstance = new MobEffectInstance(
                                ModEffects.REINFORCEMENT_EFFECT,
                                -1,
                                0,
                                true,
                                true,
                                true
                        );
                        player.addEffect(effectInstance);
                        FundamentalPrinciples.LOGGER.info("Efecto aplicado en servidor a {}", player.getScoreboardName());
                        syncEffectToAllClients(player, effectInstance);
                    }
                } else {
                    player.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                    syncEffectRemovalToAllClients(player);
                }

            }
        });
    }
    private static void syncEffectToAllClients(ServerPlayer player, MobEffectInstance effectInstance) {
        if (player.level() instanceof ServerLevel serverLevel) {
            // Usar el sistema de envío del servidor directamente
            serverLevel.getServer().getPlayerList().broadcastAll(
                    new ClientboundUpdateMobEffectPacket(
                            player.getId(),
                            effectInstance,
                            true
                    ),
                    serverLevel.dimension() // enviar solo a esta dimensión
            );
        }
    }

    private static void syncEffectRemovalToAllClients(ServerPlayer player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().getPlayerList().broadcastAll(
                    new ClientboundRemoveMobEffectPacket(
                            player.getId(),
                            ModEffects.REINFORCEMENT_EFFECT
                    ),
                    serverLevel.dimension() // enviar solo a esta dimensión
            );
        }
    }

    private static boolean hasEnoughMana(ServerPlayer player) {
        var magicData = MagicData.getPlayerMagicData(player);
        return magicData != null && magicData.getMana() >= player.getAttributeValue(AttributeRegistry.MAX_MANA) * 0.10;
    }



}
