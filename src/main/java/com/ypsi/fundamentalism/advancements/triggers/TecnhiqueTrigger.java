package com.ypsi.fundamentalism.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Techniques;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class TecnhiqueTrigger extends SimpleCriterionTrigger<TecnhiqueTrigger.Instance> {

    @Override
    public Codec<TecnhiqueTrigger.Instance> codec() {
        return TecnhiqueTrigger.Instance.CODEC;
    }

    public void trigger(ServerPlayer player, String category, int level) {
        this.trigger(player, instance -> instance.matches(category, level));
    }

    public record Instance(String category, MinMaxBounds.Ints level) implements SimpleInstance {
        public static final Codec<TecnhiqueTrigger.Instance> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Codec.STRING.fieldOf("category").forGetter(TecnhiqueTrigger.Instance::category),
                    MinMaxBounds.Ints.CODEC.optionalFieldOf("level", MinMaxBounds.Ints.ANY).forGetter(TecnhiqueTrigger.Instance::level)
            ).apply(inst, TecnhiqueTrigger.Instance::new));

        public boolean matches(String targetCategory, int level) {
            return this.category.equals(targetCategory) && this.level.matches(level);
        }

        public static TecnhiqueTrigger.Instance hasAcquired(Techniques technique, int level) {
            return new TecnhiqueTrigger.Instance(technique.name(), MinMaxBounds.Ints.atLeast(level));
        }

        @Override
        public Optional<ContextAwarePredicate> player() {
            return Optional.empty();
        }
    }

}
