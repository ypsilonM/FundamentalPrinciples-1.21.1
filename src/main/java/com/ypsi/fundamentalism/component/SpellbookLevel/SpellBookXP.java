package com.ypsi.fundamentalism.component.SpellbookLevel;

import com.mojang.serialization.Codec;

public record SpellBookXP(int xp) {
    public static final Codec<SpellBookXP> CODEC = Codec.INT
            .xmap(SpellBookXP::new, SpellBookXP::xp);

    public SpellBookXP() {
        this(0);
    }
}
