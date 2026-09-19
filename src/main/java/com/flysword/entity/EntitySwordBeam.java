/**
 * Copyright (C) <2017> <coolAlias>
 * <p>
 * This file is part of coolAlias' Dynamic Sword Skills Minecraft Mod; as such,
 * you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.flysword.entity;

import com.flysword.enchantment.ModEnchantments;
import com.flysword.utils.PlayerUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Sword beam shot from Link's sword when at full health. Inflicts a portion of
 * the original sword's base damage to the first entity struck, less 20% for each
 * additional target thus struck.
 * <p>
 * If using the Master Sword, the beam will shoot through enemies, hitting all
 * entities in its direct path.
 */
public class EntitySwordBeam extends ThrowableProjectile {
    /**
     * Damage that will be inflicted on impact
     */
    private float damage = 4.0F;

    /**
     * Skill level of user; affects range
     */
    private int level = 1;

    /**
     * Base number of ticks this entity can exist
     */
    private int lifespan = 12;

    /**
     * The amount of knockback an arrow applies when it hits a mob.
     */
    private int knockbackStrength = 1;

    public EntitySwordBeam(EntityType<? extends EntitySwordBeam> type, Level level) {
        super(type, level);
    }

    public EntitySwordBeam(EntityType<? extends EntitySwordBeam> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    public EntitySwordBeam(EntityType<? extends EntitySwordBeam> type, double x, double y, double z, Level level) {
        super(type, x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
    }

    /**
     * Each level increases the distance the beam will travel
     */
    public EntitySwordBeam setLevel(int level) {
        this.level = level;
        this.lifespan += level;
        return this;
    }

    /**
     * Sets amount of damage that will be caused onImpact
     */
    public EntitySwordBeam setDamage(float amount) {
        this.damage = amount;
        return this;
    }

    public float getVelocity() {
        return 1.0F + (this.level * 0.15F);
    }

    @Override
    protected float getGravity() {
        return 0.0F;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            if (this.tickCount > this.lifespan) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        for (int i = 0; i < 2; ++i) {
            ParticleOptions particle = (i % 2 == 1) ? ParticleTypes.ENCHANTED_HIT : ParticleTypes.CRIT;
            this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(),
                    motion.x + this.random.nextGaussian(), 0.01D, motion.z + this.random.nextGaussian());
            this.level().addParticle(particle, this.getX(), this.getY(), this.getZ(),
                    -motion.x + this.random.nextGaussian(), 0.01D, -motion.z + this.random.nextGaussian());
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (this.level().isClientSide) {
            return;
        }

        Player player = this.getOwner() instanceof Player owner ? owner : null;

        if (result.getType() == HitResult.Type.ENTITY) {
            Entity entityHit = ((EntityHitResult) result).getEntity();
            if (entityHit == player) {
                return;
            }
            if (player != null) {
                if (entityHit.hurt(this.damageSources().thrown(this, player), this.damage)) {
                    PlayerUtils.playSoundAtEntity(this.level(), entityHit, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.4F, 0.5F);
                    if (entityHit instanceof LivingEntity && this.knockbackStrength > 0) {
                        Vec3 motion = this.getDeltaMovement();
                        float f1 = Mth.sqrt((float) (motion.x * motion.x + motion.z * motion.z));
                        if (f1 > 0.0F) {
                            entityHit.push(motion.x * (double) this.knockbackStrength * 0.6D / (double) f1, 0.1D,
                                    motion.z * (double) this.knockbackStrength * 0.6D / (double) f1);
                        }
                    }
                    this.damage *= 0.8F;
                }
            }
            if (this.level < ModEnchantments.SWORD_BEAM.get().getMaxLevel()) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        } else if (result.getType() == HitResult.Type.BLOCK) {
            if (this.level().getBlockState(((BlockHitResult) result).getBlockPos()).blocksMotion()) {
                this.level().broadcastEntityEvent(this, (byte) 3);
                this.discard();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("damage", this.damage);
        compound.putInt("level", this.level);
        compound.putInt("lifespan", this.lifespan);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.damage = compound.getFloat("damage");
        this.level = compound.getInt("level");
        this.lifespan = compound.getInt("lifespan");
    }
}
