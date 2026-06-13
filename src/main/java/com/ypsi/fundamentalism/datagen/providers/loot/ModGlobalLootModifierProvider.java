package com.ypsi.fundamentalism.datagen.providers.loot;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, FundamentalPrinciples.MOD_ID);
    }

    @Override
    protected void start() {
        var ominousCondition = LootTableIdCondition.builder(
                ResourceLocation.parse("chests/trial_chambers/reward_ominous")
        ).build();
        var strongholdLibrary = LootTableIdCondition.builder(
                ResourceLocation.parse("chests/stronghold_library")
        ).build();

        var suspiciousSand = LootTableIdCondition.builder(
                ResourceLocation.parse("blocks/suspicious_sand")
        ).build();
        var suspiciousGravel = LootTableIdCondition.builder(
                ResourceLocation.parse("blocks/suspicious_gravel")
        ).build();

        var desertWellArcheology = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/desert_well")
        ).build();
        var desertPyramidArcheology = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/desert_pyramid")
        ).build();
        var trailRuinsArcheologyCOMMON = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/trail_ruins_common")
        ).build();
        var trailRuinsArcheologyRARE = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/trail_ruins_rare")
        ).build();
        var oceanRuinWarmArcheology = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/ocean_ruin_warm")
        ).build();
        var oceanRuinColdArcheology = LootTableIdCondition.builder(
                ResourceLocation.parse("archaeology/ocean_ruin_cold")
        ).build();





        add("ominous_vault_scroll_case",
                new ItemLootModifier(
                        new LootItemCondition[]{ominousCondition},2, 0.9F, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );
        add("stronghold_library_scroll_case",
                new ItemLootModifier(new LootItemCondition[]{strongholdLibrary}, 1, 0.6F, ModItems.ANCIENT_SCROLL_CASE.get())
        );

        add("desert_well_archeology_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{desertWellArcheology}, 0.05f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );
        add("desert_pyramid_archeology_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{desertPyramidArcheology}, 0.10f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );

        add("trail_ruins_archeology_common_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{trailRuinsArcheologyCOMMON}, 0.05f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );
        add("trail_ruins_archeology_rare_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{trailRuinsArcheologyRARE}, 0.20f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );

        add("ocean_ruin_warm_archeology_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{oceanRuinWarmArcheology}, 0.10f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );
        add("ocean_ruin_cold_archeology_scroll_case",
                new ArcheologyLootModifier(
                        new LootItemCondition[]{oceanRuinColdArcheology}, 0.10f, ModItems.ANCIENT_SCROLL_CASE.get()
                )
        );
    }
}
