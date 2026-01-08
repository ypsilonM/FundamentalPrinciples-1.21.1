package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.component.YpsDataComponents;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.block.arcane_anvil.ArcaneAnvilBlock;
import io.redspace.ironsspellbooks.gui.arcane_anvil.ArcaneAnvilMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArcaneAnvilMenu.class)
public abstract class ArcaneAnvilMixin {
    @Redirect(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/redspace/ironsspellbooks/api/spells/ISpellContainer;getMaxSpellCount()I"
            ),
            remap = false
    )
    private int addLevel5Requirement(ISpellContainer container) {
        ArcaneAnvilMenu menu = (ArcaneAnvilMenu)(Object)this;
        ItemStack spellbook = menu.getSlot(0).getItem();
        if (spellbook.get(YpsDataComponents.SPELLBOOK_LEVEL.get()).level() >= 5) {
            return container.getMaxSpellCount();
        } else {
            return 100;
        }
    }

}
