package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.component.YpsDataComponents;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import io.redspace.ironsspellbooks.gui.inscription_table.InscriptionTableMenu;
import io.redspace.ironsspellbooks.item.Scroll;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InscriptionTableMenu.class)
public abstract class InscriptionMixin {

    @Shadow(remap = false)
    public abstract Slot getSpellBookSlot();
    @Shadow(remap = false)
    public abstract Slot getScrollSlot();

    @Inject(method = "clickMenuButton", at = @At(value = "INVOKE",
            target = "Lnet/neoforged/bus/api/IEventBus;post(Lnet/neoforged/bus/api/Event;)Lnet/neoforged/bus/api/Event;",
            shift = At.Shift.AFTER
    ), cancellable = true, remap = false)
    private void ypsi$verifySpellBookTier(Player pPlayer, int pId, CallbackInfoReturnable<Boolean> cir) {

        ItemStack spellBookItemStack = getSpellBookSlot().getItem();
        if (spellBookItemStack.getItem() instanceof SpellBook) {
            if (spellBookItemStack.has(YpsDataComponents.SPELLBOOK_LEVEL.get())) {
                int spellBookLevel = spellBookItemStack.get(YpsDataComponents.SPELLBOOK_LEVEL.get()).level();

                ItemStack scrollItemStack = getScrollSlot().getItem();
                if (scrollItemStack.getItem() instanceof Scroll) {
                    var scrollContainer = ISpellContainer.get(scrollItemStack);
                    var scrollSlot = scrollContainer.getSpellAtIndex(0);
                    SpellRarity spellRarity = scrollSlot.getRarity();
                    int spellRarityLevel = switch (spellRarity) {
                        case COMMON -> 1;
                        case UNCOMMON -> 2;
                        case RARE -> 3;
                        case EPIC -> 4;
                        case LEGENDARY -> 5;
                    };
                    if (spellRarityLevel > spellBookLevel) {
                        cir.setReturnValue(false);
                    }
                }
            }
        }
    }

}
