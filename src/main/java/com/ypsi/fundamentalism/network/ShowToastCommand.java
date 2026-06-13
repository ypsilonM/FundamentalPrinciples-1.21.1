package com.ypsi.fundamentalism.network;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.gui.PrincipleLevelUpToast;
import com.ypsi.fundamentalism.util.Principles;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class ShowToastCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("showToast")
                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("level", IntegerArgumentType.integer(0, 20))
                                                .executes(context -> setExhaustion(
                                                        context.getSource(),
                                                        IntegerArgumentType.getInteger(context, "level")
                                                ))
                                )
        );
    }
    private static int setExhaustion(CommandSourceStack source,int level) {
        Minecraft.getInstance().getToasts().addToast(new PrincipleLevelUpToast("createEntity", level));
        return 1;
    }
}
