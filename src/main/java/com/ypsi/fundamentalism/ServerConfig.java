package com.ypsi.fundamentalism;

import java.util.Arrays;
import java.util.List;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC;

    //Spellbooks
    public static ModConfigSpec.BooleanValue UNIQUE_SPELLBOOKS;
    public static ModConfigSpec.BooleanValue SPELLBOOK_LEVELS;
    public static ModConfigSpec.BooleanValue RESTRICTED_INSCRIPTION;
    public static ModConfigSpec.IntValue XP_SPELLBOOK_MULTIPLIER;

    //Dominan Spells
    public static ModConfigSpec.ConfigValue<List<? extends Integer>> DOMINAN_LEVELS;
    public static ModConfigSpec.IntValue DOMINAN_PRINCIPLES;

    //Fatigue System
    public static ModConfigSpec.BooleanValue FATIGUE_SYSTEM;
    public static ModConfigSpec.ConfigValue<List<? extends Integer>> MAX_FATIGUE_PER_LVL;
    public static ModConfigSpec.DoubleValue FATIGUE_MULTIPLIER;
    public static ModConfigSpec.ConfigValue<List<? extends Double>> FATIGUE_SPELLPOWER;
    public static ModConfigSpec.DoubleValue MANA_REGEN_DEBUFF;

    //Principles
    public static ModConfigSpec.BooleanValue PRINCIPLES_SYSTEM;
    public static ModConfigSpec.DoubleValue BASE_PRINCIPLE_POWER;
    public static ModConfigSpec.DoubleValue BASE_PRINCIPLE_ADD;
    public static ModConfigSpec.BooleanValue SUBCATEGORIES_HALF;
    public static ModConfigSpec.IntValue XP_PRINCIPLE_MULTIPLIER;

        //1.- Concentratio
        public static ModConfigSpec.BooleanValue ACTIVE_CONCENTRATIO;
        public static ModConfigSpec.IntValue ADD_MANA;
        //2.- Potentia
        public static ModConfigSpec.BooleanValue ACTIVE_POTENTIA;
        public static ModConfigSpec.DoubleValue BASE_ACCURACY;
        public static ModConfigSpec.DoubleValue ADD_ACCURACY;
        //3.- Vitale
        public static ModConfigSpec.BooleanValue ACTIVE_VITALE;
        public static ModConfigSpec.DoubleValue COOLDOWN_REDUCTION_ADD;
        //4.- Expansio
        public static ModConfigSpec.BooleanValue ACTIVE_EXPANSIO;
        public static ModConfigSpec.DoubleValue BASE_RADIUS;
        public static ModConfigSpec.DoubleValue ADD_RADIUS;
        //5.- Apparitio
        public static ModConfigSpec.BooleanValue ACTIVE_APPARITIO;
        public static ModConfigSpec.DoubleValue ADD_PERCENTAGE_CHANCE;
        //6.- Repetitio
        public static ModConfigSpec.BooleanValue ACTIVE_REPETITIO;
        public static ModConfigSpec.DoubleValue ADD_SUCCESS_CHANCE;
        //7.- Perceptio
        public static ModConfigSpec.BooleanValue ACTIVE_PERCEPTIO;
        public static ModConfigSpec.DoubleValue ADD_DISTANCE;
        //8.- Locus
        public static ModConfigSpec.BooleanValue ACTIVE_LOCUS;
        public static ModConfigSpec.DoubleValue BASE_PERCENTAGE_DISTANCE;
        public static ModConfigSpec.DoubleValue ADD_PERCENTAGE_DISTANCE;
        //9 - Certum
        public static ModConfigSpec.BooleanValue ACTIVE_CERTUM;
        public static ModConfigSpec.ConfigValue<List<? extends Double>> MANA_ADD_FATIGUE;
        public static ModConfigSpec.DoubleValue MANA_REDUCTION_BUFF;
        //10.- Remedium
        public static ModConfigSpec.BooleanValue ACTIVE_REMEDIUM;
        public static ModConfigSpec.DoubleValue BASE_FOOD_PTS;
        public static ModConfigSpec.DoubleValue SUB_FOOD_PTS;
        //11.- Motus
        //12.- Augere
        //13.- Pertinacia

    public static ModConfigSpec.DoubleValue MOB_STAR_ALIGNMENT;
    public static ModConfigSpec.DoubleValue ALIGNMENT_MULTIPLIER;

    static{
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("|                     FUNDAMENTAL PRINCIPLES CONFIG                |");
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("");

        BUILDER.push("spellbooks");
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

            XP_SPELLBOOK_MULTIPLIER = BUILDER
                    .worldRestart()
                    .comment("Multiplier for experience added to spellbook.")
                    .defineInRange("spellbookXpMultiplier", 1, 0, 10);

        }
        BUILDER.pop();

        BUILDER.push("dominan");
        {
            DOMINAN_LEVELS = BUILDER
                    .worldRestart()
                    .comment("Level required for each principle to use dominan spells according to their rarity.")
                    .comment("Order: [COMMON, UNCOMMON, RARE, EPIC, LEGENDARY] -> DEFAULT: [0, 5, 8, 12, 15]")
                    .defineList("dominanMinLevels",
                            () -> Arrays.asList(0, 5, 8, 12, 15),
                            obj -> obj instanceof Integer && (int) obj >= 0 && (int) obj <= 20);

            DOMINAN_PRINCIPLES = BUILDER
                    .worldRestart()
                    .comment("The amount of principles a spell should have to be considered a DOMINAN spell.")
                    .defineInRange("principlesForDominan", 4, 0, 13);

        }
        BUILDER.pop();

        BUILDER.push("fatigue");
        {

            FATIGUE_SYSTEM = BUILDER
                    .worldRestart()
                    .comment("Whether all the Fatigue System should be active.")
                    .define("fatigueSystem", true);

            MAX_FATIGUE_PER_LVL = BUILDER
                    .worldRestart()
                    .comment("Max amount of fatigue pts per fatigue stage.")
                    .comment("DEFAULT: [0, 5, 8, 12, 15]")
                    .defineList("maxFatigue",
                            () -> Arrays.asList(50, 100, 200, 100, 50),
                            obj -> obj instanceof Integer && (int) obj >= 0 && (int) obj <= 10000000);

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

            MANA_REGEN_DEBUFF = BUILDER
                    .worldRestart()
                    .comment("The percentage multiplier of mana regen reduction per fatigue level. (DEFAULT -15% per fatigue level -> -60% in 4th fatigue level.")
                    .defineInRange("fatigueManaRegen", 0.15, 0, 0.25);

        }
        BUILDER.pop();

        BUILDER.push("principles_system");
        {
            PRINCIPLES_SYSTEM = BUILDER
                    .worldRestart().comment("Whether all about PRINCIPLES SYSTEM should be active.")
                    .define("principles", true);

            BASE_PRINCIPLE_POWER = BUILDER
                    .worldRestart()
                    .comment("Defines the base SP percentage reduction (or addition) applied to spells at the start per Principle.")
                    .defineInRange("basePower", -0.1, -0.9, 100);
            BASE_PRINCIPLE_ADD = BUILDER
                    .worldRestart()
                    .comment("Defines the SP addition per Principle level.")
                    .defineInRange("levelAddition", 0.01, 0.0, 100);
            SUBCATEGORIES_HALF = BUILDER
                    .worldRestart()
                    .comment("Whether Subcategories (Potentia, Vitale and Expansio) should be affected by half the power of buff/debuff of the originals.")
                    .define("subprinciples", true);
            XP_PRINCIPLE_MULTIPLIER = BUILDER
                    .worldRestart()
                    .comment("Experience multiplier for Priciples leveling.")
                    .defineInRange("principleXpMultiplier", 1, 0, 10);


            //1.-
            ACTIVE_CONCENTRATIO = BUILDER
                    .worldRestart()
                    .comment("Whether CONCENTRATIO passive should be active.")
                    .define("concentratioPassive", true);
            ADD_MANA = BUILDER
                    .worldRestart().comment("Mana addition per Concentratio level.")
                    .defineInRange("manaAdd", 20, 0, 10000);
            //2.-
            ACTIVE_POTENTIA = BUILDER
                    .worldRestart()
                    .comment("Whether POTENTIA passive should be active.")
                    .define("potentiaPassive", true);
            BASE_ACCURACY = BUILDER
                    .worldRestart().comment("Base accuracy for projectiles.")
                    .defineInRange("accuracyBase", 0.4, 0, 1);
            ADD_ACCURACY = BUILDER
                    .worldRestart().comment("Accuracy Addition per Potentia Level.")
                    .defineInRange("accuradyAdd", 0.03, 0, 1);
            //3.-
            ACTIVE_VITALE = BUILDER
                    .worldRestart()
                    .comment("Whether VITALE passive should be active.")
                    .define("vitalePassive", true);
            COOLDOWN_REDUCTION_ADD = BUILDER
                    .worldRestart().comment("Percentage Cooldown reduction per Vitale level.")
                    .defineInRange("cdrAdd", 0.045, 0, 1);
            //4.-
            ACTIVE_EXPANSIO = BUILDER
                    .worldRestart()
                    .comment("Whether EXPANSIO passive should be active.")
                    .define("expansioPassive", true);
            BASE_RADIUS = BUILDER
                    .worldRestart().comment("Base radius multiplier for AoE entities")
                    .defineInRange("aoeBaseRadius", 0.5, 0, 10);
            ADD_RADIUS = BUILDER
                    .worldRestart().comment("Radius addition per Expansio level.")
                    .defineInRange("aoeAddRadius", 0.05, 0, 10);
            //5.-
            ACTIVE_APPARITIO = BUILDER
                    .worldRestart()
                    .comment("Whether APPARITIO passive should be active.")
                    .define("apparitioPassive", true);
            ADD_PERCENTAGE_CHANCE = BUILDER
                    .worldRestart()
                    .comment("Percentage addition for successful teleport per Apparitio Level.")
                    .defineInRange("successTpAdd", 0.025, 0,1);
            //6.-
            ACTIVE_REPETITIO = BUILDER
                    .worldRestart()
                    .comment("Whether REPETITIO passive should be active.")
                    .define("repetitioPassive", true);
            ADD_SUCCESS_CHANCE = BUILDER
                    .worldRestart().comment("Chance addition to get an additional cast per Repetitio level.")
                    .defineInRange("addChanceCast", 0.05, 0, 1);
            //7.-
            ACTIVE_PERCEPTIO = BUILDER
                    .worldRestart()
                    .comment("Whether PERCEPTIO passive should be active.")
                    .define("perceptioPassive", true);
            ADD_DISTANCE = BUILDER
                    .worldRestart().comment("Range addition per Perceptio level")
                    .defineInRange("addRange", 1.5, 0, 10);
            //8.-
            ACTIVE_LOCUS = BUILDER
                    .worldRestart()
                    .comment("Whether LOCUS passive should be active.")
                    .define("locusPassive", true);
            BASE_PERCENTAGE_DISTANCE = BUILDER
                    .worldRestart().comment("Percentage base distance for targeting")
                    .defineInRange("percentageBaseRange", 0.40, 0, 1);
            ADD_PERCENTAGE_DISTANCE = BUILDER
                    .worldRestart().comment("Percentage addition for targeting per Locus level.")
                    .defineInRange("percentageAddRange", 0.02, 0, 1);
            //9.-
            ACTIVE_CERTUM = BUILDER
                    .worldRestart()
                    .comment("Whether CERTUM passive should be active.")
                    .define("certumPassive", true);
            MANA_ADD_FATIGUE = BUILDER
                    .worldRestart()
                    .comment("Fatigue Mana Addition multipliers for Certum: ")
                    .comment("Fatigue Levels: [0,1,2,3,4] -> DEFAULT: [0.00, 0.50, 0.80, 1.20, 1.60]")
                    .defineList("fatigueManaAdditionMultipliers",
                            () -> Arrays.asList(0.00, 0.50, 0.80, 1.20, 1.60),
                            obj -> obj instanceof Double && (double) obj >= 0.0 && (double) obj <= 10.0);
            MANA_REDUCTION_BUFF = BUILDER
                    .worldRestart().comment("Mana percentage reduction debuff per CERTUM level.")
                    .defineInRange("manaDebuffReduction", 0.02, 0,1);

            //10.-
            ACTIVE_REMEDIUM = BUILDER
                    .worldRestart()
                    .comment("Whether REMEDIUM passive should be active.")
                    .define("remediumPassive", true);
            BASE_FOOD_PTS = BUILDER
                    .worldRestart().comment("Base amount of food pts to be consumed per healing point.")
                    .defineInRange("baseFoodPts", 3.0, 0.0, 100.0);
            SUB_FOOD_PTS = BUILDER
                    .worldRestart().comment("Points to be reduced from the base amount per Remedium level")
                    .defineInRange("subFoodPts",0.125, 0, 10);


        }
        BUILDER.pop();

        BUILDER.push("others");
        {
            MOB_STAR_ALIGNMENT = BUILDER
                    .worldRestart()
                    .comment("Chance of a melee spellcaster mob of landing a star alignment.")
                    .defineInRange("mobStarAlignment", 0.05, 0, 1);
            ALIGNMENT_MULTIPLIER = BUILDER
                    .worldRestart()
                    .comment("Damage Multiplier for star alignment critic.")
                    .defineInRange("alignmentDamageMultiplier", 2.0,0.0,100.0);

        }
        BUILDER.pop();

        SPEC = BUILDER.build();
    }


    public static boolean lockedSpells;
    public static boolean restrictedInsc;
    public static boolean spellbookLevel;
    public static int spellbookXPMultiplier;

    public static List<Integer> dominanLvls;
    public static int dominanPrinciples;

    public static boolean fatigueSystem;
    public static double fatigueMultiplier;
    public static List<Double> fatigueSpellpowerMultipliers;
    public static double fatigueManaRegen;

    public static boolean principlesSYSTEM;
    public static double basePower;
    public static double baseAddition;
    public static boolean subcategories;
    public static int principlesXPMultiplier;

    public static boolean concentratioActive;
    public static int manaAdd;

    public static boolean potentiaActive;
    public static double baseAccuracy;
    public static double addAccuracy;

    public static boolean vitaleActive;
    public static double crdAdd;

    public static boolean expansioActive;
    public static double aoeBaseRadius;
    public static double aoeBaseAdd;

    public static boolean apparitioActive;
    public static double successTpAdd;

    public static boolean repetitioActive;
    public static double addChanceCast;

    public static boolean perceptioActive;
    public static double addRange;

    public static boolean locusActive;
    public static double percentageBaseRange;
    public static double percentageAddRange;

    public static boolean certumActive;
    public static List<Double> fatigueManaAdditionMultipliers;
    public static double manaDebuffReduction;

    public static boolean remediumActive;
    public static double baseFoodPts;
    public static double subFoodPts;

    public static double mobStarAlignment;
    public static double starAlignmentMultiplier;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent.Loading event) {

        lockedSpells = UNIQUE_SPELLBOOKS.get();
        restrictedInsc = RESTRICTED_INSCRIPTION.get();
        spellbookLevel = SPELLBOOK_LEVELS.get();
        spellbookXPMultiplier = XP_SPELLBOOK_MULTIPLIER.get();

        dominanLvls = (List<Integer>) DOMINAN_LEVELS.get();
        dominanPrinciples = DOMINAN_PRINCIPLES.get();

        fatigueSystem = FATIGUE_SYSTEM.get();
        fatigueMultiplier = FATIGUE_MULTIPLIER.get();
        fatigueSpellpowerMultipliers = (List<Double>) FATIGUE_SPELLPOWER.get();
        fatigueManaRegen = MANA_REGEN_DEBUFF.get();

        principlesSYSTEM = PRINCIPLES_SYSTEM.get();
        basePower = BASE_PRINCIPLE_POWER.get();
        baseAddition = BASE_PRINCIPLE_ADD.get();
        subcategories = SUBCATEGORIES_HALF.get();
        principlesXPMultiplier = XP_PRINCIPLE_MULTIPLIER.get();
        //
            concentratioActive = ACTIVE_CONCENTRATIO.get();
            manaAdd = ADD_MANA.get();

            potentiaActive = ACTIVE_POTENTIA.get();
            baseAccuracy = BASE_ACCURACY.get();
            addAccuracy = ADD_ACCURACY.get();

            vitaleActive = ACTIVE_VITALE.get();
            crdAdd = COOLDOWN_REDUCTION_ADD.get();

            expansioActive = ACTIVE_EXPANSIO.get();
            aoeBaseRadius = BASE_RADIUS.get();
            aoeBaseAdd = ADD_RADIUS.get();

            apparitioActive = ACTIVE_APPARITIO.get();
            successTpAdd = ADD_PERCENTAGE_CHANCE.get();

            repetitioActive = ACTIVE_REPETITIO.get();
            addChanceCast = ADD_SUCCESS_CHANCE.get();

            perceptioActive = ACTIVE_PERCEPTIO.get();
            addRange = ADD_DISTANCE.get();

            locusActive = ACTIVE_LOCUS.get();
            percentageBaseRange = BASE_PERCENTAGE_DISTANCE.get();
            percentageAddRange = ADD_PERCENTAGE_DISTANCE.get();

            certumActive = ACTIVE_CERTUM.get();
            fatigueManaAdditionMultipliers = (List<Double>) MANA_ADD_FATIGUE.get();
            manaDebuffReduction = MANA_REDUCTION_BUFF.get();

            remediumActive = ACTIVE_REMEDIUM.get();
            baseFoodPts = BASE_FOOD_PTS.get();
            subFoodPts = SUB_FOOD_PTS.get();

        mobStarAlignment = MOB_STAR_ALIGNMENT.get();
        starAlignmentMultiplier = ALIGNMENT_MULTIPLIER.get();

    }
}
