package com.ypsi.fundamentalism.entity.mobs.hemomancer;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class HemomancerModel extends GeoModel<HemomancerEntity> {
    @Override
    public ResourceLocation getModelResource(HemomancerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/hemomancer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HemomancerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/hemomancer/hemomancer_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HemomancerEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/hemomancer.animation.json");
    }
}
