package com.ypsi.fundamentalism.entity.mobs.hemomancer;

import com.ypsi.fundamentalism.entity.mobs.imp.ImpModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HemomancerRenderer extends GeoEntityRenderer<HemomancerEntity> {
    public HemomancerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HemomancerModel());
    }
}
