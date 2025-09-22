package com.ypsi.fundamentalism.entity.spells.sacredDisk;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public class SacredDiskRenderer extends EntityRenderer<Projectile> {
    public static final ModelLayerLocation MODEL_LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "disk_model"), "main");

    private static ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/entity/disk/disk.png");

    private final ModelPart disk;

    public SacredDiskRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelpart = context.bakeLayer(MODEL_LAYER_LOCATION);
        this.disk = modelpart.getChild("disk");
    }

    public static LayerDefinition createDiskLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Crear un disco plano (cuadrado delgado)
        partdefinition.addOrReplaceChild("disk", CubeListBuilder.create()
                        .texOffs(0, 0)
                        // El último parámetro scale ajusta cómo se aplica la textura
                        .addBox(-8.0F, -0.4F, -8.0F, 16.0F, 0.4F, 16.0F), // Usar escala 1:1 para la textura
                PartPose.offset(0.0f, 0.0f, 0.0f));

        return LayerDefinition.create(meshdefinition, 16, 16);
    }

    @Override
    public void render(Projectile entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int light) {
        poseStack.pushPose();
        poseStack.translate(0, entity.getBoundingBox().getYsize() * 0.5f, 0);

        Vec3 motion = entity.getDeltaMovement();
        float xRot = -((float) (Mth.atan2(motion.horizontalDistance(), motion.y) * (double) (180F / (float) Math.PI)) - 90.0F);
        float yRot = -((float) (Mth.atan2(motion.z, motion.x) * (double) (180F / (float) Math.PI)) + 90.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot));

        float spin = (entity.tickCount + partialTicks) * 15.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));
        this.disk.render(poseStack, consumer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, bufferSource, light);
    }

    @Override
    public ResourceLocation getTextureLocation(Projectile entity) {
        return TEXTURE;
    }
}
