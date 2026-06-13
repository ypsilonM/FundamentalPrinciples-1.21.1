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

public class ItemLootModifier extends LootModifier {

    private final int maxCount;
    private final float chance;
    private final Item item;

    public static final MapCodec<ItemLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(inst ->
                    LootModifier.codecStart(inst)
                            .and(Codec.INT.fieldOf("max_count").forGetter(m -> m.maxCount))
                            .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                            .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(e -> e.item))
                            .apply(inst, ItemLootModifier::new)
            );

    protected ItemLootModifier(LootItemCondition[] conditionsIn, int max, float chance, Item item) {
        super(conditionsIn);
        this.maxCount = max;
        this.chance = chance;
        this.item = item;
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> objectArrayList, LootContext lootContext) {
        //int amount = lootContext.getRandom().nextInt(0, maxCount+1);
        int amount = maxCount;
        if(amount>0 && lootContext.getRandom().nextFloat() < chance)
            objectArrayList.add(new ItemStack(item, amount));

        return objectArrayList;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
