package com.ypsi.fundamentalism.attachments.customAtt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ypsi.fundamentalism.advancements.triggers.YpTriggers;
import com.ypsi.fundamentalism.attachments.YpsAttributeManager;
import com.ypsi.fundamentalism.spells.ModSpells;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;

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

    public void setLevel(Player player, String category, int level) {
        int oldLevel = getLevel(category);
        level = Mth.clamp(level, 0, 20);

        if (oldLevel != level) {
            categoryLevels.put(category, level);
            if (listener != null) {
                listener.onLevelChanged(category, oldLevel, level);

            }
            if (level == 20) {
                YpTriggers.PRINCIPLES_LEVEL_TRIGGER_SUPPLIER.get().trigger((ServerPlayer) player, category, level);
            }
            if (category.equals("createEntity")) {
                YpsAttributeManager.MANA.applyModifier(player, level);
            }
            if(level==10 && category.equals("usesHealing")){
                MagicData.getPlayerMagicData(player).getSyncedData().learnSpell(ModSpells.REMEDIUM_SPELL.get());
            }
        }
    }

    public void setExperience(String category, int experience) {
        categoryExperience.put(category, Math.max(0, experience));
    }

    public void addExperience(Player player, String category, int amount) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        int currentXp = getExperience(category);
        int currentLevel = getLevel(category);

        if (currentLevel >= 20) return;

        int totalXp = currentXp + amount;

        while(currentLevel<20){
            int xp4NextLvl = getExpForLevel(currentLevel + 1);
            if (totalXp >= xp4NextLvl) {
                totalXp-=xp4NextLvl;
                currentLevel++;
                setLevel(player, category, currentLevel);
            }else{
                break;
            }
        }
        setExperience(category, totalXp);

    }

    public int getExpForLevel(int level) {
        return Util.getExpForPrincipleLevel(level);
    }

    public float getProgress(String category) {
        int currentLevel = getLevel(category);
        if (currentLevel >= 20) return 1.0f;

        int currentExp = getExperience(category);
        int expForNextLevel = getExpForLevel(currentLevel + 1);

        return (float) currentExp / expForNextLevel;
    }


}
