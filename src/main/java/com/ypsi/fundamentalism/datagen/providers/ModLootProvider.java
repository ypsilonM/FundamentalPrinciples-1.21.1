package com.ypsi.fundamentalism.datagen.providers;

import com.mojang.serialization.MapCodec;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.datagen.providers.loot.ArcheologyLootModifier;
import com.ypsi.fundamentalism.datagen.providers.loot.ItemLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootProvider {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, FundamentalPrinciples.MOD_ID);

    public static final Supplier<MapCodec<ItemLootModifier>> OMINOUS_VAULT =
            LOOT_MODIFIERS.register("ominous_vault_scroll_case", () -> ItemLootModifier.CODEC);
    public static final Supplier<MapCodec<ItemLootModifier>> STRONGHOLD_LIBRARY  =
            LOOT_MODIFIERS.register("stronghold_library_scroll_case", () -> ItemLootModifier.CODEC);
    public static final Supplier<MapCodec<ArcheologyLootModifier>> DESERT_WELL  =
            LOOT_MODIFIERS.register("desert_well_archeology_scroll_case", () -> ArcheologyLootModifier.CODEC);
    public static final Supplier<MapCodec<ArcheologyLootModifier>> DESERT_PYRAMID  =
            LOOT_MODIFIERS.register("desert_pyramid_archeology_scroll_case", () -> ArcheologyLootModifier.CODEC);
//    public static final Supplier<MapCodec<ItemLootModifier>> TRAIL_RUINS_COMMON  =
//            LOOT_MODIFIERS.register("trail_ruins_archeology_common_scroll_case", () -> ItemLootModifier.CODEC);
//    public static final Supplier<MapCodec<ItemLootModifier>> TRAIL_RUINS_RARE  =
//            LOOT_MODIFIERS.register("trail_ruins_archeology_rare_scroll_case", () -> ItemLootModifier.CODEC);
//    public static final Supplier<MapCodec<ItemLootModifier>> OCEAN_RUIN_WARM  =
//            LOOT_MODIFIERS.register("ocean_ruin_warm_archeology_scroll_case", () -> ItemLootModifier.CODEC);
//    public static final Supplier<MapCodec<ItemLootModifier>> OCEAN_RUIN_COLD  =
//            LOOT_MODIFIERS.register("ocean_ruin_cold_archeology_scroll_case", () -> ItemLootModifier.CODEC);



    public static void register(IEventBus eventBus){
        LOOT_MODIFIERS.register(eventBus);
    }

}
