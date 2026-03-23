package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;

@Mixin(Utils.class)
public class TargetHelperMixin {
//
//    @ModifyVariable(
//            method = "preCastTargetHelper(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;IFZLjava/util/function/Predicate;)Z",
//            at = @At("HEAD"), argsOnly = true, ordinal = 0, remap = false
//    )
//    private static int modifyRange(int originalRange, Level level, LivingEntity caster, MagicData playerMagicData, AbstractSpell spell,
//                                   int range, float aimAssist, boolean sendFailureMessage, Predicate<LivingEntity> filter) {
//
//        if (caster instanceof ServerPlayer player) {
//                int categoryLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.LOCUS);
//                int finalRange = Util.getTotalRange(categoryLevel);
//                int baseDetectionRange;
//                if(originalRange>=30){
//                    baseDetectionRange = 8;
//                }else{
//                    baseDetectionRange = range;
//                }
//                finalRange+=baseDetectionRange;
//                return finalRange;
//        }
//        return originalRange;
//    }

}
