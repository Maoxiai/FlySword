package com.flysword.utils;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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
}
