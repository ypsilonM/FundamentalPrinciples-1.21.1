package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.component.SpellbookLevel.SpellbookTooltip;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SpellBook.class)
public abstract class SpellBookTooltipMixin {

    @Inject(method = "appendHoverText", at = @At("TAIL"), remap = false)
    private void ypsi$appendSpellBookInfo(ItemStack itemStack, Item.TooltipContext context,
                                          List<Component> lines, TooltipFlag flag, CallbackInfo ci) {
        SpellbookTooltip.addSpellBookInfo(itemStack, lines);
    }
}