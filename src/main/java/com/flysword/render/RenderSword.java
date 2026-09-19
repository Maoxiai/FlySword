package com.flysword.render;

import com.flysword.entity.EntitySword;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class RenderSword extends EntityRenderer<EntitySword> {
    private final ItemRenderer itemRenderer;

    public RenderSword(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntitySword entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack renderItemStack = entity.getRenderItemStack();
        if (renderItemStack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        // 等价于 GlStateManager.rotate(90, 1, 1, 0)
        poseStack.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(90.0D), 1.0F, 1.0F, 0.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entityYaw + 90.0F));

        this.itemRenderer.renderStatic(renderItemStack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySword entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
