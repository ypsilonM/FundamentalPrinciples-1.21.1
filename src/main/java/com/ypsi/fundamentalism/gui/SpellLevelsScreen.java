package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellLevelsScreen extends Screen {

    private static final int WINDOW_WIDTH = 260;
    private static final int WINDOW_HEIGHT = 200;
    private int leftPos, topPos;

    private final String[] CATEGORIES = {
            "createEntity", "usesShoot", "usesSummon", "usesTargeting",
            "hasRecasts", "usesTeleport", "addEffects",
            "createsAoeEntities", "usesMobility", "usesRaycast",
            "usesHealing", "usesPotentiation"
    };

    public SpellLevelsScreen() {
        super(Component.literal("Fundamentals Experience"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (int) ((this.width - WINDOW_WIDTH) / 2.0);
        this.topPos = (int) ((this.height - WINDOW_HEIGHT) / 2.0);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0x00101010);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.fill(leftPos, topPos, leftPos + WINDOW_WIDTH, topPos + WINDOW_HEIGHT, 0xFF2D2D2D);

        Component styledTitle = this.title.copy().withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
        int titleWidth = this.font.width(styledTitle);
        int titleX = leftPos + (WINDOW_WIDTH - titleWidth) / 2;
        guiGraphics.drawString(this.font, styledTitle, titleX, topPos + 6, 0xFFFFFF, false);

        for (int i = 0; i < CATEGORIES.length; i++) {
            renderCategory(guiGraphics, CATEGORIES[i], i, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderCategory(GuiGraphics guiGraphics, String category, int index, int mouseX, int mouseY) {
        int x = (leftPos + 10);
        int y = (topPos + 20 + (index * 16));

        int level = ClientCategoryLevelsData.getLevel(category);
        float progress = ClientCategoryLevelsData.getProgress(category);
        String displayName = SpellCategoryProgression.getCategoryDisplayName(category)+" "+ SpellCategoryProgression.getCategorySymbol(category);
        guiGraphics.fill(x, y, x + 200, y + 16, 0xFF2D2D2D);

        guiGraphics.drawString(this.font, displayName, x, y + 4, 0xFFE0E0E0, false);

//        String levelText = "Nvl " + level;
//        int levelWidth = getSmallStringWidth(levelText);
//
//        guiGraphics.drawString(this.font, levelText, leftPos + WINDOW_WIDTH - levelWidth - 60, y + 4, 0x404040, false);

        renderProgressBar(guiGraphics, x + 130, y+2, progress, level);

        if (isMouseOverCategory(mouseX, mouseY, x, y)) {
            renderTooltip(guiGraphics, category, level, progress, mouseX, mouseY);
        }
    }

    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y, float progress, int level) {
        int barWidth = 110;
        int barHeight = 12;
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF555555);

        int progressWidth = (int) (barWidth * progress);
        if (progressWidth > 0) {
            int color = level >= 20 ? 0xFF830CE8 : 0xFF0C7AE8; // Verde si es nivel máximo
            guiGraphics.fill(x, y, x + progressWidth, y + barHeight, color);
        }
        //
        //guiGraphics.renderOutline(x, y, barWidth, barHeight, 0xFF000000);
        if (level < 20) {
            String progressText = (int)(progress * 100) + "%";
            int textX = x + (barWidth - getSmallStringWidth(progressText)) / 2;
            int textY = y + (barHeight - 8) / 2;
            guiGraphics.drawString(this.font, progressText, textX, textY, 0xFFE0E0E0, false);
        } else {
            String maxText = "MAX";
            int textX = x + (barWidth - getSmallStringWidth(maxText)) / 2;
            int textY = y + (barHeight - 8) / 2;
            guiGraphics.drawString(this.font, maxText, textX, textY, 0xFFE0E0E0, false);
        }
    }
    private int getSmallStringWidth(String text) {
        return (int)(this.font.width(text));
    }

    private void renderTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(SpellCategoryProgression.getCategoryDisplayName(category))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Lvl: " + level + "/20")
                .withStyle(ChatFormatting.GRAY));
        if (level < 20) {
            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int expNeeded = 100 * (level + 1);
            tooltip.add(Component.literal("Progress: " + currentExp + "/" + expNeeded + " XP")
                    .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal((int)(progress * 100) + "% to lvl " + (level + 1))
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.literal("Max Level Reached!")
                    .withStyle(ChatFormatting.GREEN).withStyle(ChatFormatting.BOLD));
        }
        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
    }

    private boolean isMouseOverCategory(int mouseX, int mouseY, int categoryX, int categoryY) {
        return mouseX >= categoryX && mouseX <= categoryX + 200 &&
                mouseY >= categoryY && mouseY <= categoryY + 16;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == InputConstants.KEY_ESCAPE ||
                ModKeyBinds.SPELL_CATEGORIES.get().matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
