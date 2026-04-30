package com.ypsi.fundamentalism.advancements.triggers;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class YpTriggers {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS =
            DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, FundamentalPrinciples.MOD_ID);


    public static final Supplier<PrinciplesLevelTrigger> PRINCIPLES_LEVEL_TRIGGER_SUPPLIER =
            TRIGGERS.register("principles_trigger_level", PrinciplesLevelTrigger::new);

    public static void register(IEventBus bus){
        TRIGGERS.register(bus);
    }
}
