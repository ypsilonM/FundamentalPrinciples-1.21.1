package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
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
    private void addAllExtraInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        try {
            SpellWheelOverlay overlay = (SpellWheelOverlay) (Object) this;

            // Obtener la selección actual
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

                    // Obtener campos necesarios
                    Field ringOuterEdgeMaxField = SpellWheelOverlay.class.getDeclaredField("ringOuterEdgeMax");
                    ringOuterEdgeMaxField.setAccessible(true);
                    float ringOuterEdgeMax = ringOuterEdgeMaxField.getFloat(overlay);

                    int textHeight = Math.max(3, info.size()) * font.lineHeight + 5;
                    int textTitleMargin = 5;
                    int textCenterMargin = 5;

                    // 1. Calcular posición base (donde empieza el título)
                    int spellNameY = (int) (centerY - (ringOuterEdgeMax + textHeight));

                    // 2. Símbolos de categorías (arriba del nombre)
                    String symbolsText = categories.stream()
                            .map(SpellCategoryProgression::getCategorySymbol)
                            .collect(Collectors.joining(" "));

                    Component symbolsComponent = Component.literal(symbolsText)
                            .withStyle(ChatFormatting.GOLD);

                    int symbolsWidth = font.width(symbolsComponent);
                    int symbolsY = spellNameY - font.lineHeight - 2; // Una línea arriba del título

                    guiGraphics.drawString(font, symbolsComponent,
                            centerX - symbolsWidth / 2, symbolsY, 0xFFFFFF, true);

                    // 3. Probabilidad de teleport (al final de la info)
                    if(categories.contains("usesTeleport")) {
                        var swsm = ClientMagicData.getSpellSelectionManager();
                        int categoryLevel = SpellCategoryProgression.getCategoryLevel(minecraft.player, "usesTeleport");
                        int cooldown = MagicManager.getEffectiveSpellCooldown(selectedSpell.getSpell(), minecraft.player, swsm.getSpellSlot(wheelSelection).getCastSource()) / 20;

                        int probability = (int) calculateProbabilityForFailedTp(categoryLevel, cooldown);
                        String probabilityText = (100 - probability) + "% Success";

                        Component probabilityComponent = Component.literal(probabilityText)
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);

                        int probabilityY = (int) (centerY - (ringOuterEdgeMax + textHeight) +
                                font.lineHeight * (info.size() + 1) + textTitleMargin);

                        guiGraphics.drawString(font, probabilityComponent, centerX + textCenterMargin, probabilityY, 0xFFFFFF, true);
                    }
//                    else if(categories.contains("hasRecasts")){
//                        int categoryLevel = SpellCategoryProgression.getCategoryLevel(minecraft.player, "hasRecasts");
//
//                        int probability = (int) calculateProbabilityForRecast(categoryLevel);
//                        String probabilityText = probability + "% +Recast";
//
//                        Component probabilityComponent = Component.literal(probabilityText)
//                                .withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD);
//
//                        int probabilityY = (int) (centerY - (ringOuterEdgeMax + textHeight) +
//                                font.lineHeight * (info.size() + 1) + textTitleMargin);
//
//                        // Dibujar al final de la info
//                        guiGraphics.drawString(font, probabilityComponent,
//                                centerX + textCenterMargin, probabilityY, 0xFFFFFF, true);
//                    }

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
    private static double calculateProbabilityForFailedTp(int categoryLevel, int cooldown) {
        if(cooldown==0) cooldown = 1;
        double chance = Math.min(90,((100/(cooldown))*2));
        return chance - (categoryLevel*0.025*chance);
    }
    private float calculateProbabilityForRecast(int categoryLevel) {
        return (float) (categoryLevel * 0.04 * 100);
    }
}