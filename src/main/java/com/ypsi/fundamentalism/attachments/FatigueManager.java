package com.ypsi.fundamentalism.attachments;

import net.minecraft.world.entity.player.Player;

import static com.ypsi.fundamentalism.util.Util.getMaxFatigue;

public class FatigueManager {

    public static void setFatigueLevel(Player player, int level){
        player.setData(YpsAttachments.LEVEL_FATIGUE, level);
        YpsAttributeManager.FATIGUE.applyModifier(player, level);
    }

    public static void setFatigueAmount(Player player, int amount){
        player.setData(YpsAttachments.CURRENT_FATIGUE, amount);
    }

    public static int getFatigueLevel(Player player){
        return player.getData(YpsAttachments.LEVEL_FATIGUE);
    }
    public static int getFatigueAmount(Player player){
        return player.getData(YpsAttachments.CURRENT_FATIGUE);
    }

    public static void cleanFatigue(Player player){
        player.setData(YpsAttachments.CURRENT_FATIGUE, 0);
        player.setData(YpsAttachments.LEVEL_FATIGUE, 0);
    }

    public static void addFatigue(Player player, int amountToAdd){
        int currentLevel = getFatigueLevel(player);
        int currentEx = getFatigueAmount(player);
        int remainingToAdd = amountToAdd;

        while (remainingToAdd > 0 && currentLevel <= 4) {
            int maxExPts = getMaxFatigue(currentLevel, player);
            int remainingSpace = maxExPts - currentEx;

            if (remainingToAdd <= remainingSpace) {
                currentEx += remainingToAdd;
                remainingToAdd = 0;
            } else {
                currentEx = maxExPts;
                remainingToAdd -= remainingSpace;

                if (currentLevel < 4) {
                    currentLevel++;
                    currentEx = 0;
                } else {
                    remainingToAdd = 0;
                }
            }
        }
        FatigueManager.setFatigueLevel(player, currentLevel);
        FatigueManager.setFatigueAmount(player, currentEx);
    }
}
