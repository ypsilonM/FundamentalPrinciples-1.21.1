package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.enchantment.FundEnchantments;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;
import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {

    protected ModEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);

    }


    @Override
    public void generate() {

        add(ModEntities.HEMOMANCER.get(),
                LootTable.lootTable()
                        .withPool(createBonePool(1, 2))
                        .withPool(createArcanePool(3,6, 0.5F, 1, 2))
        );
        add(ModEntities.IMP.get(),
                LootTable.lootTable()
                        .withPool(createCoalPool(1, 4))
                        .withPool(createArcanePool(2, 3, 0.3F, 0, 1))
        );
        add(ModEntities.VENEMERUS.get(),
                LootTable.lootTable()
                        .withPool(createSpiderEyePool(1, 1))
                        .withPool(createArcanePool(2, 3, 0.2F, 1, 1))
        );
        add(ModEntities.RUNEAR.get(),
                LootTable.lootTable()
                        .withPool(createRunePool(3))

                        .withPool(createArcanePool(35, 45, 0.7F, 10, 12))
        );

    }

    protected LootPool.Builder createCoalPool(int min, int max) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.COAL)
                        .apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(min, max)
                        ))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(0,1)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                );
    }
    protected LootPool.Builder createSpiderEyePool(int min, int max) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.SPIDER_EYE)
                        .apply(SetItemCountFunction.setCount(
                                UniformGenerator.between(min, max)
                        ))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(0,1)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                );
    }
    protected LootPool.Builder createBonePool(int min, int max) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(Items.BONE)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(1,2)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                );
    }
    protected LootPool.Builder createRunePool(int value){
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ItemRegistry.BLANK_RUNE.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(value)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(0,1)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                );
    }
    protected LootPool.Builder createFurPool(int value){
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ModItems.URSIDAE_FUR.get())
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(value+1)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(1,2)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                );
    }


    protected LootPool.Builder createArcanePool(int min, int max, float chance, int minAd, int maxAd) {
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ItemRegistry.ARCANE_ESSENCE.get())
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(
                                this.registries, UniformGenerator.between(minAd,maxAd)))
                        .when(LootItemKilledByPlayerCondition.killedByPlayer())
                        .when(LootItemRandomChanceCondition.randomChance(chance))
                );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        Set<EntityType<?>> entitiesWithLoot = Set.of(
                ModEntities.HEMOMANCER.get(),
                ModEntities.IMP.get(),
                ModEntities.VENEMERUS.get(),
                ModEntities.RUNEAR.get()
        );

        return ModEntities.ENTITY_TYPES.getEntries().stream()
                .map(Holder::value)
                .filter(entitiesWithLoot::contains)
                .map(entityType -> (EntityType<?>) entityType);
    }
}
