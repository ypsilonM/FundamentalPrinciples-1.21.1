package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.extensions.IDataComponentHolderExtension;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)

public class ServerEvents {

    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
//            player.getPersistentData().get()
        }
    }
    public static void ensureFatigueInitialized(Player player) {

    }
}
