package com.ypsi.fundamentalism.attachments;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class YpsAttributeManager {

    public static final AttributeModifierManager MANA = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "entity_mastery"),
            AttributeRegistry.MAX_MANA,
            AttributeModifier.Operation.ADD_VALUE,
            level -> Util.getTotalMana(level)
    );

    public static final AttributeModifierManager FATIGUE = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fatigue_mana"),
            AttributeRegistry.MANA_REGEN,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            fatigue -> -0.15 * fatigue
    );
}
