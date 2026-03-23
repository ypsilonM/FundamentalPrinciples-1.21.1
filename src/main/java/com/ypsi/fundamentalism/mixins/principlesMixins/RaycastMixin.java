package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(Utils.class)
public class RaycastMixin {

        //JUST PERCEPTIO
        @ModifyVariable(
                method = "raycastForEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;FZ)Lnet/minecraft/world/phys/HitResult;",
                at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false
        )
        private static float raycastForEntity(float originalDistance, Level level, Entity originEntity, float distance, boolean checkForBlocks) {
            if (originEntity instanceof ServerPlayer player) {
                int categoryLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.PERCEPTIO);
                float finalRange = Util.getTotalRange(categoryLevel);
                float baseDetectionRange = 8f+ originalDistance/5f;
                finalRange+=baseDetectionRange;

                return finalRange;
            }
            return originalDistance;
        }

        //PERCEPTIO AND LOCUS
        @ModifyVariable(
                method = "raycastForEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;FZF)Lnet/minecraft/world/phys/HitResult;",
                at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false
        )
        private static float raycastForEntityBB(float originalDistance, Level level, Entity originEntity, float distance, boolean checkForBlocks, float bbInflation) {
            if (originEntity instanceof ServerPlayer player) {
                int perceptioLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.PERCEPTIO);
                float finalRange = Util.getTotalRange(perceptioLevel);
                float baseDetectionRange = 8f+ originalDistance/5f;
                finalRange+=baseDetectionRange;

                int locusLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.LOCUS);
                finalRange = (float) Util.returnLocusDistance(locusLevel, finalRange);

                return finalRange;
            }
            return originalDistance;
        }



}
