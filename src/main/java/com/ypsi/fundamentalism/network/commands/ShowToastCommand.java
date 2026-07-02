package com.ypsi.fundamentalism.network.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.ypsi.fundamentalism.gui.PrincipleLevelUpToast;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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
