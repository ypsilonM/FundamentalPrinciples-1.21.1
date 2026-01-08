package com.ypsi.fundamentalism.advancements;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.ResourceLocationException;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider {

    public static AdvancementProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        return new AdvancementProvider(output, registries, helper, List.of(new ModAdvancements()));
    }

    public static class ModAdvancements implements AdvancementProvider.AdvancementGenerator {

        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper existingFileHelper) {

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ModItems.FUNDAMENTALISM_SCROLL.get(),
                            Component.literal("Fundamentalism Path"),
                            Component.literal("The principles are the basis for the magical epitome."),
                            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/adventure.png"),
                            AdvancementType.TASK,
                            true, true, false
                    )
                    .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "root"), existingFileHelper);


            //Advancement: Effect
            AdvancementHolder burnoutAdvancement = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            Items.FIRE_CHARGE,
                            Component.literal("Under Pressure"),
                            Component.literal("Experience for the first time the spell burnout."),
                            null,
                            AdvancementType.GOAL, true,
                            true, false
                    )
                    //.rewards(AdvancementRewards.Builder.experience(100))
                    .addCriterion("get_so_exhausted", EffectsChangedTrigger.TriggerInstance.hasEffects(
                            MobEffectsPredicate.Builder.effects().and(ModEffects.BURNOUT_EFFECT)
                    )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "burnout_advancement"), existingFileHelper);

            AdvancementHolder mindfulAdvancement = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            Items.ENDER_EYE,
                            Component.literal("So clear!"),
                            Component.literal("Experience for the first time star alignment."),
                            null,
                            AdvancementType.GOAL, true,
                            true, false
                    )
                    //.rewards(AdvancementRewards.Builder.experience(100))
                    .addCriterion("its_so_clear", EffectsChangedTrigger.TriggerInstance.hasEffects(
                            MobEffectsPredicate.Builder.effects().and(ModEffects.MINDFUL_EFFECT)
                    )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful_advancement"), existingFileHelper);

            AdvancementHolder luminaireExtract = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.LUMINAIRE_EXTRACT,
                            Component.literal("Deep Blue"),
                            Component.literal("Obtain Luminaire Extract"),
                            null,
                            AdvancementType.TASK,
                            true, true, false
                    )
                    .addCriterion("get_lum", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.LUMINAIRE_EXTRACT.get()))
                    .save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "luminaire_advancement"), existingFileHelper);

        }
    }
}
