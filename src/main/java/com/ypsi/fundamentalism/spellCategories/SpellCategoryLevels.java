package com.ypsi.fundamentalism.spellCategories;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;

public class SpellCategoryLevels {
    private final Map<String, Integer> categoryLevels;
    private final Map<String, Integer> categoryExperience;

    public static final Codec<SpellCategoryLevels> CODEC = RecordCodecBuilder.create(spellCategoryLevelsInstance ->
            spellCategoryLevelsInstance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("categoryLevels").forGetter(SpellCategoryLevels::getCategoryLevels),
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("categoryExperience").forGetter(SpellCategoryLevels::getCategoryExperience)
            ).apply(spellCategoryLevelsInstance, SpellCategoryLevels::new)
    );

    public SpellCategoryLevels() {
        this(new HashMap<>(), new HashMap<>());
        initializeCategories();
    }

    public SpellCategoryLevels(Map<String, Integer> categoryLevels, Map<String, Integer> categoryExperience) {
        this.categoryLevels = new HashMap<>(categoryLevels);
        this.categoryExperience = new HashMap<>(categoryExperience);
        initializeCategories();
    }

    private void initializeCategories() {
        String[] categories = {
                "createEntity", "usesShoot", "usesSummon", "usesTargeting",
                "hasRecasts", "usesTeleport", "addEffects",
                "createsAoeEntities", "usesMobility", "usesRaycast",
                "usesHealing", "usesPotentiation"
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

    public void setLevel(String category, int level) {
        categoryLevels.put(category, Mth.clamp(level, 0, 20));
    }

    public void setExperience(String category, int experience) {
        categoryExperience.put(category, Math.max(0, experience));
    }

    public void addExperience(String category, int amount) {
        int currentExp = getExperience(category);
        int currentLevel = getLevel(category);

        if (currentLevel >= 20) return;

        int newExp = currentExp + amount;
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        if (newExp >= expForNextLevel) {
            setLevel(category, currentLevel + 1);
            setExperience(category, newExp - expForNextLevel);
        } else {
            setExperience(category, newExp);
        }
    }

    public int getExpForLevel(int level) {
        return 100 * level;
    }

    public float getProgress(String category) {
        int currentLevel = getLevel(category);
        if (currentLevel >= 20) return 1.0f;

        int currentExp = getExperience(category);
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        return (float) currentExp / expForNextLevel;
    }
}
