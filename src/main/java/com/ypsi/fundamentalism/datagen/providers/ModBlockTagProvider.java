package com.ypsi.fundamentalism.datagen.providers;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FundamentalPrinciples.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
//        tag(BlockTags.MINEABLE_WITH_PICKAXE)
//                .add(YpsBlocks.MAGIC_BLOCK.get())
//                .add(YpsBlocks.MANA_BLOCK.get())
//                .add(YpsBlocks.MANA_ORE.get());
//
//        tag(BlockTags.NEEDS_IRON_TOOL)
//                .add(YpsBlocks.MANA_ORE.get());

    }
}
