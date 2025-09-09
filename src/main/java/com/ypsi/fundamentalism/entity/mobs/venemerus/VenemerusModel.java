package com.ypsi.fundamentalism.entity.mobs.venemerus;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerEntity;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import software.bernie.geckolib.model.GeoModel;

public class VenemerusModel extends GeoModel<VenemerusEntity> {

    @Override
    public ResourceLocation getModelResource(VenemerusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/venemerus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VenemerusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/venemerus/venemerus_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VenemerusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/venemerus.animation.json");
    }
}
