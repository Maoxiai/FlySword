/**
    Copyright (C) <2017> <coolAlias>

    This file is part of coolAlias' Dynamic Sword Skills Minecraft Mod; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.flysword.render;

import com.flysword.FlySwordMod;
import com.flysword.entity.EntitySwordBeam;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderEntitySwordBeam extends EntityRenderer<EntitySwordBeam> {
	private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FlySwordMod.MODID, "textures/entity/sword_beam.png");

	public RenderEntitySwordBeam(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius = 0.25F;
	}

	@Override
	public void render(EntitySwordBeam entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
		poseStack.scale(1.5F, 0.5F, 1.5F);

		VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
		Matrix4f matrix = poseStack.last().pose();
		vertex(consumer, matrix, -0.5F, -0.25F, 0.0F, 0.0F, 1.0F, packedLight);
		vertex(consumer, matrix, 0.5F, -0.25F, 0.0F, 1.0F, 1.0F, packedLight);
		vertex(consumer, matrix, 0.5F, 0.75F, 0.0F, 1.0F, 0.0F, packedLight);
		vertex(consumer, matrix, -0.5F, 0.75F, 0.0F, 0.0F, 0.0F, packedLight);

		poseStack.popPose();

		super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
	}

	private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float u, float v, int packedLight) {
		consumer.vertex(matrix, x, y, z)
				.color(255, 255, 255, 255)
				.uv(u, v)
				.overlayCoords(OverlayTexture.NO_OVERLAY)
				.uv2(packedLight)
				.normal(0.0F, 1.0F, 0.0F)
				.endVertex();
	}

	@Override
	public ResourceLocation getTextureLocation(EntitySwordBeam entity) {
		return TEXTURE;
	}
}
