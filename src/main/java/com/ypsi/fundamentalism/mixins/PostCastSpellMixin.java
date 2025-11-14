package com.ypsi.fundamentalism.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(AbstractSpell.class)
public abstract class PostCastSpellMixin {

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
                baseSpellPower += getModificator(levelCat, baseSpellPower);
            }
            return baseSpellPower;
        }
        return original;
    }
    private float getModificator(int level, float base){
        double basePercentage = -0.10;
        for(int i=0;i<level;i++){
            double increment = basePercentage<0?0.01:0.02;
            basePercentage+=increment;
        }
        return (float) (base*basePercentage);
    }

}
