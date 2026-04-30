package com.ypsi.fundamentalism.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class ExhaustionCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("exhaustion")
                .requires(source -> source.hasPermission(2)) // Solo ops
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.players())
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 4))
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(0))
                                                .executes(context -> setExhaustion(
                                                        context.getSource(),
                                                        EntityArgument.getPlayers(context, "target"),
                                                        IntegerArgumentType.getInteger(context, "level"),
                                                        IntegerArgumentType.getInteger(context, "amount")
                                                ))
                                        )
                                )
                        )
                )
        );
    }
    private static int setExhaustion(CommandSourceStack source, Collection<ServerPlayer> targets, int level, int amount) {
        for (ServerPlayer player : targets) {
            FatigueManager.setFatigueLevel(player, level);
            //player.setData(YpsAttachments.LEVEL_EXHAUSTION, level);
            int maxEx = getMaxExPerLevel(level, player);
            int clampedAmount = Mth.clamp(amount, 0, maxEx);
            FatigueManager.setFatigueAmount(player, clampedAmount);
           // player.setData(YpsAttachments.CURRENT_EXHAUSTION, clampedAmount);
            //SyncExhaustionLevelPacket.sendToPlayer(player, level);
            //SyncExhaustionPacket.sendToPlayer(player, clampedAmount);
            player.getPersistentData().putInt("exhaustionTickCounter", 0);
            player.getPersistentData().putInt("reduceCounter", 0);
        }
        if (targets.size() == 1) {
            ServerPlayer player = targets.iterator().next();
            source.sendSuccess(() -> Component.literal(
                    "Set exhaustion level " + level + " with amount " + amount + "/" + getMaxExPerLevel(level, player) +
                            " for " + player.getDisplayName().getString()
            ), true);
        } else {
            source.sendSuccess(() -> Component.literal(
                    "Set exhaustion level " + level + " with amount " + amount + " for " + targets.size() + " players"
            ), true);
        }

        return targets.size();
    }

    public static int getMaxExPerLevel(int level, Player player){
        return (int) ((switch (level){
            case 0,4 -> 50;
            case 1,3 -> 100;
            case 2 -> 200;
            default -> 100;
        })+player.getAttributeValue(YpsAttributes.MAX_FATIGUE));
    }
}
