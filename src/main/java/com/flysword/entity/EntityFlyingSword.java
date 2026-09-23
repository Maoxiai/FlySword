package com.flysword.entity;

import com.flysword.config.FlySwordConfig;
import com.flysword.utils.PlayerUtils;
import com.flysword.utils.SwordBeamLauncher;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 御剑术召出的飞剑。与骑乘用的 EntitySword 共用同一套渲染与物品承载基座，
 * 但没有骑手：先沿玩家朝向正面出剑，再以大弧度转向最近的敌对生物，
 * 斩中后保持直线穿身而过而不是绕着目标打转，命中达到上限、发现无目标或离主人过远后飞回背包。
 */
public class EntityFlyingSword extends EntitySword {

    /**
     * 飞回主人到这个距离以内就归还
     */
    private static final double RECALL_DISTANCE_SQR = 4.0D;

    /**
     * 进入这个距离后放弃限速转向，直接咬住目标，避免因转弯半径大于目标距离而绕着目标打转
     */
    private static final double TERMINAL_RANGE = 4.0D;

    @Nullable
    private Entity target;
    @Nullable
    private Vec3 launchDirection;
    private int launchTicks;
    private boolean launchDone;
    private int searchCooldown;
    private int hitCooldown;
    private int hits;
    private int life;
    private boolean returning;
    private int returningTicks;

    /**
     * 万剑归宗的编队模式。开启后不再搜索目标与返航，改为在 holdPosition 编队悬停再齐射
     */
    private boolean myriad;
    @Nullable
    private Vec3 holdPosition;
    @Nullable
    private Vec3 salvoDirection;
    private float myriadDamage;
    private int riseTicks;

    public EntityFlyingSword(EntityType<? extends EntityFlyingSword> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        // 不做方块碰撞，否则剑撞上地形后会被卡在原地、原地打转再也回不来
        this.noPhysics = true;
    }

    /**
     * 切换为万剑归宗的编队模式：先飞到 holdPosition 编队悬停，蓄势结束后沿 salvoDirection 齐射。
     * 与御剑术共用同一套实体与渲染，因此不新增实体类型
     *
     * @param salvoDirection 齐射方向，全部剑共用同一个方向，形成平行射出的一束剑而不是各自散射
     * @param damage         命中伤害，由服务端在释放瞬间按招式公式算好，避免每刻重新求主人
     */
    public void setMyriadFormation(Vec3 holdPosition, Vec3 salvoDirection, float damage) {
        this.myriad = true;
        this.holdPosition = holdPosition;
        // 方向退化时留空，齐射阶段退回原地不动，避免归一化出 NaN 让剑飞向无穷远
        this.salvoDirection = salvoDirection.lengthSqr() < 1.0E-6D ? null : salvoDirection.normalize();
        this.myriadDamage = damage;
        this.riseTicks = FlySwordConfig.MYRIAD_SWORDS_RISE_TICKS.get();
    }

    /**
     * 无人骑乘，位移由本类在 tick 中自行驱动，因此不套用载具的骑乘者控制逻辑
     */
    @Override
    public void travel(Vec3 travelVector) {
    }

    @Override
    public void tick() {
        // 位置是在 super.tick() 内部才更新的，因此拖尾必须在它之后再采样，
        // 否则 getX() 与 xOld 恒等、位移为 0，会被"悬停不产生拖尾"的判断整条挡掉
        double prevX = this.getX();
        double prevY = this.getY();
        double prevZ = this.getZ();

        if (!this.level().isClientSide) {
            this.tickFlyingSword();
        }
        super.tick();

        if (this.level().isClientSide) {
            this.spawnTrail(prevX, prevY, prevZ);
        }
    }

    /**
     * 飞行拖尾。与骑乘飞剑共用同一套按境界分级的粒子与密度配置，悬停时不产生拖尾
     */
    @OnlyIn(Dist.CLIENT)
    private void spawnTrail(double prevX, double prevY, double prevZ) {
        if (!FlySwordConfig.FLY_TRAIL_ENABLED.get()) {
            return;
        }
        double dx = this.getX() - prevX;
        double dy = this.getY() - prevY;
        double dz = this.getZ() - prevZ;
        if (dx * dx + dy * dy + dz * dz < 1.0E-4D) {
            return;
        }

        int level = Mth.clamp(this.getEnchantLevel(), 1, 5);
        int count = FlySwordConfig.FLY_TRAIL_PARTICLES_BASE.get()
                + (level - 1) * FlySwordConfig.FLY_TRAIL_PARTICLES_PER_LEVEL.get();
        if (count <= 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double t = this.random.nextDouble();
            this.level().addParticle(EntitySword.particleForLevel(level, i),
                    this.xOld + dx * t,
                    this.yOld + dy * t + RENDER_OFFSET_Y,
                    this.zOld + dz * t,
                    -dx * 0.1D, -dy * 0.1D, -dz * 0.1D);
        }
    }

    /**
     * 飞行状态必须存档：区块卸载重载后若状态归零，剑会重新进入出剑阶段并再次乱飞
     */
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("FlyingHits", this.hits);
        compound.putInt("FlyingLife", this.life);
        compound.putBoolean("FlyingReturning", this.returning);
        compound.putBoolean("FlyingLaunchDone", this.launchDone);
        compound.putInt("FlyingReturningTicks", this.returningTicks);
        // 编队模式必须存档：重载后若退回普通御剑术，剑会把携带的剑物品归还给主人
        compound.putBoolean("FlyingMyriad", this.myriad);
        compound.putFloat("FlyingMyriadDamage", this.myriadDamage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.hits = compound.getInt("FlyingHits");
        this.life = compound.getInt("FlyingLife");
        this.returning = compound.getBoolean("FlyingReturning");
        this.launchDone = compound.getBoolean("FlyingLaunchDone");
        this.returningTicks = compound.getInt("FlyingReturningTicks");
        // 只恢复编队标记与伤害，不恢复蓄势进度：重载后直接进入直线穿行阶段，不会凭空重新聚拢
        this.myriad = compound.getBoolean("FlyingMyriad");
        this.myriadDamage = compound.getFloat("FlyingMyriadDamage");
    }

    private void tickFlyingSword() {
        Player owner = this.getOwner();
        this.life++;

        // 万剑归宗走独立流程：不搜索目标、不返航，因此必须在牵引与返航判定之前分流
        if (this.myriad) {
            this.tickMyriadSalvo(owner);
            return;
        }

        // 牵引半径只在没有交战目标时才生效。否则穿身而过冲出若干格就会误判为过远，
        // 表现为刚砍中一刀就被强制返航。注意 owner 可能为空（主人不在当前维度），必须先判空
        boolean tooFar = owner != null
                && !this.hasLiveTarget()
                && this.position().distanceToSqr(owner.position()) > maxRangeSqr();

        if (owner == null || owner.isDeadOrDying()
                || this.life > FlySwordConfig.FLYING_SWORD_LIFETIME.get()
                || this.hits >= FlySwordConfig.FLYING_SWORD_MAX_HITS.get()
                || tooFar) {
            this.returning = true;
        }

        if (this.returning) {
            this.tickReturning(owner);
            return;
        }

        if (this.hitCooldown > 0) {
            // 刚斩中目标，保持直线冲出去，形成穿身而过的斩击感而不是围着目标打转
            this.hitCooldown--;
            this.move(MoverType.SELF, this.getDeltaMovement());
            return;
        }

        if (!this.launchDone) {
            this.tickLaunch();
            return;
        }

        if (--this.searchCooldown <= 0) {
            this.searchCooldown = FlySwordConfig.FLYING_SWORD_SEARCH_INTERVAL.get();
            this.target = this.findTarget(owner);
        }
        if (!this.hasLiveTarget()) {
            // 附近没有目标就直接返航，不在原地滑行，否则剑会越飘越远
            this.returning = true;
            this.tickReturning(owner);
            return;
        }

        this.steerTowards(this.target.getEyePosition());
        this.tryHit(owner);
    }

    /**
     * 出剑阶段：沿玩家朝向正面飞出固定刻数，之后才开始寻敌转向
     */
    private void tickLaunch() {
        if (this.launchDirection == null) {
            this.launchDirection = Vec3.directionFromRotation(this.getXRot(), this.getYRot());
        }
        this.setDeltaMovement(this.launchDirection.scale(FlySwordConfig.FLYING_SWORD_SPEED.get()));
        this.faceAlong(this.launchDirection);
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (++this.launchTicks >= FlySwordConfig.FLYING_SWORD_LAUNCH_TICKS.get()) {
            this.launchDone = true;
        }
    }

    private void tickReturning(@Nullable Player owner) {
        if (owner != null
                && this.position().distanceToSqr(owner.getEyePosition()) < RECALL_DISTANCE_SQR) {
            this.putAwaySword(owner);
            return;
        }

        // 兜底：绕圈、被甩在高空，或主人不在当前维度时都返航不成，超时后直接收手：
        // 主人在就归还背包，不在就把剑落地
        if (++this.returningTicks > FlySwordConfig.FLYING_SWORD_RETURN_TIMEOUT.get()) {
            if (owner != null) {
                this.putAwaySword(owner);
            } else {
                this.spawnAtLocation(this.getItemStack());
                this.setItemStack(ItemStack.EMPTY);
                this.discard();
            }
            return;
        }

        if (owner != null) {
            // 返航用固定转向速率，不用按距离缩放的弧线律，否则掉头半径过大会撞上超时
            this.steerTowards(owner.getEyePosition(), false);
        }
        // 主人暂时查不到时（例如区块比玩家先加载）原地悬停，等宽限期结束再处理，避免加载瞬间丢剑
    }

    /**
     * 万剑归宗：升起编队、悬停蓄势，结束后朝准星目标点齐射，随后直线穿行直至超时消散。
     * 全程不搜索目标也不返航，因此不会在齐射后掉头
     */
    private void tickMyriadSalvo(@Nullable Player owner) {
        if (this.riseTicks > 0) {
            this.tickRise();
            return;
        }

        if (this.life > FlySwordConfig.MYRIAD_SWORDS_LIFETIME.get()) {
            this.discard();
            return;
        }

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.hitCooldown > 0) {
            this.hitCooldown--;
            return;
        }
        if (this.hits >= FlySwordConfig.MYRIAD_SWORDS_MAX_HITS.get()) {
            this.discard();
            return;
        }
        this.tryHit(this.myriadDamage, owner);
    }

    /**
     * 升起到位并悬停。剑先飞向各自的编队点，到位后原地不动等待齐射
     */
    private void tickRise() {
        --this.riseTicks;

        Vec3 toHold = this.holdPosition == null ? Vec3.ZERO : this.holdPosition.subtract(this.position());
        double distance = toHold.length();
        if (distance > 0.05D) {
            double speed = FlySwordConfig.MYRIAD_SWORDS_SPEED.get();
            Vec3 direction = toHold.scale(1.0D / distance);
            // 距离不足一整步时按剩余距离走，避免在编队点附近来回过冲
            this.setDeltaMovement(direction.scale(Math.min(speed, distance)));
            this.faceAlong(direction);
        } else {
            // 到位后悬停：速度为 0 也就不再产生拖尾
            this.setDeltaMovement(Vec3.ZERO);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());

        if (this.riseTicks == 0) {
            this.fireSalvo();
        }
    }

    /**
     * 齐射：整束剑沿同一个方向冲出去，彼此保持编队间距平行飞行。
     * 方向在释放瞬间就已定好、之后不再修正，因此不会出现各自拐弯追目标点的散射感
     */
    private void fireSalvo() {
        if (this.salvoDirection == null) {
            return;
        }
        this.setDeltaMovement(this.salvoDirection.scale(FlySwordConfig.MYRIAD_SWORDS_SPEED.get()));
        this.faceAlong(this.salvoDirection);
        this.salvoDirection = null;
    }

    private boolean hasLiveTarget() {
        return this.target != null && this.target.isAlive();
    }

    private static double maxRangeSqr() {
        double range = FlySwordConfig.FLYING_SWORD_MAX_RANGE.get();
        return range * range;
    }

    /**
     * 带转角上限的转向。允许转角随距离收紧，于是先划大弧再逐渐切入；
     * 同时有下限约束，避免远距离时转弯半径过大导致绕大圈甚至一路爬升。
     * 进入末段距离后取消限制，保证能真正咬住目标。
     */
    private void steerTowards(Vec3 destination) {
        this.steerTowards(destination, true);
    }

    /**
     * @param arcByDistance 是否让转向半径随距离缩放。追击时开启，得到先划大弧再切入的效果；
     *                      返航时关闭，改用固定转向速率，保证一定能掉头回家
     */
    private void steerTowards(Vec3 destination, boolean arcByDistance) {
        Vec3 toDestination = destination.subtract(this.position());
        double distance = toDestination.length();
        if (distance < 1.0E-4D) {
            return;
        }

        double speed = FlySwordConfig.FLYING_SWORD_SPEED.get();
        Vec3 desired = toDestination.scale(1.0D / distance);
        Vec3 current = this.getDeltaMovement();
        Vec3 direction;

        if (current.lengthSqr() < 1.0E-6D || distance < TERMINAL_RANGE) {
            direction = desired;
        } else {
            Vec3 currentDirection = current.normalize();
            double angle = Math.acos(Mth.clamp(currentDirection.dot(desired), -1.0D, 1.0D));
            double maxTurn;
            if (arcByDistance) {
                maxTurn = Math.min(
                        Math.toRadians(FlySwordConfig.FLYING_SWORD_TURN_RATE.get()),
                        Math.max(speed / Math.max(distance * 0.35D, 0.1D),
                                Math.toRadians(FlySwordConfig.FLYING_SWORD_MIN_TURN_RATE.get())));
            } else {
                maxTurn = Math.toRadians(FlySwordConfig.FLYING_SWORD_TURN_RATE.get());
            }
            direction = angle <= maxTurn
                    ? desired
                    : rotateTowards(currentDirection, desired, maxTurn);
        }

        this.setDeltaMovement(direction.scale(speed));
        this.faceAlong(direction);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    /**
     * 在 current 与 towards 张成的平面内把 current 精确旋转 turn 弧度，转向朝 towards。
     * 不能改用「两个单位向量线性插值再归一化」：夹角接近 180° 时加权和的长度趋近 0，
     * 归一化后方向几乎不变，而掉头恰好就是 180° 场景，会导致剑永远转不回来。
     */
    private static Vec3 rotateTowards(Vec3 current, Vec3 towards, double turn) {
        Vec3 perpendicular = towards.subtract(current.scale(current.dot(towards)));
        if (perpendicular.lengthSqr() < 1.0E-6D) {
            // 恰好完全反向时平面退化，取任意与 current 垂直的轴兜底
            perpendicular = Math.abs(current.y) < 0.9D
                    ? current.cross(new Vec3(0.0D, 1.0D, 0.0D))
                    : current.cross(new Vec3(1.0D, 0.0D, 0.0D));
        }
        perpendicular = perpendicular.normalize();
        return current.scale(Math.cos(turn)).add(perpendicular.scale(Math.sin(turn)));
    }

    private void faceAlong(Vec3 direction) {
        this.setYRot((float) (Mth.atan2(direction.z, direction.x) * (180.0D / Math.PI)) - 90.0F);
    }

    @Nullable
    private Entity findTarget(Player owner) {
        double maxRangeSqr = maxRangeSqr();
        List<Entity> candidates = this.level().getEntities(this,
                this.getBoundingBox().inflate(FlySwordConfig.FLYING_SWORD_SEARCH_RADIUS.get()),
                candidate -> candidate instanceof Enemy && candidate.isAlive()
                        && candidate.distanceToSqr(owner) <= maxRangeSqr);

        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            double distance = candidate.distanceToSqr(this);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }

    private void tryHit(Player owner) {
        this.tryHit(SwordBeamLauncher.baseDamage(owner, this.getItemStack())
                * (float) (this.getEnchantLevel() * FlySwordConfig.FLYING_SWORD_DAMAGE_PER_LEVEL.get()), owner);
    }

    /**
     * @param damage 命中伤害，由调用方按所属招式的公式算好传入
     * @param owner  伤害归属，主人不在当前维度时可能为空
     */
    private void tryHit(float damage, @Nullable Player owner) {
        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(0.6D))) {
            if (!(entity instanceof Enemy) || !entity.isAlive()) {
                continue;
            }
            entity.hurt(this.damageSources().thrown(this, owner), damage);
            this.spawnSlashEffect(entity);
            this.hits++;
            this.hitCooldown = this.myriad
                    ? FlySwordConfig.MYRIAD_SWORDS_HIT_COOLDOWN.get()
                    : FlySwordConfig.FLYING_SWORD_HIT_COOLDOWN.get();
            break;
        }
    }

    /**
     * 命中瞬间在目标身上打出一道横向斩击痕，让「砍中」这件事看得见
     */
    private void spawnSlashEffect(Entity victim) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        double y = victim.getY() + victim.getBbHeight() * 0.55D;
        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK,
                victim.getX(), y, victim.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.CRIT,
                victim.getX(), y, victim.getZ(), 8, 0.3D, 0.3D, 0.3D, 0.2D);
        PlayerUtils.playSoundAtEntity(this.level(), victim, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 0.4F, 0.6F);
    }
}
