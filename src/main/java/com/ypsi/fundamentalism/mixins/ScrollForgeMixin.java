package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.gui.scroll_forge.ScrollForgeScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(ScrollForgeScreen.class)

public abstract class ScrollForgeMixin {
    private int currentSpellLevel;
    @ModifyVariable(
            method = "generateSpellList",
            at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0),
            index = 0,
            remap = false
    )
    private int captureSpellLevel(int i) {
        this.currentSpellLevel = i;
        return i;
    }

    @Redirect(
            method = "generateSpellList",
            at = @At(value = "INVOKE", target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;isEnabled()Z"),
            remap = false
    )
    private boolean checkEligibility(AbstractSpell spell) {
        if (!spell.isEnabled()) return false;
        if (Minecraft.getInstance() == null) return false;

        Player player = Minecraft.getInstance().player;
        if (player == null) return false;

        SpellRarity spellRarity = spell.getRarity(currentSpellLevel + 1);

        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        if (categories.isEmpty()) return true;

        int requiredLevel = getLevelRequiredForRARITY(spellRarity);
        for (String cat : categories) {
            if (PrinciplesProgressionManager.getCategoryLevel(player, cat) < requiredLevel) {
                return false;
            }
        }
        return true;
    }

    private static int getLevelRequiredForRARITY(SpellRarity rarity){
        return
                switch (rarity) {
                    case COMMON -> ServerConfig.dominanLvls.get(0);
                    case UNCOMMON -> ServerConfig.dominanLvls.get(1);
                    case RARE -> ServerConfig.dominanLvls.get(2);
                    case EPIC -> ServerConfig.dominanLvls.get(3);
                    case LEGENDARY -> ServerConfig.dominanLvls.get(4);
                };
    }
}
