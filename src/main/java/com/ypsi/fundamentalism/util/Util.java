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
    public static int getExpForPrincipleLevel(int level) {
        return (int) (20+20*(Math.pow(1.3, level)));
    }
    //Efficiency leveling
    public static int getExpForEfficiencyLevel(int level) {
        return (int) (10+10*(Math.pow(1.3, level)));
    }

    //Principles methods

    //CONCENTRATIO
    public static int getTotalMana(int level) { //20
        if(ServerConfig.concentratioActive && ServerConfig.principlesSYSTEM) {
            return level * ServerConfig.manaAdd;
        }else{
            return 0;
        }
    }
    //POTENTIA
    public static float getAccuracy(int level){ //0.40f - 0.03f
        return Math.clamp((float) (ServerConfig.baseAccuracy + level * ServerConfig.addAccuracy), 0f, 1f);
    }
    //VITALE
    public static double getCooldownReduction(int level){ //0.045
        return Math.clamp((level * ServerConfig.crdAdd), 0, 1);
    }
    //EXPANSIO
    public static float getVolumeMultiplier(int level){ //0.5f + 0.05f
        return  Math.max((float)(ServerConfig.aoeBaseRadius + level * ServerConfig.aoeBaseAdd), 0f);
    }
    //APPARITIO
    public static double getFailureTPChance(int categoryLevel, int cooldown) {
        if(cooldown==0) cooldown = 1;
        double chance = Math.min(90,((100/(cooldown))*2));
        return chance - (getFailureTPReduction(categoryLevel)*chance);
    }
    public static float getFailureTPReduction(int categoryLevel) { //0.025f
        return (float) Math.clamp((categoryLevel * ServerConfig.successTpAdd),0,1);
    }
    //REPETITIO
    public static double getRecastAddChance(int categoryLevel) { //0.05
        return Math.clamp(categoryLevel * ServerConfig.addChanceCast, 0, 1);
    }
    public static float getFloatRecastAddChance(int categoryLevel) {
        return (float) (getRecastAddChance(categoryLevel) * 100);
    }
    //PERCEPTIO
    public static int getTotalPerceptioRange(int level){ //1.5
        return (int) (level * ServerConfig.addRange);
    }
    //LOCUS
    public static double returnLocusDistance(int level, float distance){
        return getLocusMultiplier(level) * distance;
    }
    public static double getLocusMultiplier(int level){ //0.40 + 0.02
        return Math.clamp(ServerConfig.percentageBaseRange + (ServerConfig.percentageAddRange * level), 0, 10);
    }
    //CERTUM
    public static double certumManaMultiplier(int level, int catLevel){
        return ServerConfig.fatigueManaAdditionMultipliers.get(level) * (1.00 - manaReduction(catLevel));
    }
    public static double manaReduction(int level){ // 0.02
        return (level * ServerConfig.manaDebuffReduction);
    }
    //REMEDIUM
    public static float getFoodToCONSUME(int level){//3F, 0.125f
        return (float) Math.max(ServerConfig.baseFoodPts - (ServerConfig.subFoodPts * level), 0);
    }
    //PERTINACIA
    public static double beneficialPertinaciaMultiplier(int level){
        return 0.6 + (0.04*level);
    }
    public static double harmfulPertinaciaMultiplier(int level){
        return 1.4 - (0.04*level);
    }
    //AUGERE
//    public static double getAdditionalWeaponDamage(int level){
//        if(level<=8){
//            return level;
//        }else{
//            return level
//        }
//    }


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
