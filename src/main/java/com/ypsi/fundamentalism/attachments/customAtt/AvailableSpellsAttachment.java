package com.ypsi.fundamentalism.attachments.customAtt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.HashMap;
import java.util.Map;

public class AvailableSpellsAttachment {
    private final HashMap<String, Integer> spells;
    private SpellChangeListener listener;

    public static final Codec<AvailableSpellsAttachment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("spells").forGetter(AvailableSpellsAttachment::getSpells)
            ).apply(instance, AvailableSpellsAttachment::new)
    );



    @FunctionalInterface
    public interface SpellChangeListener {
        void onSpellChanged(String spellId, int oldLevel, int newLevel);
    }

    public void setSpellChangeListener(SpellChangeListener listener) {
        this.listener = listener;
    }
    public AvailableSpellsAttachment(IAttachmentHolder iAttachmentHolder) {
        this(new HashMap<>());
    }

    public AvailableSpellsAttachment(Map<String, Integer> spells) {
        this.spells = new HashMap<>(spells);
        this.spells.replaceAll((key, value) -> Mth.clamp(value, 0, 100));
    }

    public HashMap<String, Integer> getSpells() {
        return spells;
    }
    public int getSpellLevel(String spellId) {
        return spells.getOrDefault(spellId, 0);
    }

    public void setSpellLevel(String spellId, int level) {
        int oldLevel = getSpellLevel(spellId);
        int newLevel = Mth.clamp(level, 0, 1000);

        if (oldLevel != newLevel) {
            spells.put(spellId, newLevel);
            if (listener != null) {
                listener.onSpellChanged(spellId, oldLevel, newLevel);
            }
        }
    }

    public void addSpell(String spellId, int level) {
        setSpellLevel(spellId, level);
    }
    public void addSpells(HashMap<String, Integer> map){
        spells.putAll(map);
    }

    public void removeSpell(String spellId) {
        if (spells.containsKey(spellId)) {
            int oldLevel = spells.remove(spellId);
            if (listener != null) {
                listener.onSpellChanged(spellId, oldLevel, 0);
            }
        }
    }
    public void incrementSpellLevel(String spellId) {
        int currentLevel = getSpellLevel(spellId);
        setSpellLevel(spellId, currentLevel + 1);
    }
    public void decrementSpellLevel(String spellId) {
        int currentLevel = getSpellLevel(spellId);
        setSpellLevel(spellId, currentLevel - 1);
    }
    public boolean hasSpell(String spellId) {
        return spells.containsKey(spellId);
    }
    public Map<String, Integer> getAvailableSpells() {
        return new HashMap<>(spells);
    }
    public void clearSpells() {
        if (!spells.isEmpty()) {
            Map<String, Integer> oldSpells = new HashMap<>(spells);
            spells.clear();

            if (listener != null) {
                for (Map.Entry<String, Integer> entry : oldSpells.entrySet()) {
                    listener.onSpellChanged(entry.getKey(), entry.getValue(), 0);
                }
            }
        }
    }
    public int getTotalSpellsCount() {
        return spells.size();
    }
}
