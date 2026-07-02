package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.*;
import com.ypsi.fundamentalism.attachments.customAtt.PrinciplesLevelsAttachment;
import com.ypsi.fundamentalism.util.Principles;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class YpsAttributesHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {
                server.execute(() -> initializeAttributes(serverPlayer));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server != null) {
                server.execute(() -> initializeAttributes(serverPlayer));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath() && event.getEntity() instanceof ServerPlayer newPlayer) {
            MinecraftServer server = newPlayer.getServer();
            if (server != null) {
                server.execute(() -> initializeAttributes(newPlayer));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        YpsAttributeManager.MANA.cleanup(event.getEntity());
        YpsAttributeManager.FATIGUE.cleanup(event.getEntity());

        YpsAttributeManager.CASTING_MOVESPEED.cleanup(event.getEntity());

    }

    private static void initializeAttributes(ServerPlayer player) {

        int entityLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.CONCENTRATIO);
        YpsAttributeManager.MANA.applyModifier(player, entityLevel);

        int mana_fatigue = FatigueManager.getFatigueLevel(player);
        //int mana_fatigue = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
        YpsAttributeManager.FATIGUE.applyModifier(player, mana_fatigue);

        int speedLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.MOTUS);
        YpsAttributeManager.CASTING_MOVESPEED.applyModifier(player, speedLevel);

    }
}
