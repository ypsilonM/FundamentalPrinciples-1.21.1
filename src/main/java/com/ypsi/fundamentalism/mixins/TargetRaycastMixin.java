package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(Utils.class)
public class TargetRaycastMixin {

    @ModifyVariable(
            method = "preCastTargetHelper(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lio/redspace/ironsspellbooks/api/magic/MagicData;Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;IFZLjava/util/function/Predicate;)Z",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0,
            remap = false
    )
    private static int modifyRange(int originalRange, Level level, LivingEntity caster,
                                   MagicData playerMagicData, AbstractSpell spell,
                                   int range, float aimAssist, boolean sendFailureMessage,
                                   Predicate<LivingEntity> filter) {

        if (caster instanceof ServerPlayer player) {
            String spellId = spell.getSpellId();
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);
            if (categories.contains("usesTargeting")) {
                int categoryLevel = SpellCategoryProgression.getCategoryLevel(player, "usesTargeting");
                int finalRange = getTotalRange(categoryLevel);
                int baseDetectionRange;
                if(originalRange>=30){
                    baseDetectionRange = 8;
                }else{
                    baseDetectionRange = range;
                }
                finalRange+=baseDetectionRange;
                return finalRange;
            }
        }
        return originalRange;
    }
    private static int getTotalRange(int level){
        return (int) (level*1.5);
    }
}
