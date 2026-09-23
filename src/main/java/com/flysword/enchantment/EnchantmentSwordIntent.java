package com.flysword.enchantment;

import com.flysword.config.FlySwordConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * 剑意通明：持剑时提升攻击伤害的附魔，是剑道境界带来的额外斩击威力
 */
public class EnchantmentSwordIntent extends Enchantment {
    public static final String NAME = "swordintent";

    public EnchantmentSwordIntent() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * 与御剑飞行、剑气保持同一等级曲线：L1 [1,8]、L2 [9,16]、L3 [17,24]、L4 [25,32]、L5 [33,40]。
     * 附魔台在 15 书架时 cost 上限为 30，落在 4 级区间，因此最高附出 4 级，5 级需铁砧合成。
     */
    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 8;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 7;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }

    /**
     * 持剑时的额外攻击伤害。默认值与原版锋利同曲线，1 级 +1.0、5 级 +3.0。
     * 本附魔不与锋利互斥，两者可以叠加。
     */
    @Override
    public float getDamageBonus(int level, MobType mobType) {
        return (float) (FlySwordConfig.SWORD_INTENT_DAMAGE_BASE.get()
                + (level - 1) * FlySwordConfig.SWORD_INTENT_DAMAGE_PER_LEVEL.get());
    }
}
