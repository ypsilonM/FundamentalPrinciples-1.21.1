package com.ypsi.fundamentalism.attributes;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.minecraft.world.entity.ai.attributes.Attribute;

public class YpsAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, FundamentalPrinciples.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> MAX_EXHAUSTION =
            ATTRIBUTES.register("exhaustion.max", () ->
                    new RangedAttribute("attribute.ypfundamentals.exhaustion.max", 0, 0, 10000)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> EXHAUSTION_REGEN =
            ATTRIBUTES.register("exhaustion.regen", () ->
                    new RangedAttribute("attribute.ypfundamentals.exhaustion.regen", 1, 0, 10)
                            .setSyncable(true)
            );

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
