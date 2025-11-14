package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.item.custom.TonicItem;
import com.ypsi.fundamentalism.network.packets.SyncCategoryLevelsPacket;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionLevelPacket;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import com.ypsi.fundamentalism.network.packets.SyncReinforcementPacket;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.*;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ModEvents {

    @SubscribeEvent
    public static void tonicRefill(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        ItemStack stackInHand = player.getItemInHand(hand);

        InteractionHand otherHand = (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack stackInOtherHand = player.getItemInHand(otherHand);

        if (stackInHand.getItem() instanceof TonicItem tonic && stackInOtherHand.is(ModItems.LUMINAIRE_EXTRACT)) {
            if (tonic.recharge(stackInHand)) {
                stackInOtherHand.shrink(1);
                ItemStack replacementItem = new ItemStack(ModItems.TEST_TUBE.get());
                if (!player.getInventory().add(replacementItem)) {
                    player.drop(replacementItem, false);
                }
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BOTTLE_EMPTY,
                        SoundSource.PLAYERS,
                        0.5F, 1.0F);
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onManaChangeReinforcementBreak(ChangeManaEvent event){
            Player player = event.getEntity();
            float totalMana = (float)player.getAttributeValue(AttributeRegistry.MAX_MANA);
            float newMana = event.getNewMana();

            if(newMana < totalMana*0.10 && player.hasEffect(ModEffects.REINFORCEMENT_EFFECT)){
                player.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK,
                        SoundSource.PLAYERS,
                        0.5F,
                        0.4F
                );
                if (player.level() instanceof ServerLevel) {
                    PacketDistributor.sendToAllPlayers(new SyncReinforcementPacket(player.getId(), false));
                }
            }
    }
    @SubscribeEvent
    public static void starAlignment(LivingDamageEvent.Pre event){
        if(event.getSource().getDirectEntity() instanceof ServerPlayer player
                && event.getSource().is(DamageTypes.PLAYER_ATTACK)){
            if (!event.getEntity().level().isClientSide()) {
                int n = 100;
//                int n = 5;
                Random random1 = new Random();
                int amplifier = 0;
//                mindful levels
                if(player.hasEffect(ModEffects.MINDFUL_EFFECT)){
                    int level = player.getEffect(ModEffects.MINDFUL_EFFECT).getAmplifier()+1;
                    amplifier = Math.clamp(level,0,2);
                    n-=(10)*(level);
                }
//                low health
                if (player.getHealth() <= player.getMaxHealth() * 0.50) {
                    n-=40;
                }
                //critic attack
                LivingEntity enemy = event.getEntity();
                boolean isCritical = isCriticalHit(player, enemy);

                int r1 = random1.nextInt(n);
                //player.displayClientMessage(Component.literal(" "+r1).withStyle(ChatFormatting.AQUA), true);
                if (r1==0 && isCritical) {

                    float originalDamage = event.getNewDamage();
                    float modifiedDamage = (originalDamage*2);
                    event.setNewDamage(modifiedDamage);
                    //player.displayClientMessage(Component.literal("Dmg: "+modifiedDamage).withStyle(ChatFormatting.AQUA), true);
                    int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());

                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(currentExhaustion - 50, 0));

                    SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, false, true, true));

                    ServerLevel serverLevel = (ServerLevel) enemy.level();

                    serverLevel.sendParticles(
                            ParticleTypes.END_ROD,
                            enemy.getX(),
                            enemy.getY() + enemy.getBbHeight() * 0.5,
                            enemy.getZ(),
                            30,
                            enemy.getBbWidth() * 1,
                            enemy.getBbHeight() * 0.4,
                            enemy.getBbWidth() * 1,
                            0.01
                    );

                    serverLevel.sendParticles(
                            new DustParticleOptions(
                                    new Vector3f(0.0f, 0.0f, 0.0f),
                                    1.0f
                            ),
                            enemy.getX(),
                            enemy.getY() + enemy.getBbHeight() * 0.5,
                            enemy.getZ(),
                            60,
                            enemy.getBbWidth() * 1,
                            enemy.getBbHeight() * 0.4,
                            enemy.getBbWidth() * 1,
                            0.01
                    );
                    serverLevel.sendParticles(
                            ModParticles.CONSTELLATION_PARTICLE.get(),
                            enemy.getX(),
                            enemy.getY() + enemy.getBbHeight() * 0.5,
                            enemy.getZ(),
                            1,
                            0,
                            0,
                            0,
                            0.01
                    );

                    player.addEffect(new MobEffectInstance(ModEffects.MINDFUL_EFFECT, 30*20, amplifier, false, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4*20, 1, false, false, true));

                    player.level().playSound(
                            null,
                            enemy.getX(), enemy.getY(), enemy.getZ(),
                            SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                            SoundSource.PLAYERS,
                            2.0F,
                            0.8F
                    );

                    CameraShakeManager.addCameraShake(new CameraShakeData(15, enemy.position(), 10));
                }
            }
        }

    }
    private static boolean isCriticalHit(Player player, Entity target) {
        return player.fallDistance > 0.0F &&
                !player.onGround() &&
                !player.isPassenger() &&
                !player.isInWater() &&
                !player.hasEffect(MobEffects.BLINDNESS) &&
                target instanceof LivingEntity;
    }
//    @SubscribeEvent
//    public static void moreMobResistances(FinalizeSpawnEvent event){
//        var mob = event.getEntity();
//
//        if(mob instanceof ImpEntity){
//            setIfNonNull(mob, AttributeRegistry.FIRE_SPELL_POWER, 1.2);
//            setIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
//        }
//        if(mob instanceof EnderMan || mob instanceof Shulker || mob instanceof Endermite){
//            setIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 1.8);
//            setIfNonNull(mob, AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.5);
//        }
//        if(mob instanceof EnderDragon){
//            setIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 3.0);
//            setIfNonNull(mob, AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.6);
//        }
//        if(mob instanceof Creeper){
//            setIfNonNull(mob, AttributeRegistry.NATURE_MAGIC_RESIST, 1.5);
//        }
//        if(mob instanceof IronGolem){
//            setIfNonNull(mob, AttributeRegistry.EVOCATION_MAGIC_RESIST, 1.5);
//        }
//
//    }
    @SubscribeEvent
    public static void PreSpellVerification(SpellPreCastEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
        int currentExLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
        int level = event.getSpellLevel();
        int manaUsed = spell.getManaCost(level);
        int totalLevel = spell.getMaxLevel();
        if(totalLevel<6){//More mana wasted
            double manaMult = switch(currentExLvl){
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 0.75;
                case 4 -> 1.00;
                default -> 0;
            };
            int modifiedManaUsed = (int) (manaUsed+(manaUsed*manaMult));
            float currentMana = MagicData.getPlayerMagicData(player).getMana();
            if(currentMana<modifiedManaUsed && !player.isCreative()) {
                event.setCanceled(true);
            }
        }else{//Max lvl is higher than 5
            double levelRedMult = switch(currentExLvl){
                case 1 -> 0.20;
                case 2 -> 0.30;
                case 3 -> 0.40;
                case 4 -> 0.50;
                default -> 0;
            };
            int reduced = (int) (level-(totalLevel*levelRedMult));
            if(reduced<1) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void SpellNerfCast(SpellOnCastEvent event){
        Player player = event.getEntity();
        CastSource castSource = event.getCastSource();
        int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
        int currentExLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
        int manaUsed = event.getManaCost();
        int level = event.getSpellLevel();
        int totalLevel = SpellRegistry.getSpell(event.getSpellId()).getMaxLevel();
        int spellLevelResult;

        if(totalLevel<6){//More mana wasted
            double manaMult = switch(currentExLvl){
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 0.75;
                case 4 -> 1.00;
                default -> 0;
            };
            int modifiedManaUsed = (int) (manaUsed+(manaUsed*manaMult));
            if(manaUsed != modifiedManaUsed && castSource!=CastSource.SCROLL) {
                player.displayClientMessage(Component.literal("Mana used: " + modifiedManaUsed).withStyle(ChatFormatting.AQUA), true);
            }
            event.setManaCost(modifiedManaUsed);
            spellLevelResult = level;
        }else{//Max lvl is higher than 5
            double levelRedMult = switch(currentExLvl){
                case 1 -> 0.20;
                case 2 -> 0.30;
                case 3 -> 0.40;
                case 4 -> 0.50;
                default -> 0;
            };
            int reduced = (int) (level-(totalLevel*levelRedMult));
            if(reduced != level && castSource!=CastSource.SCROLL) {
                player.displayClientMessage(Component.literal("Level casted: " + reduced).withStyle(ChatFormatting.GREEN), true);
            }
            event.setSpellLevel(reduced);
            spellLevelResult = reduced;
        }

        boolean continuous = SpellRegistry.getSpell(event.getSpellId()).getCastType() == CastType.CONTINUOUS;
        AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
        String schoolIdName = spell.getSchoolType().getId().toString();
        Optional<Double> elementalPower = getSchoolSpellPowerSafe(player,schoolIdName);
        double spellPower = player.getAttributeValue(AttributeRegistry.SPELL_POWER);
        boolean notElementalPower = elementalPower.isEmpty() || elementalPower.get() <= 0 || spellPower<=0;

        int formula = 0;

        if(notElementalPower){
            formula = (int) (continuous?
                    (level+manaUsed):
                    ((double) level /2) + (manaUsed)
            );
        }else{
            formula = (int) (continuous?
                    (level+manaUsed)/(4*spellPower*elementalPower.get()):
                    ((double) level /2) + (manaUsed/(3*spellPower*elementalPower.get()))
            );
        }

        int addition = currentExhaustion+formula;
        int maxEx = getMaxExPerLevel(currentExLvl, player);

        if(addition>maxEx){
            if(currentExLvl!=4) {
                int difference = addition - maxEx;
                player.setData(YpsAttachments.CURRENT_EXHAUSTION,
                        Mth.clamp(difference, 0, maxEx));
                player.setData(YpsAttachments.LEVEL_EXHAUSTION, Mth.clamp(currentExLvl + 1, 0, 4));
            }else{
                player.setData(YpsAttachments.CURRENT_EXHAUSTION,
                        Mth.clamp(addition,0,maxEx));
            }
        }else{
            player.setData(YpsAttachments.CURRENT_EXHAUSTION,
                    Mth.clamp(addition,0,maxEx));
        }

        //Leveling up
        String spellId = event.getSpellId();
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);
        int levelBonus = (spellLevelResult);
        if(castSource != CastSource.SCROLL) {
            for (String category : categories) {
                SpellCategoryProgression.addCategoryExperience(player, category, levelBonus);
            }
        }

        SyncExhaustionPacket.sendToPlayer((ServerPlayer) player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
        SyncExhaustionLevelPacket.sendToPlayer((ServerPlayer)player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));
    }
    public static Optional<Double> getSchoolSpellPowerSafe(Player player, String schoolIdName) {
        try {
            String schoolName = schoolIdName.split(":")[1].toUpperCase() + "_SPELL_POWER";
            Field field = AttributeRegistry.class.getField(schoolName);

            DeferredHolder<Attribute, Attribute> attributeHolder = (DeferredHolder<Attribute, Attribute>) field.get(null);
            return Optional.of(player.getAttributeValue(attributeHolder));

        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @SubscribeEvent
    public static void exhaustionDecrement(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            final String TICK_COUNTER_KEY = "exhaustionTickCounter";
            final String LEVEL_COUNTER_KEY = "exhaustionLevelCounter";

            int tickCounter = player.getPersistentData().getInt(TICK_COUNTER_KEY);
            tickCounter++;

            final int TICKS_PER_SECOND = 20;

            if (tickCounter >= TICKS_PER_SECOND) {
                int levelEx = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
                int currentEx = player.getData(YpsAttachments.CURRENT_EXHAUSTION);
                int regen = (int) player.getAttributeValue(YpsAttributes.EXHAUSTION_REGEN);

                int levelCounter = player.getPersistentData().getInt(LEVEL_COUNTER_KEY);

                if (currentEx == 0) {
                    levelCounter++;
                    player.getPersistentData().putInt(LEVEL_COUNTER_KEY, levelCounter);

                    if (levelEx >= 1 && levelCounter >= 5) {
                        int newLevel = levelEx - 1;
                        player.setData(YpsAttachments.LEVEL_EXHAUSTION, newLevel);
                        int newMax = getMaxExPerLevel(newLevel, player);
                        player.setData(YpsAttachments.CURRENT_EXHAUSTION, newMax);
                        player.getPersistentData().putInt(LEVEL_COUNTER_KEY, 0);
                    }
                } else {
                    int result = currentEx - regen;
                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(result, 0));
                    if (levelCounter > 0) {
                        player.getPersistentData().putInt(LEVEL_COUNTER_KEY, 0);
                    }
                }

                tickCounter = 0;
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);
                SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                SyncExhaustionLevelPacket.sendToPlayer(player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));

            } else {
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoginExSync(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (!player.getPersistentData().contains("exhaustionTickCounter")) {
                player.getPersistentData().putInt("exhaustionTickCounter", 0);
            }
            if (!player.getPersistentData().contains("reduceCounter")) {
                player.getPersistentData().putInt("reduceCounter", 0);
            }
        }
    }
    public static int getMaxExPerLevel(int level, Player player){
        return (int) ((switch (level){
                    case 0,4 -> 50;
                    case 1,3 -> 100;
                    case 2 -> 200;
                    default -> 100;
                })
                +player.getAttributeValue(YpsAttributes.MAX_EXHAUSTION));
    }

    @SubscribeEvent
    public static void luminaireBrewingRecipeRegister(RegisterBrewingRecipesEvent event){
        PotionBrewing.Builder builder = event.getBuilder();
        builder.addRecipe(
                Ingredient.of(ModItems.TEST_TUBE),
                Ingredient.of(ModItems.ARCANE_MIXTURE),
                ModItems.LUMINAIRE_EXTRACT.toStack(1)
        );

    }
    @SubscribeEvent
    public static void reinforcementLayerSync(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer targetPlayer && event.getEntity() instanceof ServerPlayer trackingPlayer) {
            if (targetPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                PacketDistributor.sendToPlayer(trackingPlayer, new SyncReinforcementPacket(targetPlayer.getId(), true));
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerReinforcementHurt(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {

                MagicData magicData = MagicData.getPlayerMagicData(event.getEntity());

                float originalDamage = event.getNewDamage();
                float currentMana = magicData.getMana();

                double maxMana = serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                double baseSpellPower = serverPlayer.getAttributeBaseValue(AttributeRegistry.SPELL_POWER);
                double removeSpellBase = baseSpellPower*0.2;
                double spellPower = serverPlayer.getAttributeValue(AttributeRegistry.SPELL_POWER)+removeSpellBase;

                int currentExLvl = serverPlayer.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
                float manaMult = switch(currentExLvl){
                    case 1 -> 0.125f;
                    case 2 -> 0.25f;
                    case 3 -> 0.375f;
                    case 4 -> 0.5f;
                    default -> 0;
                };

                float mitigatedDamage = (float)(Math.sqrt((maxMana/100))*spellPower);
                mitigatedDamage = mitigatedDamage-(mitigatedDamage*manaMult);
                float modifiedDamage = originalDamage;

                float manaToConsume = (float)(maxMana*0.05);
                int exhaustionAcc = 0;
                //0.05
                if(currentMana>=(maxMana*.05)) {

                    if (originalDamage < mitigatedDamage) {
                        modifiedDamage = 0.0f;
                        manaToConsume/=2;
                        magicData.addMana(-manaToConsume);
                        exhaustionAcc = 1;
                    } else {
                        modifiedDamage = originalDamage - mitigatedDamage;
                        magicData.addMana(-manaToConsume);
                        exhaustionAcc = 2;
                    }
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            0.5F,
                            0.4F
                    );

                    PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData));
                }

                int currentExhaustion = serverPlayer.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
                int addition = currentExhaustion+exhaustionAcc;
                int maxEx = getMaxExPerLevel(currentExLvl, serverPlayer);
                if(addition>maxEx){
                    if(currentExLvl!=4) {
                        int difference = addition - maxEx;
                        serverPlayer.setData(YpsAttachments.CURRENT_EXHAUSTION,
                                Mth.clamp(difference, 0, maxEx));
                        serverPlayer.setData(YpsAttachments.LEVEL_EXHAUSTION, Mth.clamp(currentExLvl + 1, 0, 4));
                    }else{
                        serverPlayer.setData(YpsAttachments.CURRENT_EXHAUSTION,
                                Mth.clamp(addition,0,maxEx));
                    }
                }else{
                    serverPlayer.setData(YpsAttachments.CURRENT_EXHAUSTION,
                            Mth.clamp(addition,0,maxEx));
                }

                SyncExhaustionPacket.sendToPlayer(serverPlayer,serverPlayer.getData(YpsAttachments.CURRENT_EXHAUSTION));
                SyncExhaustionLevelPacket.sendToPlayer(serverPlayer, serverPlayer.getData(YpsAttachments.LEVEL_EXHAUSTION));

                event.setNewDamage(modifiedDamage);
            }
        }
    }
//    @SubscribeEvent
//    public static void detectingCast(SpellOnCastEvent castEvent) {
//        SpellCategoriesConfig config = SpellCategoriesConfig.getInstance();
//        String spellId = castEvent.getSpellId();
//        Set<String> categories = config.getCategoriesForSpell(spellId);
//        if (!categories.isEmpty()) {
//            Player player = castEvent.getEntity();
//            player.sendSystemMessage(Component.literal("----------------------------------------------"));
//            if (categories.contains("createEntity")) {
//                player.sendSystemMessage(Component.literal("Entity Spell"));
//            }
//            if (categories.contains("usesShoot")) {
//                player.sendSystemMessage(Component.literal("Projectile Spell"));
//            }
//            if (categories.contains("usesSummon")) {
//                player.sendSystemMessage(Component.literal("Summon Spell"));
//            }
//            if (categories.contains("usesTargeting")) {
//                player.sendSystemMessage(Component.literal("Targeted Spell"));
//            }
//            if (categories.contains("hasRecasts")) {
//                player.sendSystemMessage(Component.literal("Recast Spell"));
//            }
//            if (categories.contains("usesTeleport")) {
//                player.sendSystemMessage(Component.literal("Teleport Spell"));
//            }
//            if (categories.contains("addEffects")) {
//                player.sendSystemMessage(Component.literal("Effect spell"));
//            }
//            if (categories.contains("createsEffectEntities")) {
//                player.sendSystemMessage(Component.literal("Entity Effect Spell"));
//            }
//            if (categories.contains("createsAoeEntities")) {
//                player.sendSystemMessage(Component.literal("AoE entity Spell"));
//            }
//            if (categories.contains("usesMobility")) {
//                player.sendSystemMessage(Component.literal("Mobility spell"));
//            }
//            if (categories.contains("usesRaycast")) {
//                player.sendSystemMessage(Component.literal("Raycast Spell"));
//            }
//            player.sendSystemMessage(Component.literal("----------------------------------------------"));
//        }
//    }

    @SubscribeEvent
    public static void onPlayerLogin1(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);
        }
    }
    @SubscribeEvent
    public static void onPlayerRespawn1(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.server.execute(() -> {
                SyncCategoryLevelsPacket.sendToPlayer(player);
            });
        }
    }
    @SubscribeEvent
    public static void onDimensionChange1(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);
        }
    }


}
