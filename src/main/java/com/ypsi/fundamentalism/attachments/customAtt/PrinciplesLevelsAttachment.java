package com.ypsi.fundamentalism.attachments.customAtt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.advancements.triggers.YpTriggers;
import com.ypsi.fundamentalism.attachments.YpsAttributeManager;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class PrinciplesLevelsAttachment {
    private final Map<String, Integer> categoryLevels;
    private final Map<String, Integer> categoryExperience;
    private LevelChangeListener listener;

    public static final Codec<PrinciplesLevelsAttachment> CODEC = RecordCodecBuilder.create(spellCategoryLevelsInstance ->
            spellCategoryLevelsInstance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("categoryLevels").forGetter(PrinciplesLevelsAttachment::getCategoryLevels),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("categoryExperience").forGetter(PrinciplesLevelsAttachment::getCategoryExperience)
            ).apply(spellCategoryLevelsInstance, PrinciplesLevelsAttachment::new)
    );

    @FunctionalInterface
    public interface LevelChangeListener {
        void onLevelChanged(String category, int oldLevel, int newLevel);
    }

    public void setLevelChangeListener(LevelChangeListener listener) {
        this.listener = listener;
    }
    public PrinciplesLevelsAttachment() {
        this(new HashMap<>(), new HashMap<>());
        initializeCategories();
    }
    public PrinciplesLevelsAttachment(Map<String, Integer> categoryLevels, Map<String, Integer> categoryExperience) {
        this.categoryLevels = new HashMap<>(categoryLevels);
        this.categoryExperience = new HashMap<>(categoryExperience);
        initializeCategories();
    }
    private void initializeCategories() {
        String[] categories = {
                "createEntity", "usesShoot", "usesSummon", "usesTargeting",
                "hasRecasts", "usesTeleport", "addEffects",
                "createsAoeEntities", "usesMobility", "usesRaycast",
                "usesHealing", "usesPotentiation", "immutable"
        };

        for (String category : categories) {
            categoryLevels.putIfAbsent(category, 0);
            categoryExperience.putIfAbsent(category, 0);
        }
    }

    public Map<String, Integer> getCategoryLevels() {
        return categoryLevels;
    }
    public Map<String, Integer> getCategoryExperience() {
        return categoryExperience;
    }

    public int getLevel(String category) {
        return categoryLevels.getOrDefault(category, 0);
    }

    public int getExperience(String category) {
        return categoryExperience.getOrDefault(category, 0);
    }

    public void setLevel(ServerPlayer serverPlayer, String category, int level) {
        int oldLevel = getLevel(category);
        level = Mth.clamp(level, 0, 20);

        if (oldLevel != level) {
            categoryLevels.put(category, level);
            if (listener != null) {
                listener.onLevelChanged(category, oldLevel, level);
            }
            if (level == 20) {
                YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get().trigger(serverPlayer, category, level);
            }
            if (category.equals("createEntity")) {
                YpsAttributeManager.MANA.applyModifier(serverPlayer, level);
            }
        }
    }

    public void setExperience(String category, int experience) {
        categoryExperience.put(category, Math.max(0, experience));
    }

    public void addExperience(ServerPlayer serverPlayer, String category, int amount) {
        int currentExp = getExperience(category);
        int currentLevel = getLevel(category);

        if (currentLevel >= 20) return;

        int newExp = currentExp + amount;
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        if (newExp >= expForNextLevel) {
            setLevel(serverPlayer, category, currentLevel + 1);
            setExperience(category, newExp - expForNextLevel);
        } else {
            setExperience(category, newExp);
        }
    }

    public int getExpForLevel(int level) {
        return Util.getExpForLevel(level);
    }

    public float getProgress(String category) {
        int currentLevel = getLevel(category);
        if (currentLevel >= 20) return 1.0f;

        int currentExp = getExperience(category);
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        return (float) currentExp / expForNextLevel;
    }


}
