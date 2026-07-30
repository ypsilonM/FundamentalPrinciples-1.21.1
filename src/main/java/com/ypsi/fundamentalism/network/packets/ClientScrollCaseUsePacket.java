package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.render.animation.AnimationHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientScrollCaseUsePacket(int playerId) implements CustomPacketPayload {
    public static final Type<ClientScrollCaseUsePacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sync_scroll_case")
    );

    public static final StreamCodec<FriendlyByteBuf, ClientScrollCaseUsePacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.playerId());
                    },
                    buf -> new ClientScrollCaseUsePacket(buf.readInt())
            );

    public static void handle(ClientScrollCaseUsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            var entity = level.getEntity(packet.playerId());

            if (entity instanceof Player player && player.level().isClientSide) {
                player.playSound(
                        SoundEvents.COPPER_BULB_BREAK, 1, 0.6F
                );
                ItemStack stack = new ItemStack(ModItems.ANCIENT_SCROLL_CASE.get());
                Minecraft.getInstance().gameRenderer.displayItemActivation(stack);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
