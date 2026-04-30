package com.ypsi.fundamentalism.attachments;

import com.ypsi.fundamentalism.attachments.customAtt.PrinciplesLevelsAttachment;
import com.ypsi.fundamentalism.network.packets.SyncCategoryLevelsPacket;
import com.ypsi.fundamentalism.network.packets.data.ClientCategoryLevelsData;
import com.ypsi.fundamentalism.util.Principles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PrinciplesProgressionManager {

    public static PrinciplesLevelsAttachment getCategoryLevels(Player player) {
        return player.getData(YpsAttachments.PRINCIPLES_LEVELS);
    }
    public static int getCategoryLevel(Player player, String category) {
        if (player == null) {
            return 10;
        }
        if (player.level().isClientSide) {
            return ClientCategoryLevelsData.getLevel(category);
        } else {
            return getCategoryLevels(player).getLevel(category);
        }
    }
    public static int getCategoryLevel(Player player, Principles principle) {
        if (player == null) {
            return 10;
        }
        if (player.level().isClientSide) {
            return ClientCategoryLevelsData.getLevel(getTechnicalName(principle));
        } else {
            return getCategoryLevels(player).getLevel(getTechnicalName(principle));
        }
    }
    public static int getCategoryExperience(Player player, String category) {
        if (player == null) {
            return 0;
        }
        if (player.level().isClientSide) {
            return ClientCategoryLevelsData.getExperience(category);
        } else {
            return getCategoryLevels(player).getExperience(category);
        }
    }
    public static void setCategoryLevel(Player player, String category, int level) {
        PrinciplesLevelsAttachment levels = getCategoryLevels(player);
        levels.setLevel((ServerPlayer) player, category, level);
    }

    public static void addCategoryExperience(Player player, String category, int amount) {
        //if (player.level().isClientSide) return;
        PrinciplesLevelsAttachment levels = getCategoryLevels(player);
        int oldLevel = levels.getLevel(category);
        levels.addExperience((ServerPlayer) player,category, amount);

        if (levels.getLevel(category) > oldLevel) {
            onLevelUp(player, category, levels.getLevel(category));
        }

        if (player instanceof ServerPlayer serverPlayer) {
            SyncCategoryLevelsPacket.sendToPlayer(serverPlayer);
        }
    }
    public static void setCategoryExperience(Player player, String category, int amount) {
        if (player.level().isClientSide) return;
        PrinciplesLevelsAttachment levels = getCategoryLevels(player);
        levels.setExperience(category, amount);
        if (player instanceof ServerPlayer serverPlayer) {
            SyncCategoryLevelsPacket.sendToPlayer(serverPlayer);
        }
    }
    private static void onLevelUp(Player player, String category, int newLevel) {
            player.displayClientMessage(
                    Component.literal(getCategoryDisplayName(category) + " " + newLevel + " ↑ ")
                            .withStyle(ChatFormatting.GREEN),
                    true
            );

    }

    public static String getCategoryDisplayName(String category) {
        return switch (category) {
            case "createEntity" -> "Concentratio Principle";
            case "addEffects" -> "Pertinacia Principle";
            case "hasRecasts" -> "Repetitio Principle";
            case "usesShoot" -> "Potentia Principle";
            case "usesSummon" -> "Vitale Principle";
            case "usesTeleport" -> "Apparitio Principle";
            case "createsAoeEntities" -> "Expansio Principle";
            case "usesMobility" -> "Motus Principle";
            case "usesRaycast" -> "Perceptio Principle";
            case "usesTargeting" -> "Locus Principle";
            case "usesHealing" -> "Remedium Principle";
            case "usesPotentiation" -> "Augere Principle";
            case "immutable" -> "Certum Principle";
            default -> category;
        };
    }

    public static String getTechnicalName(Principles principle){
        return switch (principle){
            case CONCENTRATIO -> "createEntity";
            case POTENTIA -> "usesShoot";
            case VITALE -> "usesSummon";
            case LOCUS -> "usesTargeting";
            case REPETITIO -> "hasRecasts";
            case APPARITIO -> "usesTeleport";
            case PERTINACIA -> "addEffects";
            case EXPANSIO -> "createsAoeEntities";
            case MOTUS -> "usesMobility";
            case PERCEPTIO -> "usesRaycast";
            case REMEDIUM -> "usesHealing";
            case AUGERE -> "usesPotentiation";
            case CERTUM -> "immutable";
        };
    }

    public static String getCategorySymbol(String category) {
        return switch (category) {
            case "createEntity" -> "\uD83D\uDEE1";
            case "addEffects" -> "\uD83E\uDDEA";
            case "hasRecasts" -> "⇄";
            case "usesShoot" -> "☄";
            case "usesSummon" -> "\uD83C\uDF56";
            case "usesTeleport" -> "⭐";
            case "createsAoeEntities" -> "\uD83C\uDF0A";
            case "usesMobility" -> "⚡";
            case "usesRaycast" -> "☈";
            case "usesTargeting" -> "\uD83C\uDFF9";
            case "usesHealing" -> "⛨";
            case "usesPotentiation" -> "♢";
            case "immutable" -> "⌛";
            default -> "";
        };
    }
}
