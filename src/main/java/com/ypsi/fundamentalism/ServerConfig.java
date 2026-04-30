package com.ypsi.fundamentalism;

import java.util.Arrays;
import java.util.List;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC;

    //Other systems
    public static ModConfigSpec.BooleanValue UNIQUE_SPELLBOOKS;
    public static ModConfigSpec.BooleanValue SPELLBOOK_LEVELS;
    public static ModConfigSpec.BooleanValue RESTRICTED_INSCRIPTION;

    //Fatigue System
    public static ModConfigSpec.BooleanValue FATIGUE_SYSTEM;
    public static ModConfigSpec.DoubleValue FATIGUE_MULTIPLIER;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> FATIGUE_SPELLPOWER;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> FATIGUE_MANA;

    //Principles
    public static ModConfigSpec.BooleanValue MODIFIED_HEALING;
    public static ModConfigSpec.DoubleValue BASE_PRINCIPLE_POWER;
    public static ModConfigSpec.DoubleValue BASE_PRINCIPLE_ADD;
    public static ModConfigSpec.BooleanValue SUBCATEGORIES_ADD;

    //Dominan Spells
    public static ModConfigSpec.ConfigValue<List<? extends Integer>> DOMINAN_LEVELS;


    static{
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("|                     FUNDAMENTAL PRINCIPLES CONFIG                |");
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("");

        BUILDER.push("systems");
        {
            UNIQUE_SPELLBOOKS = BUILDER
                    .worldRestart()
                    .comment("Whether unique spellbooks should have locked spells. Default: false")
                    .define("lockedSpells", false);

            SPELLBOOK_LEVELS = BUILDER
                    .worldRestart()
                    .comment("Whether leveling spellbook mechanic should be active. Default: false")
                    .comment("(!MPORTANT) Slot changes will not be applied to existing spellbooks generated before turning the config off.")
                    .define("spellbookLeveling", true);

            RESTRICTED_INSCRIPTION = BUILDER
                    .worldRestart()
                    .comment("Whether spell inscription should be restricted to spellbook level. Default: true")
                    .define("restrictedInscription", true);
        }
        BUILDER.pop();

        BUILDER.push("fatigue");
        {
            FATIGUE_SYSTEM = BUILDER
                    .worldRestart()
                    .comment("Whether all the Fatigue System should be active.")
                    .define("fatigueSystem", true);

            FATIGUE_MULTIPLIER = BUILDER
                    .worldRestart()
                    .comment("The multiplier to produce fatigue.")
                    .defineInRange("fatigueMultiplier", 1.0, 0.0, 10.0);

            FATIGUE_SPELLPOWER = BUILDER
                    .worldRestart()
                    .comment("Fatigue Spell Power Debuff multipliers: ")
                    .comment("Fatigue Levels: [0,1,2,3,4] -> DEFAULT: [1.00, 0.90, 0.75, 0.50, 0.20]")
                    .defineList("fatigueSpellpowerMultipliers",
                            () -> Arrays.asList(1.00, 0.90, 0.75, 0.50, 0.20),
                            obj -> obj instanceof Double && (double) obj >= 0.0 && (double) obj <= 10.0);

            FATIGUE_MANA = BUILDER
                    .worldRestart()
                    .comment("Fatigue Mana Addition multipliers: ")
                    .comment("Fatigue Levels: [0,1,2,3,4] -> DEFAULT: [0.00, 0.50, 0.80, 1.20, 1.60]")
                    .defineList("fatigueManaAdditionMultipliers",
                            () -> Arrays.asList(0.00, 0.50, 0.80, 1.20, 1.60),
                            obj -> obj instanceof Double && (double) obj >= 0.0 && (double) obj <= 10.0);
        }
        BUILDER.pop();

        BUILDER.push("principles_system");
        {
            BASE_PRINCIPLE_POWER = BUILDER
                    .worldRestart()
                    .comment("Defines the base SP percentage reduction (or addition) applied to spells at the start per Principle.")
                    .defineInRange("basePower", -0.1, -0.9, 100);
            BASE_PRINCIPLE_ADD = BUILDER
                    .worldRestart()
                    .comment("Defines the SP addition per Principle level.")
                    .defineInRange("levelAddition", 0.01, 0.0, 100);
            SUBCATEGORIES_ADD = BUILDER
                    .worldRestart()
                    .comment("Whether Potentia, Vitale and Expansio should be considered as subcategories and the power buff/debuff should be half of the originals.")
                    .define("subprinciples", true);


            DOMINAN_LEVELS = BUILDER
                    .worldRestart()
                    .comment("Level required for each principle to use dominan spells according to their rarity.")
                    .comment("Order: [COMMON, UNCOMMON, RARE, EPIC, LEGENDARY] -> DEFAULT: [0, 5, 8, 12, 15]")
                    .defineList("dominanMinLevels",
                            () -> Arrays.asList(0, 5, 8, 12, 15),
                            obj -> obj instanceof Integer && (int) obj >= 0 && (int) obj <= 20);



            MODIFIED_HEALING = BUILDER
                    .worldRestart()
                    .comment("Whether spell healing should have the new healing system. Default: true")
                    .define("newRemediumHealing", true);
        }
        BUILDER.pop();

        SPEC = BUILDER.build();
    }


    public static boolean lockedSpells;
    public static boolean restrictedInsc;
    public static boolean spellbookLevel;

    public static boolean fatigueSystem;
    public static double fatigueMultiplier;
    public static List<Double> fatigueSpellpowerMultipliers;
    public static List<Double> fatigueManaAdditionMultipliers;

    public static List<Integer> dominanLvls;
    public static double basePower;
    public static double baseAddition;
    public static boolean subcategories;

    public static boolean newHealing;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        lockedSpells = UNIQUE_SPELLBOOKS.get();
        restrictedInsc = RESTRICTED_INSCRIPTION.get();
        spellbookLevel = SPELLBOOK_LEVELS.get();

        fatigueSystem = FATIGUE_SYSTEM.get();
        fatigueMultiplier = FATIGUE_MULTIPLIER.get();
        fatigueSpellpowerMultipliers = (List<Double>) FATIGUE_SPELLPOWER.get();
        fatigueManaAdditionMultipliers = (List<Double>) FATIGUE_MANA.get();

        basePower = BASE_PRINCIPLE_POWER.get();
        baseAddition = BASE_PRINCIPLE_ADD.get();
        subcategories = SUBCATEGORIES_ADD.get();

        dominanLvls = (List<Integer>) DOMINAN_LEVELS.get();
        newHealing =  MODIFIED_HEALING.get();

    }
}
