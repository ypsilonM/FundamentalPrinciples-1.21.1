package com.ypsi.fundamentalism.entity.mobs.runear;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RunearRenderer extends GeoEntityRenderer<RunearEntity> {
    public RunearRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RunearModel());
    }
}
