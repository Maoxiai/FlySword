package com.flysword.entity;

import com.flysword.config.FlySwordConfig;
import com.flysword.enchantment.ModEnchantments;
import com.flysword.key.ModKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class EntitySword extends LivingEntity {

    /**
     * 飞剑模型的渲染高度偏移，Renderer 与拖尾粒子共用，保证拖尾贴合剑身
     */
    public static final double RENDER_OFFSET_Y = 1.0D;

    /**
     * 化神境（5 级）拖尾混合使用的金色仙尘
     */
    private static final ParticleOptions GOLD_DUST =
            new DustParticleOptions(new Vector3f(1.0F, 0.85F, 0.35F), 0.9F);

    private static final String NBT_KEY_RENDER_ITEM = "RenderItem";
    private static final String NBT_KEY_OWNER_UUID = "OwnerUUID";

    private static final EntityDataAccessor<ItemStack> SWORD_ITEM_STACK =
            SynchedEntityData.defineId(EntitySword.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Byte> CONTROL_STATE =
            SynchedEntityData.defineId(EntitySword.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UNIQUE_ID =
            SynchedEntityData.defineId(EntitySword.class, EntityDataSerializers.OPTIONAL_UUID);

    private ItemStack renderItemStack = ItemStack.EMPTY;

    public EntitySword(EntityType<? extends EntitySword> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 20.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWORD_ITEM_STACK, ItemStack.EMPTY);
        this.entityData.define(CONTROL_STATE, (byte) 0);
        this.entityData.define(OWNER_UNIQUE_ID, Optional.empty());
    }

    public void setItemStack(ItemStack itemStack) {
        this.entityData.set(SWORD_ITEM_STACK, itemStack);
    }

    public ItemStack getItemStack() {
        return this.entityData.get(SWORD_ITEM_STACK);
    }

    public ItemStack getRenderItemStack() {
        if (this.renderItemStack.isEmpty() && !this.getItemStack().isEmpty()) {
            this.renderItemStack = new ItemStack(this.getItemStack().getItem());
        }
        return this.renderItemStack;
    }

    /**
     * 飞剑自身的附魔等级，直接从剑物品读取，因此客户端同样可用
     */
    public int getEnchantLevel() {
        return EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.FLY_SWORD.get(), this.getItemStack());
    }

    @Nullable
    public UUID getOwnerId() {
        return this.entityData.get(OWNER_UNIQUE_ID).orElse(null);
    }

    public void setOwnerId(@Nullable UUID uuid) {
        this.entityData.set(OWNER_UNIQUE_ID, Optional.ofNullable(uuid));
    }

    @Nullable
    public Player getOwner() {
        UUID uuid = this.getOwnerId();
        return uuid == null ? null : this.level().getPlayerByUUID(uuid);
    }

    public boolean isOwner(Entity entityIn) {
        return entityIn instanceof Player && entityIn == this.getOwner();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        if (!this.getItemStack().isEmpty()) {
            compound.put(NBT_KEY_RENDER_ITEM, this.getItemStack().save(new CompoundTag()));
        }
        if (this.getOwnerId() != null) {
            compound.putUUID(NBT_KEY_OWNER_UUID, this.getOwnerId());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        if (compound.contains(NBT_KEY_RENDER_ITEM, 10)) {
            this.setItemStack(ItemStack.of(compound.getCompound(NBT_KEY_RENDER_ITEM)));
        }
        if (compound.hasUUID(NBT_KEY_OWNER_UUID)) {
            this.setOwnerId(compound.getUUID(NBT_KEY_OWNER_UUID));
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity living ? living : null;
    }

    @Nullable
    private Player getControllingPlayer() {
        return this.getFirstPassenger() instanceof Player player ? player : null;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction callback) {
        super.positionRider(passenger, callback);

        if (passenger instanceof LivingEntity living) {
            this.yBodyRot = living.yBodyRot;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.getControllingPlayer() != null) {
            return false;
        }
        if (this.isOwner(source.getEntity())) {
            this.putAwaySword((Player) source.getEntity());
        }
        return false;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void tick() {
        Player player = this.getControllingPlayer();
        if (player != null && player.isShiftKeyDown()) {
            this.putAwaySword(player);
            return;
        }

        double prevX = this.getX();
        double prevY = this.getY();
        double prevZ = this.getZ();

        super.tick();

        if (this.level().isClientSide) {
            if (this.getControllingPlayer() != null) {
                this.spawnTrailParticles(prevX, prevY, prevZ);
            }
            this.updateClientControls();
        }
    }

    /**
     * 各附魔等级对应的拖尾风格，随境界递进：
     * <ul>
     *   <li>1 级 炼气 —— 灵气符文</li>
     *   <li>2 级 筑基 —— 剑罡电芒</li>
     *   <li>3 级 金丹 —— 真元灵光</li>
     *   <li>4 级 元婴 —— 幽蓝灵焰</li>
     *   <li>5 级 化神 —— 金霞仙光（仙尘与金光交替）</li>
     * </ul>
     */
    private static ParticleOptions trailParticleFor(int level, int index) {
        return switch (Mth.clamp(level, 1, 5)) {
            case 1 -> ParticleTypes.ENCHANT;
            case 2 -> ParticleTypes.ELECTRIC_SPARK;
            case 3 -> ParticleTypes.END_ROD;
            case 4 -> ParticleTypes.SOUL_FIRE_FLAME;
            default -> index % 2 == 0 ? ParticleTypes.TOTEM_OF_UNDYING : GOLD_DUST;
        };
    }

    /**
     * 沿本刻的移动路径撒粒子形成拖尾，仅客户端执行。粒子数量与风格随附魔等级提升
     */
    @OnlyIn(Dist.CLIENT)
    private void spawnTrailParticles(double prevX, double prevY, double prevZ) {
        if (!FlySwordConfig.FLY_TRAIL_ENABLED.get()) {
            return;
        }

        double dx = this.getX() - prevX;
        double dy = this.getY() - prevY;
        double dz = this.getZ() - prevZ;
        // 悬停时不产生拖尾
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
            this.level().addParticle(trailParticleFor(level, i),
                    prevX + dx * t,
                    prevY + dy * t + RENDER_OFFSET_Y,
                    prevZ + dz * t,
                    -dx * 0.1D, -dy * 0.1D, -dz * 0.1D);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void updateClientControls() {
        Minecraft minecraft = Minecraft.getInstance();
        if (this.getControllingPlayer() == minecraft.player) {
            this.up(minecraft.options.keyJump.isDown());
            this.down(ModKeys.KEY_FLY_SWORD_DOWN.isDown());
        }
    }

    private void putAwaySword(Player player) {
        if (!this.level().isClientSide) {
            ItemStack sword = this.getItemStack();
            this.setItemStack(ItemStack.EMPTY);
            if (!sword.isEmpty() && !player.getInventory().add(sword)) {
                this.spawnAtLocation(sword);
            }
        }
        this.discard();
    }

    private boolean up() {
        return (this.entityData.get(CONTROL_STATE) & 1) == 1;
    }

    private boolean down() {
        return ((this.entityData.get(CONTROL_STATE) >> 1) & 1) == 1;
    }

    private void up(boolean up) {
        this.setStateField(0, up);
    }

    private void down(boolean down) {
        this.setStateField(1, down);
    }

    private void setStateField(int i, boolean newState) {
        byte prevState = this.entityData.get(CONTROL_STATE);
        if (newState) {
            this.entityData.set(CONTROL_STATE, (byte) (prevState | (1 << i)));
        } else {
            this.entityData.set(CONTROL_STATE, (byte) (prevState & ~(1 << i)));
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        Player player = this.getControllingPlayer();

        if (player == null) {
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        // 仅由骑乘者的客户端进行模拟，服务端通过 ServerboundMoveVehiclePacket 跟随
        if (!this.isControlledByLocalInstance()) {
            return;
        }

        // 飞行性能随附魔等级提升
        int level = this.getEnchantLevel();
        double levelBonus = Math.max(0, level - 1);

        double verticalAcceleration = FlySwordConfig.FLY_VERTICAL_ACCELERATION_BASE.get()
                + levelBonus * FlySwordConfig.FLY_VERTICAL_ACCELERATION_PER_LEVEL.get();
        if (this.down()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -verticalAcceleration, 0.0D));
        }
        if (this.up()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, verticalAcceleration, 0.0D));
        }

        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.yBodyRot;

        float strafe = player.xxa * 0.5F;
        float forward = player.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }

        double speedMultiplier = FlySwordConfig.FLY_SPEED_MULTIPLIER_BASE.get()
                + levelBonus * FlySwordConfig.FLY_SPEED_MULTIPLIER_PER_LEVEL.get();
        float acceleration = (float) (player.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedMultiplier * 0.1D);
        this.moveRelative(acceleration, new Vec3(strafe, 0.0D, forward));
        this.move(MoverType.SELF, this.getDeltaMovement());
        double horizontalDrag = FlySwordConfig.FLY_HORIZONTAL_DRAG.get();
        this.setDeltaMovement(this.getDeltaMovement().multiply(horizontalDrag,
                FlySwordConfig.FLY_VERTICAL_DRAG.get(), horizontalDrag));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }
}
