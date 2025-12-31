package com.ypsi.fundamentalism.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(AbstractSpell.class)
public abstract class SpellPowerSpellsMixin {

    @Shadow(remap = false)
    public abstract String getSpellId();

    @ModifyReturnValue(method = "getSpellPower", at = @At("RETURN"), remap = false)
    private float multiplySpecificSpellPower(float original, int spellLevel, @Nullable Entity sourceEntity) {
        if(sourceEntity instanceof Player) {
            String id = this.getSpellId();
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(id);
            float baseSpellPower = original;

            for (String category : categories) {
                int levelCat = SpellCategoryProgression.getCategoryLevel((Player) sourceEntity, category);
                    //Subcategories
                    if(category.contains("usesShoot") || category.contains("createsAoeEntities") || category.contains("usesSummon")) {
                        baseSpellPower += getSubEntityModificator(levelCat, baseSpellPower);
                    }else{
                        baseSpellPower += getModificator(levelCat, baseSpellPower);
                    }
            }
            return baseSpellPower;

        }
        return original;
    }
    private float getModificator(int level, float base){
        double basePercentage = -0.10;
        for(int i=0;i<level;i++){
            double increment = 0.01;
            basePercentage+=increment;
        }
        return (float) (base*basePercentage);
    }
    private float getSubEntityModificator(int level, float base){
        double basePercentage = -0.05;
        for(int i=0;i<level;i++){
            double increment = 0.005;
            basePercentage+=increment;
        }
        return (float) (base*basePercentage);
    }

}
