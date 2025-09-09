package com.ypsi.fundamentalism.item;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FundamentalPrinciples.MOD_ID);

    public static final Supplier<CreativeModeTab> MANA_ITEMS_TAB = CREATIVE_MODE_TAB.register("mana_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.PURE_ORB.get()))
                    .title(Component.translatable("creativetab.ypfundamentals.mana_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.PURE_ORB);
                        output.accept(ModItems.ORB);
                        output.accept(ModItems.MANA_FRUIT);

                    })
                    .build());
//    public static final Supplier<CreativeModeTab> MANA_BLOCKS_TAB = CREATIVE_MODE_TAB.register("mana_blocks_tab",
//            () -> CreativeModeTab.builder()
//                    .icon(() -> new ItemStack(ModBlocks.MANA_BLOCK.get()))
//                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mana_items_tab"))
//                    .title(Component.translatable("creativetab.ypfundamentals.mana_blocks"))
//                    .displayItems((parameters, output) -> {
//                        output.accept(ModBlocks.MANA_BLOCK);
//                        output.accept(ModBlocks.MANA_ORE);
//                        output.accept(ModBlocks.MAGIC_BLOCK);
//
//                        output.accept(ModItems.FROSTFIRE_ICE);
//                        output.accept(ModItems.STARLIGHT_ASHES);
//
//                    })
//                    .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
