package com.flysword.key;

import com.flysword.FlySwordMod;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = FlySwordMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeys {
    public static final KeyMapping KEY_FLY_SWORD_DOWN = new KeyMapping(
            "key.fly_sword_down",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_CONTROL,
            "key.categories.gameplay");

    /**
     * 专用招式键。释放哪一招由手持剑上御剑飞行的境界等级决定，因此全mod只需要这一个招式键。
     * 默认避开已被占用的左 Ctrl，以及被 JEI 占用的 R。
     */
    public static final KeyMapping KEY_SWORD_SKILL = new KeyMapping(
            "key.fly_sword_skill",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.gameplay");

    /**
     * 第二个招式键，承载万剑归宗与剑雨。与第一个招式键分开是为了不挤占已发布的键位，
     * 同时保留「同一键 + 修饰键」的分派方式。
     */
    public static final KeyMapping KEY_SWORD_SKILL_ALT = new KeyMapping(
            "key.fly_sword_skill_2",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "key.categories.gameplay");

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KEY_FLY_SWORD_DOWN);
        event.register(KEY_SWORD_SKILL);
        event.register(KEY_SWORD_SKILL_ALT);
    }
}
