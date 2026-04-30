package com.ypsi.fundamentalism.attributes;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.attribute.MagicRangedAttribute;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.entity.ai.attributes.Attribute;

public class YpsAttributes {
    public static final DeferredRegister<Attribute> YPATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, FundamentalPrinciples.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> MAX_FATIGUE =
            YPATTRIBUTES.register("exhaustion.max", () ->
                    new RangedAttribute("attribute.ypfundamentals.exhaustion.max", 0, 0, 10000)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> FATIGUE_REGEN =
            YPATTRIBUTES.register("exhaustion.regen", () ->
                    new RangedAttribute("attribute.ypfundamentals.exhaustion.regen", 1, 0, 10)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> FUNDAMENTALISM_SPELL_POWER =
            YPATTRIBUTES.register("fundamentalism_spell_power",
                    () -> new MagicRangedAttribute("attribute.ypfundamentals.fundamentalism_spell_power",
                            1.0, -50, 50).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> FUNDAMENTALISM_MAGIC_RESIST =
            YPATTRIBUTES.register("fundamentalism_magic_resist",
                    () -> new MagicRangedAttribute("attribute.ypfundamentals.fundamentalism_magic_resist",
                            1.0, -50, 50).setSyncable(true));

    public static void register(IEventBus bus) {
        YPATTRIBUTES.register(bus);
    }
}
