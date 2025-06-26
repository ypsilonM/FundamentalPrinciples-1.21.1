package com.ypsi.fundamentalism.entity.imp;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ImpRenderer extends GeoEntityRenderer<ImpEntity> {
    public ImpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ImpModel());
    }
}
