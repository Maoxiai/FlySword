package com.flysword.loader;

import com.flysword.FlySwordMod;
import com.flysword.render.RenderEntitySwordBeam;
import com.flysword.render.RenderSword;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FlySwordMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderLoader {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityLoader.SWORD.get(), RenderSword::new);
        event.registerEntityRenderer(EntityLoader.SWORD_BEAM.get(), RenderEntitySwordBeam::new);
    }
}
