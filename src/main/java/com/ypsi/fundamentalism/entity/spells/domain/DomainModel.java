package com.ypsi.fundamentalism.entity.spells.domain;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class DomainModel extends GeoModel<DomainEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/domain.png");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/domain.geo.json");
    public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/domain_animations.json");

    @Override
    public ResourceLocation getModelResource(DomainEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(DomainEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(DomainEntity animatable) {
        return ANIMS;
    }
}
