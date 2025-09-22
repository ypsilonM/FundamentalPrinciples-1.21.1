package com.ypsi.fundamentalism.entity.mobs.imp;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ImpModel extends GeoModel<ImpEntity> {

    @Override
    public ResourceLocation getModelResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/imp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/imp/imp_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ImpEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/imp.animation.json");
    }
}
