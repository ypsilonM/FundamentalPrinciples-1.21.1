package com.ypsi.fundamentalism.entity.mobs.imp;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ImpRenderer extends GeoEntityRenderer<ImpEntity> {
    public ImpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ImpModel());
    }
}
