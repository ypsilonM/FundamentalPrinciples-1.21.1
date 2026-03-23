package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

import java.util.*;

public class SpellLevelsScreen extends Screen {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 210;
    private int leftPos, topPos;

    private String selectedCategory = null;
    private static final int LARGE_ICON_SIZE = 48;
    private static final int CENTER_ANIMATION_TIME = 30;
    private int animationTimer = 0;

    private static final int ICON_SIZE = 32;
    private static final int ICON_SPACING = 34; // Espacio entre iconos
    private static final int ICONS_PER_ROW = 4; // Número de iconos por fila
    private static final int GRID_START_X = 95; // Margen izquierdo para los iconos
    private static final int GRID_START_Y = 60; // Margen superior para los iconos

    private final String[] CATEGORIES = {
            "createEntity", "usesShoot", "usesSummon", "usesTargeting",
            "hasRecasts", "usesTeleport", "addEffects",
            "createsAoeEntities", "usesMobility", "usesRaycast",
            "usesHealing", "usesPotentiation","immutable"
    };

    private Map<String, Integer> hoverTimers = new HashMap<>();
    private static final int HOVER_ANIMATION_TIME = 10;


    public SpellLevelsScreen() {
        super(Component.literal("Magic Principles"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - WINDOW_WIDTH) / 2;
        this.topPos = (this.height - WINDOW_HEIGHT) / 2;
    }


    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        ResourceLocation backgroundTexture = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "textures/gui/pbook.png");

        float scale = 1.3f;

        int originalWidth = 288;
        int originalHeight = 180;

        int scaledWidth = (int) (originalWidth * scale);
        int scaledHeight = (int) (originalHeight * scale);

        int offsetX = leftPos + (WINDOW_WIDTH - scaledWidth) / 2;
        int offsetY = topPos + (WINDOW_HEIGHT - scaledHeight) / 2;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(offsetX, offsetY, 0);
        poseStack.scale(scale, scale, 1.0f);

        guiGraphics.blit(backgroundTexture, 0, 0, 0, 0, originalWidth, originalHeight, originalWidth, originalHeight);

        poseStack.popPose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        Component styledTitle = this.title.copy().withStyle(ChatFormatting.BOLD).withColor(0x4D4942);

        int titleWidth = this.font.width(styledTitle);
        int titleX = leftPos+10 + (WINDOW_WIDTH - titleWidth) / 4;
        guiGraphics.drawString(this.font, styledTitle, titleX, topPos + 30, 0xFFFFFF, false);

        guiGraphics.enableScissor(leftPos + 8, topPos + 20, leftPos + WINDOW_WIDTH - 8, topPos + WINDOW_HEIGHT - 8);

        renderGridIcons(guiGraphics, mouseX, mouseY);

        guiGraphics.disableScissor();
    }

    private void renderGridIcons(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int centerX = leftPos + WINDOW_WIDTH / 2;
        int centerY = topPos + WINDOW_HEIGHT / 2;

        if (selectedCategory != null) {
            int level = ClientCategoryLevelsData.getLevel(selectedCategory);
            float progress = ClientCategoryLevelsData.getProgress(selectedCategory);
            renderSelectedCategoryTooltip(guiGraphics, selectedCategory, level, progress, centerX, centerY);
        }

        for (int i = 0; i < CATEGORIES.length; i++) {

            int row = i / ICONS_PER_ROW;
            int col = i % ICONS_PER_ROW;
            int x = leftPos + GRID_START_X + (col * ICON_SPACING);
            int y = topPos + GRID_START_Y + (row * ICON_SPACING);

            if (!isMouseOverIcon(mouseX, mouseY, x, y)) {
                renderIcon(guiGraphics, CATEGORIES[i], x, y, mouseX, mouseY, i, false);
            }
        }

        for (int i = 0; i < CATEGORIES.length; i++) {

            int row = i / ICONS_PER_ROW;
            int col = i % ICONS_PER_ROW;
            int x = leftPos + GRID_START_X + (col * ICON_SPACING);
            int y = topPos + GRID_START_Y + (row * ICON_SPACING);

            if (isMouseOverIcon(mouseX, mouseY, x, y)) {
                renderIcon(guiGraphics, CATEGORIES[i], x, y, mouseX, mouseY, i, true);
                break;
            }
        }
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
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(""));

        } else {
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("Level: " + level + "/20")
                    .withColor(0x4D4942));
            tooltip.add(Component.literal("Progress: " + (int)(progress * 100) + "%")
                    .withStyle(ChatFormatting.BLUE));

            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int nextLevel = level+1;
            int expNeeded = Util.getExpForLevel(nextLevel);
            tooltip.add(Component.literal("XP: " + currentExp + "/" + expNeeded)
                    .withColor(0x4D4942));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(""));
        }

        tooltip.add(Component.literal("Stats:")
                .withColor(0x4D4942));

        float power = calculatePowerForCategory(category, level);
        power = Math.round(power * 100.0f)/100.0f;
        String sign = power > 0 ? "+" : "";
        //ChatFormatting color = power < 0 ? ChatFormatting.RED : ChatFormatting.BLUE;
        int color = power < 0 ? 0xAD1330 : 0x192BB0;
//        if(!category.equals("immutable")) {
//            tooltip.add(Component.literal("• " + sign + power + "% Power")
//                    .withColor(color));
//        }
        if(category.equals("hasRecasts")) {
            float recastProb = Util.getFloatRecastAddChance(level);
            recastProb = Math.round(recastProb * 100.0f) / 100.0f;
            tooltip.add(Component.literal("• " + recastProb + "% +1 Recast")
                    .withStyle(ChatFormatting.DARK_AQUA));
        }
        if(category.equals("usesTeleport")){
            int addProb = (int) (Util.getFailureTPReduction(level) * 100f);
            tooltip.add(Component.literal("• +" + addProb + "% Chance")
                    .withColor(0x9E2997));
        }
        if(category.equals("usesTargeting")){
            int addRange = (int) (Util.getLocusMultiplier(level)*100);
            tooltip.add(Component.literal("• " + addRange + "% Target Distance")
                    .withColor(0x51A326));
        }
        if(category.equals("usesRaycast")){
            int addRange = Util.getTotalRange(level);
            tooltip.add(Component.literal("• +" + addRange + " Distance")
                    .withColor(0x51A326));
        }
        if(category.equals("createEntity")){
            int addMana = Util.getTotalMana(level);
            tooltip.add(Component.literal("• +" + addMana + " Mana ")
                    .withColor(0x1B4EC2));
        }
        if(category.equals("usesShoot")){
            int accuracy = (int) (Util.getAccuracy(level) * 100f);
            tooltip.add(Component.literal("• " + accuracy + "% Accuracy")
                    .withColor(0xC2AC1B));
        }
        if(category.equals("immutable")){
            int percentage = (int) (Util.manaReduction(level) * 100);
            tooltip.add(Component.literal("• -" + percentage + "% Fatigue Cost")
                    .withColor(0xC2AC1B));
        }
        if(category.equals("createsAoeEntities")){
            int size = (int) (Util.getVolumeMultiplier(level) * 100f);
            tooltip.add(Component.literal("• " + size + "% Radius")
                    .withStyle(ChatFormatting.DARK_RED) );
        }
        double additionalFatigue = 5.0 - (0.5*level);
        tooltip.add(Component.literal("• "+(additionalFatigue>=0?"+":"") + additionalFatigue + "% Fatigue")
                .withStyle((additionalFatigue>=0?ChatFormatting.RED:ChatFormatting.BLUE)));

        renderLargeIconWithTooltip(guiGraphics, category, level, progress, centerX, centerY, tooltip, isMaxLevel ? -1 : progress);
    }

    private void renderLargeIconWithTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int centerX, int centerY, List<Component> tooltipLines, float progressValue) {
        int maxWidth = tooltipLines.stream()
                .mapToInt(line -> this.font.width(line))
                .max()
                .orElse(120);
        maxWidth = Math.max(maxWidth, 120);

        int offsetX = 30;
        int offsetY = -80;

        int tooltipX = centerX + offsetX;
        int tooltipY = centerY + offsetY;


        int iconX = tooltipX +60+ (LARGE_ICON_SIZE) / 2;
        int iconY = tooltipY  + 20;

        float totalScale;
        if (animationTimer > 0) {
            float animProgress = 1.0f - (float)animationTimer / CENTER_ANIMATION_TIME;
            totalScale = 0.5f + animProgress * 1.5f;
            animationTimer--;
        } else {
            totalScale = 2.0f;
        }

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        poseStack.translate(iconX + LARGE_ICON_SIZE / 2, iconY + LARGE_ICON_SIZE / 2, 0);
        poseStack.scale(totalScale, totalScale, 1.0f);

        String symbol = SpellCategoryProgression.getCategorySymbol(category);
        int symbolX = -this.font.width(symbol) / 2;
        int symbolY = -4;
        guiGraphics.drawString(this.font, symbol, symbolX, symbolY, 0xEFBF04, false);

        poseStack.popPose();

        for (int i = 0; i < tooltipLines.size(); i++) {
            guiGraphics.drawString(this.font, tooltipLines.get(i), tooltipX, tooltipY + i * 10, 0xFFFFFF, false);
        }
        if (progressValue >= 0) {
            renderProgressBar(guiGraphics, progressValue, tooltipX, tooltipY + tooltipLines.size() * 10 + 5, 0);
        }
    }


    private float calculatePowerForCategory(String category, int level) {
        if (category.equals("usesShoot") || category.equals("createsAoeEntities") || category.equals("usesSummon")) {
            return (float) getSubEntityModificator(level) * 100;
        } else {
            return (float) getModificator(level) * 100;
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

        // Fondo de la barra
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF8B7E6B);

        int progressWidth = (int) (barWidth * progress);
        if (progressWidth > 0) {
            // Animación de brillo dorado
            float time = (System.currentTimeMillis() % 4000) / 4000.0f; // Ciclo de 2 segundos
            float pulse = 0.7f + 0.3f * (float) Math.sin(time * Math.PI * 2); // 0.7 a 1.3

            // Color dorado base (0xFFD700)
            int goldR = 0xFF;
            int goldG = 0xD7;
            int goldB = 0x00;

            // Aplicar variación por el pulso
            goldR = Math.min(255, (int) (goldR * pulse));
            goldG = Math.min(255, (int) (goldG * pulse));

            int animatedColor = (0xFF << 24) | (goldR << 16) | (goldG << 8) | goldB;

            // Dibujar barra con color animado
            guiGraphics.fill(barX, barY, barX + progressWidth, barY + barHeight, animatedColor);

        }

        // Outline color dorado fijo
        guiGraphics.renderOutline(barX, barY, barWidth, barHeight, 0xFFD700);
    }

    private void drawAncientGradientBar(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        if (width <= 0) return;

        int color = 0xFFB89F73;

        guiGraphics.fill(x, y, x + width, y + height, color);
    }

    private void renderTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(SpellCategoryProgression.getCategoryDisplayName(category))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Level: " + level + "/20")
                .withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        if (level < 20) {
            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int nextLevel = level+1;
            int expNeeded = Util.getExpForLevel(nextLevel);
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

    private void renderIcon(GuiGraphics guiGraphics, String category, int x, int y, int mouseX, int mouseY, int index, boolean isHovered) {
        int level = ClientCategoryLevelsData.getLevel(category);
        float progress = ClientCategoryLevelsData.getProgress(category);

        int hoverTimer = hoverTimers.getOrDefault(category, 0);
        if (isHovered && hoverTimer < HOVER_ANIMATION_TIME) {
            hoverTimer++;
            hoverTimers.put(category, hoverTimer);
        } else if (!isHovered && hoverTimer > 0) {
            hoverTimer--;
            hoverTimers.put(category, hoverTimer);
        }
        float hoverScale = 1.0f + (hoverTimer / (float)HOVER_ANIMATION_TIME) * 0.2f;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(x + ICON_SIZE / 2, y + ICON_SIZE / 2, 0);
        poseStack.scale(hoverScale, hoverScale, 1.0f);
        poseStack.translate(-(x + ICON_SIZE / 2), -(y + ICON_SIZE / 2), 0);

        int borderColor = isHovered ? 0xFFFFD700 : 0xD1BFA188;
        guiGraphics.renderOutline(x, y, ICON_SIZE, ICON_SIZE, borderColor);

        if (isHovered) {
            guiGraphics.renderOutline(x - 1, y - 1, ICON_SIZE + 2, ICON_SIZE + 2, 0xFFFFD700);
        }

        String symbol = SpellCategoryProgression.getCategorySymbol(category);
        int symbolX = x + (ICON_SIZE - this.font.width(symbol)) / 2;
        int symbolY = y + (ICON_SIZE - 8) / 2;

        int textColor = isHovered ? 0xEFBF04 : 0xD1BFA1;
        guiGraphics.drawString(this.font, symbol, symbolX, symbolY, textColor, false);

        if (level < 20 && progress > 0) {
            renderProgressOutline(guiGraphics, x, y, progress);
        }

        poseStack.popPose();

        if (isHovered) {
            renderTooltip(guiGraphics, category, level, progress, mouseX, mouseY);
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
        // Verificar clics en los iconos de la cuadrícula
        for (int i = 0; i < CATEGORIES.length; i++) {
            int row = i / ICONS_PER_ROW;
            int col = i % ICONS_PER_ROW;

            int x = leftPos + GRID_START_X + (col * ICON_SPACING);
            int y = topPos + GRID_START_Y + (row * ICON_SPACING);

            if (isMouseOverIcon((int)mouseX, (int)mouseY, x, y)) {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.player.playSound(
                            SoundEvents.UI_BUTTON_CLICK.value(),
                            0.5f,
                            1.0f
                    );
                }

                if (CATEGORIES[i].equals(selectedCategory)) {
                    selectedCategory = null;
                } else {
                    selectedCategory = CATEGORIES[i];
                    animationTimer = CENTER_ANIMATION_TIME;
                }
                return true;
            }
        }

        // Si se hace clic fuera de los iconos, deseleccionar
        selectedCategory = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
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


