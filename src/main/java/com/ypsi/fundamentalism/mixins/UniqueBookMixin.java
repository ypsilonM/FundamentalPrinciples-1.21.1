package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(UniqueSpellBook.class)
public abstract class UniqueBookMixin {

    @Shadow public abstract List<SpellData> getSpells();

    @Inject(method = "initializeSpellContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void ypsInitializeSpellContainer(ItemStack itemStack, CallbackInfo ci) {
        if (itemStack == null) {
            return;
        }
        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
        if (!ISpellContainer.isSpellContainer(itemStack)) {
            var spellContainer = ISpellContainer.create(4, true, true).mutableCopy();
            getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), true));
            ISpellContainer.set(itemStack, spellContainer.toImmutable());
        }
        ci.cancel();
    }
}
