package com.ypsi.fundamentalism.network.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class SpellbookLevelCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spellbookLevel")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("level", StringArgumentType.word())
                                        .suggests(SpellbookLevelCommand::suggestLevels)
                                                .executes(context -> setLevel(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "target"),
                                                        StringArgumentType.getString(context, "level")
                                                ))
                                        )
                                )
                        )
        );
    }

    private static CompletableFuture<Suggestions> suggestLevels(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        String[] rarities = {
                "COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC"
        };
        String currentInput = builder.getRemaining().toLowerCase();
        for (String rarity : rarities) {
            if (rarity.toLowerCase().startsWith(currentInput)) {
                builder.suggest(rarity);
            }
        }

        return builder.buildFuture();
    }

    private static int setLevel(CommandSourceStack source, Collection<ServerPlayer> targets, String level) {
        for (ServerPlayer player : targets) {
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                inv.findCurios(Curios.SPELLBOOK_SLOT).forEach(curio -> {
                    ItemStack spellBook = curio.stack();
                    if (spellBook.getItem() instanceof SpellBook) {
                        int rarity = getRarityByName(level);
                        SpellBookComponentHelper.setLevel(spellBook, rarity, player);
                    }
                });
            });
        }
        return targets.size();
    }
    private static int getRarityByName(String level){
        return switch (level.trim().toLowerCase()){
            case "common" -> 1;
            case "uncommon" -> 2;
            case "rare" -> 3;
            case "epic" -> 4;
            case "legendary" -> 5;
            case "mythic" -> 6;
            default -> 1;
        };
    }

}
