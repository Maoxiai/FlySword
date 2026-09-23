package com.flysword.network.server;

import com.flysword.config.FlySwordConfig;
import com.flysword.enchantment.ModEnchantments;
import com.flysword.utils.PlayerUtils;
import com.flysword.utils.SwordBeamLauncher;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 剑气：手持带剑气附魔的剑左键点击空气时，向前方发射一道剑气
 */
public class SpawnSwordBeamPacket {

    public SpawnSwordBeamPacket() {
    }

    public SpawnSwordBeamPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }

            ItemStack stack = player.getMainHandItem();
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SWORD_BEAM.get(), stack);
            if (level <= 0 || player.getCooldowns().isOnCooldown(stack.getItem())) {
                return;
            }

            if (player.getRandom().nextDouble() < FlySwordConfig.BEAM_DURABILITY_COST_CHANCE.get()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
            PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.4F, 0.5F);

            float damage = SwordBeamLauncher.baseDamage(player, stack)
                    * (float) (level * FlySwordConfig.BEAM_DAMAGE_PER_LEVEL.get());
            SwordBeamLauncher.launch(player, level, damage, 0.0F);

            player.getCooldowns().addCooldown(stack.getItem(),
                    FlySwordConfig.BEAM_COOLDOWN_BASE.get() - level * FlySwordConfig.BEAM_COOLDOWN_PER_LEVEL.get());
        });
        ctx.setPacketHandled(true);
    }
}
