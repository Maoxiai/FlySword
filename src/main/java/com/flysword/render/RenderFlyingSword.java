package com.flysword.render;

import com.flysword.config.FlySwordConfig;
import com.flysword.entity.EntityFlyingSword;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * 御剑术飞剑的渲染器。沿用载具飞剑的渲染与摆正方式，额外让剑体绕剑刃长轴持续自旋
 */
public class RenderFlyingSword extends RenderSword<EntityFlyingSword> {

    public RenderFlyingSword(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected float spinDegrees(EntityFlyingSword entity, float partialTick) {
        return (entity.tickCount + partialTick) * FlySwordConfig.FLYING_SWORD_SPIN_SPEED.get().floatValue();
    }
}
