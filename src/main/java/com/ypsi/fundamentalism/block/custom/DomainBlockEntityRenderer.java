package com.ypsi.fundamentalism.block.custom;

import com.mojang.blaze3d.vertex.*;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.domain.DomainRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

public class DomainBlockEntityRenderer implements BlockEntityRenderer<DomainBlockEntity> {
    private static final ResourceLocation TEXTURA = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID
            , "textures/block/domain_block.png");
    private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEXTURA);


    public DomainBlockEntityRenderer(BlockEntityRendererProvider.Context context){

    }

    @Override
    public void render(DomainBlockEntity domainBlockEntity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        int argb = domainBlockEntity.getColor();
        float a = ((argb >> 24) & 0xFF) / 255f;
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8)  & 0xFF) / 255f;
        float b = (argb & 0xFF)         / 255f;

        BlockState state = domainBlockEntity.getBlockState();
        if (state.isAir()) return;

        Level level = domainBlockEntity.getLevel();
        if (level == null) return;

        BlockPos pos = domainBlockEntity.getBlockPos();
        RenderType renderType = RenderType.solid();

        VertexConsumer base = multiBufferSource.getBuffer(renderType);
        VertexConsumer tinted = new VertexConsumer() {
            @Override
            public VertexConsumer addVertex(float x, float y, float z) {
                return base.addVertex(x, y, z);
            }

            @Override
            public VertexConsumer setColor(int red, int green, int blue, int alpha) {
                int nr = (int) ((red   / 255f) * r * 255f);
                int ng = (int) ((green / 255f) * g * 255f);
                int nb = (int) ((blue  / 255f) * b * 255f);
                int na = (int) ((alpha / 255f) * a * 255f);
                return base.setColor(nr, ng, nb, na);
            }
            @Override
            public VertexConsumer setUv(float u, float v) {
                return base.setUv(u, v);
            }
            @Override
            public VertexConsumer setUv1(int u, int v) {
                return base.setUv1(u, v);
            }
            @Override
            public VertexConsumer setUv2(int u, int v) {
                return base.setUv2(u, v);
            }
            @Override
            public VertexConsumer setNormal(float nx, float ny, float nz) {
                return base.setNormal(nx, ny, nz);
            }
        };
        Minecraft.getInstance().getBlockRenderer().renderBatched(
                state, pos, level, poseStack, tinted, true, RandomSource.create(42L)
        );

    }
}
