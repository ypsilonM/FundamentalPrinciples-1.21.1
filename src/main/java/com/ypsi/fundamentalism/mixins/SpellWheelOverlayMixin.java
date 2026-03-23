package com.ypsi.fundamentalism.mixins;

import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


//CLIENT STUFF
@Mixin(SpellWheelOverlay.class)
public class SpellWheelOverlayMixin {

    @Inject(method = "render", at = @At("TAIL"), remap = false)
    private void addAllExtraInfo(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
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

                    Field ringOuterEdgeMaxField = SpellWheelOverlay.class.getDeclaredField("ringOuterEdgeMax");
                    ringOuterEdgeMaxField.setAccessible(true);
                    float ringOuterEdgeMax = ringOuterEdgeMaxField.getFloat(overlay);

                    int textHeight = Math.max(3, info.size()) * font.lineHeight + 5;
                    int textTitleMargin = 5;
                    int textCenterMargin = 5;

                    int spellNameY = (int) (centerY - (ringOuterEdgeMax + textHeight));

                    String symbolsText = categories.stream()
                            .map(SpellCategoryProgression::getCategorySymbol)
                            .collect(Collectors.joining(" "));

                    Component symbolsComponent = categories.size()>=4?
                            Component.literal(symbolsText).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xCCFFFF))):
                            Component.literal(symbolsText).withStyle(ChatFormatting.GOLD);


                    int symbolsWidth = font.width(symbolsComponent);
                    int symbolsY = spellNameY - font.lineHeight - 2;

                    guiGraphics.drawString(font, symbolsComponent,
                            centerX - symbolsWidth / 2, symbolsY, 0xFFFFFF, true);

                    if(categories.contains("usesTeleport")) {
                        var swsm = ClientMagicData.getSpellSelectionManager();
                        int categoryLevel = SpellCategoryProgression.getCategoryLevel(minecraft.player, "usesTeleport");
                        int cooldown = MagicManager.getEffectiveSpellCooldown(selectedSpell.getSpell(), minecraft.player, swsm.getSpellSlot(wheelSelection).getCastSource()) / 20;

                        int probability = (int) Util.getFailureTPChance(categoryLevel, cooldown);
                        String probabilityText = (100 - probability) + "% Success";

                        Component probabilityComponent = Component.literal(probabilityText)
                                .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD);

                        int probabilityY = (int) (centerY - (ringOuterEdgeMax + textHeight) +
                                font.lineHeight * (info.size() + 1) + textTitleMargin);

                        guiGraphics.drawString(font, probabilityComponent, centerX + textCenterMargin, probabilityY, 0xFFFFFF, true);
                    }

                }
            }
        } catch (Exception e) {
            IronsSpellbooks.LOGGER.error("Error en SpellWheelOverlayMixin", e);
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getDisplayName(Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/network/chat/MutableComponent;"
            ),
            remap = false
    )
    private MutableComponent modifySpellDisplayName(AbstractSpell spell, Player player) {
        MutableComponent originalName = spell.getDisplayName(player);
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        if (categories.size() >=4){
            MutableComponent newTitle = Component.literal("");
            newTitle.append(originalName.copy()
                    .withStyle(Style.EMPTY.withUnderlined(false).withColor(TextColor.fromRgb(0xCCFFFF))));
            return newTitle;
        }else{
            return originalName.withStyle(Style.EMPTY.withUnderlined(true));
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lio/redspace/ironsspellbooks/api/spells/AbstractSpell;getManaCost(I)I"
            ),
            remap = false
    )
    private int redirectGetManaCost(AbstractSpell spell, int spellLevel,
                                    GuiGraphics guiHelper,
                                    DeltaTracker deltaTracker) {

        Player player =Minecraft.getInstance().player;

        int originalCost = spell.getManaCost(spellLevel);
        int certumLevel = 0;
        if(player!=null){
            certumLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.CERTUM);
        }
        if(SpellCategoriesGenerator.isInCategory(spell.getSpellId(), "immutable")){
           originalCost = (int)(originalCost * (1+Util.manaMultiplier( player.getData(YpsAttachments.LEVEL_EXHAUSTION), certumLevel )));
        }
        return (originalCost);
    }


    private List<String> getSpellCategories(AbstractSpell spell) {
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        return new ArrayList<>(categories);
    }

}