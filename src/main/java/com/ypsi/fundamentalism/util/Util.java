package com.ypsi.fundamentalism.util;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.config.ModConfig;
import org.joml.Vector3f;

import java.util.LinkedHashMap;
import java.util.Map;

public class Util {

    public static int getMaxFatigue(int level, Player player){
        return (int) ((switch (level){
            case 0,4 -> 50;
            case 1,3 -> 100;
            case 2 -> 200;
            default -> 100;
        })
                +player.getAttributeValue(YpsAttributes.MAX_FATIGUE));
    }

    public static MutableComponent getPlainLevelComponenet(SpellData spellData, LivingEntity caster) {
        int levelTotal = spellData.getSpell().getLevelFor(spellData.getLevel(), caster);
        int diff = levelTotal - spellData.getLevel();
        if (diff > 0) {
            return Component.literal(String.valueOf(levelTotal-diff));
        } else if (diff < 0) {
            return Component.literal(String.valueOf(levelTotal+(-diff)));
        } else {
            return Component.literal(String.valueOf(levelTotal));
        }
    }

    public static Vector3f getElementalColor(Player player){
        final double EPSILON = 0.001;  // 0.1%
        Map<SchoolType, Double> schoolPowers = new LinkedHashMap<>();
        for(SchoolType school : SchoolRegistry.REGISTRY){
            double power = school.getPowerFor(player);
            double rounded = Math.round(power * 1000.0) / 1000.0;
            schoolPowers.put(school, rounded);
        }
        double maxValue = schoolPowers.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
        if(maxValue <= 0){
            return Utils.deconstructRGB(0x30ABFF);
        }
        long count = schoolPowers.values().stream()
                .filter(power -> Math.abs(power - maxValue) < EPSILON)
                .count();
        if(count != 1){
            return Utils.deconstructRGB(0x30ABFF);
        }
        return schoolPowers.entrySet().stream()
                .filter(entry -> Math.abs(entry.getValue() - maxValue) < EPSILON)
                .map(Map.Entry::getKey)
                .findFirst()
                .map(SchoolType::getTargetingColor)
                .orElse(Utils.deconstructRGB(0x30ABFF));
    }
    //Old color 8FEDF2

    public static double getElementalMaxValue(Player player){
        Map<SchoolType, Double> schoolPowers = new LinkedHashMap<>();
        for(SchoolType school : SchoolRegistry.REGISTRY){
            double power = school.getPowerFor(player);
            double rounded = Math.round(power * 1000.0) / 1000.0;
            schoolPowers.put(school, rounded);
        }
        return schoolPowers.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(1);
    }
    //Principles leveling
    public static int getExpForLevel(int level) {
        return (int) (20+20*(Math.pow(1.3, level)));
    }

    //Principles methods
    public static double getFailureTPChance(int categoryLevel, int cooldown) {
        if(cooldown==0) cooldown = 1;
        double chance = Math.min(90,((100/(cooldown))*2));
        return chance - (getFailureTPReduction(categoryLevel)*chance);
    }
    public static float getFailureTPReduction(int categoryLevel) {
        return (categoryLevel * 0.025f);
    }
    public static double getRecastAddChance(int categoryLevel) {
        return categoryLevel * 0.04;
    }
    public static float getFloatRecastAddChance(int categoryLevel) {
        return (float) (getRecastAddChance(categoryLevel) * 100);
    }
    public static int getTotalRange(int level){
        return (int) (level * 1.5);
    }
    public static double returnLocusDistance(int level, float distance){
        return getLocusMultiplier(level) * distance;
    }
    public static double getLocusMultiplier(int level){
        return 0.40 + (0.02 * level);
    }
    public static int getTotalMana(int level) {
        return level * 20;
    }
    public static double certumManaMultiplier(int level, int catLevel){
        return ServerConfig.fatigueManaAdditionMultipliers.get(level) * (1.00 - manaReduction(catLevel));
    }
    public static double manaReduction(int level){
        return (level * 0.02);
    }
    public static float getAccuracy(int level){
        return 0.40f + level * 0.03f;
    }
    public static float getVolumeMultiplier(int level){
        return 0.50f + level * 0.05f;
    }
    public static double getCooldownReduction(int level){
        return (level * 0.045);
    }
    public static float getFoodToCONSUME(int level){
        return 3F - (0.125f * level);
    }

    //Principles SpellPower Modifiers
    public static float getModificator(int level, float base){
        //-0.1 + 0.01 per level
        double basePercentage = ServerConfig.basePower + (ServerConfig.baseAddition*level);
        return (float) (base*basePercentage);
    }
    public static double getPureModificator(int level){
        return  (ServerConfig.basePower + (ServerConfig.baseAddition*level));
    }
    public static float getSubEntityModificator(int level, float base){
        //-0.05 + 0.005 per level
        int divider = ServerConfig.subcategories ? 2:1;
        double basePercentage = (ServerConfig.basePower/divider) + ((ServerConfig.baseAddition/divider)*level);
        return (float) (base*basePercentage);
    }
    public static double getPureSubEntityModificator(int level){
        int divider = ServerConfig.subcategories ? 2:1;
        return (ServerConfig.basePower/divider) + ((ServerConfig.baseAddition/divider)*level);
    }




}
