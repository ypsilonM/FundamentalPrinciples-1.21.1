package com.ypsi.fundamentalism.entity.mobs.cherry_bird;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CherryBirdRenderer extends GeoEntityRenderer<CherryBirdEntity> {
    public CherryBirdRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CherryBirdModel());
    }
}
