package com.ypsi.fundamentalism.dimension;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class ModDimensions {

    public static final ResourceKey<Level> DOMAIN_DIMENSION =  ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "domain_dimension")
    );
}
