package com.ypsi.fundamentalism.effect;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.effect.custom.*;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.awt.*;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, FundamentalPrinciples.MOD_ID);

    public static final Holder<MobEffect> MARKED_EFFECT = MOB_EFFECTS.register("marked",
            () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0x820404));

    public static final Holder<MobEffect> BURNOUT_EFFECT = MOB_EFFECTS.register("burnout",
            () -> new UnclearableEffect(MobEffectCategory.HARMFUL, 0xeb0c2d));

    public static final Holder<MobEffect> MINDFUL_EFFECT = MOB_EFFECTS.register("mindful",
            () -> new MindfulEffect(MobEffectCategory.BENEFICIAL, 0x0722a9)
                    .addAttributeModifier(AttributeRegistry.MANA_REGEN,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful"), 0.5,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final Holder<MobEffect> FLAME_GRANT_STRENGTH = MOB_EFFECTS.register("flame_boost",
            () -> new FlamePoweredEffect(MobEffectCategory.BENEFICIAL, 0xE37D24));

    public static final Holder<MobEffect> BLOODSTREAM_EFFECT = MOB_EFFECTS.register("bloodstream",
            () -> new BloodstreamEffect(MobEffectCategory.BENEFICIAL, 0xFF4545)
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

    public static final Holder<MobEffect> REINFORCEMENT_EFFECT = MOB_EFFECTS.register("reinforcement",
            () -> new ReinforcementEffect(MobEffectCategory.BENEFICIAL, 0x28DDFA)
                    .addAttributeModifier(
                            AttributeRegistry.SPELL_RESIST,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"),
                            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                            amplifier -> switch (amplifier) {
                                case 0 -> ReinforcementEffect.BASE_SPELL_RESISTANCE;
                                case 1 -> ReinforcementEffect.BASE_SPELL_RESISTANCE + 0.05f;
                                case 2 -> ReinforcementEffect.BASE_SPELL_RESISTANCE + 0.10f;
                                case 3 -> ReinforcementEffect.BASE_SPELL_RESISTANCE + 0.15f;
                                default -> ReinforcementEffect.BASE_SPELL_RESISTANCE + 0.20f;
                            }
                    )
                    .addAttributeModifier(
                            AttributeRegistry.SPELL_POWER,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"),
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE,
                            amplifier -> switch (amplifier) {
                                case 0 -> ReinforcementEffect.SPELL_POWER_REDUCTION;
                                case 1 -> ReinforcementEffect.SPELL_POWER_REDUCTION + 0.05f;
                                case 2 -> ReinforcementEffect.SPELL_POWER_REDUCTION + 0.10f;
                                case 3 -> ReinforcementEffect.SPELL_POWER_REDUCTION + 0.15f;
                                default -> ReinforcementEffect.SPELL_POWER_REDUCTION + 0.25f;
                            }
                    )
                    .addAttributeModifier(
                            Attributes.ARMOR_TOUGHNESS,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"),
                            AttributeModifier.Operation.ADD_VALUE,
                            amplifier -> switch (amplifier) {
                                case 0 -> ReinforcementEffect.ARMOUR_TOUGHNESS;
                                case 1 -> ReinforcementEffect.ARMOUR_TOUGHNESS + 1f;
                                case 2 -> ReinforcementEffect.ARMOUR_TOUGHNESS + 3f;
                                case 3 -> ReinforcementEffect.ARMOUR_TOUGHNESS + 4f;
                                default -> ReinforcementEffect.ARMOUR_TOUGHNESS + 6f;
                            }
                    )
        );
    public static final Holder<MobEffect> SOOTHE_EFFECT = MOB_EFFECTS.register("soothe",
            () -> new UnclearableEffect(MobEffectCategory.BENEFICIAL, 0x5cd3db)
                    .addAttributeModifier(YpsAttributes.FATIGUE_REGEN,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "soothe"), 1, AttributeModifier.Operation.ADD_VALUE)
        );
    public static final Holder<MobEffect> CHAINED_EFFECT = MOB_EFFECTS.register("chained",
            () -> new ChainedEffect(MobEffectCategory.HARMFUL, 0x8CEEFF)
    );
    public static final Holder<MobEffect> LACERATED_EFFECT = MOB_EFFECTS.register("lacerated",
            () -> new LaceratedEffect(MobEffectCategory.HARMFUL, 0x8A000E)
    );

    public static final Holder<MobEffect> TRAPPED_EFFECT = MOB_EFFECTS.register("trapped",
            () -> new TrappedEffect(MobEffectCategory.NEUTRAL, Color.CYAN.getRGB())
                    .addAttributeModifier(
                            Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "trapped"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(
                            Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "trapped"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(
                            Attributes.GRAVITY, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "trapped"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(
                            Attributes.FLYING_SPEED, ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "trapped"),
                            -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );
    public static final Holder<MobEffect> SPATIAL_DISRUPTION = MOB_EFFECTS.register("spatial_disruption",
            () -> new SpatialDisruption(MobEffectCategory.NEUTRAL, Color.BLACK.getRGB())
    );




//    public static final Holder<MobEffect>

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
