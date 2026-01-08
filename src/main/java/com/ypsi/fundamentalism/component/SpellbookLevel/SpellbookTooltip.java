package com.ypsi.fundamentalism.component.SpellbookLevel;

import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;


public class SpellbookTooltip {

    public static void addSpellBookInfo(ItemStack stack, List<Component> tooltip) {
        if (stack.getItem() instanceof SpellBook) {
            int level = SpellBookComponentHelper.getLevel(stack);
            float progress = SpellBookComponentHelper.getProgressToNextLevel(stack);
            ChatFormatting chatFormatting = getChatFormatting(level);
            String rarity = getRarity(level);

            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal(rarity)
                    .withStyle(chatFormatting));

            int xpToNext = SpellBookComponentHelper.getXPToNextLevel(stack);
            String progressBar = createProgressBar(progress, 10, chatFormatting);
            tooltip.add(Component.literal(progressBar + " " + xpToNext + " XP")
                    .withStyle(ChatFormatting.GRAY));

        }
    }

    private static String createProgressBar(float progress, int length, ChatFormatting chatFormatting) {
        int filled = (int)(progress * length);
        int empty = length - filled;

        StringBuilder sb = new StringBuilder();
        sb.append(chatFormatting);
        for (int i = 0; i < filled; i++) sb.append("█");
        sb.append(ChatFormatting.GRAY);
        for (int i = 0; i < empty; i++) sb.append("█");

        return sb.toString();
    }
    private static String getRarity(int level){
        return switch (level){
            case 2 -> "Uncommon";
            case 3 -> "Rare";
            case 4 -> "Epic";
            case 5 -> "Legendary";
            default -> "Common";
        };
    }
    private static ChatFormatting getChatFormatting(int level){
        return switch (level){
            case 1 -> ChatFormatting.WHITE;
            case 2 -> ChatFormatting.GREEN;
            case 3 -> ChatFormatting.BLUE;
            case 4 -> ChatFormatting.DARK_PURPLE;
            case 5 -> ChatFormatting.GOLD;
            default -> ChatFormatting.GRAY;
        };
    }
}
