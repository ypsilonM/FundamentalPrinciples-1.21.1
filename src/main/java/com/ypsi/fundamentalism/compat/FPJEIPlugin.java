package com.ypsi.fundamentalism.compat;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.datagen.custom.EnchantingShieldSmithingRecipe;
import com.ypsi.fundamentalism.datagen.custom.EnchantingShieldSmithingSerializer;
import com.ypsi.fundamentalism.enchantment.FundEnchantments;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@JeiPlugin
public class FPJEIPlugin implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

//        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "shield_upgrade");
//        EnchantingShieldSmithingRecipe recipe = new EnchantingShieldSmithingRecipe(
//                recipeId,
//                Ingredient.of(ItemRegistry.BLANK_RUNE.get()),
//                Ingredient.of(Items.SHIELD),
//                Ingredient.of(ModItems.URSIDAE_FUR),FundEnchantments.AEGIS, 1);
//
//        RecipeHolder<SmithingRecipe> holder = new RecipeHolder<>(recipeId, recipe);
//
//        registration.addRecipes(RecipeTypes.SMITHING, List.of(
//                holder
//        ));

        registration.addRecipes(RecipeTypes.BREWING, List.of(
                new IJeiBrewingRecipe(){
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of(ModItems.TEST_TUBE.toStack());
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of(Items.PITCHER_PLANT.getDefaultInstance());
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return ModItems.PITCHER_EXTRACT.toStack();
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 1;
                    }

                    @Override
                    public @Nullable ResourceLocation getUid() {
                        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "pitcher_extract_jei_recipe");
                    }
                },

                new IJeiBrewingRecipe(){
                    @Override
                    public @Unmodifiable List<ItemStack> getPotionInputs() {
                        return List.of(ModItems.TEST_TUBE.toStack());
                    }

                    @Override
                    public @Unmodifiable List<ItemStack> getIngredients() {
                        return List.of(ModItems.ARCANE_MIXTURE.toStack());
                    }

                    @Override
                    public ItemStack getPotionOutput() {
                        return ModItems.LUMINAIRE_EXTRACT.toStack();
                    }

                    @Override
                    public int getBrewingSteps() {
                        return 1;
                    }

                    @Override
                    public @Nullable ResourceLocation getUid() {
                        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "luminaire_extract_jei_recipe");
                    }
                }
        ));
    }



    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "pluginfp");
    }
}
