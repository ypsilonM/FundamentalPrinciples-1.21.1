package com.ypsi.fundamentalism.attachments.customAtt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

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

    public int getExpForLevel(int level) {
        return Util.getXpForEfficiencyLevel(level);
    }

    public float getProgress() {
        int currentLevel = getEfficiencyLevel();
        int currentExp = getEfficiencyExperience();
        int expForNextLevel = getExpForLevel(currentLevel + 1);
        return (float) currentExp / expForNextLevel;
    }


}