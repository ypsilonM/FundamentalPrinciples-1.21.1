package com.ypsi.fundamentalism.entity.spells.domain;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class DomainRenderer extends GeoEntityRenderer<DomainEntity> {
    public DomainRenderer(EntityRendererProvider.Context context) {
        super(context, new DomainModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DomainEntity domainEntity) {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/domain.png");
    }

    @Override
    public Color getRenderColor(DomainEntity domainEntity, float partialTick, int packedLight) {
        Color baseColor = super.getRenderColor(domainEntity, partialTick, packedLight);
        int domainTint = domainEntity.getColor();

        int newA = (baseColor.getAlpha() * ((domainTint >> 24) & 0xFF)) / 255;
        int newR = (baseColor.getRed() * ((domainTint >> 16) & 0xFF)) / 255;
        int newG = (baseColor.getGreen() * ((domainTint >> 8) & 0xFF)) / 255;
        int newB = (baseColor.getBlue() * ((domainTint) & 0xFF)) / 255;

        return Color.ofARGB(newA, newR, newG, newB);
    }
}
