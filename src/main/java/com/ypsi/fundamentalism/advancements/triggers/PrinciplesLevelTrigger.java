package com.ypsi.fundamentalism.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PrinciplesLevelTrigger extends SimpleCriterionTrigger<PrinciplesLevelTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, String category, int newLevel) {
        this.trigger(player, instance -> instance.matches(category, newLevel));
    }

    public record Instance(String category, MinMaxBounds.Ints level) implements SimpleInstance {
        public static final Codec<Instance> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                Codec.STRING.fieldOf("category").forGetter(Instance::category),
                MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(Instance::level)
            ).apply(inst, Instance::new));

        public boolean matches(String targetCategory, int currentLevel) {
            return this.category.equals(targetCategory) && this.level.matches(currentLevel);
        }

        public static Instance levelAtLeast(Principles category, int minLevel) {
            String principle = PrinciplesProgressionManager.getTechnicalName(category);
            return new Instance(principle, MinMaxBounds.Ints.atLeast(minLevel));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }
}
