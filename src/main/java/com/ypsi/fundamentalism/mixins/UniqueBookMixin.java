package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(UniqueSpellBook.class)
public abstract class UniqueBookMixin {

    @Unique
    public abstract List<SpellData> getSpells();

    @Inject(method = "initializeSpellContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void ypsInitializeSpellContainer(ItemStack itemStack, CallbackInfo ci) {
//        if (itemStack == null) {
//            return;
//        }
//        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
//        if (!ISpellContainer.isSpellContainer(itemStack)) {
//            if(ServerConfig.spellbookLevel) {
//
//                var spellContainer = ISpellContainer.create(4, true, true).mutableCopy();
//                getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), ServerConfig.lockedSpells));
//                SpellBook spellBook = (SpellBook) (Object) this;
//                itemStack.set(YpsDataComponents.YP_SPELL_SLOTS.get(), spellBook.getMaxSpellSlots());
//                itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
//
//            }else{
//
//                SpellBook spellBook = (SpellBook) (Object) this;
//                var spellContainer = ISpellContainer.create(spellBook.getMaxSpellSlots(), true, true).mutableCopy();
//                getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), ServerConfig.lockedSpells));
//                itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
//
//            }
//        }
//        ci.cancel();
    }
}
