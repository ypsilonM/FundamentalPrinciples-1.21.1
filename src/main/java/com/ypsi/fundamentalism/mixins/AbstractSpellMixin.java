package com.ypsi.fundamentalism.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSpell.class)
public abstract class AbstractSpellMixin {

    @ModifyReturnValue(method = "allowLooting", at = @At("RETURN"), remap = false)
    private boolean isAllowLooting(boolean original){
        String spellId = ((AbstractSpell)(Object)this).getSpellId();

        var categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);

        return categories.size()<=3;

    }
}
