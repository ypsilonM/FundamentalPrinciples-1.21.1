package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.util.Principles;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.network.SyncAnimationPacket;
import io.redspace.ironsspellbooks.render.animation.AnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

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
                        int augereLvl = PrinciplesProgressionManager.getCategoryLevel(player, Principles.AUGERE);
                        MobEffectInstance effectInstance = getMobEffectInstance(augereLvl);

                            player.addEffect(effectInstance);
                            PacketDistributor.sendToAllPlayers(new SyncReinforcementPacket(player.getId(), true));

                    }
                } else {
                    player.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                    PacketDistributor.sendToAllPlayers(new SyncReinforcementPacket(player.getId(), false));
                }
                //PacketDistributor.sendToPlayer(player, new PlayPlayerAnimationPacket(player.getUUID(), animationId));

            }


        });
    }

    private static @NotNull MobEffectInstance getMobEffectInstance(int augereLvl) {
        int amplifier = switch (augereLvl) {
            case 0,1,2,3,4 -> 0;
            case 5,6,7,8,9 -> 1;
            case 10,11,12,13,14 -> 2;
            case 15,16,17,18,19 -> 3;
            case 20 -> 4;
            default -> 0;
        };
        MobEffectInstance effectInstance = new MobEffectInstance(
                ModEffects.REINFORCEMENT_EFFECT, -1, amplifier, true, true, true
        );
        return effectInstance;
    }

    private static boolean hasEnoughMana(ServerPlayer player) {
        var magicData = MagicData.getPlayerMagicData(player);
        return magicData != null && magicData.getMana() >= player.getAttributeValue(AttributeRegistry.MAX_MANA) * 0.10;
    }



}
