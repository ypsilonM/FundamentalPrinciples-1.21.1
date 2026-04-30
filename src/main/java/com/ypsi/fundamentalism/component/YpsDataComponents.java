package com.ypsi.fundamentalism.component;

import com.mojang.serialization.Codec;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookLevel;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookXP;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
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


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpellBookXP>> SPELLBOOK_XP =
            register("spellbook_xp", objectBuilder -> objectBuilder.persistent(SpellBookXP.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(SpellBookXP.CODEC)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SpellBookLevel>> SPELLBOOK_LEVEL =
            register("spellbook_level", objectBuilder -> objectBuilder.persistent(SpellBookLevel.CODEC)
                    .networkSynchronized(ByteBufCodecs.fromCodec(SpellBookLevel.CODEC)));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> YP_SPELL_SLOTS = register("yp_slots",
            integerBuilder -> integerBuilder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));


    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderUnaryOperator){
        return DATA_COMPONENT_TYPES.register(name, () -> builderUnaryOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus){
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
