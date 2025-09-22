package com.ypsi.fundamentalism.worldgen;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.ModEntities;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> SPAWN_HEM = registerKey("spawn_hemomancer");
    public static final ResourceKey<BiomeModifier> SPAWN_IMP = registerKey("spawn_imp");
    public static final ResourceKey<BiomeModifier> SPAWN_VENE = registerKey("spawn_venemerus");

    public static void bootstrap(BootstrapContext<BiomeModifier> context){
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(SPAWN_HEM, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SOUL_SAND_VALLEY), biomes.getOrThrow(Biomes.BASALT_DELTAS)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.HEMOMANCER.get(), 2, 1, 1))
        ));
        context.register(SPAWN_IMP, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.CRIMSON_FOREST), biomes.getOrThrow(Biomes.BADLANDS), biomes.getOrThrow(Biomes.ERODED_BADLANDS), biomes.getOrThrow(Biomes.WOODED_BADLANDS)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.IMP.get(), 18, 1, 3))
        ));
        context.register(SPAWN_VENE, new BiomeModifiers.AddSpawnsBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP), biomes.getOrThrow(Biomes.DRIPSTONE_CAVES), biomes.getOrThrow(Biomes.JUNGLE), biomes.getOrThrow(Biomes.SPARSE_JUNGLE)),
                List.of(new MobSpawnSettings.SpawnerData(ModEntities.VENEMERUS.get(), 10, 2, 4))
        ));

    }

    private static ResourceKey<BiomeModifier> registerKey(String name){
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, name));
    }
}
