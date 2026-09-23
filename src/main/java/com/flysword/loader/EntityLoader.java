package com.flysword.loader;

import com.flysword.FlySwordMod;
import com.flysword.entity.EntityFlyingSword;
import com.flysword.entity.EntitySword;
import com.flysword.entity.EntitySwordBeam;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class EntityLoader {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FlySwordMod.MODID);

    public static final RegistryObject<EntityType<EntitySword>> SWORD =
            ENTITY_TYPES.register("entity_sword", () -> EntityType.Builder
                    .<EntitySword>of(EntitySword::new, MobCategory.MISC)
                    // 高度 1.8 与 1.12.2 的 Entity 默认尺寸一致，使 getPassengersRidingOffset() = 1.35，
                    // 玩家才能正好站在剑上
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("entity_sword"));

    public static final RegistryObject<EntityType<EntitySwordBeam>> SWORD_BEAM =
            ENTITY_TYPES.register("entity_sword_beam", () -> EntityType.Builder
                    .<EntitySwordBeam>of(EntitySwordBeam::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build("entity_sword_beam"));

    public static final RegistryObject<EntityType<EntityFlyingSword>> FLYING_SWORD =
            ENTITY_TYPES.register("entity_flying_sword", () -> EntityType.Builder
                    .<EntityFlyingSword>of(EntityFlyingSword::new, MobCategory.MISC)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("entity_flying_sword"));

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(EntityLoader::onEntityAttributeCreation);
    }

    private static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(SWORD.get(), EntitySword.createAttributes().build());
        event.put(FLYING_SWORD.get(), EntitySword.createAttributes().build());
    }
}
