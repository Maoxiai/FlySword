package com.flysword.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 模组平衡参数。生成于 config/flysword-common.toml。
 * <p>
 * 注意：飞剑的移动由骑乘者客户端模拟（服务端通过 ServerboundMoveVehiclePacket 跟随），
 * 因此 fly_sword 分节的参数由各客户端本地配置生效；sword_beam 分节在服务端计算伤害与冷却，
 * 以服务端配置为准。
 */
public final class FlySwordConfig {
    public static final ForgeConfigSpec SPEC;

    // ---- 御剑飞行 ----
    public static final ForgeConfigSpec.DoubleValue FLY_VERTICAL_ACCELERATION;
    public static final ForgeConfigSpec.DoubleValue FLY_SPEED_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue FLY_HORIZONTAL_DRAG;
    public static final ForgeConfigSpec.DoubleValue FLY_VERTICAL_DRAG;

    // ---- 剑气 ----
    public static final ForgeConfigSpec.IntValue BEAM_COOLDOWN_BASE;
    public static final ForgeConfigSpec.IntValue BEAM_COOLDOWN_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BEAM_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BEAM_VELOCITY_BASE;
    public static final ForgeConfigSpec.DoubleValue BEAM_VELOCITY_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue BEAM_LIFESPAN_BASE;
    public static final ForgeConfigSpec.IntValue BEAM_LIFESPAN_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue BEAM_PIERCE_DAMAGE_DECAY;
    public static final ForgeConfigSpec.IntValue BEAM_KNOCKBACK_STRENGTH;
    public static final ForgeConfigSpec.DoubleValue BEAM_DURABILITY_COST_CHANCE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("御剑飞行").push("fly_sword");

        FLY_VERTICAL_ACCELERATION = builder
                .comment("按住上升/下降键时每刻施加的垂直加速度（格/刻）")
                .defineInRange("verticalAcceleration", 0.03D, 0.0D, 1.0D);

        FLY_SPEED_MULTIPLIER = builder
                .comment("水平加速度 = 玩家移动速度 × 此值 × 0.1")
                .defineInRange("speedMultiplier", 2.0D, 0.0D, 100.0D);

        FLY_HORIZONTAL_DRAG = builder
                .comment("水平阻尼，每刻速度乘以此值，越小减速越快")
                .defineInRange("horizontalDrag", 0.91D, 0.0D, 1.0D);

        FLY_VERTICAL_DRAG = builder
                .comment("垂直阻尼，每刻速度乘以此值")
                .defineInRange("verticalDrag", 0.98D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("剑气").push("sword_beam");

        BEAM_COOLDOWN_BASE = builder
                .comment("冷却时间基数（刻）")
                .defineInRange("cooldownBase", 80, 0, 72000);

        BEAM_COOLDOWN_PER_LEVEL = builder
                .comment("每级附魔减少的冷却时间（刻）")
                .defineInRange("cooldownReductionPerLevel", 13, 0, 72000);

        BEAM_DAMAGE_PER_LEVEL = builder
                .comment("伤害 = (玩家攻击力 + 武器附魔伤害加成) × 等级 × 此值")
                .defineInRange("damagePerLevel", 0.25D, 0.0D, 1000.0D);

        BEAM_VELOCITY_BASE = builder
                .comment("剑气基础飞行速度")
                .defineInRange("velocityBase", 1.0D, 0.0D, 100.0D);

        BEAM_VELOCITY_PER_LEVEL = builder
                .comment("每级附魔增加的飞行速度")
                .defineInRange("velocityPerLevel", 0.15D, 0.0D, 100.0D);

        BEAM_LIFESPAN_BASE = builder
                .comment("存活时间基数（刻）")
                .defineInRange("lifespanBase", 12, 1, 72000);

        BEAM_LIFESPAN_PER_LEVEL = builder
                .comment("每级附魔增加的存活时间（刻）")
                .defineInRange("lifespanPerLevel", 1, 0, 72000);

        BEAM_PIERCE_DAMAGE_DECAY = builder
                .comment("命中一个目标后，后续伤害的衰减系数")
                .defineInRange("pierceDamageDecay", 0.8D, 0.0D, 1.0D);

        BEAM_KNOCKBACK_STRENGTH = builder
                .comment("击退强度，0 为不击退")
                .defineInRange("knockbackStrength", 1, 0, 1000);

        BEAM_DURABILITY_COST_CHANCE = builder
                .comment("每次发射消耗 1 点耐久的概率")
                .defineInRange("durabilityCostChance", 0.5D, 0.0D, 1.0D);

        builder.pop();

        SPEC = builder.build();
    }

    private FlySwordConfig() {
    }
}
