package com.flysword.utils;

import com.flysword.entity.EntitySwordBeam;
import com.flysword.loader.EntityLoader;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/**
 * 剑气发射器。所有以剑气为弹体的招式（剑气、剑气纵横、日后的群剑类招式）都走这里，
 * 避免各自复制一遍生成实体、设定朝向与伤害的流程。
 */
public final class SwordBeamLauncher {

    private SwordBeamLauncher() {
    }

    /**
     * 玩家攻击力加上武器的附魔伤害加成，即剑气伤害公式里的基础值
     */
    public static float baseDamage(Player player, ItemStack stack) {
        float attack = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float enchantBonus = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
        return attack + enchantBonus;
    }

    /**
     * 发射一道剑气
     *
     * @param yawOffset 相对玩家朝向的水平偏转角（度），0 为正前方
     */
    public static void launch(Player player, int level, float damage, float yawOffset) {
        EntitySwordBeam beam = new EntitySwordBeam(EntityLoader.SWORD_BEAM.get(), player, player.level())
                .setLevel(level)
                .setDamage(damage);
        beam.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffset, 0.0F,
                beam.getVelocity(), 1.0F);
        player.level().addFreshEntity(beam);
    }
}
