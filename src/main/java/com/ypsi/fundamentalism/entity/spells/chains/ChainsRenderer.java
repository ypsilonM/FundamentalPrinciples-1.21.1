package com.ypsi.fundamentalism.entity.spells.chains;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.entity.spells.root.RootModel;
import io.redspace.ironsspellbooks.render.GeoLivingEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class ChainsRenderer extends GeoLivingEntityRenderer<ChainsEntity> {
    public ChainsRenderer(EntityRendererProvider.Context context) {
        super(context, new ChainsModel());
    }

    public void preRender(PoseStack poseStack, ChainsEntity animatable, BakedGeoModel model, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        Entity rooted = animatable.getFirstPassenger();
        if (rooted != null) {
            float scale = rooted.getBbWidth() / 0.6F;
            float hscale = rooted.getBbHeight() * 0.8F;
            poseStack.scale(scale, hscale, scale);
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }
}
