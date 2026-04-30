package com.ypsi.fundamentalism.datagen;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.datagen.custom.EnchantingShieldSmithingSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, FundamentalPrinciples.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, EnchantingShieldSmithingSerializer> ENCHANTING_SHIELD_SMITHING =
            RECIPE_SERIALIZERS.register("enchanting_shield_smithing",
                    EnchantingShieldSmithingSerializer::new);

    public static void registrar(IEventBus bus){
        RECIPE_SERIALIZERS.register(bus);
    }
}
