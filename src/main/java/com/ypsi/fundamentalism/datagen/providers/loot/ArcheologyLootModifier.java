package com.ypsi.fundamentalism.datagen.providers.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class ArcheologyLootModifier extends LootModifier {

    private final float chance;
    private final Item item;

    protected ArcheologyLootModifier(LootItemCondition[] conditionsIn, float chance, Item item) {
        super(conditionsIn);
        this.chance = chance;
        this.item = item;
    }

    public static final MapCodec<ArcheologyLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst)
                            .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(e -> e.item))
                            .apply(inst, ArcheologyLootModifier::new)
            );

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext lootContext) {
        if(lootContext.getRandom().nextFloat() < chance) {
            objectArrayList.clear();
            objectArrayList.add(new ItemStack(item, 1));
        }

        return objectArrayList;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
