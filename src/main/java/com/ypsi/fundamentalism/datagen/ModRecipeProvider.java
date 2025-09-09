package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.block.ModBlocks;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
//        List<ItemLike> MANA_SMELTABLES = List.of(ModItems.PURE_ORB,
//                ModBlocks.MANA_ORE);
//
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MANA_FRUIT.get())
                .pattern("EFE")
                .pattern("FAF")
                .pattern("EFE")
                .define('A', Items.GOLDEN_APPLE)
                .define('F', Items.CHORUS_FRUIT)
                .define('E', ItemRegistry.ARCANE_ESSENCE.get())
                .unlockedBy("has_essence", has(ItemRegistry.ARCANE_ESSENCE.get())).save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 9)
//                .requires(ModBlocks.MANA_BLOCK)
//                .unlockedBy("has_mana_block", has(ModBlocks.MANA_BLOCK)).save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 18)
//                .requires(ModBlocks.MAGIC_BLOCK)
//                .unlockedBy("has_magic_block", has(ModBlocks.MAGIC_BLOCK))
//                .save(recipeOutput, "ypfundamentals:orb_from_magic_block");
//
//        oreSmelting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,200, "mana");
//        oreBlasting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,100, "mana");
    }
}
