package com.ypsi.fundamentalism.entity.spells.chains;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ChainsModel extends GeoModel<ChainsEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/chains.png");
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "geo/chains.geo.json");
    public static final ResourceLocation ANIMS = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "animations/chains_animations.json");

    public ChainsModel() {
    }

    @Override
    public ResourceLocation getTextureResource(ChainsEntity object) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getModelResource(ChainsEntity object) {
        return MODEL;
    }

    @Override
    public ResourceLocation getAnimationResource(ChainsEntity animatable) {
        return ANIMS;
    }
}
