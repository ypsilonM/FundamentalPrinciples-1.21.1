package com.ypsi.fundamentalism.attachments;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class YpsAttributeManager {

    public static final AttributeModifierManager MANA = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "entity_mastery"),
            AttributeRegistry.MAX_MANA,
            AttributeModifier.Operation.ADD_VALUE,
            Util::getTotalMana
    );

    public static final AttributeModifierManager CASTING_MOVESPEED = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "cast_movespeed"),
            AttributeRegistry.CASTING_MOVESPEED,
            AttributeModifier.Operation.ADD_VALUE,
            Util::getAdditionalCastingMovespeed
    );

    public static final AttributeModifierManager FATIGUE = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fatigue_mana"),
            AttributeRegistry.MANA_REGEN,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            fatigue -> -ServerConfig.fatigueManaRegen * fatigue
            //-0.15
    );

    public static final AttributeModifierManager RESONANCE = new AttributeModifierManager(
            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "resonance_modifier"),
            YpsAttributes.RESONANCE,
            AttributeModifier.Operation.ADD_VALUE,
            resonance -> 0.2
    );
}
