package com.ypsi.fundamentalism.compat;

import net.neoforged.fml.ModList;

public class DynamicLightsIntegration {

    public static void register() {
        if (!ModList.get().isLoaded("lambdynamiclights")) return;

    }

}