package com.flysword;

import com.flysword.config.FlySwordConfig;
import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntitySword;
import com.flysword.loader.EntityLoader;
import com.flysword.network.server.SpawnSwordBeamPacket;
import com.flysword.utils.PacketDispatcher;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.joml.Vector3f;

@Mod(FlySwordMod.MODID)
public class FlySwordMod {
    public static final String MODID = "flysword";

    public FlySwordMod(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();

        context.registerConfig(ModConfig.Type.COMMON, FlySwordConfig.SPEC);

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

    /**
     * 剑罡护体：手持带有御剑飞行附魔的剑受击时按等级减免伤害。
     * 御剑飞行的 5 个等级对应炼气至化神五个境界，因此以它的等级作为招式门槛。
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!FlySwordConfig.SWORD_WARD_ENABLED.get()) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        // 虚空伤害与 /kill 一类的伤害不参与减免
        if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        }
        if (event.getAmount() <= 0.0F) {
            return;
        }

        int level = EnchantmentHelper.getItemEnchantmentLevel(
                ModEnchantments.FLY_SWORD.get(), player.getMainHandItem());
        if (level <= 0) {
            return;
        }

        double reduction = Math.min(
                FlySwordConfig.SWORD_WARD_REDUCTION_BASE.get()
                        + (level - 1) * FlySwordConfig.SWORD_WARD_REDUCTION_PER_LEVEL.get(),
                FlySwordConfig.SWORD_WARD_MAX_REDUCTION.get());
        event.setAmount(event.getAmount() * (float) (1.0D - reduction));

        if (player.level() instanceof ServerLevel serverLevel) {
            if (FlySwordConfig.SWORD_WARD_PLAY_SOUND.get()) {
                // 受击瞬间播挡格声，境界越高音调越低沉厚重
                serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS,
                        0.7F, 1.2F - (level - 1) * 0.1F);
            }
            spawnWardParticles(serverLevel, player, level);
        }
    }

    /**
     * 化神境（5 级）护体螺旋混合使用的金色仙尘，与飞行拖尾的化神境用料一致
     */
    private static final ParticleOptions WARD_GOLD_DUST =
            new DustParticleOptions(new Vector3f(1.0F, 0.85F, 0.35F), 0.9F);

    /**
     * 各境界的护体粒子风格，与飞行拖尾的境界递进保持同一套视觉语言：
     * 1 级炼气灵气符文、2 级筑基剑罡电芒、3 级金丹真元灵光、4 级元婴幽蓝灵焰、5 级化神金霞仙光
     */
    private static ParticleOptions wardParticleFor(int level, int index) {
        return switch (level) {
            case 1 -> ParticleTypes.ENCHANT;
            case 2 -> ParticleTypes.ELECTRIC_SPARK;
            case 3 -> ParticleTypes.END_ROD;
            case 4 -> ParticleTypes.SOUL_FIRE_FLAME;
            default -> index % 2 == 0 ? ParticleTypes.TOTEM_OF_UNDYING : WARD_GOLD_DUST;
        };
    }

    /**
     * 剑罡护体触发时在玩家身上拉出一条缠绕全身的灵气螺旋。
     * 螺旋自脚踝绕至肩颈，把整个人罩在里面；粒子数、圈数与半径均随境界递增
     */
    private static void spawnWardParticles(ServerLevel level, Player player, int enchantLevel) {
        int wardLevel = Mth.clamp(enchantLevel, 1, 5);
        int count = 24 + (wardLevel - 1) * 6;
        double turns = 2.0D + (wardLevel - 1) * 0.5D;
        double radius = 0.55D + (wardLevel - 1) * 0.03D;
        double baseY = player.getY() + 0.15D;
        double span = Math.max(player.getBbHeight() - 0.3D, 0.2D);

        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            double angle = t * Math.PI * 2.0D * turns;
            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);
            level.sendParticles(wardParticleFor(wardLevel, i),
                    player.getX() + dirX * radius,
                    baseY + span * t,
                    player.getZ() + dirZ * radius,
                    1, dirX * 0.02D, 0.01D, dirZ * 0.02D, 0.0D);
        }
    }
}
