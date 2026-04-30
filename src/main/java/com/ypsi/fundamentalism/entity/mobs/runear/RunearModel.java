package com.ypsi.fundamentalism.entity.mobs.runear;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RunearModel extends GeoModel<RunearEntity> {
    @Override
    public ResourceLocation getModelResource(RunearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/runear.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RunearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/runear/runear_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RunearEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/runear.animation.json");
    }
}
