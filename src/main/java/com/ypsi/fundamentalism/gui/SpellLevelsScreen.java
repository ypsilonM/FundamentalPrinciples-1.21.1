package com.ypsi.fundamentalism.gui;

import com.mojang.blaze3d.platform.InputConstants;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellLevelsScreen extends Screen {
    private static final int WINDOW_WIDTH = 328;
    private static final int WINDOW_HEIGHT = 210;
    private int leftPos, topPos;
    private double scrollOffset;
    private boolean isDragging = false;
    private double lastMouseX;

    private final String[] CATEGORIES = {
            "createEntity", "usesShoot", "usesSummon", "usesTargeting",
            "hasRecasts", "usesTeleport", "addEffects",
            "createsAoeEntities", "usesMobility", "usesRaycast",
            "usesHealing", "usesPotentiation"
    };

    private static final int CARD_WIDTH = 40;
    private static final int CARD_HEIGHT = 60;
    private static final int CARD_SPACING = 10;
    private static final int VISIBLE_CARDS = 6; // Número de cartas visibles a la vez

    public SpellLevelsScreen() {
        super(Component.literal("Fundamentals Experience"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - WINDOW_WIDTH) / 2;
        this.topPos = (this.height - WINDOW_HEIGHT) / 2;
        this.scrollOffset = 0;
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
        super.renderBlurredBackground(partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Fondo semitransparente
        guiGraphics.fill(0, 0, this.width, this.height, 0x80101010);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Fondo principal de la ventana
        guiGraphics.fill(leftPos, topPos, leftPos + WINDOW_WIDTH, topPos + WINDOW_HEIGHT, 0x882D2D2D);
        guiGraphics.renderOutline(leftPos, topPos, WINDOW_WIDTH, WINDOW_HEIGHT, 0xFF2D2D2D);

        // Título
        Component styledTitle = this.title.copy().withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_PURPLE);
        int titleWidth = this.font.width(styledTitle);
        int titleX = leftPos + (WINDOW_WIDTH - titleWidth) / 2;
        guiGraphics.drawString(this.font, styledTitle, titleX, topPos + 6, 0xFFFFFF, false);

        // Área de contenido con recorte para las cartas
        int contentTop = topPos + 25;
        int contentHeight = WINDOW_HEIGHT - 35;
        guiGraphics.enableScissor(leftPos + 8, contentTop, leftPos + WINDOW_WIDTH - 8, contentTop + contentHeight);

        renderCardDeck(guiGraphics, mouseX, mouseY, contentTop, contentHeight);

        guiGraphics.disableScissor();

        // Flechas de navegación (opcional)
        renderNavigationArrows(guiGraphics, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderCardDeck(GuiGraphics guiGraphics, int mouseX, int mouseY, int contentTop, int contentHeight) {
        int totalCardsWidth = CATEGORIES.length * (CARD_WIDTH + CARD_SPACING) - CARD_SPACING;
        int maxScroll = Math.max(0, totalCardsWidth - (WINDOW_WIDTH - 30));

        // Limitar el scroll
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        int startX = leftPos + 15 - (int)scrollOffset;
        int cardY = contentTop + (contentHeight - CARD_HEIGHT) / 2;

        for (int i = 0; i < CATEGORIES.length; i++) {
            int cardX = startX + i * (CARD_WIDTH + CARD_SPACING);

            // Solo renderizar cartas que estén dentro del área visible
            if (cardX + CARD_WIDTH >= leftPos + 8 && cardX <= leftPos + WINDOW_WIDTH - 8) {
                renderCard(guiGraphics, CATEGORIES[i], cardX, cardY, mouseX, mouseY, i);
            }
        }
    }

    private void renderCard(GuiGraphics guiGraphics, String category, int x, int y, int mouseX, int mouseY, int index) {
        int level = ClientCategoryLevelsData.getLevel(category);
        float progress = ClientCategoryLevelsData.getProgress(category);

        boolean isHovered = isMouseOverCard(mouseX, mouseY, x, y);
        boolean isSelected = isCardSelected(index);

        // Color de fondo de la carta (dependiendo del nivel)
        int bgColor = getCardColor(level);
        int borderColor = isHovered ? 0xFFFFFFAA : (isSelected ? 0xFFFFD700 : 0xFF888888);

        // Cuerpo de la carta
        guiGraphics.fill(x, y, x + CARD_WIDTH, y + CARD_HEIGHT, bgColor);
        guiGraphics.renderOutline(x, y, CARD_WIDTH, CARD_HEIGHT, borderColor);

        // Efecto de elevación al hacer hover
        if (isHovered) {
            guiGraphics.fill(x + 2, y + 2, x + CARD_WIDTH - 2, y + CARD_HEIGHT - 2, 0x22FFFFFF);
        }

        // Símbolo de la categoría (centrado)
        String symbol = SpellCategoryProgression.getCategorySymbol(category);
        int symbolX = x + (CARD_WIDTH - this.font.width(symbol)) / 2;
        int symbolY = y + 10;
        guiGraphics.drawString(this.font, symbol, symbolX, symbolY, 0xEFBF04, false);

        // Nivel (en la parte inferior)
        String levelText = "Lvl " + level;
        int levelX = x + (CARD_WIDTH - this.font.width(levelText)) / 2;
        int levelY = y + CARD_HEIGHT - 20;
        guiGraphics.drawString(this.font, levelText, levelX, levelY, 0xFFFFFF, false);

        // Barra de progreso para niveles no máximos
        if (level < 20 && progress > 0) {
            renderProgressBar(guiGraphics, x, y, progress);
        }

        // Indicador de nivel máximo
        if (level >= 20) {
            renderMaxLevelIndicator(guiGraphics, x, y);
        }

        // Tooltip al hacer hover
        if (isHovered) {
            renderCardTooltip(guiGraphics, category, level, progress, mouseX, mouseY);
        }
    }

    private int getCardColor(int level) {
        // Colores basados en el nivel (similar a cartas de poker/trading)
        if (level >= 20) return 0xFF4A148C; // Púrpura - nivel máximo
        if (level >= 15) return 0xFF1565C0; // Azul - épico
        if (level >= 10) return 0xFF2E7D32; // Verde - raro
        if (level >= 5) return 0xFFF9A825;  // Amarillo - poco común
        return 0xFF424242; // Gris - común
    }

    private void renderProgressBar(GuiGraphics guiGraphics, int x, int y, float progress) {
        int barWidth = CARD_WIDTH - 8;
        int barHeight = 3;
        int barX = x + 4;
        int barY = y + CARD_HEIGHT - 25;

        // Fondo de la barra
        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF555555);

        // Progreso
        int progressWidth = (int)(barWidth * progress);
        if (progressWidth > 0) {
            guiGraphics.fill(barX, barY, barX + progressWidth, barY + barHeight, 0xFF00FF00);
        }
    }

    private void renderMaxLevelIndicator(GuiGraphics guiGraphics, int x, int y) {
        // Estrella dorada para nivel máximo
        String star = "★";
        int starX = x + CARD_WIDTH - 8;
        int starY = y + 2;
        guiGraphics.drawString(this.font, star, starX, starY, 0xFFFFD700, false);
    }

    private boolean isMouseOverCard(int mouseX, int mouseY, int cardX, int cardY) {
        return mouseX >= cardX && mouseX <= cardX + CARD_WIDTH &&
                mouseY >= cardY && mouseY <= cardY + CARD_HEIGHT;
    }

    private boolean isCardSelected(int index) {
        // Aquí puedes implementar lógica para cartas seleccionadas
        // Por ejemplo, basado en el scroll o selección del jugador
        return false;
    }

    private void renderCardTooltip(GuiGraphics guiGraphics, String category, int level, float progress, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.literal(SpellCategoryProgression.getCategoryDisplayName(category))
                .withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("Level: " + level + "/20")
                .withStyle(ChatFormatting.GRAY));

        if (level < 20) {
            int currentExp = ClientCategoryLevelsData.getExperience(category);
            int expNeeded = 100 * (level + 1);
            tooltip.add(Component.literal("Progress: " + (int)(progress * 100) + "%")
                    .withStyle(ChatFormatting.GREEN));
            tooltip.add(Component.literal("XP: " + currentExp + "/" + expNeeded)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.literal("MAX LEVEL")
                    .withStyle(ChatFormatting.GOLD).withStyle(ChatFormatting.BOLD));
        }

        // Calcular aproximadamente el ancho del tooltip (la línea más larga)
        int estimatedWidth = 0;
        for (Component line : tooltip) {
            estimatedWidth = Math.max(estimatedWidth, this.font.width(line));
        }
        estimatedWidth += 15; // Margen

        // Determinar si mostrar a la izquierda o derecha
        int tooltipX = mouseX;
        if (mouseX > this.width / 2) {
            tooltipX = mouseX - estimatedWidth - 5; // Siempre a la izquierda cuando está en la mitad derecha
        } else {
            tooltipX += 5; // A la derecha cuando está en la mitad izquierda
        }

        guiGraphics.renderTooltip(this.font, tooltip, Optional.empty(), tooltipX, mouseY);
    }

    private void renderNavigationArrows(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Flecha izquierda
        int arrowY = topPos + WINDOW_HEIGHT / 2;
        boolean leftHovered = isMouseOverArrow(mouseX, mouseY, leftPos + 5, arrowY, true);
        int leftColor = leftHovered ? 0xFFFFFFAA : 0xFF888888;

        // Flecha derecha
        boolean rightHovered = isMouseOverArrow(mouseX, mouseY, leftPos + WINDOW_WIDTH - 15, arrowY, false);
        int rightColor = rightHovered ? 0xFFFFFFAA : 0xFF888888;

        // Dibujar flechas (símbolos simples)
        guiGraphics.drawString(this.font, "◀", leftPos + 8, arrowY - 4, leftColor, false);
        guiGraphics.drawString(this.font, "▶", leftPos + WINDOW_WIDTH - 12, arrowY - 4, rightColor, false);
    }

    private boolean isMouseOverArrow(int mouseX, int mouseY, int arrowX, int arrowY, boolean isLeft) {
        int arrowWidth = 10;
        int arrowHeight = 12;
        return mouseX >= arrowX && mouseX <= arrowX + arrowWidth &&
                mouseY >= arrowY && mouseY <= arrowY + arrowHeight;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Verificar clic en flechas de navegación
            int arrowY = topPos + WINDOW_HEIGHT / 2;

            if (isMouseOverArrow((int)mouseX, (int)mouseY, leftPos + 5, arrowY, true)) {
                scrollOffset = Math.max(0, scrollOffset - (CARD_WIDTH + CARD_SPACING));
                return true;
            }

            if (isMouseOverArrow((int)mouseX, (int)mouseY, leftPos + WINDOW_WIDTH - 15, arrowY, false)) {
                int totalCardsWidth = CATEGORIES.length * (CARD_WIDTH + CARD_SPACING) - CARD_SPACING;
                int maxScroll = Math.max(0, totalCardsWidth - (WINDOW_WIDTH - 30));
                scrollOffset = Math.min(maxScroll, scrollOffset + (CARD_WIDTH + CARD_SPACING));
                return true;
            }

            // Verificar clic en cartas
            int contentTop = topPos + 25;
            int contentHeight = WINDOW_HEIGHT - 35;
            int startX = leftPos + 15 - (int)scrollOffset;
            int cardY = contentTop + (contentHeight - CARD_HEIGHT) / 2;

            for (int i = 0; i < CATEGORIES.length; i++) {
                int cardX = startX + i * (CARD_WIDTH + CARD_SPACING);
                if (isMouseOverCard((int)mouseX, (int)mouseY, cardX, cardY)) {
                    // Aquí puedes agregar interacción con la carta
                    handleCardClick(CATEGORIES[i], i);
                    return true;
                }
            }
        }

        // Iniciar arrastre
        if (button == 0) {
            isDragging = true;
            lastMouseX = mouseX;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && button == 0) {
            double deltaX = mouseX - lastMouseX;
            scrollOffset = Math.max(0, Math.min(
                    scrollOffset - deltaX * 1.5,
                    getMaxScrollOffset()
            ));
            lastMouseX = mouseX;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        // Scroll horizontal con la rueda del mouse
        scrollOffset = Math.max(0, Math.min(
                scrollOffset - scrollY * (CARD_WIDTH + CARD_SPACING),
                getMaxScrollOffset()
        ));
        return true;
    }

    private int getMaxScrollOffset() {
        int totalCardsWidth = CATEGORIES.length * (CARD_WIDTH + CARD_SPACING) - CARD_SPACING;
        return Math.max(0, totalCardsWidth - (WINDOW_WIDTH - 30));
    }

    private void handleCardClick(String category, int index) {
        // Aquí puedes implementar lo que pasa al hacer clic en una carta
        // Por ejemplo: mostrar detalles, seleccionar, etc.
        Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Selected: " + SpellCategoryProgression.getCategoryDisplayName(category))
                        .withStyle(ChatFormatting.YELLOW),
                true
        );
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

        // Navegación con teclado
        if (keyCode == InputConstants.KEY_LEFT) {
            scrollOffset = Math.max(0, scrollOffset - (CARD_WIDTH + CARD_SPACING));
            return true;
        }
        if (keyCode == InputConstants.KEY_RIGHT) {
            scrollOffset = Math.min(getMaxScrollOffset(), scrollOffset + (CARD_WIDTH + CARD_SPACING));
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}