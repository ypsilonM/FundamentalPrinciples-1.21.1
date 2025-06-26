package com.ypsi.fundamentalism.effect;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.custom.MindfulEffect;
import com.ypsi.fundamentalism.effect.custom.ReinforcementEffect;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, FundamentalPrinciples.MOD_ID);

    public static final Holder<MobEffect> MINDFUL_EFFECT = MOB_EFFECTS.register("mindful",
            () -> new MindfulEffect(MobEffectCategory.BENEFICIAL, 0x0722a9)
                    .addAttributeModifier(AttributeRegistry.MANA_REGEN,
                            ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful"), 1.5,
                            AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

    public static final Holder<MobEffect> REINFORCEMENT_EFFECT = MOB_EFFECTS.register("reinforcement",
            () -> new ReinforcementEffect(MobEffectCategory.NEUTRAL, 0x0722a9));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
