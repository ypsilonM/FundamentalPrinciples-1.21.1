package com.ypsi.fundamentalism.network.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.ypsi.fundamentalism.attachments.EfficiencyManager;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.Collection;

public class EfficiencyCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("efficiency")
                .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.players())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 10))
                                        .executes(context -> setEfficiency(
                                                context.getSource(),
                                                EntityArgument.getPlayers(context, "target"),
                                                IntegerArgumentType.getInteger(context,"level")
                                        ))
                                )
                        )
                    )
        );
    }

    private static int setEfficiency(CommandSourceStack source, Collection<ServerPlayer> targets, int level) {
        for (ServerPlayer player : targets) {
            EfficiencyManager.setLevel(level, player);
        }
        if (targets.size() == 1) {
            ServerPlayer player = targets.iterator().next();
            source.sendSuccess(() -> Component.literal(
                    "Set efficiency level " + level +
                            " for " + player.getDisplayName().getString()
            ), true);
        } else {
            source.sendSuccess(() -> Component.literal(
                    "Set efficiency level " + level + " for " + targets.size() + " players"
            ), true);
        }

        return targets.size();
    }
}
