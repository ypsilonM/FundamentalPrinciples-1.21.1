package com.ypsi.fundamentalism.mixins.clientMixins;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attachments.FatigueManager;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
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

    //EXTRA INFO LIKE "% TP FAILURE" AND PRINCIPLES SYMBOLS
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
                            .map(PrinciplesProgressionManager::getCategorySymbol)
                            .collect(Collectors.joining(" "));

                    Component symbolsComponent = Component.literal(symbolsText).withStyle(ChatFormatting.GOLD);


                    int symbolsWidth = font.width(symbolsComponent);
                    int symbolsY = spellNameY - font.lineHeight - 2;

                    guiGraphics.drawString(font, symbolsComponent,
                            centerX - symbolsWidth / 2, symbolsY, 0xFFFFFF, true);

                    if(categories.contains("usesTeleport") && ServerConfig.PRINCIPLES_SYSTEM.get() && ServerConfig.ACTIVE_APPARITIO.get()) {
                        var swsm = ClientMagicData.getSpellSelectionManager();
                        int categoryLevel = PrinciplesProgressionManager.getCategoryLevel(minecraft.player, "usesTeleport");
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
            FundamentalPrinciples.LOGGER.error("Error in SpellWheelOverlayMixin", e);
        }
    }

    //DOMINAN SPELLS VISUAL COLOR CHANGED
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
        if (categories.size() >= ServerConfig.DOMINAN_PRINCIPLES.get() ){
            MutableComponent newTitle = Component.literal("");
            newTitle.append(originalName.copy()
                    .withStyle(ChatFormatting.GOLD).withStyle(Style.EMPTY.withUnderlined(false)));

            return newTitle;
        }else{
            return originalName.withStyle(Style.EMPTY.withUnderlined(true));
        }
    }


    //RENDER VISUAL MANA MODIFIED COST
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
        if(player!=null) {

            int efficiencyLvl = 5;
            if(ServerConfig.EFFICIENCY_ATTRIBUTE.get())
                efficiencyLvl = player.getData(YpsAttachments.CAST_EFFICIENCY.get()).getEfficiencyLevel();

            certumLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.CERTUM);

            if (ServerConfig.FATIGUE_SYSTEM.get() && ServerConfig.ACTIVE_CERTUM.get() && ServerConfig.PRINCIPLES_SYSTEM.get()) {

                if(SpellCategoriesGenerator.isInPrinciple(spell.getSpellId(), Principles.CERTUM)) {
                    originalCost = (int) (
                            originalCost * (1 + Util.certumManaMultiplier(FatigueManager.getFatigueLevel(player), certumLevel)) * (
                                    ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, true) : 1)
                    );
                }else{
                    originalCost = (int) (
                            originalCost * (
                                    ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1)
                    );
                }
            }else{
                originalCost = (int) (
                        originalCost * (
                                ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1)
                );
            }
        }
        return (originalCost);
    }


    private List<String> getSpellCategories(AbstractSpell spell) {
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spell.getSpellId());
        return new ArrayList<>(categories);
    }

}