package com.flysword.network.server;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntitySwordBeam;
import com.flysword.loader.EntityLoader;
import com.flysword.utils.PlayerUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

            if (player.getRandom().nextBoolean()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
            PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.4F, 0.5F);

            float damage = getDamage(player, stack) * (level * 0.25F);
            EntitySwordBeam beam = new EntitySwordBeam(EntityLoader.SWORD_BEAM.get(), player, player.level())
                    .setLevel(level)
                    .setDamage(damage);
            beam.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, beam.getVelocity(), 1.0F);
            player.level().addFreshEntity(beam);

            // TODO 可配置
            player.getCooldowns().addCooldown(stack.getItem(), 80 - level * 13);
        });
        ctx.setPacketHandled(true);
    }

    /**
     * Returns player's base damage (with sword) plus 1.0F per level
     */
    private float getDamage(Player player, ItemStack heldItemStack) {
        float f = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        float f1 = EnchantmentHelper.getDamageBonus(heldItemStack, MobType.UNDEFINED);
        return f + f1;
    }
}
