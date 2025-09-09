package com.ypsi.fundamentalism.datagen;

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
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
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
                        .withPool(createHemomancerDrops(
                                ItemRegistry.ARCANE_ESSENCE.get(), 3f, 7f,
                                Items.BONE, 1f, 3f
                        )));
        add(ModEntities.IMP.get(),
                LootTable.lootTable()
                        .withPool(createImpDrops(
                                ItemRegistry.ARCANE_ESSENCE.get(), 1f, 4f,
                                Items.COAL, 1f, 3f
                        )));
        add(ModEntities.VENEMERUS.get(),
                LootTable.lootTable()
                        .withPool(createVenuDrops(
                                ItemRegistry.ARCANE_ESSENCE.get(), 2f, 3f,
                                Items.SPIDER_EYE, 1f, 1f
                        )));

    }
    protected LootPool.Builder createHemomancerDrops(Item item1, float minDrop1, float maxDrop1, Item item2, float minDrop2, float maxDrop2){
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(2))
                .add(LootItem.lootTableItem(item1)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop1, maxDrop1)))
                )
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))))
                .add(LootItem.lootTableItem(item2)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop2, maxDrop2)))
                );
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))));
    }
    protected LootPool.Builder createImpDrops(Item item1, float minDrop1, float maxDrop1, Item item2, float minDrop2, float maxDrop2){
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(2))
                .add(LootItem.lootTableItem(item1)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop1, maxDrop1)))
                )
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))))
                .add(LootItem.lootTableItem(item2)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop2, maxDrop2)))
                );
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))));
    }
    protected LootPool.Builder createVenuDrops(Item item1, float minDrop1, float maxDrop1, Item item2, float minDrop2, float maxDrop2){
        HolderLookup.RegistryLookup<Enchantment> registryLookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return LootPool.lootPool()
                .setRolls(ConstantValue.exactly(2))
                .add(LootItem.lootTableItem(item1)
                        .setWeight(1)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop1, maxDrop1)))
                )
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))))
                .add(LootItem.lootTableItem(item2)
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrop2, maxDrop2)))
                );
//                        .apply(ApplyBonusCount.addUniformBonusCount(registryLookup.getOrThrow(Enchantments.LOOTING))));
    }
    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        Set<EntityType<?>> entitiesWithLoot = Set.of(
                ModEntities.HEMOMANCER.get(),
                ModEntities.IMP.get(),
                ModEntities.VENEMERUS.get()
        );

        return ModEntities.ENTITY_TYPES.getEntries().stream()
                .map(Holder::value)
                .filter(entitiesWithLoot::contains)
                .map(entityType -> (EntityType<?>) entityType);
    }
}
