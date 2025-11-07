package com.ypsi.fundamentalism.network;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.network.packets.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetwork::onRegisterPayloads);
    }

    private static void onRegisterPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(FundamentalPrinciples.MOD_ID)
                .versioned("1.0");

        //Spell Categories
        registrar.playToClient(
                SyncCategoryLevelsPacket.TYPE,
                SyncCategoryLevelsPacket.STREAM_CODEC,
                SyncCategoryLevelsPacket::handle
        );

        registrar.playToServer(
                ToggleReinforcementPacket.TYPE,
                ToggleReinforcementPacket.STREAM_CODEC,
                ToggleReinforcementPacket::handle
        );
        registrar.playToClient(
                SyncReinforcementPacket.TYPE,
                SyncReinforcementPacket.STREAM_CODEC,
                SyncReinforcementPacket::handle
        );

        //Exhaustion
        registrar.playToClient(
                SyncExhaustionPacket.TYPE,
                SyncExhaustionPacket.STREAM_CODEC,
                SyncExhaustionPacket::handle
        );
        //LvlExhaustion
        registrar.playToClient(
                SyncExhaustionLevelPacket.TYPE,
                SyncExhaustionLevelPacket.STREAM_CODEC,
                SyncExhaustionLevelPacket::handle
        );
        registrar.playToClient(
                LookAtEntityPacket.TYPE,
                LookAtEntityPacket.STREAM_CODEC,
                LookAtEntityPacket::handle
        );
        registrar.playToServer(
                UpdateSpellLevelPacket.TYPE,
                UpdateSpellLevelPacket.STREAM_CODEC,
                UpdateSpellLevelPacket::handle
        );
    }
}
