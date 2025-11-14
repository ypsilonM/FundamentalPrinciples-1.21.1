package com.ypsi.fundamentalism.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryLevels;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class SpellCategoriesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spellCategories")
                .requires(source -> source.hasPermission(0))
                .then(Commands.literal("get")
                        .executes(context -> getLevels(context.getSource(), context.getSource().getPlayerOrException()))
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> getLevels(context.getSource(), EntityArgument.getPlayer(context, "target")))
                        )
                )
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("category", StringArgumentType.word())
                                        .suggests(SpellCategoriesCommand::suggestCategories)
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, 20))
                                                .executes(context -> setLevel(
                                                        context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "category"),
                                                        IntegerArgumentType.getInteger(context, "level")
                                                ))
                                        )
                                )
                        )
                )
        );
    }
    private static CompletableFuture<Suggestions> suggestCategories(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String[] categories = {
                "createEntity", "usesShoot", "usesSummon", "usesTargeting",
                "hasRecasts", "usesTeleport", "addEffects",
                "createsAoeEntities", "usesMobility", "usesRaycast",
                "usesHealing", "usesPotentiation"
        };

        String currentInput = builder.getRemaining().toLowerCase();

        for (String category : categories) {
            if (category.toLowerCase().startsWith(currentInput)) {
                builder.suggest(category);
            }
        }

        return builder.buildFuture();
    }
    private static int getLevels(CommandSourceStack source, ServerPlayer player) {
        SpellCategoryLevels levels = SpellCategoryProgression.getCategoryLevels(player);

        source.sendSuccess(() -> Component.literal("Categories for " + player.getDisplayName().getString() + ":"), false);

        String[] categories = {
                "createEntity", "usesShoot", "usesSummon", "usesTargeting",
                "hasRecasts", "usesTeleport", "addEffects",
                "createsAoeEntities", "usesMobility", "usesRaycast",
                "usesHealing", "usesPotentiation"
        };

        for (String category : categories) {
            int level = levels.getLevel(category);
            int exp = levels.getExperience(category);
            float progress = levels.getProgress(category);
            int expNeeded = levels.getExpForLevel(level + 1);

            source.sendSuccess(() -> Component.literal(
                    String.format("  %s: Level %d (%d/%d XP - %.1f%%)",
                            SpellCategoryProgression.getCategoryDisplayName(category),
                            level, exp, expNeeded, progress * 100
                    )
            ), false);
        }

        return 1;
    }

    private static int setLevel(CommandSourceStack source, ServerPlayer player, String category, int level) {
        SpellCategoryProgression.setCategoryLevel(player, category, level);
        SpellCategoryProgression.setCategoryExperience(player, category, 0);

        source.sendSuccess(() -> Component.literal(
                "Set " + SpellCategoryProgression.getCategoryDisplayName(category) +
                        " to level " + level + " for " + player.getDisplayName().getString()
        ), true);

        return 1;
    }

    private static int addExperience(CommandSourceStack source, ServerPlayer player, String category, int amount) {
        int oldLevel = SpellCategoryProgression.getCategoryLevel(player, category);
        SpellCategoryProgression.addCategoryExperience(player, category, amount);
        int newLevel = SpellCategoryProgression.getCategoryLevel(player, category);

        source.sendSuccess(() -> Component.literal(
                "Added " + amount + " XP to " + SpellCategoryProgression.getCategoryDisplayName(category) +
                        " for " + player.getDisplayName().getString() +
                        (newLevel > oldLevel ? " (Level up! " + oldLevel + " → " + newLevel + ")" : "")
        ), true);

        return 1;
    }
}
