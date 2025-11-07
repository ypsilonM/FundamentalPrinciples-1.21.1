package com.ypsi.fundamentalism.attachments;

import com.mojang.serialization.Codec;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryLevels;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class YpsAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FundamentalPrinciples.MOD_ID);

    public static final Supplier<AttachmentType<Integer>> CURRENT_EXHAUSTION =
            ATTACHMENTS.register("current_exhaustion", () ->
                    AttachmentType.<Integer>builder(() -> 0)
                            .serialize(Codec.INT)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<Integer>> LEVEL_EXHAUSTION =
            ATTACHMENTS.register("level_exhaustion", () ->
                    AttachmentType.<Integer>builder(() -> 0)
                            .serialize(Codec.INT)
                            .copyOnDeath()
                            .build());

    public static final Supplier<AttachmentType<SpellCategoryLevels>> SPELL_CATEGORY_LEVELS =
            ATTACHMENTS.register("spell_category_levels", () ->
                    AttachmentType.<SpellCategoryLevels>builder(SpellCategoryLevels::new)
                            .serialize(SpellCategoryLevels.CODEC)
                            .copyOnDeath()
                            .build()
            );

    public static void register(IEventBus eventBus){
        ATTACHMENTS.register(eventBus);
        
    }
}
