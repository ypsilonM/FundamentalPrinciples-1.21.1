package com.ypsi.fundamentalism.network.packets;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LookAtEntityPacket(int casterId, float yRot, float xRot) implements CustomPacketPayload {
    public static final Type<LookAtEntityPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "look_at_entity")
    );

    public static final StreamCodec<FriendlyByteBuf, LookAtEntityPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeInt(packet.casterId());
                        buf.writeFloat(packet.yRot());
                        buf.writeFloat(packet.xRot());
                    },
                    buf -> new LookAtEntityPacket(buf.readInt(), buf.readFloat(), buf.readFloat())
            );

    @Override
    public Type<LookAtEntityPacket> type() {
        return TYPE;
    }

    public static void handle(LookAtEntityPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.setYRot(packet.yRot());
                player.setXRot(packet.xRot());

                player.yRotO = packet.yRot();
                player.xRotO = packet.xRot();
                player.yHeadRot = packet.yRot();
                player.yBodyRot = packet.yRot();
            }
        });
    }

    public static void sendToPlayer(ServerPlayer targetPlayer, Entity caster) {
        AABB aabb = caster.getBoundingBox();
        Vec3 center = aabb.getCenter();

        Vec3 playerEyes = targetPlayer.getEyePosition(1.0F);
        Vec3 direction = center.subtract(playerEyes).normalize();

        float yRot = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0F;
        float xRot = -(float) Math.toDegrees(Math.atan2(direction.y, Math.hypot(direction.x, direction.z)));

        targetPlayer.connection.send(new LookAtEntityPacket(
                caster.getId(),
                yRot,
                xRot
        ));
    }
}
