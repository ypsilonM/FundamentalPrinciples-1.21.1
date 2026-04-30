package com.ypsi.fundamentalism.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ypsi.fundamentalism.attachments.customAtt.PrinciplesLevelsAttachment;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.concurrent.CompletableFuture;

public class SpellCategoriesCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("principle")
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
                                                        Principles.valueOf(StringArgumentType.getString(context, "category").toUpperCase()),
                                                        IntegerArgumentType.getInteger(context, "level")
                                                ))
                                        )
                                )
                        )
                )
        );
    }
    private static CompletableFuture<Suggestions> suggestCategories(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {

        for(Principles principle : Principles.values()){
            builder.suggest(principle.name().toLowerCase());
        }

        return builder.buildFuture();
    }
    private static int getLevels(CommandSourceStack source, ServerPlayer player) {
        PrinciplesLevelsAttachment levels = PrinciplesProgressionManager.getCategoryLevels(player);

        source.sendSuccess(() -> Component.literal("Categories for " + player.getDisplayName().getString() + ":"), false);

        String[] categories = {
                "createEntity", "usesShoot", "usesSummon", "usesTargeting",
                "hasRecasts", "usesTeleport", "addEffects",
                "createsAoeEntities", "usesMobility", "usesRaycast",
                "usesHealing", "usesPotentiation", "immutable"
        };

        for (String category : categories) {
            int level = levels.getLevel(category);
            int exp = levels.getExperience(category);
            float progress = levels.getProgress(category);
            int expNeeded = levels.getExpForLevel(level + 1);

            source.sendSuccess(() -> Component.literal(
                    String.format("  %s: Level %d (%d/%d XP - %.1f%%)",
                            PrinciplesProgressionManager.getCategoryDisplayName(category),
                            level, exp, expNeeded, progress * 100
                    )
            ), false);
        }

        return 1;
    }

    private static int setLevel(CommandSourceStack source, ServerPlayer player, Principles principle, int level) {
        String technicalName = PrinciplesProgressionManager.getTechnicalName(principle);

        PrinciplesProgressionManager.setCategoryLevel(player, technicalName, level);
        PrinciplesProgressionManager.setCategoryExperience(player, technicalName, 0);

        source.sendSuccess(() -> Component.literal(
                "Set " + PrinciplesProgressionManager.getCategoryDisplayName(technicalName) +
                        " to level " + level + " for " + player.getDisplayName().getString()
        ), true);

        return 1;
    }

    private static int addExperience(CommandSourceStack source, ServerPlayer player, String category, int amount) {
        int oldLevel = PrinciplesProgressionManager.getCategoryLevel(player, category);
        PrinciplesProgressionManager.addCategoryExperience(player, category, amount);
        int newLevel = PrinciplesProgressionManager.getCategoryLevel(player, category);

        source.sendSuccess(() -> Component.literal(
                "Added " + amount + " XP to " + PrinciplesProgressionManager.getCategoryDisplayName(category) +
                        " for " + player.getDisplayName().getString() +
                        (newLevel > oldLevel ? " (Level up! " + oldLevel + " → " + newLevel + ")" : "")
        ), true);

        return 1;
    }
}
