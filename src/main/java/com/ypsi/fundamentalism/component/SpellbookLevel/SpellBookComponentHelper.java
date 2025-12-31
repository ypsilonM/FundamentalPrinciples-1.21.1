package com.ypsi.fundamentalism.component.SpellbookLevel;

import com.google.common.collect.Multimap;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.SpellBook;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public class SpellBookComponentHelper {
    private static final int[] XP_REQUIREMENTS = {
            0,      // Nivel    COMMON
            50,    // Nivel     UNCOMMON
            200,    // Nivel    RARE
            650,    // Nivel    EPIC
            1650   // Nivel     LEGENDARY
    };

    private static final int MAX_LEVEL = 5;

    public static void ensureSpellBookComponents(ItemStack stack) {
        if (stack.getItem() instanceof SpellBook) {
            if (!stack.has(YpsDataComponents.SPELLBOOK_XP.get())) {
                stack.set(YpsDataComponents.SPELLBOOK_XP.get(), new SpellBookXP(0));
            }
            if (!stack.has(YpsDataComponents.SPELLBOOK_LEVEL.get())) {
                int level = 1;
                stack.set(YpsDataComponents.SPELLBOOK_LEVEL.get(), new SpellBookLevel(level));
                int initialXP = getXPForLevel(level);
                stack.set(YpsDataComponents.SPELLBOOK_XP.get(), new SpellBookXP(initialXP));
            }
        }
    }
    public static int getSpellbookLvlBySlots(int spellSlots){
        if(spellSlots < 8) return 1;
        if(spellSlots < 10) return 2;
        if(spellSlots < 12) return 3;
        return 4;
    }
    public static int getXP(ItemStack stack) {
        if (stack.getItem() instanceof SpellBook) {
            ensureSpellBookComponents(stack);
            SpellBookXP xp = stack.get(YpsDataComponents.SPELLBOOK_XP.get());
            return xp != null ? xp.xp() : 0;
        }
        return 0;
    }
    public static int getLevel(ItemStack stack) {
        if (stack.getItem() instanceof SpellBook) {
            ensureSpellBookComponents(stack);
            SpellBookLevel level = stack.get(YpsDataComponents.SPELLBOOK_LEVEL.get());
            return level != null ? level.level() : 1;
        }
        return 0;
    }
    public static void addXP(ItemStack stack, int xpToAdd) {
        if (stack.getItem() instanceof SpellBook) {
            ensureSpellBookComponents(stack);

            int currentXP = getXP(stack);
            int currentLevel = getLevel(stack);

            int newXP = currentXP + xpToAdd;
            stack.set(YpsDataComponents.SPELLBOOK_XP.get(), new SpellBookXP(newXP));

            int newLevel = calculateLevelFromXP(newXP);
            if (newLevel > currentLevel) {
                stack.set(YpsDataComponents.SPELLBOOK_LEVEL.get(), new SpellBookLevel(newLevel));

            }
        }
    }

    public static void setXP(ItemStack stack, int xp) {
        if (stack.getItem() instanceof SpellBook) {
            ensureSpellBookComponents(stack);

            xp = Math.max(0, xp);
            stack.set(YpsDataComponents.SPELLBOOK_XP.get(), new SpellBookXP(xp));

            int newLevel = calculateLevelFromXP(xp);
            stack.set(YpsDataComponents.SPELLBOOK_LEVEL.get(), new SpellBookLevel(newLevel));
        }
    }

    public static void setLevel(ItemStack stack, int level) {
        if (stack.getItem() instanceof SpellBook) {
            ensureSpellBookComponents(stack);

            level = Math.min(Math.max(1, level), MAX_LEVEL);
            stack.set(YpsDataComponents.SPELLBOOK_LEVEL.get(), new SpellBookLevel(level));

            int xpForLevel = getXPForLevel(level);
            stack.set(YpsDataComponents.SPELLBOOK_XP.get(), new SpellBookXP(xpForLevel));
        }
    }

    public static int getXPToNextLevel(ItemStack stack) {
        int currentLevel = getLevel(stack);
        if (currentLevel >= MAX_LEVEL) {
            return 0;
        }

        int xpForNextLevel = getXPForLevel(currentLevel + 1);
        int currentXP = getXP(stack);

        return Math.max(0, xpForNextLevel - currentXP);
    }

    public static float getProgressToNextLevel(ItemStack stack) {
        int currentLevel = getLevel(stack);
        if (currentLevel >= MAX_LEVEL) {
            return 1.0f;
        }

        int currentXP = getXP(stack);
        int xpForCurrentLevel = getXPForLevel(currentLevel);
        int xpForNextLevel = getXPForLevel(currentLevel + 1);

        int xpInLevel = currentXP - xpForCurrentLevel;
        int xpNeededForLevel = xpForNextLevel - xpForCurrentLevel;

        return (float) xpInLevel / xpNeededForLevel;
    }

    private static int calculateLevelFromXP(int xp) {
        for (int level = MAX_LEVEL; level >= 1; level--) {
            if (xp >= getXPForLevel(level)) {
                return level;
            }
        }
        return 1;
    }

    private static int getXPForLevel(int level) {
        if (level < 1) return 0;
        if (level > MAX_LEVEL) return getXPForLevel(MAX_LEVEL);
        return XP_REQUIREMENTS[level - 1];
    }
}
