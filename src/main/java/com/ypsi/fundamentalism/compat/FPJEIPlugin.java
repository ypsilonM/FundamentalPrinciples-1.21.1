package com.ypsi.fundamentalism.compat;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

@JeiPlugin
public class FPJEIPlugin implements IModPlugin {

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

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
