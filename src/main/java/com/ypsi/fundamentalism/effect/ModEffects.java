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

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, FundamentalPrinciples.MOD_ID);

    public static final Holder<MobEffect> MARKED_EFFECT = MOB_EFFECTS.register("marked",
            () -> new MagicMobEffect(MobEffectCategory.HARMFUL, 0xA52A2A));

//    public static final Holder<MobEffect> BURNOUT_EFFECT = MOB_EFFECTS.register("burnout",
//            () -> new BurnoutEffect(MobEffectCategory.HARMFUL, 0xeb0c2d));

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
                    .addAttributeModifier(AttributeRegistry.SPELL_RESIST,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"), 0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(AttributeRegistry.SPELL_POWER,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"), -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "reinforcement"), 4, AttributeModifier.Operation.ADD_VALUE)
        );
    public static final Holder<MobEffect> SOOTHE_EFFECT = MOB_EFFECTS.register("soothe",
            () -> new UnclearableEffect(MobEffectCategory.BENEFICIAL, 0x5cd3db)
                    .addAttributeModifier(YpsAttributes.FATIGUE_REGEN,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "soothe"), 1, AttributeModifier.Operation.ADD_VALUE)
        );

    //Change color
    public static final Holder<MobEffect> CHAINED_EFFECT = MOB_EFFECTS.register("chained",
            () -> new ChainedEffect(MobEffectCategory.HARMFUL, 0xeb0c2d)
    );
    public static final Holder<MobEffect> LACERATED_EFFECT = MOB_EFFECTS.register("lacerated",
            () -> new LaceratedEffect(MobEffectCategory.HARMFUL, 0xeb0c2d)
    );


//    public static final Holder<MobEffect>

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
