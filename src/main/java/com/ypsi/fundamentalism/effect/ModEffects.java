package com.ypsi.fundamentalism.effect;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.custom.*;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, FundamentalPrinciples.MOD_ID);

    public static final Holder<MobEffect> MARKED_EFFECT = MOB_EFFECTS.register("marked",
            () -> new MarkedEffect(MobEffectCategory.HARMFUL, 0xA52A2A));

    public static final Holder<MobEffect> MINDFUL_EFFECT = MOB_EFFECTS.register("mindful",
            () -> new MindfulEffect(MobEffectCategory.BENEFICIAL, 0x0722a9)
                    .addAttributeModifier(AttributeRegistry.MANA_REGEN,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful"), 1.5,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final Holder<MobEffect> FLAME_GRANT_STRENGTH = MOB_EFFECTS.register("flame_boost",
            () -> new FlamePoweredEffect(MobEffectCategory.BENEFICIAL, 0xA52A2A));

    public static final Holder<MobEffect> BLOODSTREAM_EFFECT = MOB_EFFECTS.register("bloodstream",
            () -> new BloodstreamEffect(MobEffectCategory.BENEFICIAL, 0xA52A2A)
                    .addAttributeModifier(Attributes.ATTACK_SPEED, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream"),
                            BloodstreamEffect.ATTACK_SPEED_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream"),
                            BloodstreamEffect.SPEED_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream"),
                            BloodstreamEffect.JUMP_PER_LEVEL, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.STEP_HEIGHT, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream"),
                            0.4, AttributeModifier.Operation.ADD_VALUE)
                    .addAttributeModifier(Attributes.SCALE, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "bloodstream"),
                            0.03, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
    );

//    public static final Holder<MobEffect> SHAPELESS_MOTHER = MOB_EFFECTS.register("shapeless_mother",
//            () -> new ShapelessMotherEffect(MobEffectCategory.BENEFICIAL, 0xA52A2A)
//                    .addAttributeModifier(AttributeRegistry.BLOOD_SPELL_POWER, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "shapeless_mother"),
//                            0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
//    );

//    public static final Holder<MobEffect> TREES_BLESSING = MOB_EFFECTS.register("treesblessing",
//            () -> new TreesBlessingEffect(MobEffectCategory.BENEFICIAL, 0x0722a9));

    public static final Holder<MobEffect> REINFORCEMENT_EFFECT = MOB_EFFECTS.register("reinforcement",
            () -> new ReinforcementEffect(MobEffectCategory.NEUTRAL, 0x0722a9)
                    .addAttributeModifier(AttributeRegistry.SPELL_RESIST,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"), 0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(AttributeRegistry.SPELL_POWER,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
        );

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
