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
    public static final ForgeConfigSpec.DoubleValue FLY_SPEED_MULTIPLIER_BASE;
    public static final ForgeConfigSpec.DoubleValue FLY_SPEED_MULTIPLIER_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue FLY_VERTICAL_ACCELERATION_BASE;
    public static final ForgeConfigSpec.DoubleValue FLY_VERTICAL_ACCELERATION_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue FLY_HORIZONTAL_DRAG;
    public static final ForgeConfigSpec.DoubleValue FLY_VERTICAL_DRAG;

    // ---- 飞行拖尾 ----
    public static final ForgeConfigSpec.BooleanValue FLY_TRAIL_ENABLED;
    public static final ForgeConfigSpec.IntValue FLY_TRAIL_PARTICLES_BASE;
    public static final ForgeConfigSpec.IntValue FLY_TRAIL_PARTICLES_PER_LEVEL;

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

    // ---- 剑罡护体 ----
    public static final ForgeConfigSpec.BooleanValue SWORD_WARD_ENABLED;
    public static final ForgeConfigSpec.BooleanValue SWORD_WARD_PLAY_SOUND;
    public static final ForgeConfigSpec.DoubleValue SWORD_WARD_REDUCTION_BASE;
    public static final ForgeConfigSpec.DoubleValue SWORD_WARD_REDUCTION_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SWORD_WARD_MAX_REDUCTION;

    // ---- 剑意通明 ----
    public static final ForgeConfigSpec.DoubleValue SWORD_INTENT_DAMAGE_BASE;
    public static final ForgeConfigSpec.DoubleValue SWORD_INTENT_DAMAGE_PER_LEVEL;

    // ---- 剑招（专用招式键）----
    public static final ForgeConfigSpec.BooleanValue SWORD_SKILL_ENABLED;
    public static final ForgeConfigSpec.IntValue SKILL_COOLDOWN_BASE;
    public static final ForgeConfigSpec.IntValue SKILL_COOLDOWN_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SKILL_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SKILL_SPREAD_DAMAGE_RATIO;
    public static final ForgeConfigSpec.DoubleValue SKILL_SPREAD_ANGLE_STEP;
    public static final ForgeConfigSpec.DoubleValue SKILL_AIM_RANGE;
    public static final ForgeConfigSpec.DoubleValue SKILL_AUTO_AIM_RADIUS;

    // ---- 御剑术 ----
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_UNLOCK_LEVEL;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_SEARCH_RADIUS;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_SEARCH_INTERVAL;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_MAX_HITS;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_HIT_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_LIFETIME;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_SPEED;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_LAUNCH_TICKS;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_TURN_RATE;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_SPIN_SPEED;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_MAX_RANGE;
    public static final ForgeConfigSpec.DoubleValue FLYING_SWORD_MIN_TURN_RATE;
    public static final ForgeConfigSpec.IntValue FLYING_SWORD_RETURN_TIMEOUT;

    // ---- 万剑归宗 ----
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_UNLOCK_LEVEL;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_COUNT_BASE;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_COUNT_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_MAX_ENTITIES;
    public static final ForgeConfigSpec.DoubleValue MYRIAD_SWORDS_RISE_DISTANCE;
    public static final ForgeConfigSpec.DoubleValue MYRIAD_SWORDS_RISE_RADIUS;
    public static final ForgeConfigSpec.DoubleValue MYRIAD_SWORDS_RISE_HEIGHT;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_RISE_TICKS;
    public static final ForgeConfigSpec.DoubleValue MYRIAD_SWORDS_SPEED;
    public static final ForgeConfigSpec.DoubleValue MYRIAD_SWORDS_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_MAX_HITS;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_HIT_COOLDOWN;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_LIFETIME;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_COOLDOWN_BASE;
    public static final ForgeConfigSpec.IntValue MYRIAD_SWORDS_COOLDOWN_PER_LEVEL;

    // ---- 剑雨 ----
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_UNLOCK_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SWORD_RAIN_RADIUS;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_COUNT_BASE;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_COUNT_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_MAX_ENTITIES;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_WAVES;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_WAVE_INTERVAL;
    public static final ForgeConfigSpec.DoubleValue SWORD_RAIN_DAMAGE_PER_LEVEL;
    public static final ForgeConfigSpec.DoubleValue SWORD_RAIN_FALL_SPEED;
    public static final ForgeConfigSpec.DoubleValue SWORD_RAIN_SPAWN_HEIGHT;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_COOLDOWN_BASE;
    public static final ForgeConfigSpec.IntValue SWORD_RAIN_COOLDOWN_PER_LEVEL;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("御剑飞行。以下带 Base / PerLevel 的参数按",
                "实际值 = base + (附魔等级 - 1) × perLevel 计算，等级范围 1 ~ 5。").push("fly_sword");

        FLY_SPEED_MULTIPLIER_BASE = builder
                .comment("1 级时的水平速度倍率。水平加速度 = 玩家移动速度 × 此值 × 0.1")
                .defineInRange("speedMultiplierBase", 2.0D, 0.0D, 100.0D);

        FLY_SPEED_MULTIPLIER_PER_LEVEL = builder
                .comment("每提升一级附魔增加的水平速度倍率")
                .defineInRange("speedMultiplierPerLevel", 0.4D, 0.0D, 100.0D);

        FLY_VERTICAL_ACCELERATION_BASE = builder
                .comment("1 级时按住上升/下降键每刻施加的垂直加速度（格/刻）")
                .defineInRange("verticalAccelerationBase", 0.03D, 0.0D, 1.0D);

        FLY_VERTICAL_ACCELERATION_PER_LEVEL = builder
                .comment("每提升一级附魔增加的垂直加速度")
                .defineInRange("verticalAccelerationPerLevel", 0.01D, 0.0D, 1.0D);

        FLY_HORIZONTAL_DRAG = builder
                .comment("水平阻尼，每刻速度乘以此值，越小减速越快")
                .defineInRange("horizontalDrag", 0.91D, 0.0D, 1.0D);

        FLY_VERTICAL_DRAG = builder
                .comment("垂直阻尼，每刻速度乘以此值")
                .defineInRange("verticalDrag", 0.98D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("飞行拖尾粒子").push("fly_trail");

        FLY_TRAIL_ENABLED = builder
                .comment("是否启用飞行拖尾粒子")
                .define("enabled", true);

        FLY_TRAIL_PARTICLES_BASE = builder
                .comment("1 级时每刻生成的拖尾粒子数量")
                .defineInRange("particlesBase", 1, 0, 100);

        FLY_TRAIL_PARTICLES_PER_LEVEL = builder
                .comment("每提升一级附魔增加的拖尾粒子数量")
                .defineInRange("particlesPerLevel", 1, 0, 100);

        builder.pop();

        builder.comment("剑气。以下带 Base / PerLevel 的参数按",
                "实际值 = base + (附魔等级 - 1) × perLevel 计算，等级范围 1 ~ 5。").push("sword_beam");

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

        builder.comment("剑罡护体。手持带有御剑飞行附魔的剑受击时按等级减免伤害，",
                "实际值 = base + (附魔等级 - 1) × perLevel，等级范围 1 ~ 5。").push("sword_ward");

        SWORD_WARD_ENABLED = builder
                .comment("是否启用剑罡护体")
                .define("enabled", true);

        SWORD_WARD_PLAY_SOUND = builder
                .comment("护体触发时是否播放挡格音效，关闭后只保留粒子效果")
                .define("playSound", true);

        SWORD_WARD_REDUCTION_BASE = builder
                .comment("1 级时的伤害减免比例，0.10 表示减免 10%")
                .defineInRange("damageReductionBase", 0.10D, 0.0D, 1.0D);

        SWORD_WARD_REDUCTION_PER_LEVEL = builder
                .comment("每提升一级附魔增加的减伤比例")
                .defineInRange("damageReductionPerLevel", 0.05D, 0.0D, 1.0D);

        SWORD_WARD_MAX_REDUCTION = builder
                .comment("减伤比例上限，防止配置叠加后完全免伤")
                .defineInRange("maxReduction", 0.80D, 0.0D, 1.0D);

        builder.pop();

        builder.comment("剑意通明。持剑时的额外攻击伤害，",
                "实际值 = base + (附魔等级 - 1) × perLevel，等级范围 1 ~ 5。",
                "该附魔不与锋利互斥，两者可以叠加。").push("sword_intent");

        SWORD_INTENT_DAMAGE_BASE = builder
                .comment("1 级时的额外攻击伤害，与原版锋利 I 一致")
                .defineInRange("damageBonusBase", 1.0D, 0.0D, 1000.0D);

        SWORD_INTENT_DAMAGE_PER_LEVEL = builder
                .comment("每提升一级附魔增加的额外攻击伤害")
                .defineInRange("damageBonusPerLevel", 0.5D, 0.0D, 1000.0D);

        builder.pop();

        builder.comment("招式键释放的剑招。释放哪一招由手持剑上御剑飞行的境界等级解锁：",
                "招式键一（默认 V）无修饰为剑气纵横、潜行为御剑术；",
                "招式键二（默认 B）无修饰为万剑归宗、潜行为剑雨。",
                "本分节的冷却为剑气纵横与御剑术共用，万剑归宗与剑雨各自独立；",
                "所有招式的冷却都按玩家加招式分别计时，彼此不会互相锁住。").push("sword_skill");

        SWORD_SKILL_ENABLED = builder
                .comment("是否启用专用招式键")
                .define("enabled", true);

        SKILL_COOLDOWN_BASE = builder
                .comment("剑气纵横与御剑术的冷却基数（刻）")
                .defineInRange("cooldownBase", 100, 0, 72000);

        SKILL_COOLDOWN_PER_LEVEL = builder
                .comment("每级境界减少的冷却时间（刻），实际冷却 = 基数 - 境界等级 × 此值")
                .defineInRange("cooldownReductionPerLevel", 15, 0, 72000);

        SKILL_DAMAGE_PER_LEVEL = builder
                .comment("单道剑气伤害 = (玩家攻击力 + 武器附魔伤害加成) × 境界等级 × 此值")
                .defineInRange("damagePerLevel", 0.25D, 0.0D, 1000.0D);

        SKILL_SPREAD_DAMAGE_RATIO = builder
                .comment("多道时单道伤害的打折比例，仅在道数大于 1 时生效；设为 1.0 即退化为总伤害线性叠加")
                .defineInRange("spreadDamageRatio", 0.6D, 0.0D, 10.0D);

        SKILL_SPREAD_ANGLE_STEP = builder
                .comment("相邻两道剑气之间的水平偏转角（度）")
                .defineInRange("spreadAngleStep", 8.0D, 0.0D, 90.0D);

        SKILL_AIM_RANGE = builder
                .comment("按准星求落点时的最大射线距离（格），万剑归宗与剑雨共用；",
                        "射线被方块挡住时取命中点，否则取射程末端")
                .defineInRange("aimRange", 24.0D, 1.0D, 128.0D);

        SKILL_AUTO_AIM_RADIUS = builder
                .comment("自动索敌半径（格），万剑归宗与剑雨共用：范围内有敌对生物时改为瞄准最近的那一只，",
                        "没有则不索敌、按准星落点释放；设为 0 关闭自动索敌")
                .defineInRange("autoAimRadius", 16.0D, 0.0D, 64.0D);

        builder.pop();

        builder.comment("御剑术。境界达到 unlockLevel 后用招式键加潜行释放：",
                "剑离手飞出追杀附近敌对生物，命中达到上限或超时后飞回主人并归还背包。").push("flying_sword");

        FLYING_SWORD_UNLOCK_LEVEL = builder
                .comment("解锁御剑术所需的最低境界等级")
                .defineInRange("unlockLevel", 3, 1, 5);

        FLYING_SWORD_SEARCH_RADIUS = builder
                .comment("搜索敌对生物的半径（格）")
                .defineInRange("searchRadius", 16.0D, 1.0D, 64.0D);

        FLYING_SWORD_SEARCH_INTERVAL = builder
                .comment("每隔多少刻重新搜索一次目标，调大可降低服务端遍历开销")
                .defineInRange("searchInterval", 5, 1, 200);

        FLYING_SWORD_MAX_HITS = builder
                .comment("命中多少次后转为飞回。设小了会出现砍几刀就走、怪还没死的情况")
                .defineInRange("maxHits", 5, 1, 100);

        FLYING_SWORD_HIT_COOLDOWN = builder
                .comment("两次命中之间的间隔（刻），避免同一目标被逐刻连续伤害")
                .defineInRange("hitCooldown", 10, 1, 200);

        FLYING_SWORD_LIFETIME = builder
                .comment("最长存活时间（刻），超过后强制飞回")
                .defineInRange("lifetime", 200, 20, 72000);

        FLYING_SWORD_SPEED = builder
                .comment("飞行速度（格/刻）")
                .defineInRange("speed", 0.9D, 0.1D, 10.0D);

        FLYING_SWORD_DAMAGE_PER_LEVEL = builder
                .comment("命中伤害 = (玩家攻击力 + 武器附魔伤害加成) × 境界等级 × 此值")
                .defineInRange("damagePerLevel", 0.4D, 0.0D, 1000.0D);

        FLYING_SWORD_LAUNCH_TICKS = builder
                .comment("出剑后沿玩家朝向正面直飞的刻数，之后才开始转向寻敌")
                .defineInRange("launchTicks", 6, 0, 200);

        FLYING_SWORD_TURN_RATE = builder
                .comment("转向速率上限（度/刻）。数值越小弧线越大，越大越贴直线")
                .defineInRange("turnRate", 20.0D, 1.0D, 180.0D);

        FLYING_SWORD_SPIN_SPEED = builder
                .comment("剑体绕自身剑刃长轴自旋的速度（度/刻），即电钻感，设为 0 则不自旋")
                .defineInRange("spinSpeed", 45.0D, 0.0D, 720.0D);

        FLYING_SWORD_MAX_RANGE = builder
                .comment("飞剑离主人的最大距离（格）。超出后立即强制返航，也不会去追这个范围外的目标。",
                        "调大可搜索更远，但剑离开加载范围就会卡住不动，因此不建议超过 48")
                .defineInRange("maxRange", 24.0D, 4.0D, 128.0D);

        FLYING_SWORD_MIN_TURN_RATE = builder
                .comment("转向速率下限（度/刻），决定弧线的最大半径 = 飞行速度 ÷ 此值。",
                        "调小会让弧线更舒展，但过大半径会让剑一边爬升一边绕大圈、半天回不来")
                .defineInRange("minTurnRate", 8.0D, 1.0D, 180.0D);

        FLYING_SWORD_RETURN_TIMEOUT = builder
                .comment("返航超过这么多刻仍未到家就强制召回，直接归还背包，",
                        "作为绕圈、被甩在高空等异常情况下的兜底")
                .defineInRange("returnTimeoutTicks", 80, 20, 72000);

        builder.pop();

        builder.comment("万剑归宗。境界达到 unlockLevel 后用第二个招式键释放：",
                "身后按环形升起多柄飞剑，蓄势后整束剑沿同一方向平行齐射（方向取自动索敌目标或准星落点）。",
                "飞剑沿用御剑术的实体与渲染，因此不新增实体类型与贴图。").push("myriad_swords");

        MYRIAD_SWORDS_UNLOCK_LEVEL = builder
                .comment("解锁万剑归宗所需的最低境界等级")
                .defineInRange("unlockLevel", 4, 1, 5);

        MYRIAD_SWORDS_COUNT_BASE = builder
                .comment("1 级时升起的剑数")
                .defineInRange("countBase", 3, 1, 64);

        MYRIAD_SWORDS_COUNT_PER_LEVEL = builder
                .comment("每提升一级境界增加的剑数")
                .defineInRange("countPerLevel", 1, 0, 64);

        MYRIAD_SWORDS_MAX_ENTITIES = builder
                .comment("单次释放的剑数硬上限。剑数量乘以境界成长后不得超过此值，",
                        "用于兑现「单次释放实体数不超过配置上限」")
                .defineInRange("maxEntities", 8, 1, 64);

        MYRIAD_SWORDS_RISE_DISTANCE = builder
                .comment("环形阵列中心相对玩家的后移距离（格）")
                .defineInRange("riseDistance", 1.5D, 0.0D, 16.0D);

        MYRIAD_SWORDS_RISE_RADIUS = builder
                .comment("环形阵列的半径（格）")
                .defineInRange("riseRadius", 1.8D, 0.0D, 16.0D);

        MYRIAD_SWORDS_RISE_HEIGHT = builder
                .comment("升起点相对玩家脚底的高度（格）")
                .defineInRange("riseHeight", 2.2D, 0.0D, 32.0D);

        MYRIAD_SWORDS_RISE_TICKS = builder
                .comment("升起到位后的蓄势刻数，期间悬停不动，结束后齐射")
                .defineInRange("riseTicks", 16, 0, 200);

        MYRIAD_SWORDS_SPEED = builder
                .comment("升起与齐射的飞行速度（格/刻）")
                .defineInRange("speed", 1.1D, 0.1D, 10.0D);

        MYRIAD_SWORDS_DAMAGE_PER_LEVEL = builder
                .comment("命中伤害 = (玩家攻击力 + 武器附魔伤害加成) × 境界等级 × 此值")
                .defineInRange("damagePerLevel", 0.35D, 0.0D, 1000.0D);

        MYRIAD_SWORDS_MAX_HITS = builder
                .comment("单柄剑的命中次数上限，达到后消散，避免同一把剑反复收割")
                .defineInRange("maxHits", 3, 1, 100);

        MYRIAD_SWORDS_HIT_COOLDOWN = builder
                .comment("两次命中之间的间隔（刻）")
                .defineInRange("hitCooldown", 10, 1, 200);

        MYRIAD_SWORDS_LIFETIME = builder
                .comment("齐射后最长存活时间（刻），超过后消散")
                .defineInRange("lifetime", 60, 1, 72000);

        MYRIAD_SWORDS_COOLDOWN_BASE = builder
                .comment("万剑归宗的冷却基数（刻），与剑气纵横、御剑术各自独立")
                .defineInRange("cooldownBase", 180, 0, 72000);

        MYRIAD_SWORDS_COOLDOWN_PER_LEVEL = builder
                .comment("每级境界减少的冷却时间（刻），实际冷却 = 基数 - 境界等级 × 此值")
                .defineInRange("cooldownReductionPerLevel", 20, 0, 72000);

        builder.pop();

        builder.comment("剑雨。境界达到 unlockLevel 后用第二个招式键加潜行释放：",
                "以准星落点为中心，在半径内随机取点，分波天降落剑。").push("sword_rain");

        SWORD_RAIN_UNLOCK_LEVEL = builder
                .comment("解锁剑雨所需的最低境界等级")
                .defineInRange("unlockLevel", 5, 1, 5);

        SWORD_RAIN_RADIUS = builder
                .comment("落点半径（格），即以准星落点为圆心的散布范围")
                .defineInRange("radius", 5.0D, 0.5D, 32.0D);

        SWORD_RAIN_COUNT_BASE = builder
                .comment("1 级时落下的剑数")
                .defineInRange("countBase", 6, 1, 64);

        SWORD_RAIN_COUNT_PER_LEVEL = builder
                .comment("每提升一级境界增加的剑数")
                .defineInRange("countPerLevel", 3, 0, 64);

        SWORD_RAIN_MAX_ENTITIES = builder
                .comment("单次释放的剑数硬上限，包含全部分波")
                .defineInRange("maxEntities", 20, 1, 64);

        SWORD_RAIN_WAVES = builder
                .comment("分几波落下，波数大于总剑数时按总剑数处理")
                .defineInRange("waves", 3, 1, 20);

        SWORD_RAIN_WAVE_INTERVAL = builder
                .comment("相邻两波之间的间隔（刻）")
                .defineInRange("waveInterval", 6, 1, 200);

        SWORD_RAIN_DAMAGE_PER_LEVEL = builder
                .comment("单道落剑伤害 = (玩家攻击力 + 武器附魔伤害加成) × 境界等级 × 此值")
                .defineInRange("damagePerLevel", 0.3D, 0.0D, 1000.0D);

        SWORD_RAIN_FALL_SPEED = builder
                .comment("落剑的下坠速度（格/刻）")
                .defineInRange("fallSpeed", 1.6D, 0.1D, 10.0D);

        SWORD_RAIN_SPAWN_HEIGHT = builder
                .comment("落剑的生成高度，以准星落点为基准向上偏移（格）")
                .defineInRange("spawnHeight", 8.0D, 1.0D, 64.0D);

        SWORD_RAIN_COOLDOWN_BASE = builder
                .comment("剑雨的冷却基数（刻），与剑气纵横、御剑术各自独立；",
                        "实体数最多、持续时间最长，因此基数比万剑归宗更高")
                .defineInRange("cooldownBase", 260, 0, 72000);

        SWORD_RAIN_COOLDOWN_PER_LEVEL = builder
                .comment("每级境界减少的冷却时间（刻），实际冷却 = 基数 - 境界等级 × 此值")
                .defineInRange("cooldownReductionPerLevel", 30, 0, 72000);

        builder.pop();

        SPEC = builder.build();
    }

    private FlySwordConfig() {
    }
}
