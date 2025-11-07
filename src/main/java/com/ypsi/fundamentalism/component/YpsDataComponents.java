package com.ypsi.fundamentalism.component;

import com.mojang.serialization.Codec;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class YpsDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FundamentalPrinciples.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> TONIC_CHARGES = register("tonic_charges",
            objectBuilder -> objectBuilder.persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator){
        return DATA_COMPONENT_TYPES.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus){
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
