package com.ypsi.fundamentalism.datagen.custom;

import com.ypsi.fundamentalism.datagen.RecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class EnchantingShieldSmithingRecipe implements SmithingRecipe {
    private final ResourceLocation id;
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;

    private final ResourceKey<Enchantment> enchantmentToAdd;
    private final int level;


    public EnchantingShieldSmithingRecipe(ResourceLocation id, Ingredient template, Ingredient base,
                                          Ingredient addition, ResourceKey<Enchantment> enchantment, int level) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.enchantmentToAdd = enchantment;
        this.level = level;
    }

    @Override
    public boolean matches(SmithingRecipeInput smithingRecipeInput, Level level) {
        if (!this.template.test(smithingRecipeInput.template()) ||
                !this.base.test(smithingRecipeInput.base()) ||
                !this.addition.test(smithingRecipeInput.addition())) {
            return false;
        }
        ItemStack shield = smithingRecipeInput.base();
        var enchantments = shield.getEnchantments();
        HolderLookup.Provider registries = level.registryAccess();
        Optional<HolderLookup.RegistryLookup<Enchantment>> lookup = registries.lookup(Registries.ENCHANTMENT);
        if (lookup.isPresent()) {
            var enchHolder = lookup.get().get(enchantmentToAdd);
            if (enchHolder.isPresent()) {
                int currentLevel = enchantments.getLevel(enchHolder.get());
                int maxLevel = 5;
                if (currentLevel >= maxLevel) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput smithingRecipeInput, HolderLookup.Provider provider) {
        ItemStack result = smithingRecipeInput.base().copy();

        Optional<HolderLookup.RegistryLookup<Enchantment>> lookup = provider.lookup(Registries.ENCHANTMENT);
        if (lookup.isPresent()) {
            var enchantHolder = lookup.get().get(enchantmentToAdd);
            if (enchantHolder.isPresent()) {

                var existingEnchants = result.getEnchantments();
                int currentLevel = existingEnchants.getLevel(enchantHolder.get());
                int increment = this.level;
                int maxLevel = 5;
                int newLevel = Math.min(currentLevel + increment, maxLevel);

                if (newLevel > currentLevel) {
                    var mutable = new ItemEnchantments.Mutable(existingEnchants);
                    mutable.set(enchantHolder.get(), newLevel);
                    result.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
                }
            }
        }
        return result;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack itemStack) {
        return this.template.test(itemStack);
    }

    @Override
    public boolean isBaseIngredient(ItemStack itemStack) {
        return this.base.test(itemStack);
    }

    @Override
    public boolean isAdditionIngredient(ItemStack itemStack) {
        return this.addition.test(itemStack);
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        ItemStack result = new ItemStack(Items.SHIELD);
        var lookup = provider.lookup(Registries.ENCHANTMENT);
        if (lookup.isPresent()) {
            var ench = lookup.get().get(enchantmentToAdd);
            if (ench.isPresent()) {
                var mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
                mutable.set(ench.get(), level);
                result.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializers.ENCHANTING_SHIELD_SMITHING.get();
    }
    public ResourceLocation getId() { return id; }
    public Ingredient getTemplate() { return template; }
    public Ingredient getBase() { return base; }
    public Ingredient getAddition() { return addition; }
    public int getLevel() {
        return level;
    }
    public ResourceKey<Enchantment> getEnchantmentToAdd() {
        return enchantmentToAdd;
    }

}
