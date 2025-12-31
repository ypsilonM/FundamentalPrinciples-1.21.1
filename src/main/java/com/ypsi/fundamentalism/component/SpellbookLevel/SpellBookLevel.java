package com.ypsi.fundamentalism.component.SpellbookLevel;

import com.mojang.serialization.Codec;

public record SpellBookLevel(int level) {
    public static final Codec<SpellBookLevel> CODEC = Codec.INT
            .xmap(SpellBookLevel::new, SpellBookLevel::level);

    public SpellBookLevel() {
        this(1);
    }
}
