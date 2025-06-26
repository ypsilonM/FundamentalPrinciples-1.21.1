package com.ypsi.fundamentalism.item;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.custom.ChiselItem;
import com.ypsi.fundamentalism.item.custom.FuelItem;
import net.minecraft.world.item.Item;
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
    public static final DeferredItem<Item> CHISEL = ITEMS.register("chisel",
            () -> new ChiselItem(new Item.Properties().durability(32)));


    public static final DeferredItem<Item> RADISH = ITEMS.register("radish",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RADISH)));


    public static final DeferredItem<Item> FROSTFIRE_ICE = ITEMS.register("frostfire_ice",
            () -> new FuelItem(new Item.Properties(), 800));
    public static final DeferredItem<Item> STARLIGHT_ASHES = ITEMS.register("starlight_ashes",
            () -> new Item(new Item.Properties()));



    private static <T extends Item> DeferredItem<T> registerItem(String name, Supplier<T> item){
        DeferredItem<T> toReturn = ITEMS.register(name, item);
        return null;
    }

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
