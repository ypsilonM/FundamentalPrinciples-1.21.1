package com.ypsi.fundamentalism.datagen.providers;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlockStatesProvider extends BlockStateProvider {
    public ModBlockStatesProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, FundamentalPrinciples.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
//        blockWithItem(YpsBlocks.MAGIC_BLOCK);
//        blockWithItem(YpsBlocks.MANA_BLOCK);
//        blockWithItem(YpsBlocks.MANA_ORE);
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
}
