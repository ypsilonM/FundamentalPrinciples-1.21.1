package com.ypsi.fundamentalism.attachments;

import com.ypsi.fundamentalism.attachments.customAtt.EfficiencyAttachment;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.world.entity.player.Player;

public class EfficiencyManager {

    public static int getCurrentLvl(Player player){
        return player.getData(YpsAttachments.CAST_EFFICIENCY.get()).getEfficiencyLevel();
    }
    public static int getCurrentXP(Player player){
        return player.getData(YpsAttachments.CAST_EFFICIENCY.get()).getEfficiencyExperience();
    }

    public static void addXp(int manaWasted, Player player) {
        int currentLevel = getCurrentLvl(player);
        int currentExp = getCurrentXP(player);
        int totalMana = manaWasted;

        while (totalMana > 0) {
            int costPerPoint = getValidAmount(currentLevel);
            if (totalMana >= costPerPoint) {
                totalMana -= costPerPoint;
                currentExp++;

                int xpNeeded = getExpForLevel(currentLevel + 1);
                if (currentExp >= xpNeeded) {
                    currentExp -= xpNeeded;
                    currentLevel++;
                    setLevel(currentLevel, player);
                }
            } else {
                break;
            }
        }

        setExperience(currentExp, player);
        player.syncData(YpsAttachments.CAST_EFFICIENCY);
    }

    public void subXp(int amount, Player player) {
        int currentExp = getCurrentXP(player);
        int currentLevel = getCurrentLvl(player);
        int totalXp = currentExp - amount;

        while (currentLevel > 0 && totalXp < 0) {
            totalXp += getExpForLevel(currentLevel);
            currentLevel--;
            setLevel(currentLevel, player);
        }
        if (totalXp < 0) {
            totalXp = 0;
        }
        setExperience(totalXp, player);
    }


    public static int getValidAmount(int level){
        return switch (level) {
            case 0 -> 5;
            case 1 -> 10;
            case 2 -> 15;
            case 3 -> 20;
            case 4 -> 25;
            case 5 -> 30;
            case 6 -> 40;
            case 7 -> 50;
            case 8 -> 60;
            case 9 -> 80;
            case 10 -> 80;
            default -> 5;
        };
    }


    public static void setExperience(int amount, Player player){
        player.getData(YpsAttachments.CAST_EFFICIENCY.get()).setExperience(amount);
        player.syncData(YpsAttachments.CAST_EFFICIENCY);
    }
    public static void setLevel(int level, Player player){
        player.getData(YpsAttachments.CAST_EFFICIENCY.get()).setLevel(level);
        player.syncData(YpsAttachments.CAST_EFFICIENCY);
    }

    public static int getExpForLevel(int level) {
        return Util.getXpForEfficiencyLevel(level);
    }
}
