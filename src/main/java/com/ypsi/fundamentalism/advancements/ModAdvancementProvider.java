package com.ypsi.fundamentalism.advancements;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.advancements.triggers.PrinciplesLevelTrigger;
import com.ypsi.fundamentalism.advancements.triggers.YpTriggers;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.util.Principles;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.advancements.*;
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

            AdvancementHolder rootPrinciples = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.FUNDAMENTALISM_SCROLL.get(),
                            Component.literal("Principles"),
                            Component.literal("Follow the path of the scholar"),
                            ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/adventure.png"),
                            AdvancementType.TASK,
                            false, false, false
                    )
                    .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                    .save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "root_principles"), existingFileHelper);


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

            //ALL PRINCIPLES

            AdvancementHolder principleCONCENTRATIO = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.WATER_BUCKET,
                            Component.literal("Concentratio"),
                            Component.literal("Reach the highest knowledge of Concentratio Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalConcentratio",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.CONCENTRATIO, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "concentratio_advancement"), existingFileHelper);

            AdvancementHolder principlePOTENTIA = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.ARROW,
                            Component.literal("Potentia"),
                            Component.literal("Reach the highest knowledge of Potentia Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalPotentia",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.POTENTIA, 20)
                    )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "potentia_advancement"), existingFileHelper);

            AdvancementHolder principleVITALE = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.BONE,
                            Component.literal("Vitale"),
                            Component.literal("Reach the highest knowledge of Vitale Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalVitale",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.VITALE, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "vitale_advancement"), existingFileHelper);

            AdvancementHolder principleEXPANSIO = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.LINGERING_POTION,
                            Component.literal("Expansio"),
                            Component.literal("Reach the highest knowledge of Expansio Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalExpansio",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.EXPANSIO, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "expansio_advancement"), existingFileHelper);

            AdvancementHolder principleAPPARITIO = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.ENDER_PEARL,
                            Component.literal("Apparitio"),
                            Component.literal("Reach the highest knowledge of Apparitio Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalApparitio",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.APPARITIO, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "apparitio_advancement"), existingFileHelper);

            AdvancementHolder principlePERCEPTIO = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.OBSERVER,
                            Component.literal("Perceptio"),
                            Component.literal("Reach the highest knowledge of Perceptio Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalPerceptio",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.PERCEPTIO, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "perceptio_advancement"), existingFileHelper);

            AdvancementHolder principleLOCUS = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.TARGET,
                            Component.literal("Locus"),
                            Component.literal("Reach the highest knowledge of Locus Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalLocus",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.LOCUS, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "locus_advancement"), existingFileHelper);

            AdvancementHolder principleREPETITIO = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.REPEATER,
                            Component.literal("Repetitio"),
                            Component.literal("Reach the highest knowledge of Repetitio Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalRepetitio",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.REPETITIO, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "repetitio_advancement"), existingFileHelper);

            AdvancementHolder principlePERTINACIA = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.POTION,
                            Component.literal("Pertinacia"),
                            Component.literal("Reach the highest knowledge of Pertinacia Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalPertinacia",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.PERTINACIA, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "pertinacia_advancement"), existingFileHelper);

            AdvancementHolder principleMOTUS = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.ELYTRA,
                            Component.literal("Motus"),
                            Component.literal("Reach the highest knowledge of Motus Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalMotus",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.MOTUS, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "motus_advancement"), existingFileHelper);

            AdvancementHolder principleREMEDIUM = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.TOTEM_OF_UNDYING,
                            Component.literal("Remedium"),
                            Component.literal("Reach the highest knowledge of Remedium Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalRemedium",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.REMEDIUM, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "remedium_advancement"), existingFileHelper);

            AdvancementHolder principleAUGERE = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.GOLDEN_SWORD,
                            Component.literal("Augere"),
                            Component.literal("Reach the highest knowledge of Augere Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalAugere",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.AUGERE, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "augere_advancement"), existingFileHelper);

            AdvancementHolder principleCERTUM = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.CRYING_OBSIDIAN,
                            Component.literal("Certum"),
                            Component.literal("Reach the highest knowledge of Certum Principle"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalCertum",
                            new Criterion<>(
                                    YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get(),
                                    PrinciplesLevelTrigger.Instance.levelAtLeast(Principles.CERTUM, 20)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "certum_advancement"), existingFileHelper);
        }
    }
}
