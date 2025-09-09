package com.ypsi.fundamentalism.item;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.custom.ChiselItem;
import com.ypsi.fundamentalism.item.custom.FuelItem;
import com.ypsi.fundamentalism.item.custom.food.ManaFruit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(FundamentalPrinciples.MOD_ID);

    public static final DeferredItem<Item> ORB = ITEMS.register("orb",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item>  PURE_ORB = ITEMS.register("pure_orb",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> MANA_FRUIT = ITEMS.register("mana_fruit",
            () -> new ManaFruit());

    public static final DeferredItem<Item> TEMPLAR_SWORD = ITEMS.register("templar_sword",
            () -> new SwordItem(Tiers.DIAMOND, new Item.Properties()
                    .attributes(SwordItem.createAttributes(Tiers.DIAMOND, 2, -2.4f))));

    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item){
        DeferredItem<T> toReturn = ITEMS.register(name, item);
        return null;
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
