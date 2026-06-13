package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.advancements.ModAdvancementProvider;
//import com.ypsi.fundamentalism.datagen.book.DemoLeaflet;
import com.ypsi.fundamentalism.datagen.providers.*;
import com.ypsi.fundamentalism.datagen.providers.loot.ModGlobalLootModifierProvider;
import com.ypsi.fundamentalism.entity.ModEntityTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class DataGenerators {


    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();

        PackOutput packOutput = generator.getPackOutput();

        if (ModList.get().isLoaded("modonomicon")) {
            try {
                Class<?> helper = Class.forName("com.ypsi.fundamentalism.compat.ModonomiconCompat");
                Method method = helper.getMethod("addProviders", GatherDataEvent.class);
                method.invoke(null, event);
            } catch (Exception e) {
                // Log pero no falla
            }
        }
       // generator.addProvider(event.includeClient(), new ItemModelProvider(generator.getPackOutput(), event.getExistingFileHelper()));


        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ModDataPackProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(),
                ModAdvancementProvider.create(packOutput, lookupProvider, existingFileHelper));

        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(
                        new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(ModEntityLootTableProvider::new, LootContextParamSets.ENTITY)
                ), lookupProvider));

        generator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));

        BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput,lookupProvider,existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));

        generator.addProvider(event.includeServer(), new ModDataMapProvider(packOutput, lookupProvider));

        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput,existingFileHelper));
        generator.addProvider(event.includeClient(), new ModBlockStatesProvider(packOutput,existingFileHelper));



        generator.addProvider(event.includeServer(), new ModEntityTagProvider(packOutput, lookupProvider, existingFileHelper));



    }

}
