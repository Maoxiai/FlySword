package com.flysword.enchantment;

import com.flysword.FlySwordMod;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, FlySwordMod.MODID);

    public static final RegistryObject<Enchantment> FLY_SWORD =
            ENCHANTMENTS.register(EnchantmentFlySword.NAME, EnchantmentFlySword::new);

    public static final RegistryObject<Enchantment> SWORD_BEAM =
            ENCHANTMENTS.register(EnchantmentSwordBeam.NAME, EnchantmentSwordBeam::new);

    public static final RegistryObject<Enchantment> SWORD_INTENT =
            ENCHANTMENTS.register(EnchantmentSwordIntent.NAME, EnchantmentSwordIntent::new);

    public static void register(IEventBus modBus) {
        ENCHANTMENTS.register(modBus);
    }
}
