package com.flysword.network.server;

import com.flysword.config.FlySwordConfig;
import com.flysword.enchantment.ModEnchantments;
import com.flysword.entity.EntityFlyingSword;
import com.flysword.entity.EntitySwordBeam;
import com.flysword.loader.EntityLoader;
import com.flysword.utils.PlayerUtils;
import com.flysword.utils.SwordBeamLauncher;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * 专用招式键触发的剑招。招式由御剑飞行（境界）的等级解锁，按手持剑上的境界决定释放哪一招，
 * 因此不需要为每个招式单独新增附魔。
 */
public class SwordSkillPacket {

    /**
     * 剑气纵横：一次放出多道扇形剑气，道数等于境界等级
     */
    public static final int MODE_SPREAD = 0;

    /**
     * 御剑术：剑离手飞出追杀敌人，命中达到上限或超时后飞回并归还背包
     */
    public static final int MODE_FLYING_SWORD = 1;

    /**
     * 万剑归宗：身后升起多柄剑，蓄势后齐射准星所指处
     */
    public static final int MODE_MYRIAD_SWORDS = 2;

    /**
     * 剑雨：以准星落点为中心，分批天降落剑
     */
    public static final int MODE_SWORD_RAIN = 3;

    /**
     * 招式冷却记账用的 NBT 键前缀，后面拼招式 mode
     */
    private static final String COOLDOWN_KEY_PREFIX = "FlySwordSkillCooldown";

    /**
     * 合法冷却长度的上界（刻）。冷却结束时刻存在玩家存档里，跨会话读取时服务端计时器已经归零，
     * 残留值会远大于当前刻；超过这个上界一律当作已过期，避免招式被永久锁死
     */
    private static final int MAX_COOLDOWN_TICKS = 72000;

    private final int mode;

    public SwordSkillPacket() {
        this(MODE_SPREAD);
    }

    public SwordSkillPacket(int mode) {
        this.mode = mode;
    }

    public SwordSkillPacket(FriendlyByteBuf buf) {
        this.mode = buf.readByte();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeByte(this.mode);
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null || !FlySwordConfig.SWORD_SKILL_ENABLED.get()) {
                return;
            }

            ItemStack stack = player.getMainHandItem();
            int level = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FLY_SWORD.get(), stack);
            if (level <= 0) {
                return;
            }

            int remaining = cooldownRemaining(player, this.mode);
            if (remaining > 0) {
                // 冷却改为按招式记账后不再有原版物品冷却的灰条，因此用动作栏补上反馈
                sendCooldownHint(player, this.mode, remaining);
                return;
            }

            if (this.mode == MODE_FLYING_SWORD) {
                castFlyingSword(player, stack, level);
            } else if (this.mode == MODE_MYRIAD_SWORDS) {
                castMyriadSwords(player, stack, level);
            } else if (this.mode == MODE_SWORD_RAIN) {
                castSwordRain(player, stack, level);
            } else {
                castSpread(player, stack, level);
            }
        });
        ctx.setPacketHandled(true);
    }

    /**
     * 剑气纵横。道数等于境界等级，相邻两道之间偏转固定角度形成扇形。
     * 多道时单道伤害按比例打折，因此总输出随境界提升，但不是道数的线性叠加。
     */
    private static void castSpread(ServerPlayer player, ItemStack stack, int level) {
        int count = Mth.clamp(level, 1, 5);
        float fullDamage = SwordBeamLauncher.baseDamage(player, stack)
                * (float) (level * FlySwordConfig.SKILL_DAMAGE_PER_LEVEL.get());
        // 只有一道时不打折，此时它就是一道普通的剑气
        float ratio = count > 1 ? FlySwordConfig.SKILL_SPREAD_DAMAGE_RATIO.get().floatValue() : 1.0F;
        float damage = fullDamage * ratio;
        double angleStep = FlySwordConfig.SKILL_SPREAD_ANGLE_STEP.get();

        if (player.getRandom().nextDouble() < FlySwordConfig.BEAM_DURABILITY_COST_CHANCE.get()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.4F, 0.5F);

        for (int i = 0; i < count; i++) {
            SwordBeamLauncher.launch(player, level, damage,
                    (float) ((i - (count - 1) / 2.0D) * angleStep));
        }

        applyCooldown(player, MODE_SPREAD,
                FlySwordConfig.SKILL_COOLDOWN_BASE.get() - level * FlySwordConfig.SKILL_COOLDOWN_PER_LEVEL.get());
    }

    /**
     * 御剑术。剑从手中移出交给飞剑实体，由它自行追敌与返航，返航到位后归还到背包。
     * 冷却记在玩家与招式上，因此剑离手期间冷却照常倒计时，与物品在不在手上无关
     */
    private static void castFlyingSword(ServerPlayer player, ItemStack stack, int level) {
        if (level < FlySwordConfig.FLYING_SWORD_UNLOCK_LEVEL.get()) {
            sendUnlockHint(player, MODE_FLYING_SWORD, FlySwordConfig.FLYING_SWORD_UNLOCK_LEVEL.get(), level);
            return;
        }

        if (player.getRandom().nextDouble() < FlySwordConfig.BEAM_DURABILITY_COST_CHANCE.get()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.4F, 0.5F);

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        EntityFlyingSword flyingSword = new EntityFlyingSword(EntityLoader.FLYING_SWORD.get(), player.level());
        flyingSword.setItemStack(stack);
        flyingSword.setOwnerId(player.getUUID());
        flyingSword.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        player.level().addFreshEntity(flyingSword);

        applyCooldown(player, MODE_FLYING_SWORD,
                FlySwordConfig.SKILL_COOLDOWN_BASE.get() - level * FlySwordConfig.SKILL_COOLDOWN_PER_LEVEL.get());
    }

    /**
     * 万剑归宗。剑数为「基础值 + 每级成长」并受实体数上限截断，飞剑沿用御剑术的实体与渲染，
     * 只是改用编队模式：身后环形升起、蓄势、齐射。玩家手中的剑不被取走，实体上只放一份渲染副本。
     */
    private static void castMyriadSwords(ServerPlayer player, ItemStack stack, int level) {
        if (level < FlySwordConfig.MYRIAD_SWORDS_UNLOCK_LEVEL.get()) {
            sendUnlockHint(player, MODE_MYRIAD_SWORDS, FlySwordConfig.MYRIAD_SWORDS_UNLOCK_LEVEL.get(), level);
            return;
        }

        int count = Mth.clamp(
                FlySwordConfig.MYRIAD_SWORDS_COUNT_BASE.get()
                        + (level - 1) * FlySwordConfig.MYRIAD_SWORDS_COUNT_PER_LEVEL.get(),
                1, FlySwordConfig.MYRIAD_SWORDS_MAX_ENTITIES.get());

        if (player.getRandom().nextDouble() < FlySwordConfig.BEAM_DURABILITY_COST_CHANCE.get()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.4F, 0.5F);

        Vec3 target = skillTargetPoint(player);
        Vec3 center = player.position()
                .add(behindDirection(player).scale(FlySwordConfig.MYRIAD_SWORDS_RISE_DISTANCE.get()))
                .add(0.0D, FlySwordConfig.MYRIAD_SWORDS_RISE_HEIGHT.get(), 0.0D);
        // 整束剑共用一个方向，因此方向只在这里求一次
        Vec3 direction = salvoDirection(player, center, target);
        double radius = FlySwordConfig.MYRIAD_SWORDS_RISE_RADIUS.get();
        ItemStack renderStack = new ItemStack(stack.getItem());
        float damage = SwordBeamLauncher.baseDamage(player, stack)
                * (float) (level * FlySwordConfig.MYRIAD_SWORDS_DAMAGE_PER_LEVEL.get());

        for (int i = 0; i < count; i++) {
            Vec3 hold = center.add(new Vec3(radius, 0.0D, 0.0D).yRot((float) (Math.PI * 2.0D * i / count)));

            EntityFlyingSword sword = new EntityFlyingSword(EntityLoader.FLYING_SWORD.get(), player.level());
            sword.setItemStack(renderStack.copy());
            sword.setOwnerId(player.getUUID());
            // 从玩家脚边的编队圆心起飞，飞到各自编队点，这段位移就是看得见的「升起」
            sword.moveTo(center.x, player.getY(), center.z, player.getYRot(), player.getXRot());
            sword.setMyriadFormation(hold, direction, damage);
            player.level().addFreshEntity(sword);
        }

        applyCooldown(player, MODE_MYRIAD_SWORDS,
                FlySwordConfig.MYRIAD_SWORDS_COOLDOWN_BASE.get()
                        - level * FlySwordConfig.MYRIAD_SWORDS_COOLDOWN_PER_LEVEL.get());
    }

    /**
     * 剑雨。先由服务端求出准星落点并打一圈粒子提示，再按配置的波数把总剑数分批生成在落点上空，
     * 每柄剑带固定下坠初速垂直落下，命中判定与单发剑气共用同一套
     */
    private static void castSwordRain(ServerPlayer player, ItemStack stack, int level) {
        if (level < FlySwordConfig.SWORD_RAIN_UNLOCK_LEVEL.get()) {
            sendUnlockHint(player, MODE_SWORD_RAIN, FlySwordConfig.SWORD_RAIN_UNLOCK_LEVEL.get(), level);
            return;
        }

        int total = Mth.clamp(
                FlySwordConfig.SWORD_RAIN_COUNT_BASE.get()
                        + (level - 1) * FlySwordConfig.SWORD_RAIN_COUNT_PER_LEVEL.get(),
                1, FlySwordConfig.SWORD_RAIN_MAX_ENTITIES.get());
        int waves = Mth.clamp(FlySwordConfig.SWORD_RAIN_WAVES.get(), 1, total);

        if (player.getRandom().nextDouble() < FlySwordConfig.BEAM_DURABILITY_COST_CHANCE.get()) {
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        }
        PlayerUtils.playSoundAtEntity(player.level(), player, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.4F, 0.5F);

        Vec3 center = skillTargetPoint(player);
        float damage = SwordBeamLauncher.baseDamage(player, stack)
                * (float) (level * FlySwordConfig.SWORD_RAIN_DAMAGE_PER_LEVEL.get());
        spawnRainMarker(player, center);

        // 余数分给前几波，保证各波剑数之和恰好等于总剑数、不超过实体数上限
        MinecraftServer server = player.getServer();
        int perWave = total / waves;
        int remainder = total % waves;
        for (int wave = 0; wave < waves; wave++) {
            int count = perWave + (wave < remainder ? 1 : 0);
            if (count <= 0) {
                continue;
            }
            int delay = server == null ? 0 : wave * FlySwordConfig.SWORD_RAIN_WAVE_INTERVAL.get();
            if (delay == 0) {
                spawnRainWave(player, center, damage, level, count);
                continue;
            }
            // 后续波次排到服务端刻调度上，避免一次性把全部实体塞进同一刻
            server.tell(new TickTask(server.getTickCount() + delay, () -> {
                if (player.isAlive()) {
                    spawnRainWave(player, center, damage, level, count);
                }
            }));
        }

        applyCooldown(player, MODE_SWORD_RAIN,
                FlySwordConfig.SWORD_RAIN_COOLDOWN_BASE.get()
                        - level * FlySwordConfig.SWORD_RAIN_COOLDOWN_PER_LEVEL.get());
    }

    private static void spawnRainWave(ServerPlayer player, Vec3 center, float damage, int level, int count) {
        Level world = player.level();
        double radius = FlySwordConfig.SWORD_RAIN_RADIUS.get();
        double spawnHeight = FlySwordConfig.SWORD_RAIN_SPAWN_HEIGHT.get();
        double fallSpeed = FlySwordConfig.SWORD_RAIN_FALL_SPEED.get();
        // 范围内的敌对生物优先：纯随机散布在半径内几乎必然错过目标，剑雨就永远砍不中怪
        List<Entity> victims = rainVictims(player, center, radius);

        for (int i = 0; i < count; i++) {
            Vec3 landing = victims.isEmpty()
                    ? randomRainPoint(world, center, radius)
                    : victimLandingPoint(victims.get(i % victims.size()), spawnHeight, fallSpeed);

            EntitySwordBeam beam = new EntitySwordBeam(EntityLoader.SWORD_BEAM.get(), player, world)
                    .setLevel(level)
                    .setDamage(damage);
            beam.moveTo(landing.x, landing.y + spawnHeight, landing.z, 0.0F, 90.0F);
            beam.setDeltaMovement(0.0D, -fallSpeed, 0.0D);
            world.addFreshEntity(beam);
        }
    }

    /**
     * 落点半径内的敌对生物。多只时按顺序轮转分配落剑，让每只都挨到
     */
    private static List<Entity> rainVictims(ServerPlayer player, Vec3 center, double radius) {
        return player.level().getEntities(player,
                new AABB(center, center).inflate(radius),
                candidate -> candidate instanceof Enemy && candidate.isAlive()
                        && candidate.distanceToSqr(center) <= radius * radius);
    }

    /**
     * 随机落点。半径取开方使落点在圆内均匀铺开，否则会过度堆在圆心
     */
    private static Vec3 randomRainPoint(Level world, Vec3 center, double radius) {
        double angle = world.random.nextDouble() * Math.PI * 2.0D;
        double distance = Math.sqrt(world.random.nextDouble()) * radius;
        return center.add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
    }

    /**
     * 目标落点。按落下所需刻数预判目标走位，落在它即将到达的位置；
     * 否则落剑飞行期间目标已经走开，即使锁定了也仍然砍不中
     */
    private static Vec3 victimLandingPoint(Entity victim, double spawnHeight, double fallSpeed) {
        double fallTicks = spawnHeight / Math.max(fallSpeed, 0.1D);
        return victim.position().add(victim.getDeltaMovement().scale(fallTicks));
    }

    /**
     * 在准星落点画一圈粒子，让玩家落剑前先看到覆盖范围
     */
    private static void spawnRainMarker(ServerPlayer player, Vec3 center) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double radius = FlySwordConfig.SWORD_RAIN_RADIUS.get();
        int points = Math.max(16, (int) (radius * 8.0D));
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.1D,
                    center.z + Math.sin(angle) * radius,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * 剩余冷却刻数。返回 0 表示可用；超出合法上界的残留记录（跨会话遗留）同样返回 0，避免招式被永久锁死
     */
    private static int cooldownRemaining(ServerPlayer player, int mode) {
        int remaining = cooldownEnd(player, mode) - serverTick(player);
        return remaining > 0 && remaining <= MAX_COOLDOWN_TICKS ? remaining : 0;
    }

    /**
     * 在动作栏提示该招式还要等多久。冷却已按招式独立计时，原版物品冷却的灰条不再覆盖这些招式
     */
    private static void sendCooldownHint(ServerPlayer player, int mode, int remaining) {
        String seconds = String.format(Locale.ROOT, "%.1f", remaining / 20.0D);
        player.displayClientMessage(Component.translatable("skill.flysword.cooldown",
                Component.translatable(skillNameKey(mode)), seconds), true);
    }

    /**
     * 境界不足时提示所需等级。否则按了键什么都不会发生，玩家只会以为招式坏了
     */
    private static void sendUnlockHint(ServerPlayer player, int mode, int requiredLevel, int currentLevel) {
        player.displayClientMessage(Component.translatable("skill.flysword.unlock",
                Component.translatable(skillNameKey(mode)), requiredLevel, currentLevel), true);
    }

    /**
     * 招式名对应的语言键，用于冷却与境界提示
     */
    private static String skillNameKey(int mode) {
        return switch (mode) {
            case MODE_FLYING_SWORD -> "skill.flysword.flyingsword";
            case MODE_MYRIAD_SWORDS -> "skill.flysword.myriad";
            case MODE_SWORD_RAIN -> "skill.flysword.rain";
            default -> "skill.flysword.spread";
        };
    }

    /**
     * 登记招式冷却。冷却按「玩家 + 招式」分别记账，存在玩家自己的存档数据里，而不是挂到物品上：
     * 物品冷却会把同一把剑上的所有招式一起锁住，一次大招就封掉了其余招式
     */
    private static void applyCooldown(ServerPlayer player, int mode, int ticks) {
        player.getPersistentData().putInt(COOLDOWN_KEY_PREFIX + mode, serverTick(player) + ticks);
    }

    private static int cooldownEnd(ServerPlayer player, int mode) {
        return player.getPersistentData().getInt(COOLDOWN_KEY_PREFIX + mode);
    }

    private static int serverTick(ServerPlayer player) {
        return player.serverLevel().getServer().getTickCount();
    }

    /**
     * 招式目标点：附近有敌对生物时自动瞄向最近的那一只，没有则按准星落点，
     * 因此没怪时仍可朝任意方向释放
     */
    private static Vec3 skillTargetPoint(ServerPlayer player) {
        return PlayerUtils.skillTargetPoint(player,
                FlySwordConfig.SKILL_AIM_RANGE.get(),
                FlySwordConfig.SKILL_AUTO_AIM_RADIUS.get());
    }

    /**
     * 齐射方向：整束剑共用这一个方向，由编队圆心指向目标点取一次即可。
     * 若逐柄剑各取一次「自身指向目标点」的方向，就会退化成各自朝目标点的散射，失去齐射感
     */
    private static Vec3 salvoDirection(ServerPlayer player, Vec3 center, Vec3 target) {
        Vec3 direction = target.subtract(center);
        // 目标点与编队圆心重合（例如贴着目标释放）时方向退化，退回玩家视线方向
        return direction.lengthSqr() < 1.0E-6D ? player.getLookAngle() : direction.normalize();
    }

    /**
     * 玩家的水平后方。视线接近垂直时水平分量会退化为零向量，改用朝向角求，避免归一化出 NaN
     */
    private static Vec3 behindDirection(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            return Vec3.directionFromRotation(0.0F, player.getYRot()).scale(-1.0D);
        }
        return horizontal.normalize().scale(-1.0D);
    }
}
