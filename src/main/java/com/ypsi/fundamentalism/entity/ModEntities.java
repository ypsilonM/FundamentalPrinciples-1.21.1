package com.ypsi.fundamentalism.entity;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.imp.ImpEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FundamentalPrinciples.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<ImpEntity>> IMP =
            ENTITY_TYPES.register("imp", () ->
                    EntityType.Builder.<ImpEntity>of(ImpEntity::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.7f)
                            .clientTrackingRange(10)
                            .fireImmune()
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "imp")))
            );

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
