package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSpell.class)
public abstract class CertumManaMixin {

    @Redirect(
            method = "canBeCastedBy",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getManaCost(I)I"), remap = false
    )
    private int redirectGetManaCost(AbstractSpell spell, int spellLevel, int originalSpellLevel, CastSource castSource, MagicData playerMagicData, Player player) {

        int originalCost = spell.getManaCost(spellLevel);

        if(SpellCategoriesGenerator.isInCategory(spell.getSpellId(), "immutable")){
            int certumLevel = 0;
            if(player!=null){
                 certumLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.CERTUM);
            }
            int exLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
            originalCost = (int)(originalCost * (1+Util.manaMultiplier(exLvl, certumLevel)));
        }
        return originalCost;
    }

}
