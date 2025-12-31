package com.ypsi.fundamentalism.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.UUID;

public class SpellAttributeUtils {
    public static boolean modifySpellLevelIfExists(Player player, String spellId, int levelModifier) {

        Optional<Holder<Attribute>> spellAttribute = getSpellAttribute(spellId);

        if (spellAttribute.isPresent()) {
            AttributeInstance attributeInstance = player.getAttribute(spellAttribute.get());
            if (attributeInstance != null) {
                ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(
                        "fundamentalism",
                        "spell_bonus/" + spellId.replace(":", "/")
                );
                attributeInstance.removeModifier(modifierId);

                if (levelModifier != 0) {
                    AttributeModifier modifier = new AttributeModifier(
                            modifierId,
                            levelModifier,
                            AttributeModifier.Operation.ADD_VALUE
                    );
                    attributeInstance.addPermanentModifier(modifier);
                }
                return true;
            }
        }
        return false;
    }

    private static Optional<Holder<Attribute>> getSpellAttribute(String spellId) {
        String attributeId = "spell/" + spellId.replace(":", "/");
        ResourceLocation attributeLocation = ResourceLocation.fromNamespaceAndPath("additional_attributes", attributeId);

        return BuiltInRegistries.ATTRIBUTE.getHolder(attributeLocation)
                .map(reference -> reference);
    }
}
