package com.ypsi.fundamentalism.entity;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerEntity;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.entity.mobs.runear.RunearEntity;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import com.ypsi.fundamentalism.entity.spells.domain.DomainEntity;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsEntity;
import com.ypsi.fundamentalism.entity.spells.holy_lightning.HolyLightningProjectile;
import com.ypsi.fundamentalism.entity.spells.pull.PullProjectile;
import com.ypsi.fundamentalism.entity.spells.sacredDisk.SacredDiskProjectile;
import com.ypsi.fundamentalism.entity.spells.sol.SolProjectile;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornProjectile;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, FundamentalPrinciples.MOD_ID);


    public static final DeferredHolder<EntityType<?>, EntityType<HolyLightningProjectile>> HOLY_LIGHTNING_PROJECTILE =
            ENTITY_TYPES.register("holy_lightning", () -> EntityType.Builder.<HolyLightningProjectile>of(HolyLightningProjectile::new, MobCategory.MISC)
                    .sized(1.25f, 1.25f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "holy_lightning").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<PullProjectile>> PULL_PROJECTILE =
            ENTITY_TYPES.register("pull", () -> EntityType.Builder.<PullProjectile>of(PullProjectile::new, MobCategory.MISC)
                    .sized(11f, 11f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "pull").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<SolProjectile>> SOL_PROJECTILE =
            ENTITY_TYPES.register("sol", () -> EntityType.Builder.<SolProjectile>of(SolProjectile::new, MobCategory.MISC)
                    .sized(12f, 12f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sol").toString()));

    public static final DeferredHolder<EntityType<?>, EntityType<ThornProjectile>> THORN_PROJECTILE =
            ENTITY_TYPES.register("thorn", () -> EntityType.Builder.<ThornProjectile>of(ThornProjectile::new, MobCategory.MISC)
                    .sized(.5f, .5f)
                    .clientTrackingRange(64)
                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "thorn").toString()));


    public static final DeferredHolder<EntityType<?>, EntityType<SacredDiskProjectile>> SACRED_DISK =
            ENTITY_TYPES.register("disk", () -> EntityType.Builder.<SacredDiskProjectile>of(SacredDiskProjectile::new, MobCategory.MISC)
                    .sized(.8f, .2f)
                    .clientTrackingRange(10)
                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "disk").toString())
            );

    public static final DeferredHolder<EntityType<?>, EntityType<ChainsEntity>> CHAINS =
            ENTITY_TYPES.register("chains", () -> EntityType.Builder.<ChainsEntity>of(ChainsEntity::new, MobCategory.MISC)
                        .sized(1.0F, 1.0F)
                        .clientTrackingRange(64)
                        .build((ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "chains")).toString()));



    public static final DeferredHolder<EntityType<?>, EntityType<ImpEntity>> IMP =
            ENTITY_TYPES.register("imp", () ->
                    EntityType.Builder.<ImpEntity>of(ImpEntity::new, MobCategory.MONSTER)
                            .sized(0.5f, 0.7f)
                            .clientTrackingRange(10)
                            .fireImmune()
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "imp")))
            );
    public static final DeferredHolder<EntityType<?>, EntityType<HemomancerEntity>> HEMOMANCER =
            ENTITY_TYPES.register("hemomancer", () ->
                    EntityType.Builder.<HemomancerEntity>of(HemomancerEntity::new, MobCategory.MONSTER)
                            .sized(0.8f, 2.3f)
                            .clientTrackingRange(10)
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "hemomancer")))
            );

    public static final DeferredHolder<EntityType<?>, EntityType<VenemerusEntity>> VENEMERUS =
            ENTITY_TYPES.register("venemerus", () ->
                    EntityType.Builder.<VenemerusEntity>of(VenemerusEntity::new, MobCategory.MONSTER)
                            .sized(1.5f, 1f)
                            .clientTrackingRange(10)
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "venemerus")))
            );

    public static final DeferredHolder<EntityType<?>, EntityType<RunearEntity>> RUNEAR =
            ENTITY_TYPES.register("runear", () ->
                    EntityType.Builder.<RunearEntity>of(RunearEntity::new, MobCategory.CREATURE)
                            .sized(1.6f, 1.6f)
                            .clientTrackingRange(20)
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "runear")))
            );


    public static final DeferredHolder<EntityType<?>, EntityType<DomainEntity>> DOMAIN_ENTITY =
            ENTITY_TYPES.register("domain", () ->
                    EntityType.Builder.<DomainEntity>of(DomainEntity::new, MobCategory.MISC)
                            .sized(1f, 1f)
                            .clientTrackingRange(128)
                            .build(String.valueOf(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "domain")))
            );

    //Summons
//    public static final DeferredHolder<EntityType<?>, EntityType<SummonedWitch>> SUMMONED_WITCH =
//            ENTITY_TYPES.register("summoned_witch", () -> EntityType.Builder.<SummonedWitch>of(SummonedWitch::new, MobCategory.MONSTER)
//                    .sized(.6f, 1.8f)
//                    .clientTrackingRange(64)
//                    .build(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "summoned_witch").toString()));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
