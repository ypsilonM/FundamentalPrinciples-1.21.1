package com.ypsi.fundamentalism.attributes;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attachments.SpellCategoryLevelsAttachment;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class YpsManaAttributeSystem {
    private static final String TARGET_CATEGORY = "createEntity";

    private static final DeferredHolder<Attribute, Attribute> TARGET_ATTRIBUTE = AttributeRegistry.MAX_MANA;
    private static final ResourceLocation MODIFIER_ID = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "entity_mastery");
    private static final String MODIFIER_NAME = "entity_mastery";
    private static final AttributeModifier.Operation OPERATION = AttributeModifier.Operation.ADD_VALUE;

    private static final Map<UUID, Integer> lastKnownLevels = new HashMap<>();

    public static void initializeForPlayer(Player player) {
        SpellCategoryLevelsAttachment levels = player.getData(YpsAttachments.SPELL_CATEGORY_LEVELS.get());

        if (levels != null) {
            levels.setLevelChangeListener((category, oldLevel, newLevel) -> {
                if (category.equals(TARGET_CATEGORY)) {
                    onCategoryLevelChanged(player, category, oldLevel, newLevel);
                }
            });
            int initialLevel = levels.getLevel(TARGET_CATEGORY);
            updatePlayerAttribute(player, initialLevel);
            lastKnownLevels.put(player.getUUID(), initialLevel);
        }
    }

    private static void onCategoryLevelChanged(Player player, String category, int oldLevel, int newLevel) {
        if (!player.level().isClientSide()) {
            updatePlayerAttribute(player, newLevel);
            lastKnownLevels.put(player.getUUID(), newLevel);
        }
    }

    private static void updatePlayerAttribute(Player player, int level) {
        Holder <Attribute> attribute = TARGET_ATTRIBUTE;
        AttributeInstance attributeInstance = player.getAttribute(attribute);

        if (attributeInstance != null) {
            removeModifier(attributeInstance, MODIFIER_ID);
            if (level > 0) {
                double bonus = Util.getTotalMana(level);
                addModifier(attributeInstance, MODIFIER_ID, MODIFIER_NAME, bonus, OPERATION);
            }
        }
    }

    private static void removeModifier(AttributeInstance attributeInstance, ResourceLocation modifierId) {
        for (AttributeModifier modifier : attributeInstance.getModifiers()) {
            if (modifier.id().equals(modifierId)) {
                attributeInstance.removeModifier(modifier);
                break;
            }
        }
    }

    private static void addModifier(AttributeInstance attributeInstance, ResourceLocation id, String name,
                                    double amount, AttributeModifier.Operation operation) {
        AttributeModifier modifier = new AttributeModifier(
                id,
                amount,
                operation
        );

        attributeInstance.addTransientModifier(modifier);
    }

    public static void cleanupPlayer(Player player) {
        lastKnownLevels.remove(player.getUUID());
        AttributeInstance attribute = player.getAttribute(TARGET_ATTRIBUTE);
        if (attribute != null) {
            attribute.removeModifier(MODIFIER_ID);
        }
    }
}
