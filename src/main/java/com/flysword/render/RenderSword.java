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

public class RenderSword<T extends EntitySword> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;

    public RenderSword(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ItemStack renderItemStack = entity.getRenderItemStack();
        if (renderItemStack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.0D, EntitySword.RENDER_OFFSET_Y, 0.0D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45.0F));
        // 等价于 GlStateManager.rotate(90, 1, 1, 0)
        poseStack.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(90.0D), 1.0F, 1.0F, 0.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entityYaw + 90.0F));

        float spin = this.spinDegrees(entity, partialTick);
        if (spin != 0.0F) {
            // 绕剑刃长轴自旋形成电钻感，长轴方向与上面用于摆正物品模型的轴一致
            poseStack.mulPose(new Quaternionf().rotationAxis((float) Math.toRadians(spin), 1.0F, 1.0F, 0.0F));
        }

        this.itemRenderer.renderStatic(renderItemStack, ItemDisplayContext.NONE, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    /**
     * 剑体绕剑刃长轴的自旋角度。默认不自旋，只有御剑术的飞剑会覆盖它
     */
    protected float spinDegrees(T entity, float partialTick) {
        return 0.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
