package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellLevelsScreen extends Screen {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 210;
    private int leftPos, topPos;
    private float rotation = 0.0f;

    private String selectedCategory = null;
    private static final int LARGE_ICON_SIZE = 48;
    private static final int CENTER_ANIMATION_TIME = 30;
    private int animationTimer = 0;

    private final String[] CATEGORIES = {
            "createEntity", "usesShoot", "usesSummon", "usesTargeting",
            "hasRecasts", "usesTeleport", "addEffects",
            "createsAoeEntities", "usesMobility", "usesRaycast",
            "usesHealing", "usesPotentiation"
    };

    private static final int ICON_SIZE = 24;
    private static final int CIRCLE_RADIUS = 70;

    public SpellLevelsScreen() {
        super(Component.literal("Fundamentals Experience"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - WINDOW_WIDTH) / 2;
        this.topPos = (this.height - WINDOW_HEIGHT) / 2;
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        super.renderBlurredBackground(partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        //guiGraphics.fill(0, 0, this.width, this.height, 0x80101010);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);

        guiGraphics.fill(leftPos, topPos, leftPos + WINDOW_WIDTH, topPos + WINDOW_HEIGHT, 0x882D2D2D);
        guiGraphics.renderOutline(leftPos, topPos, WINDOW_WIDTH, WINDOW_HEIGHT, 0xFF2D2D2D);

        Component styledTitle = this.title.copy().withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.LIGHT_PURPLE);
        int titleWidth = this.font.width(styledTitle);
        int titleX = leftPos + (WINDOW_WIDTH - titleWidth) / 2;
        guiGraphics.drawString(this.font, styledTitle, titleX, topPos + 6, 0xFFFFFF, false);

        guiGraphics.enableScissor(leftPos + 8, topPos + 20, leftPos + WINDOW_WIDTH - 8, topPos + WINDOW_HEIGHT - 8);

        renderCircularIcons(guiGraphics, mouseX, mouseY);

        guiGraphics.disableScissor();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderCircularIcons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int centerX = leftPos + WINDOW_WIDTH / 2;
        int centerY = topPos + WINDOW_HEIGHT / 2;

        if (selectedCategory != null) {
            renderLargeCenterIcon(guiGraphics, selectedCategory, centerX, centerY);
        }

        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(selectedCategory)) continue;

            double angle = 2 * Math.PI * i / CATEGORIES.length + rotation;
            int x = centerX + (int) (CIRCLE_RADIUS * Math.cos(angle)) - ICON_SIZE / 2;
            int y = centerY + (int) (CIRCLE_RADIUS * Math.sin(angle)) - ICON_SIZE / 2;

            renderIcon(guiGraphics, CATEGORIES[i], x, y, mouseX, mouseY, i);
        }
    }

    private void renderLargeCenterIcon(GuiGraphics guiGraphics, String category, int centerX, int centerY) {
        float totalScale;

        if (animationTimer > 0) {
            float progress = 1.0f - (float)animationTimer / CENTER_ANIMATION_TIME;
            // Animación de 0.5 a 2.0
            totalScale = 0.5f + progress * 1.5f; // 0.5 → 2.0
            animationTimer--;
        } else {
            // Cuando termina la animación, mantener en 2.0
            totalScale = 2.0f;
        }

        int x = centerX - LARGE_ICON_SIZE / 2;
        int y = centerY - LARGE_ICON_SIZE / 2;

        // Fondo del icono grande (TAMAÑO ORIGINAL)
        int bgColor = 0xFF444444;
        guiGraphics.fill(x, y, x + LARGE_ICON_SIZE, y + LARGE_ICON_SIZE, bgColor);

        // Borde resaltado (TAMAÑO ORIGINAL)
        int borderColor = 0xFFAA00FF;
        guiGraphics.renderOutline(x - 1, y - 1, LARGE_ICON_SIZE + 2, LARGE_ICON_SIZE + 2, borderColor);
        guiGraphics.renderOutline(x, y, LARGE_ICON_SIZE, LARGE_ICON_SIZE, 0xFFCC44FF);

        // Solo el símbolo se escala
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0);
        poseStack.scale(totalScale, totalScale, 1.0f);

        String symbol = SpellCategoryProgression.getCategorySymbol(category);
        int symbolX = -this.font.width(symbol) / 2;
        int symbolY = -4;

        int textColor = 0xEFBF04;
        guiGraphics.drawString(this.font, symbol, symbolX, symbolY, textColor, false);

        poseStack.popPose();

        // Nombre (sin escala)
        String displayName = SpellCategoryProgression.getCategoryDisplayName(category).replace("Principle", "").trim();
        int nameX = centerX - this.font.width(displayName) / 2;
        int nameY = y + LARGE_ICON_SIZE + 8;

        int nameBgWidth = this.font.width(displayName) + 6;
        int nameBgX = nameX - 3;
        guiGraphics.fill(nameBgX, nameY - 2, nameBgX + nameBgWidth, nameY + 10, 0xAA000000);
        guiGraphics.drawString(this.font, displayName, nameX, nameY, 0xFFFFFF, true);

        int level = ClientCategoryLevelsData.getLevel(category);
        float progressValue = ClientCategoryLevelsData.getProgress(category);
        renderSelectedCategoryTooltip(guiGraphics, category, level, progressValue, centerX, centerY);
    }

    private void renderSelectedCategoryTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int centerX, int centerY) {
        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.literal(SpellCategoryProgression.getCategoryDisplayName(category))
                .withStyle(ChatFormatting.BOLD, ChatFormatting.GOLD));

        boolean isMaxLevel = level >= 20;

        if (isMaxLevel) {
            tooltip.add(Component.literal("Level: MAX")
                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD, ChatFormatting.ITALIC));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(""));

        } else {
            tooltip.add(Component.literal("Level: " + level + "/20")
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            tooltip.add(Component.literal("Progress: " + (int)(progress * 100) + "%")
                    .withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int expNeeded = 100 * (level + 1);
            tooltip.add(Component.literal("XP: " + currentExp + "/" + expNeeded)
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(""));
        }

        tooltip.add(Component.literal("Stats:")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));

        float power = calculatePowerForCategory(category, level);
        power = Math.round(power * 100.0f)/100.0f;
        String sign = power > 0 ? "+" : "";
        ChatFormatting color = power < 0 ? ChatFormatting.RED : ChatFormatting.BLUE;
        tooltip.add(Component.literal("• " + sign + power + "% Power")
                .withStyle(color));

        if(category.equals("hasRecasts")) {
            float recastProb = calculateProbabilityForRecast(level);
            recastProb = Math.round(recastProb * 100.0f) / 100.0f;
            tooltip.add(Component.literal("• " + recastProb + "% +1 Recast")
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if(category.equals("usesTeleport")){
            float addProb = calculateProbabilityForAddTp(level);
            addProb = Math.round(addProb * 100.0f) / 100.0f;
            tooltip.add(Component.literal("• +" + addProb + "% Chance")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
        }
        if(category.equals("usesTargeting")){
            int addRange = getTotalRange(level);
            tooltip.add(Component.literal("• +" + addRange + " Distance")
                    .withStyle(ChatFormatting.GREEN));
        }

        renderTooltipBackgroundAndText(guiGraphics, tooltip, centerX, centerY, isMaxLevel ? -1 : progress);
    }
    private static int getTotalRange(int level){
        return (int) (level*1.5);
    }
    private static float calculateProbabilityForAddTp(int categoryLevel) {
        return (float)((categoryLevel * 0.025) * 100);
    }
    private float calculateProbabilityForRecast(int categoryLevel) {
        return (float)(categoryLevel * 0.04 * 100);
    }
    private float calculatePowerForCategory(String category, int level) {
        if (category.equals("usesShoot") || category.equals("createsAoeEntities") || category.equals("usesSummon")) {
            return (float) getSubEntityModificator(level) * 100;
        } else {
            return (float) getModificator(level) * 100;
        }
    }

    private void renderTooltipBackgroundAndText(GuiGraphics guiGraphics, List<Component> tooltip, int centerX, int centerY, float progress) {
        int maxWidth = tooltip.stream()
                .mapToInt(line -> this.font.width(line))
                .max()
                .orElse(120);
        maxWidth = Math.max(maxWidth, 120);

        int tooltipX = centerX + 100;
        int tooltipY = centerY - 50;
        int bgPadding = 6;

        int bgX = tooltipX - bgPadding;
        int bgY = tooltipY - bgPadding;
        int bgWidth = maxWidth + bgPadding * 2;
        int bgHeight = tooltip.size() * 10 + bgPadding * 2;

        boolean hasProgressBar = progress >= 0;
        if (hasProgressBar) {
            bgHeight += 10;
        }

        guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0xDD000000);
        guiGraphics.renderOutline(bgX, bgY, bgWidth, bgHeight, 0xFFAA00FF);

        if (hasProgressBar) {
            renderProgressBar(guiGraphics, progress, tooltipX, tooltipY, 4);
        }

        for (int i = 0; i < tooltip.size(); i++) {
            guiGraphics.drawString(this.font, tooltip.get(i), tooltipX, tooltipY + i * 10, 0xFFFFFF, false);
        }
    }


    private double getModificator(int level){
        double basePercentage = -0.10;
        for(int i=0;i<level;i++){
            double increment = 0.01;
            basePercentage+=increment;
        }
        return basePercentage;
    }
    private double getSubEntityModificator(int level){
        double basePercentage = -0.05;
        for(int i=0;i<level;i++){
            double increment = 0.005;
            basePercentage+=increment;
        }
        return basePercentage;
    }


    private void renderProgressBar(GuiGraphics guiGraphics, float progress, int x, int y, int lineIndex) {
        int barWidth = 120;
        int barHeight = 6;
        int barX = x;
        int barY = y + lineIndex * 10 + 3;
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF330033); // Morado muy oscuro
        int progressWidth = (int) (barWidth * progress);
        if (progressWidth > 0) {
            guiGraphics.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFFAA00FF);
            drawPurpleGradientBar(guiGraphics, barX, barY, progressWidth, barHeight);
        }
        guiGraphics.renderOutline(barX, barY, barWidth, barHeight, 0xFFCC66FF);
    }

    private void drawPurpleGradientBar(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        if (width <= 0) return;
        int segments = Math.min(width, 10);
        int segmentWidth = width / segments;

        for (int i = 0; i < segments; i++) {
            int segmentX = x + i * segmentWidth;
            int segmentEndX = (i == segments - 1) ? x + width : segmentX + segmentWidth;
            float t = (float) i / (segments - 1);
            int r = (int)(85 + (170 * t));
            int g = 0;
            int b = (int)(128 + (127 * t));

            int color = (0xFF << 24) | (r << 16) | (g << 8) | b;
            guiGraphics.fill(segmentX, y, segmentEndX, y + height, color);
        }
    }

    private void renderTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(SpellCategoryProgression.getCategoryDisplayName(category))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Level: " + level + "/20")
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        if (level < 20) {
            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int expNeeded = 100 * (level + 1);
            tooltip.add(Component.literal("Progress: " + (int)(progress * 100) + "%")
                    .withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC));
            tooltip.add(Component.literal("XP: " + currentExp + "/" + expNeeded)
                    .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
        } else {
            tooltip.add(Component.literal("MAX LEVEL")
                    .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
        }
        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), mouseX, mouseY);
    }

    private void renderIcon(GuiGraphics guiGraphics, String category, int x, int y, int mouseX, int mouseY, int index) {
        int level = ClientCategoryLevelsData.getLevel(category);
        float progress = ClientCategoryLevelsData.getProgress(category);

        int bgColor = 0xFF333333;
        guiGraphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, bgColor);
        int borderColor = 0xFF888888;

        guiGraphics.renderOutline(x, y, ICON_SIZE, ICON_SIZE, borderColor);

        String symbol = SpellCategoryProgression.getCategorySymbol(category);
        int symbolX = x + (ICON_SIZE - this.font.width(symbol)) / 2;
        int symbolY = y + (ICON_SIZE - 8) / 2;

        int textColor = 0xEFBF04;
        guiGraphics.drawString(this.font, symbol, symbolX, symbolY, textColor, false);

        // Efecto de hover
        if (isMouseOverIcon(mouseX, mouseY, x, y)) {
            guiGraphics.fill(x, y, x + ICON_SIZE, y + ICON_SIZE, 0x44FFFFFF);
            renderTooltip(guiGraphics, category, level, progress, mouseX, mouseY);
        }

        // Línea de progreso (opcional, alrededor del icono)
        if (level < 20 && progress > 0) {
            renderProgressOutline(guiGraphics, x, y, progress);
        }
    }

    private void renderProgressOutline(GuiGraphics guiGraphics, int x, int y, float progress) {
        int outlineThickness = 1;
        int totalPerimeter = (ICON_SIZE - 2) * 4;
        int remainingPixels = (int)(totalPerimeter * progress);

        int color = 0xFFCC7722;

        if (remainingPixels > 0) {
            int topPixels = Math.min(remainingPixels, ICON_SIZE - 2);
            guiGraphics.fill(x + 1, y + 1, x + 1 + topPixels, y + 1 + outlineThickness, color);
            remainingPixels -= topPixels;
        }

        if (remainingPixels > 0) {
            int rightPixels = Math.min(remainingPixels, ICON_SIZE - 2);
            guiGraphics.fill(x + ICON_SIZE - 1 - outlineThickness, y + 1,
                    x + ICON_SIZE - 1, y + 1 + rightPixels, color);
            remainingPixels -= rightPixels;
        }

        if (remainingPixels > 0) {
            int bottomPixels = Math.min(remainingPixels, ICON_SIZE - 2);
            guiGraphics.fill(x + ICON_SIZE - 1 - bottomPixels, y + ICON_SIZE - 1 - outlineThickness,
                    x + ICON_SIZE - 1, y + ICON_SIZE - 1, color);
            remainingPixels -= bottomPixels;
        }

        if (remainingPixels > 0) {
            int leftPixels = Math.min(remainingPixels, ICON_SIZE - 2);
            guiGraphics.fill(x + 1, y + ICON_SIZE - 1 - leftPixels,
                    x + 1 + outlineThickness, y + ICON_SIZE - 1, color);
        }
    }

    private boolean isMouseOverIcon(int mouseX, int mouseY, int iconX, int iconY) {
        return mouseX >= iconX && mouseX <= iconX + ICON_SIZE &&
                mouseY >= iconY && mouseY <= iconY + ICON_SIZE;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int centerX = leftPos + WINDOW_WIDTH / 2;
        int centerY = topPos + WINDOW_HEIGHT / 2;

        for (int i = 0; i < CATEGORIES.length; i++) {
            double angle = 2 * Math.PI * i / CATEGORIES.length + rotation;
            int x = centerX + (int) (CIRCLE_RADIUS * Math.cos(angle)) - ICON_SIZE / 2;
            int y = centerY + (int) (CIRCLE_RADIUS * Math.sin(angle)) - ICON_SIZE / 2;

            if (isMouseOverIcon((int)mouseX, (int)mouseY, x, y)) {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.playSound(
                            SoundEvents.UI_BUTTON_CLICK.value(),
                            0.5f,  // volumen
                            1.0f   // pitch
                    );
                }
                // Seleccionar o deseleccionar categoría
                if (CATEGORIES[i].equals(selectedCategory)) {
                    selectedCategory = null; // Deseleccionar
                } else {
                    selectedCategory = CATEGORIES[i];
                    animationTimer = CENTER_ANIMATION_TIME; // Iniciar animación
                }
                return true;
            }
        }

        selectedCategory = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        rotation += scrollY * 0.1f;
        return true;
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


