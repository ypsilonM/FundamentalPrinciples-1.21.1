package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpellBook.class)
public abstract class SpellBookMixin {

    @Unique
    public abstract int getMaxSpellSlots();

    @Inject(method = "initializeSpellContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void ypsInitializeSpellContainer(ItemStack itemStack, CallbackInfo ci) {
//        if (itemStack == null) {
//            return;
//        }
//        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
//        if (!ISpellContainer.isSpellContainer(itemStack)) {
//            if(ServerConfig.spellbookLevel) {
//
//                itemStack.set(YpsDataComponents.YP_SPELL_SLOTS.get(), getMaxSpellSlots());
//                itemStack.set(ComponentRegistry.SPELL_CONTAINER, ISpellContainer.create(4, true, true));
//
//            }else{
//
//                ISpellContainer.set(itemStack, ISpellContainer.create(getMaxSpellSlots(), true, true));
//
//            }
//            //ISpellContainer.set(itemStack, ISpellContainer.create(4, true, true));
//        }
//        ci.cancel();
    }


    @Inject(method = "appendHoverText", at = @At("HEAD"), remap = false)
    private void ypsAppendHoverText(@NotNull ItemStack itemStack, Item.TooltipContext context, @NotNull java.util.List<net.minecraft.network.chat.Component> lines, @NotNull TooltipFlag flag, CallbackInfo ci) {
        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
    }
}
