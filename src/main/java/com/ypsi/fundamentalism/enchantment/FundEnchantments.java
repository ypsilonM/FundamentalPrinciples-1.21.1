package com.ypsi.fundamentalism.enchantment;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.Tags;

public class FundEnchantments {
    public static final ResourceKey<Enchantment> AEGIS = ResourceKey.create(Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "aegis"));

    public static void bootstrap(BootstrapContext<Enchantment> context){

        var items = context.lookup(Registries.ITEM);
        var shieldTag = items.getOrThrow(Tags.Items.TOOLS_SHIELD);

        register(context, AEGIS, Enchantment.enchantment(Enchantment.definition(
                shieldTag,
                shieldTag,
                1,
                5,
                Enchantment.dynamicCost(5,7),
                Enchantment.dynamicCost(25,7),
                2,
                EquipmentSlotGroup.MAINHAND, EquipmentSlotGroup.OFFHAND))
        );

    }

    private static void register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key, Enchantment.Builder builder){
        registry.register(key, builder.build(key.location()));
    }

}
