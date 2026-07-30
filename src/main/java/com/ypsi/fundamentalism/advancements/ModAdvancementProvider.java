package com.ypsi.fundamentalism.advancements;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.advancements.triggers.PrinciplesLevelTrigger;
import com.ypsi.fundamentalism.advancements.triggers.TecnhiqueTrigger;
import com.ypsi.fundamentalism.advancements.triggers.YpTriggers;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Techniques;
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

            //LAW OF REGRESSION
            AdvancementHolder getLawOfRegression1 = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.ENCHANTED_GOLDEN_APPLE,
                            Component.literal("First-aid kit I"),
                            Component.literal("Learn Law of Regression"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("firstGoalRegression",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REGRESSION, 1)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "first_regression_advancement"), existingFileHelper);
            AdvancementHolder getLawOfRegression2 = Advancement.Builder.advancement()
                    .parent(getLawOfRegression1)
                    .display(
                            Items.ENCHANTED_GOLDEN_APPLE,
                            Component.literal("First-aid kit II"),
                            Component.literal("Understand Law of Regression"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("secondGoalRegression",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REGRESSION, 2)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "second_regression_advancement"), existingFileHelper);
            AdvancementHolder getLawOfRegression3 = Advancement.Builder.advancement()
                    .parent(getLawOfRegression2)
                    .display(
                            Items.ENCHANTED_GOLDEN_APPLE,
                            Component.literal("First-aid kit III"),
                            Component.literal("Refine Law of Regression"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("thirdGoalRegression",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REGRESSION, 3)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "third_regression_advancement"), existingFileHelper);
            AdvancementHolder getLawOfRegression4 = Advancement.Builder.advancement()
                    .parent(getLawOfRegression3)
                    .display(
                            Items.ENCHANTED_GOLDEN_APPLE,
                            Component.literal("First-aid kit IV"),
                            Component.literal("Perfect Law of Regression"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("fourthGoalRegression",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REGRESSION, 4)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fourth_regression_advancement"), existingFileHelper);

            //SAEPTUM
            AdvancementHolder getSaeptum = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.HEART_OF_THE_SEA,
                            Component.literal("Unbreakable Trap"),
                            Component.literal("Learn Saeptum Technique"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalSaeptum",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.SAEPTUM, 1)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "saeptum_advancement"), existingFileHelper);

            //REINFORCEMENT LEVELS
            AdvancementHolder getReinforcement1 = Advancement.Builder.advancement()
                    .parent(rootPrinciples)
                    .display(
                            Items.SHIELD,
                            Component.literal("Battlemage I"),
                            Component.literal("Learn Mana Reinforcement Technique"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalFirstReinforcement",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REINFORCEMENT, 1)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "first_reinforcement_advancement"), existingFileHelper);
            AdvancementHolder getReinforcement2 = Advancement.Builder.advancement()
                    .parent(getReinforcement1)
                    .display(
                            Items.SHIELD,
                            Component.literal("Battlemage II"),
                            Component.literal("Upgrade your Mana Reinforcement"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalSecondReinforcement",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REINFORCEMENT, 2)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "second_reinforcement_advancement"), existingFileHelper);
            AdvancementHolder getReinforcement3 = Advancement.Builder.advancement()
                    .parent(getReinforcement2)
                    .display(
                            Items.SHIELD,
                            Component.literal("Battlemage III"),
                            Component.literal("Understand better Mana Reinforcement"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalThirdReinforcement",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REINFORCEMENT, 3)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "third_reinforcement_advancement"), existingFileHelper);
            AdvancementHolder getReinforcement4 = Advancement.Builder.advancement()
                    .parent(getReinforcement3)
                    .display(
                            ItemRegistry.HEAVY_CHAIN.get(),
                            Component.literal("Battlemage IV"),
                            Component.literal("Refine Mana Reinforcement"),
                            null,
                            AdvancementType.GOAL,
                            true, true, false
                    ).addCriterion("goalFourthReinforcement",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REINFORCEMENT, 4)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fourth_reinforcement_advancement"), existingFileHelper);
            AdvancementHolder getReinforcement5 = Advancement.Builder.advancement()
                    .parent(getReinforcement4)
                    .display(
                            ItemRegistry.HEAVY_CHAIN.get(),
                            Component.literal("Battlemage V"),
                            Component.literal("Fully master Mana Reinforcement"),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false
                    ).addCriterion("goalFifthReinforcement",
                            new Criterion<>(
                                    YpTriggers.TECHNIQUES_TRIGGER_SUPPLIER.get(),
                                    TecnhiqueTrigger.Instance.hasAcquired(Techniques.REINFORCEMENT, 5)
                            )).save(saver, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fifth_reinforcement_advancement"), existingFileHelper);




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
                    .parent(getLawOfRegression4)
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
                    .parent(getReinforcement5)
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
