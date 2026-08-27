package com.ypsi.fundamentalism.mixins.principlesMixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
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
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getManaCost(I)I"),
            remap = false
    )
    private int redirectGetManaCost(AbstractSpell spell, int spellLevel, int originalSpellLevel, CastSource castSource, MagicData playerMagicData, Player player) {

        int originalCost = spell.getManaCost(spellLevel);

        int efficiencyLvl = 5;
        if(ServerConfig.EFFICIENCY_ATTRIBUTE.get() && player != null)
            efficiencyLvl = player.getData(YpsAttachments.CAST_EFFICIENCY.get()).getEfficiencyLevel();

        if(ServerConfig.FATIGUE_SYSTEM.get() && ServerConfig.ACTIVE_CERTUM.get() && ServerConfig.PRINCIPLES_SYSTEM.get()){

            if(SpellCategoriesGenerator.isInPrinciple(spell.getSpellId(), Principles.CERTUM)) {
                int certumLevel = 0;
                if (player != null) {
                    certumLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.CERTUM);
                    int exLvl = FatigueManager.getFatigueLevel(player);
                    originalCost = (int) (
                            (originalCost * (1 + Util.certumManaMultiplier(exLvl, certumLevel))) * (
                            ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, true) : 1)
                    );
                }
            }else{
                if(player != null) {
                    originalCost = (int) (
                            (originalCost) * (
                                    ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1)
                    );
                }
            }
        }else{
            if(player != null) {
                originalCost = (int) (
                        (originalCost) * (
                                ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1)
                );
            }
        }
        return originalCost;
    }

}
