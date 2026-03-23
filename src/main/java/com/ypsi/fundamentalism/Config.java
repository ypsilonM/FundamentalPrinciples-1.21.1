package com.ypsi.fundamentalism;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue UNIQUE_SPELLBOOKS = BUILDER
            .comment("Whether unique spellbooks should have locked spells. Default: false")
            .define("lockedSpells", false);
    private static final ModConfigSpec.DoubleValue FATIGUE_GENERATOR = BUILDER
            .comment("The multiplier to accumulated fatigue.")
            .defineInRange("fatigueMultiplier", 1.0, 0.0, 10.0);
    public static final ModConfigSpec.ConfigValue<List<? extends Double>> FATIGUE_PENALTIES = BUILDER
            .comment("Fatigue spell power multipliers: Fatigue [0,1,2,3,4] -> DEFAULT: [1.00, 0.90, 0.75, 0.50, 0.20]")
            .defineList("fatigueMultipliers",
                    () -> Arrays.asList(1.00, 0.90, 0.75, 0.50, 0.20),
                    obj -> {
                        if (!(obj instanceof Double)) return false;
                        double value = (Double) obj;
                        return value >= 0.0 && value <= 10.0;
                    });

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> DOMINAN_LEVELS = BUILDER
            .comment("Level required for each principle to use dominan spells according to their rarity. Lvl [0-20]")
            .comment("[COMMON,UNCOMMON,RARE,EPIC,LEGENDARY] -> DEFAULT: [0, 5, 8, 12, 15]")
            .defineList("dominanMinLevels",
                    () -> Arrays.asList(0, 5, 8, 12, 15),
                    obj -> {
                        if (!(obj instanceof Integer)) return false;
                        double value = (Integer) obj;
                        return value >= 0.0 && value <= 20.0;
                    });


    private static final ModConfigSpec.BooleanValue RESTRICTED_INSCRIPTION = BUILDER
            .comment("Whether spell inscription should be resctricted to the spellbook level. Default: true")
            .define("restrictedInscription", true);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> MANA_PENALTIES = BUILDER
            .comment("Mana addition per exhaustion level: Fatigue [0,1,2,3,4] -> DEFAULT: [0.00, 0.50, 0.80, 1.20, 1.60]")
            .defineList("manaAdditionMultipliers",
                    () -> Arrays.asList(0.00, 0.50, 0.80, 1.20, 1.60),
                    obj -> {
                        if (!(obj instanceof Double)) return false;
                        double value = (Double) obj;
                        return value >= 0.0 && value <= 10.0;
                    });

//
//    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
//            .comment("What you want the introduction message to be for the magic number")
//            .define("magicNumberIntroduction", "The magic number is... ");
//
//    // a list of strings that are treated as resource locations for items
//    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
//            .comment("A list of items to log on common setup.")
//            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean lockedSpells;
    public static double fatigueGen;
    public static List<Double> fatiguePenalties;
    public static List<Double> manaPenalties;
    public static List<Integer> dominanLvls;

    public static boolean restrictedInsc;

    public static String magicNumberIntroduction;
    public static Set<Item> items;

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        lockedSpells = UNIQUE_SPELLBOOKS.get();
        fatigueGen = FATIGUE_GENERATOR.get();
        fatiguePenalties = (List<Double>) FATIGUE_PENALTIES.get();
        manaPenalties = (List<Double>) MANA_PENALTIES.get();
        dominanLvls = (List<Integer>) DOMINAN_LEVELS.get();

        restrictedInsc = RESTRICTED_INSCRIPTION.get();

//        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();
//
//        // convert the list of strings into a set of items
//        items = ITEM_STRINGS.get().stream()
//                .map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName)))
//                .collect(Collectors.toSet());
    }
}
