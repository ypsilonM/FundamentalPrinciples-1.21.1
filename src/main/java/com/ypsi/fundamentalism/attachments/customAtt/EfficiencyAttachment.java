package com.ypsi.fundamentalism.attachments.customAtt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class EfficiencyAttachment {
    private int efficiencyLevel;
    private int efficiencyExperience;
    private LevelChangeListener listener;

    public static final Codec<EfficiencyAttachment> CODEC = RecordCodecBuilder.create(efficiencyLevelInstance ->
            efficiencyLevelInstance.group(
                    (Codec.INT).fieldOf("efficiencyLevel").forGetter(EfficiencyAttachment::getEfficiencyLevel),
                    (Codec.INT).fieldOf("efficiencyExperience").forGetter(EfficiencyAttachment::getEfficiencyExperience)
            ).apply(efficiencyLevelInstance, EfficiencyAttachment::new)
    );

    @FunctionalInterface
    public interface LevelChangeListener {
        void onLevelChanged(int oldLevel, int newLevel);
    }

    public void setLevelChangeListener(LevelChangeListener listener) {
        this.listener = listener;
    }
    public EfficiencyAttachment() {
        this(0, 0);
    }
    public EfficiencyAttachment(int efficiencyLevel, int efficiencyExperience) {
        this.efficiencyLevel = efficiencyLevel;
        this.efficiencyExperience = efficiencyExperience;
    }

    public int getEfficiencyLevel() {
        return efficiencyLevel;
    }
    public int getEfficiencyExperience() {
        return efficiencyExperience;
    }

    public void setLevel(int level) {
        int oldLevel = getEfficiencyLevel();
        level = Mth.clamp(level, 0, 10);

        if (oldLevel != level) {
            this.efficiencyLevel = level;
            if (listener != null) {
                listener.onLevelChanged(oldLevel, level);
            }
        }
    }
    public void setExperience(int experience) {
        this.efficiencyExperience = Math.max(0, experience);
    }

    public void addExperience(int amount) {
        int currentExp = getEfficiencyExperience();
        int currentLevel = getEfficiencyLevel();

        if (currentLevel >= 10) return;

        int totalXp = currentExp + amount;

        while(currentLevel<10){
            int xp4NextLvl = getExpForLevel(currentLevel + 1);
            if (totalXp >= xp4NextLvl) {
                totalXp-=xp4NextLvl;
                currentLevel++;
                setLevel(currentLevel);
            }else{
                break;
            }
        }
        setExperience(totalXp);
    }

    public int getExpForLevel(int level) {
        return Util.getExpForEfficiencyLevel(level);
    }

    public float getProgress(String category) {
        int currentLevel = getEfficiencyLevel();
        if (currentLevel >= 10) return 1.0f;

        int currentExp = getEfficiencyExperience();
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        return (float) currentExp / expForNextLevel;
    }


}