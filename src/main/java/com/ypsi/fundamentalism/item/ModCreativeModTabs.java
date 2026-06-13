package com.ypsi.fundamentalism.item;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FundamentalPrinciples.MOD_ID);

    public static final Supplier<CreativeModeTab> MANA_ITEMS_TAB = CREATIVE_MODE_TAB.register("mana_items_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.MANA_FRUIT.get()))
                    .title(Component.translatable("creativetab.ypfundamentals.mana_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.MANA_FRUIT);
                        output.accept(ModItems.ARCANE_MIXTURE);
                        output.accept(ModItems.TEST_TUBE);
                        output.accept(ModItems.LUMINAIRE_EXTRACT);
                        output.accept(ModItems.PITCHER_EXTRACT);
                        output.accept(ModItems.FLASK);
                        output.accept(ModItems.TONIC);
                        output.accept(ModItems.URSIDAE_FUR);
                        output.accept(ModItems.NULLIFIER);
                        output.accept(ModItems.IMP_SPAWN_EGG);
                        output.accept(ModItems.HEMOMANCER_SPAWN_EGG);
                        output.accept(ModItems.VENEMERUS_SPAWN_EGG);
                        output.accept(ModItems.RUNEAR_SPAWN_EGG);
                    })
                    .build());
//    public static final Supplier<CreativeModeTab> MANA_BLOCKS_TAB = CREATIVE_MODE_TAB.register("mana_blocks_tab",
//            () -> CreativeModeTab.builder()
//                    .icon(() -> new ItemStack(YpsBlocks.MANA_BLOCK.get()))
//                    .withTabsBefore(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mana_items_tab"))
//                    .title(Component.translatable("creativetab.ypfundamentals.mana_blocks"))
//                    .displayItems((parameters, output) -> {
//                        output.accept(YpsBlocks.MANA_BLOCK);
//                        output.accept(YpsBlocks.MANA_ORE);
//                        output.accept(YpsBlocks.MAGIC_BLOCK);
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
