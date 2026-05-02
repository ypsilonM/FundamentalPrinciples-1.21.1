package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
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

        if(SpellCategoriesGenerator.isInCategory(spell.getSpellId(), "immutable")
                && ServerConfig.certumActive
                && ServerConfig.principlesSYSTEM){

            int certumLevel = 0;
            if(player!=null) {
                certumLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.CERTUM);
                //int exLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
                int exLvl = FatigueManager.getFatigueLevel(player);
                originalCost = (int) (originalCost * (1 + Util.certumManaMultiplier(exLvl, certumLevel)));
            }
        }
        return originalCost;
    }

}
