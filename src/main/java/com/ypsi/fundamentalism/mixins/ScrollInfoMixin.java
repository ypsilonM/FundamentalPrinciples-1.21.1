package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(Scroll.class)
public class ScrollInfoMixin {
    @Inject(method = "appendHoverText", at = @At("TAIL"), remap = false)
    private void addScrollCategoriesInfo(@NotNull ItemStack itemStack, Item.TooltipContext context, @NotNull List<Component> lines, @NotNull TooltipFlag flag, CallbackInfo ci) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        if (!ISpellContainer.isSpellContainer(itemStack)) {
            return;
        }
        var spellList = ISpellContainer.get(itemStack);
        if (spellList.isEmpty()) {
            return;
        }
        var spellData = spellList.getSpellAtIndex(0);
        var spell = spellData.getSpell();

        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        if (categories.isEmpty()) {
            return;
        }
        String symbolsText = categories.stream()
                .map(SpellCategoryProgression::getCategorySymbol)
                .collect(Collectors.joining(" "));
        MutableComponent categoriesLine = Component.literal("")
                .append(Component.literal(symbolsText)
                        .withStyle(spell.getSchoolType().getDisplayName().getStyle()));

        lines.add(Component.literal(" ").append(categoriesLine));
    }
}
