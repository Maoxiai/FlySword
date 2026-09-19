package com.flysword;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntitySword;
import com.flysword.loader.EntityLoader;
import com.flysword.network.server.SpawnSwordBeamPacket;
import com.flysword.utils.PacketDispatcher;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FlySwordMod.MODID)
public class FlySwordMod {
    public static final String MODID = "flysword";

    public FlySwordMod(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        EntityLoader.register(modBus);
        ModEnchantments.register(modBus);
        PacketDispatcher.initialize();

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * 手持带有御剑飞行附魔的剑右键时，召唤飞剑并骑乘
     */
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (player.isPassenger()) {
            return;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FLY_SWORD.get(), stack) <= 0) {
            return;
        }

        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        EntitySword entitySword = new EntitySword(EntityLoader.SWORD.get(), level);
        entitySword.setItemStack(stack);
        entitySword.setOwnerId(player.getUUID());
        entitySword.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        level.addFreshEntity(entitySword);
        player.startRiding(entitySword);
    }

    /**
     * 客户端左键点击空气时通知服务端释放剑气
     */
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getEntity().level().isClientSide) {
            PacketDispatcher.sendToServer(new SpawnSwordBeamPacket());
        }
    }
}
