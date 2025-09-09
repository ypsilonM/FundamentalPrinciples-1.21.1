package com.ypsi.fundamentalism.effect.custom;

import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;

public class TreesBlessingEffect extends MagicMobEffect {

    public static final Map<Integer, Map<BlockPos, BlockState>> savedTrees = new HashMap<>();

    public TreesBlessingEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int pAmplifier) {
        super.onEffectAdded(entity, pAmplifier);
    }

    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        super.onEffectRemoved(entity, amplifier);

        // Restaurar los bloques originales
        if (savedTrees.containsKey(entity.getId())) {
            Map<BlockPos, BlockState> originalBlocks = savedTrees.get(entity.getId());
            Level level = entity.level();

            for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
                level.setBlockAndUpdate(entry.getKey(), entry.getValue());
            }

            savedTrees.remove(entity.getId());
        }
    }


}
