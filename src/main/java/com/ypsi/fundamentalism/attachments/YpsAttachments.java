package com.ypsi.fundamentalism.attachments;

import com.mojang.serialization.Codec;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.customAtt.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.attachments.customAtt.EfficiencyAttachment;
import com.ypsi.fundamentalism.attachments.customAtt.PrinciplesLevelsAttachment;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class YpsAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FundamentalPrinciples.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> BOOST =
            ATTACHMENTS.register("boost_fatigue", () ->
                AttachmentType.builder(() -> 0)
                        .serialize(Codec.INT)
                        .copyOnDeath()
                        .sync(
                                (IAttachmentHolder holder, ServerPlayer receiver) -> holder == receiver,
                                ByteBufCodecs.INT
                        )
                        .build());

    public static final Supplier<AttachmentType<Integer>> CURRENT_FATIGUE =
            ATTACHMENTS.register("current_exhaustion", () ->
                    AttachmentType.<Integer>builder(() -> 0)
                            .serialize(Codec.INT)
                            .copyOnDeath()
                            .sync(
                                    (IAttachmentHolder holder, ServerPlayer receiver) -> holder == receiver,
                                    ByteBufCodecs.INT
                            )
                            .build());

    public static final Supplier<AttachmentType<Integer>> LEVEL_FATIGUE =
            ATTACHMENTS.register("level_exhaustion", () ->
                    AttachmentType.<Integer>builder(() -> 0)
                            .serialize(Codec.INT)
                            .copyOnDeath()
                            .sync(
                                    (IAttachmentHolder holder, ServerPlayer receiver) -> holder == receiver,
                                    ByteBufCodecs.INT
                            )
                            .build());

    public static final Supplier<AttachmentType<PrinciplesLevelsAttachment>> PRINCIPLES_LEVELS =
            ATTACHMENTS.register("spell_category_levels", () ->
                    AttachmentType.<PrinciplesLevelsAttachment>builder(PrinciplesLevelsAttachment::new)
                            .serialize(PrinciplesLevelsAttachment.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static final Supplier<AttachmentType<EfficiencyAttachment>> CAST_EFFICIENCY =
            ATTACHMENTS.register("cast_efficiency", () ->
                    AttachmentType.<EfficiencyAttachment>builder(EfficiencyAttachment::new)
                            .serialize(EfficiencyAttachment.CODEC)
                            .copyOnDeath()
                            .sync(
                                    (holder, receiver) -> holder == receiver,
                                    ByteBufCodecs.fromCodec(EfficiencyAttachment.CODEC)
                            )
                            .build()
                    );

    public static final Supplier<AttachmentType<AvailableSpellsAttachment>> SPELL_LIST =
            ATTACHMENTS.register("spell_list", () ->
                    AttachmentType.<AvailableSpellsAttachment>builder(AvailableSpellsAttachment::new)
                            .serialize(AvailableSpellsAttachment.CODEC)
                            .copyOnDeath()
                            .build()
            );



    public static void register(IEventBus eventBus){
        ATTACHMENTS.register(eventBus);
        
    }
}
