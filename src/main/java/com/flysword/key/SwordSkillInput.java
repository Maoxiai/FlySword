package com.flysword.key;

import com.flysword.FlySwordMod;
import com.flysword.network.server.SwordSkillPacket;
import com.flysword.utils.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端招式键输入。按键在客户端被消费后通知服务端释放剑招
 */
@Mod.EventBusSubscriber(modid = FlySwordMod.MODID, value = Dist.CLIENT)
public final class SwordSkillInput {

    private SwordSkillInput() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        while (ModKeys.KEY_SWORD_SKILL.consumeClick()) {
            // 潜行时为御剑术，否则为剑气纵横；具体能否释放由服务端按境界判定
            int mode = minecraft.player != null && minecraft.player.isShiftKeyDown()
                    ? SwordSkillPacket.MODE_FLYING_SWORD
                    : SwordSkillPacket.MODE_SPREAD;
            PacketDispatcher.sendToServer(new SwordSkillPacket(mode));
        }
        while (ModKeys.KEY_SWORD_SKILL_ALT.consumeClick()) {
            // 第二个招式键：潜行时为剑雨，否则为万剑归宗
            int mode = minecraft.player != null && minecraft.player.isShiftKeyDown()
                    ? SwordSkillPacket.MODE_SWORD_RAIN
                    : SwordSkillPacket.MODE_MYRIAD_SWORDS;
            PacketDispatcher.sendToServer(new SwordSkillPacket(mode));
        }
    }
}
