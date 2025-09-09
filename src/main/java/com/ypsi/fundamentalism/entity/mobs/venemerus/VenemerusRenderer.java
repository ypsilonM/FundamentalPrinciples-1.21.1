package com.ypsi.fundamentalism.entity.mobs.venemerus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VenemerusRenderer extends GeoEntityRenderer<VenemerusEntity> {

    public VenemerusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VenemerusModel());
    }


}
