package com.flysword.utils;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerUtils {
    /**
     * Plays a sound on the server with randomized volume and pitch; no effect if called on client
     *
     * @param f   Volume: nextFloat() * f + add
     * @param add Pitch: 1.0F / (nextFloat() * f + add)
     */
    public static void playSoundAtEntity(Level level, Entity entity, SoundEvent sound, SoundSource category, float f, float add) {
        float volume = level.random.nextFloat() * f + add;
        float pitch = 1.0F / (level.random.nextFloat() * f + add);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), sound, category, volume, pitch);
    }

    /**
     * 沿玩家视线求准星落点：射线被方块挡住时取命中点，否则取射程末端。
     * 万剑归宗用它作为齐射目标点，剑雨用它作为落点中心。
     */
    public static Vec3 aimPoint(Player player, double range) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getLookAngle().scale(range));
        HitResult hit = player.level().clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    /**
     * 招式目标点：附近有敌对生物时优先瞄准最近的那一只，否则退回准星落点。
     * 于是「附近有怪自动索敌，没有怪仍可朝任意方向释放」
     *
     * @param searchRadius 自动索敌半径（格），小于等于 0 表示不索敌
     */
    public static Vec3 skillTargetPoint(Player player, double aimRange, double searchRadius) {
        Entity nearest = findNearestEnemy(player, searchRadius);
        return nearest == null ? aimPoint(player, aimRange) : nearest.getEyePosition();
    }

    @Nullable
    private static Entity findNearestEnemy(Player player, double radius) {
        if (radius <= 0.0D) {
            return null;
        }
        List<Entity> candidates = player.level().getEntities(player,
                player.getBoundingBox().inflate(radius),
                candidate -> candidate instanceof Enemy && candidate.isAlive());

        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity candidate : candidates) {
            double distance = candidate.distanceToSqr(player);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = candidate;
            }
        }
        return nearest;
    }
}
