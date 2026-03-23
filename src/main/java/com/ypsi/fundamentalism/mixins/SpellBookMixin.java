package com.ypsi.fundamentalism.mixins;

import com.google.common.collect.Multimap;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.effect.custom.BloodstreamEffect;
import io.redspace.ironsspellbooks.api.item.UpgradeData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.util.UpgradeUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

@Mixin(SpellBook.class)
public abstract class SpellBookMixin {

    @Unique
    public abstract int getMaxSpellSlots();

    @Inject(method = "initializeSpellContainer", at = @At("HEAD"), cancellable = true, remap = false)
    private void ypsInitializeSpellContainer(ItemStack itemStack, CallbackInfo ci) {
        if (itemStack == null) {
            return;
        }
        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
        if (!ISpellContainer.isSpellContainer(itemStack)) {
            itemStack.set(YpsDataComponents.YP_SPELL_SLOTS.get(), getMaxSpellSlots());

            itemStack.set(ComponentRegistry.SPELL_CONTAINER, ISpellContainer.create(4, true, true));
            //ISpellContainer.set(itemStack, ISpellContainer.create(4, true, true));
        }
        ci.cancel();
    }


    @Inject(method = "appendHoverText", at = @At("HEAD"), remap = false)
    private void ypsAppendHoverText(@NotNull ItemStack itemStack, Item.TooltipContext context, @NotNull java.util.List<net.minecraft.network.chat.Component> lines, @NotNull TooltipFlag flag, CallbackInfo ci) {
        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);
    }
}
