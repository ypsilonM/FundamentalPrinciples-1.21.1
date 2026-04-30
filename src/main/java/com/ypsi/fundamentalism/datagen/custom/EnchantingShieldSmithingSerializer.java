package com.ypsi.fundamentalism.datagen.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantingShieldSmithingSerializer implements RecipeSerializer<EnchantingShieldSmithingRecipe> {

    private static final MapCodec<EnchantingShieldSmithingRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EnchantingShieldSmithingRecipe::getId),
            Ingredient.CODEC_NONEMPTY.fieldOf("template").forGetter(EnchantingShieldSmithingRecipe::getTemplate),
            Ingredient.CODEC_NONEMPTY.fieldOf("base").forGetter(EnchantingShieldSmithingRecipe::getBase),
            Ingredient.CODEC_NONEMPTY.fieldOf("addition").forGetter(EnchantingShieldSmithingRecipe::getAddition),
            ResourceKey.codec(Registries.ENCHANTMENT).fieldOf("enchantment").forGetter(EnchantingShieldSmithingRecipe::getEnchantmentToAdd),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("level").forGetter(EnchantingShieldSmithingRecipe::getLevel)
    ).apply(inst, EnchantingShieldSmithingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, EnchantingShieldSmithingRecipe> STREAM_CODEC =
            StreamCodec.of(
                    EnchantingShieldSmithingSerializer::toNetwork,
                    EnchantingShieldSmithingSerializer::fromNetwork
            );

    @Override
    public MapCodec<EnchantingShieldSmithingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EnchantingShieldSmithingRecipe> streamCodec() {
        return STREAM_CODEC;
    }

    private static void toNetwork(RegistryFriendlyByteBuf buffer, EnchantingShieldSmithingRecipe recipe) {
        buffer.writeResourceLocation(recipe.getId());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getTemplate());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getBase());
        Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.getAddition());
        buffer.writeResourceKey(recipe.getEnchantmentToAdd());
        buffer.writeInt(recipe.getLevel());
    }

    private static EnchantingShieldSmithingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
        ResourceLocation id = buffer.readResourceLocation();
        Ingredient template = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient base = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        Ingredient addition = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
        ResourceKey<Enchantment> enchantment = buffer.readResourceKey(Registries.ENCHANTMENT);
        int level = buffer.readInt();
        return new EnchantingShieldSmithingRecipe(id, template, base, addition, enchantment, level);
    }

}
