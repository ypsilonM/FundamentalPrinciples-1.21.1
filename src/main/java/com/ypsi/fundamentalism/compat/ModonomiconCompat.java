package com.ypsi.fundamentalism.compat;

import com.klikli_dev.modonomicon.api.datagen.LanguageProviderCache;
import com.klikli_dev.modonomicon.api.datagen.NeoBookProvider;
import com.klikli_dev.modonomicon.datagen.EnUsProvider;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.datagen.book.DemoBook;
import com.ypsi.fundamentalism.datagen.book.DemoMultiblockProvider;
import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ModonomiconCompat {
    public static void addProviders(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        var enUsCache = new LanguageProviderCache("en_us");
        generator.addProvider(event.includeServer(), NeoBookProvider.of(event,
                        new DemoBook(FundamentalPrinciples.MOD_ID, enUsCache)
                        //new DemoLeaflet(FundamentalPrinciples.MOD_ID, enUsCache)
                )
        );
        generator.addProvider(event.includeClient(), new EnUsProvider(generator.getPackOutput(), enUsCache));
        generator.addProvider(event.includeServer(), new DemoMultiblockProvider(generator.getPackOutput(), FundamentalPrinciples.MOD_ID));
    }
}
