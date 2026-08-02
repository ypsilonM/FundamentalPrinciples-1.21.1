package com.ypsi.fundamentalism.item;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.item.custom.AncientScrollCase;
import com.ypsi.fundamentalism.item.custom.NullifierBlade;
import com.ypsi.fundamentalism.item.custom.FatigueReducerContainer;
import com.ypsi.fundamentalism.item.custom.SpellbookCover;
import com.ypsi.fundamentalism.item.custom.food.ManaFruit;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FundamentalPrinciples.MOD_ID);

    public static final DeferredItem<SwordItem> NULLIFIER = ITEMS.register("nullifier",
            () -> new NullifierBlade(Tiers.NETHERITE,
                    new Item.Properties()
                            .rarity(Rarity.EPIC)
                            .fireResistant()
                            .attributes(SwordItem.createAttributes(Tiers.NETHERITE, 2, -1.0f))));

    public static final DeferredItem<AncientScrollCase> ANCIENT_SCROLL_CASE = ITEMS.register("ancient_scroll_case",
            () -> new AncientScrollCase(new Item.Properties().rarity(Rarity.RARE)));


    public static final DeferredItem<Item> ARCANE_MIXTURE = ITEMS.register("arcane_mixture",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> TEST_TUBE = ITEMS.register("test_tube",
            () -> new Item(new Item.Properties().stacksTo(16)));

    public static final DeferredItem<Item> LUMINAIRE_EXTRACT = ITEMS.register("luminaire_extract",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> PITCHER_EXTRACT = ITEMS.register("pitcher_extract",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> TONIC = ITEMS.register("tonic",
            () -> new FatigueReducerContainer(Rarity.EPIC, 10, 32,
                    25, 45, 6));
    public static final DeferredItem<Item> FLASK = ITEMS.register("flask",
            () -> new FatigueReducerContainer(Rarity.RARE, 5, 32,
                    15,45,3));

    public static final DeferredItem<Item> MANA_FRUIT = ITEMS.register("mana_fruit",
            () -> new ManaFruit(Rarity.EPIC));

    public static final DeferredItem<Item> HEMOMANCER_SPAWN_EGG = ITEMS.register("hemomancer_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.HEMOMANCER, 0x120303, 0x704141,
                    new Item.Properties()));
    public static final DeferredItem<Item> IMP_SPAWN_EGG = ITEMS.register("imp_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.IMP, 0x4F0C0C, 0xAD1111,
                    new Item.Properties()));
    public static final DeferredItem<Item> VENEMERUS_SPAWN_EGG = ITEMS.register("venemerus_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.VENEMERUS, 0x03941B, 0x74992E,
                    new Item.Properties()));
    public static final DeferredItem<Item> RUNEAR_SPAWN_EGG = ITEMS.register("runear_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.RUNEAR, 0x221E26, 0x2a252f,
                    new Item.Properties()));
    public static final DeferredItem<Item> CHERRY_BIRD_SPAWN_EGG = ITEMS.register("cherry_bird_spawn_egg",
            () -> new DeferredSpawnEggItem(ModEntities.CHERRY_BIRD, 0x3b1924, 0xe78fc2,
                    new Item.Properties()));


    public static final DeferredItem<Item> FUNDAMENTALISM_SCROLL = ITEMS.register("scroll_fundamentalism",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> URSIDAE_FUR = ITEMS.register("ursidae_fur",
            () -> new Item(new Item.Properties().stacksTo(8)));
    public static final DeferredItem<Item> SPELLBOOK_COVER = ITEMS.register("spellbook_cover",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));
    public static final DeferredItem<Item> NOVICE_SPELLBOOK_COVER = ITEMS.register("novice_spellbook_cover",
            () -> new SpellbookCover(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> ADEPT_SPELLBOOK_COVER = ITEMS.register("adept_spellbook_cover",
            () -> new SpellbookCover(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> SORCERER_SPELLBOOK_COVER = ITEMS.register("sorcerer_spellbook_cover",
            () -> new SpellbookCover(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> SCHOLAR_SPELLBOOK_COVER = ITEMS.register("scholar_spellbook_cover",
            () -> new SpellbookCover(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<Item> ARCHMAGE_SPELLBOOK_COVER = ITEMS.register("archmage_spellbook_cover",
            () -> new SpellbookCover(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));


    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item){
        DeferredItem<T> toReturn = ITEMS.register(name, item);
        return null;
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
