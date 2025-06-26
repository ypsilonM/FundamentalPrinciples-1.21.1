package com.ypsi.fundamentalism.network;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import net.minecraft.resources.ResourceLocation;
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

        registrar.playToServer(
                ToggleReinforcementPacket.TYPE,
                ToggleReinforcementPacket.STREAM_CODEC,
                ToggleReinforcementPacket::handle
        );
    }
}
