package com.flysword.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentSwordBeam extends Enchantment {
    public static final String NAME = "swordbeam";

    public EnchantmentSwordBeam() {
        super(Rarity.COMMON, EnchantmentCategory.WEAPON, EquipmentSlot.values());
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    /**
     * 与御剑飞行保持同一等级曲线：L1 [1,8]、L2 [9,16]、L3 [17,24]、L4 [25,32]、L5 [33,40]。
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
}
