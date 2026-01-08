package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
//        List<ItemLike> MANA_SMELTABLES = List.of(ModItems.PURE_ORB,
//                YpsBlocks.MANA_ORE);
//

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ARCANE_MIXTURE.get())
                .requires(ItemRegistry.ARCANE_ESSENCE.get())
                .requires(Items.BOWL)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.SPIDER_EYE)
                .unlockedBy("has_essence", has(ItemRegistry.ARCANE_ESSENCE.get())).save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BREWING, ModItems.TEST_TUBE.get())
                .requires(Items.GLASS_PANE)
                .unlockedBy("has_glass", has(Items.GLASS)).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MANA_FRUIT.get())
                .pattern("EFE")
                .pattern("FAF")
                .pattern("EFE")
                .define('A', Items.GOLDEN_APPLE)
                .define('F', Items.CHORUS_FRUIT)
                .define('E', ItemRegistry.ARCANE_ESSENCE.get())
                .unlockedBy("has_essence", has(ItemRegistry.ARCANE_ESSENCE.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.TONIC.get())
                .pattern(" C ")
                .pattern("IBI")
                .pattern("IMI")
                .define('C', ItemRegistry.CINDER_ESSENCE.get())
                .define('I', ItemRegistry.ARCANE_INGOT.get())
                .define('B', Items.GLASS_BOTTLE)
                .define('M', ItemRegistry.MITHRIL_SCRAP.get())
                .unlockedBy("has_cinder", has(ItemRegistry.CINDER_ESSENCE.get())).save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, ModItems.FLASK.get())
                .pattern(" A ")
                .pattern("IBI")
                .pattern("IPI")
                .define('A', ItemRegistry.ARCANE_ESSENCE.get())
                .define('I', Items.IRON_INGOT)
                .define('B', Items.GLASS_BOTTLE)
                .define('P', Items.BLAZE_POWDER)
                .unlockedBy("default_powder", has(Items.BLAZE_POWDER)).save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 9)
//                .requires(YpsBlocks.MANA_BLOCK)
//                .unlockedBy("has_mana_block", has(YpsBlocks.MANA_BLOCK)).save(recipeOutput);
//
//        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.ORB.get(), 18)
//                .requires(YpsBlocks.MAGIC_BLOCK)
//                .unlockedBy("has_magic_block", has(YpsBlocks.MAGIC_BLOCK))
//                .save(recipeOutput, "ypfundamentals:orb_from_magic_block");
//
//        oreSmelting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,200, "mana");
//        oreBlasting(recipeOutput,MANA_SMELTABLES, RecipeCategory.MISC, ModItems.ORB.get(), 0.25f,100, "mana");
    }
}
