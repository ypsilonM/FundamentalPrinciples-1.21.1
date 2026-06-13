package com.ypsi.fundamentalism.network.packets.data;

import com.ypsi.fundamentalism.attachments.PrinciplesProgressionManager;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;

import java.util.HashMap;
import java.util.Map;

public class ClientCategoryLevelsData {
    private static Map<String, Integer> clientLevels = new HashMap<>();
    private static Map<String, Integer> clientExperience = new HashMap<>();

    public static void setLevels(Map<String, Integer> levels, Map<String, Integer> experience) {
        clientLevels = new HashMap<>(levels);
        clientExperience = new HashMap<>(experience);
    }

    public static int getLevel(String category) {
        return clientLevels.getOrDefault(category, 0);
    }
    public static int getLevel(Principles principle){
        return clientLevels.getOrDefault(PrinciplesProgressionManager.getTechnicalName(principle), 0);
    }

    public static int getExperience(String category) {
        return clientExperience.getOrDefault(category, 0);
    }
    public static int getExperience(Principles principle){
        return clientExperience.getOrDefault(PrinciplesProgressionManager.getTechnicalName(principle), 0);
    }

    public static float getProgress(String category) {
        int level = getLevel(category);
        if (level >= 20) return 1.0f;

        int nextLevel = level+1;
        int exp = getExperience(category);
        int expNeeded = Util.getExpForPrincipleLevel(nextLevel);

        return (float) exp / expNeeded;
    }

    public static float getProgress(Principles principle) {
        int level = getLevel(principle);
        if (level >= 20) return 1.0f;

        int nextLevel = level+1;
        int exp = getExperience(principle);
        int expNeeded = Util.getExpForPrincipleLevel(nextLevel);

        return (float) exp / expNeeded;
    }
}
