package com.ypsi.fundamentalism.attachments;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.IntToDoubleFunction;

public class AttributeModifierManager {
    private final ResourceLocation modifierId;
    private final Holder<Attribute> attribute;
    private final AttributeModifier.Operation operation;
    private final IntToDoubleFunction amountFunction;

    public AttributeModifierManager(ResourceLocation modifierId, Holder<Attribute> attribute, AttributeModifier.Operation operation, IntToDoubleFunction amountFunction) {
        this.modifierId = modifierId;
        this.attribute = attribute;
        this.operation = operation;
        this.amountFunction = amountFunction;
    }

    public void applyModifier(Player player, int value) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;

        instance.removeModifier(modifierId);

        if (value > 0) {
            double amount = amountFunction.applyAsDouble(value);
            AttributeModifier modifier = new AttributeModifier(modifierId, amount, operation);
            instance.addTransientModifier(modifier);
        }
    }

    public void cleanup(Player player) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(modifierId);
        }
    }

}
