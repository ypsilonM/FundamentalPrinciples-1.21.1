package com.ypsi.fundamentalism.attributes;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.registries.DeferredRegister;

public class YpsDamageTypes {
    public static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, name));
    }

    public static final ResourceKey<DamageType> FUNDAMENTAL_DAMAGE = register("fundamentalism_magic");

    public static void bootstrap(BootstrapContext<DamageType> context){
        context.register(FUNDAMENTAL_DAMAGE, new DamageType(FUNDAMENTAL_DAMAGE.location().getPath(), DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0f));
    }
}
