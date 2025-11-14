package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.gui.overlays.SpellWheelOverlay;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(SpellWheelOverlay.class)
public class SpellWheelOverlayMixin {
    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void addCategoryTags(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        try {
            SpellWheelOverlay overlay = (SpellWheelOverlay) (Object) this;

            Field wheelSelectionField = SpellWheelOverlay.class.getDeclaredField("wheelSelection");
            wheelSelectionField.setAccessible(true);
            int wheelSelection = wheelSelectionField.getInt(overlay);

            var selectedSpell = ClientMagicData.getSpellSelectionManager().getSpellData(wheelSelection);
            if (selectedSpell != null) {
                List<String> categories = getSpellCategories(selectedSpell.getSpell());

                if (!categories.isEmpty()) {
                    var minecraft = Minecraft.getInstance();
                    var font = minecraft.font;

                    int centerX = guiGraphics.guiWidth() / 2;
                    int centerY = guiGraphics.guiHeight() / 2;

                    var spellLevel = selectedSpell.getSpell().getLevelFor(selectedSpell.getLevel(), minecraft.player);
                    var info = selectedSpell.getSpell().getUniqueInfo(spellLevel, minecraft.player);
                    int textHeight = Math.max(3, info.size()) * font.lineHeight + 5;

                    Field ringOuterEdgeField = SpellWheelOverlay.class.getDeclaredField("ringOuterEdge");
                    ringOuterEdgeField.setAccessible(true);
                    float ringOuterEdge = ringOuterEdgeField.getFloat(overlay);

                    int spellNameY = (int) (centerY - (ringOuterEdge + textHeight));

                    String symbolsText = categories.stream()
                            .map(SpellCategoryProgression::getCategorySymbol)
                            .collect(Collectors.joining(" "));

                    Component symbolsComponent = Component.literal(symbolsText)
                            .withStyle(ChatFormatting.GOLD);

                    int textWidth = font.width(symbolsComponent);
                    int symbolsY = spellNameY - font.lineHeight - 2;

                    guiGraphics.drawString(font, symbolsComponent, centerX - textWidth / 2, symbolsY, 0xFFFFFF, true);
                }
            }
        } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Error en SpellWheelOverlayMixin", e);
        }
    }

    private List<String> getSpellCategories(AbstractSpell spell) {
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        return new ArrayList<>(categories);
    }
}