package com.flysword.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentFlySword extends Enchantment {
    public static final String NAME = "flysword";

    public EnchantmentFlySword() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * 各等级区间为 L1 [1,11]、L2 [11,21]、L3 [21,31]、L4 [31,41]、L5 [41,51]。
     * 相邻区间相接并重叠，使附魔台 cost 单调映射到等级且不存在空档。
     * 附魔台在 15 书架时 cost 上限为 30，因此最高只能附出 3 级，4~5 级需铁砧合成。
     */
    @Override
    public int getMinCost(int level) {
        return 1 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        return this.getMinCost(level) + 10;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem;
    }
}
