package com.ypsi.fundamentalism;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;

//@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    static final ModConfigSpec SPEC;

    public static ModConfigSpec.ConfigValue<Integer> XOFFSET;
    public static ModConfigSpec.ConfigValue<Integer> YOFFSET;

    static{
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("|                     FUNDAMENTAL PRINCIPLES CLIENT CONFIGS         |");
        BUILDER.comment("--------------------------------------------------------------------");
        BUILDER.comment("");

        BUILDER.push("others");
        {
            XOFFSET = BUILDER.define("fatigueCounterXOffset", 0);
            YOFFSET = BUILDER.define("fatigueCounterYOffset", 0);
        }
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
