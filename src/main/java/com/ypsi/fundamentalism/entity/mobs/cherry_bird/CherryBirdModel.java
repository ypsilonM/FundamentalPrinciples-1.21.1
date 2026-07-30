package com.ypsi.fundamentalism.entity.mobs.cherry_bird;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CherryBirdModel extends GeoModel<CherryBirdEntity> {

    @Override
    public ResourceLocation getModelResource(CherryBirdEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/cherry_bird.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CherryBirdEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/cherry_bird/cherry_bird_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CherryBirdEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/cherry_bird.animation.json");
    }
}
