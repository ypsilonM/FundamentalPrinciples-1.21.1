package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.item.custom.FlaskItem;
import com.ypsi.fundamentalism.item.custom.IExhaustionConsumable;
import com.ypsi.fundamentalism.item.custom.TonicItem;
import com.ypsi.fundamentalism.network.packets.SyncCategoryLevelsPacket;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionLevelPacket;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import com.ypsi.fundamentalism.network.packets.SyncReinforcementPacket;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryLevels;
import com.ypsi.fundamentalism.spellCategories.SpellCategoryProgression;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.OnCastFinishedPacket;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;

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

        if (stackInHand.getItem() instanceof IExhaustionConsumable tonic && stackInOtherHand.is(ModItems.LUMINAIRE_EXTRACT)) {
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

            LivingEntity enemy = event.getEntity();
            if (!isCriticalHit(player, enemy)) {
                return;
            }
            if (!event.getEntity().level().isClientSide()) {
                double n = 100;
                int amplifier = 0;
//                mindful levels
                if (player.hasEffect(ModEffects.MINDFUL_EFFECT)) {
                    int level = player.getEffect(ModEffects.MINDFUL_EFFECT).getAmplifier() + 1;
                    amplifier = Math.clamp(level, 0, 2);
                    n -= (10) * (level);
                }
//                low health
                if (player.getHealth() <= player.getMaxHealth() * 0.50) {
                    n -= 40;
                }
                double prob = 1/n;
                if (player.getRandom().nextDouble() < prob) {

                    float originalDamage = event.getNewDamage();
                    float modifiedDamage = (originalDamage*2);
                    event.setNewDamage(modifiedDamage);
                    int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());

                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(currentExhaustion - 50, 0));

                    SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, false, true, true));
                    var effect = player.getEffect(ModEffects.BURNOUT_EFFECT);
                    if( effect != null ){
                        player.removeEffect(ModEffects.BURNOUT_EFFECT);
                    }
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

                    CameraShakeManager.addCameraShake(new CameraShakeData(serverLevel,15, enemy.position(), 10));
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
    public static void onPlayerLoginExSync(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
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

                if(originalDamage==0){
                    return;
                }

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
                        exhaustionAcc = 2;
                    } else {
                        modifiedDamage = originalDamage - mitigatedDamage;
                        magicData.addMana(-manaToConsume);
                        exhaustionAcc = 5;
                    }
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            0.6F,
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

    @SubscribeEvent
    public static void PreSpellVerification(SpellPreCastEvent event){
        ServerPlayer caster = (ServerPlayer) event.getEntity();
        AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
        MagicData magicData = MagicData.getPlayerMagicData(caster);

        int currentExLvl = caster.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
        int level = event.getSpellLevel();
        int manaUsed = spell.getManaCost(level);
        int totalLevel = spell.getMaxLevel();

        String spellId = event.getSpellId();
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);

        if(cancelDominanSpells(categories, level, caster, spell)){
            cancelCast(event, magicData, caster, spell);
            if (caster instanceof ServerPlayer serverPlayer) {
                serverPlayer.displayClientMessage(Component.translatable("Complex spell unable to cast.").withStyle(ChatFormatting.DARK_PURPLE), true);
            }
            return;
        }
        if (totalLevel < 6) {//More mana wasted
            double manaMult = switch (currentExLvl) {
                case 1 -> 0.25;
                case 2 -> 0.50;
                case 3 -> 0.75;
                case 4 -> 1.00;
                default -> 0;
            };
            int modifiedManaUsed = (int) (manaUsed + (manaUsed * manaMult));
            float currentMana = MagicData.getPlayerMagicData(caster).getMana();
            if (currentMana < modifiedManaUsed && !caster.isCreative()) {
                cancelCast(event, magicData, caster, spell);
                return;
            }
        } else {//Max lvl is higher than 5
            double levelRedMult = switch (currentExLvl) {
                case 1 -> 0.20;
                case 2 -> 0.30;
                case 3 -> 0.40;
                case 4 -> 0.50;
                default -> 0;
            };
            int reduced = (int) (level - (totalLevel * levelRedMult));
            if (reduced < 1) {
                cancelCast(event, magicData, caster, spell);
                return;
            }
        }
        //Other categories Verifications
        teleportCategoryVerification(categories,caster,spell,event,magicData);
    }
    public static boolean cancelDominanSpells(Set<String> categories, int level, ServerPlayer serverPlayer, AbstractSpell spell){
        if(categories.size() >= 4){
            SpellRarity spellRarity = spell.getRarity(level);
            int minLevel = getLevelRequiredForRARITY(spellRarity);
            for (String category : categories){
                int categoryLevel =  SpellCategoryProgression.getCategoryLevel(serverPlayer, category);
                if (categoryLevel<minLevel){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    public static int getLevelRequiredForRARITY(SpellRarity rarity){
        return switch (rarity) {
            case COMMON -> 0;
            case UNCOMMON -> 5;
            case RARE -> 8;
            case EPIC -> 12;
            case LEGENDARY -> 15;
        };
    }

    public static void cancelCast(SpellPreCastEvent event , @NotNull MagicData magicData, ServerPlayer caster, AbstractSpell spell){
        if (magicData.getAdditionalCastData() instanceof TargetEntityCastData) {
            if (caster instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(spell, List.of()));
            }
        }
        magicData.resetAdditionalCastData();
        event.setCanceled(true);
        if (caster instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("").withStyle(ChatFormatting.RED), true);
        }
        ServerLevel serverLevel = (ServerLevel) caster.level();
        serverLevel.playSound(
                null,
                caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.COPPER_BULB_PLACE,
                SoundSource.PLAYERS,
                1F,
                0.5F
        );
        serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(0.5f,0.5f,0.5f),1.0f),
                caster.getX(),
                caster.getY() + caster.getBbHeight() * 0.7,
                caster.getZ(),
                6,
                0.2,
                0.2,
                0,
                0.01
        );
    }
    public static void teleportCategoryVerification(Set<String> categories, ServerPlayer caster, AbstractSpell spell, SpellPreCastEvent event, MagicData magicData){
        if(categories.contains("usesTeleport")) {
            double probabilityForFail = 0;
            int categoryLevel = SpellCategoryProgression.getCategoryLevel(caster, "usesTeleport");
            int cooldown = MagicManager.getEffectiveSpellCooldown(spell, caster, event.getCastSource()) / 20;
            probabilityForFail = calculateProbabilityForFailedTp(categoryLevel, cooldown) / 100;
            double random = caster.getRandom().nextDouble();
            //caster.sendSystemMessage(Component.literal("Random: " + random + "de " + probabilityForFail + " Con cooldown: " + cooldown + " "));
            boolean isCanceled = random < probabilityForFail;
            if (isCanceled) {
                cancelTeleportSpell(event, magicData, caster, spell);
            }
        }
    }
    public static void cancelTeleportSpell(SpellPreCastEvent event , @NotNull MagicData magicData, ServerPlayer caster, AbstractSpell spell){
        if (magicData.getAdditionalCastData() instanceof TargetEntityCastData) {
            if (caster instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(spell, List.of()));
            }
        }
        event.setCanceled(true);
        if (caster instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("").withStyle(ChatFormatting.RED), true);
        }
        ServerLevel serverLevel = (ServerLevel) caster.level();
        serverLevel.playSound(
                null,
                caster.getX(), caster.getY(), caster.getZ(),
                SoundEvents.TRIAL_SPAWNER_SPAWN_MOB,
                SoundSource.PLAYERS,
                1F,
                1F
        );
        MagicHelper.MAGIC_MANAGER.addCooldown(caster,spell,event.getCastSource());
    }
    private static double calculateProbabilityForFailedTp(int categoryLevel, int cooldown) {
        if(cooldown==0) cooldown = 1;
        double chance = Math.min(90,((100/(cooldown))*2));
        return chance - (categoryLevel*0.025*chance);
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
        SpellRarity spellRarity = SpellRegistry.getSpell(event.getSpellId()).getRarity(level);

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

        double entitySchoolPowerModifier = 1;
        entitySchoolPowerModifier = spell.getSchoolType().getPowerFor(player);

        double spellPower = player.getAttributeValue(AttributeRegistry.SPELL_POWER);
        boolean notElementalPower = entitySchoolPowerModifier <= 0 || spellPower<=0;

        int formulaAdd = 0;

        double rarityRatio = switch (spellRarity) {
            case COMMON -> 1.0;
            case UNCOMMON -> 1.10;
            case RARE -> 1.15;
            case EPIC -> 1.20;
            case LEGENDARY -> 1.30;
        };

        if(notElementalPower){
            formulaAdd = 1;
            ;
        }else{
            formulaAdd = (int) (continuous?
                            ((Math.sqrt(manaUsed))  /  (spellPower*entitySchoolPowerModifier))  * rarityRatio: //continuos
                    (5+Math.pow(manaUsed, 1/1.3))/(spellPower*entitySchoolPowerModifier)        * rarityRatio  //other
            );
        }
//        if(notElementalPower){
//            formula = (int) (continuous?
//                    (level+manaUsed):
//                    ((double) level /2) + (manaUsed)
//            );
//        }else{
//            formula = (int) (continuous?
//                    (level+manaUsed) / (4*spellPower*entitySchoolPowerModifier): //continuos
//                    ((double) level /2) + (manaUsed/(3*spellPower*entitySchoolPowerModifier)) //other
//            );
//        }
        addExhaustion(player, formulaAdd);

        //Leveling up
        String spellId = event.getSpellId();
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);
        int levelBonus = (spellLevelResult);
        if(castSource != CastSource.SCROLL) {
            for (String category : categories) {
                SpellCategoryProgression.addCategoryExperience(player, category, levelBonus);
            }
        }

        // Buscar SpellBook equipado
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
            inv.findCurios(Curios.SPELLBOOK_SLOT).forEach(curio -> {
                ItemStack spellBook = curio.stack();
                if (spellBook.getItem() instanceof SpellBook) {
                    int xpGained = calculateXpFromSpell(spellLevelResult, spell);
                    SpellBookComponentHelper.addXP(spellBook, xpGained);
                }
            });
        });

        //ServerPlayer to ClientPlayer
        SyncExhaustionPacket.sendToPlayer((ServerPlayer) player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
        SyncExhaustionLevelPacket.sendToPlayer((ServerPlayer)player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));
    }
    private static void addExhaustion(Player player, int amountToAdd) {
        int currentLevel = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
        int currentEx = player.getData(YpsAttachments.CURRENT_EXHAUSTION);
        int remainingToAdd = amountToAdd;

        while (remainingToAdd > 0 && currentLevel <= 4) {
            int maxExPts = getMaxExPerLevel(currentLevel, player);
            int remainingSpace = maxExPts - currentEx;

            if (remainingToAdd <= remainingSpace) {
                currentEx += remainingToAdd;
                remainingToAdd = 0;
            } else {
                currentEx = maxExPts;
                remainingToAdd -= remainingSpace;

                if (currentLevel < 4) {
                    currentLevel++;
                    currentEx = 0;
                } else {
                    remainingToAdd = 0;
                }
            }
        }
        player.setData(YpsAttachments.LEVEL_EXHAUSTION, currentLevel);
        player.setData(YpsAttachments.CURRENT_EXHAUSTION, currentEx);
    }

    private static int calculateXpFromSpell(int level, AbstractSpell spell) {
        SpellRarity spellRarity = spell.getRarity(level);
        switch (spellRarity) {
            case SpellRarity.COMMON:
                return 1;
            case SpellRarity.UNCOMMON:
                return 2;
            case SpellRarity.RARE:
                return 3;
            case SpellRarity.EPIC:
                return 4;
            case SpellRarity.LEGENDARY:
                return 5;
            default:
                return 0;
        }
    }

//    @SubscribeEvent
//    public static void inscriptionRarity(InscribeSpellEvent event){
//        Player player =  event.getEntity();
//
//
//        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
//            inv.findCurios(Curios.SPELLBOOK_SLOT).forEach(curio -> {
//                ItemStack spellBook = curio.stack();
//                if (spellBook.getItem() instanceof SpellBook) {
//                    if (spellBook.has(YpsDataComponents.SPELLBOOK_LEVEL.get())) {
//                        int spellBookLevel = spellBook.get(YpsDataComponents.SPELLBOOK_LEVEL.get()).level();
//
//                        SpellData spellData = event.getSpellData();
//                        SpellRarity spellRarity = spellData.getRarity();
//                        int spellRarityLevel = switch (spellRarity){
//                            case COMMON -> 1;
//                            case UNCOMMON -> 2;
//                            case RARE -> 3;
//                            case EPIC -> 4;
//                            case LEGENDARY -> 5;
//                        };
//                        if(spellRarityLevel>spellBookLevel){
//                            event.setCanceled(true);
//                        }
//                    }
//                }
//            });
//        });
//    }

    @SubscribeEvent
    public static void brainDamage(SpellOnCastEvent event){
        Player player = event.getEntity();
        ServerPlayer serverPlayer = (ServerPlayer) player;
        int currentExLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
        if(currentExLvl == 4){
            //15%
            int currentExPts = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
            int maxEx = getMaxExPerLevel(currentExLvl, player);
            if(currentExPts==maxEx){
                event.getEntity().addEffect(new MobEffectInstance(
                        ModEffects.BURNOUT_EFFECT,
                        20*60,
                        0,
                        false,
                        false,
                        true
                ));
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK,
                        SoundSource.PLAYERS,
                        1F,
                        0.5F
                );

                MagicData.getPlayerMagicData(serverPlayer).resetCastingState();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new OnCastFinishedPacket(serverPlayer.getUUID(), event.getSpellId(), true));
            }else if(player.getRandom().nextDouble() < 0.15){
                event.getEntity().addEffect(new MobEffectInstance(
                        ModEffects.BURNOUT_EFFECT,
                        20*60,
                        0,
                        false,
                        false,
                        true
                ));
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK,
                        SoundSource.PLAYERS,
                        1F,
                        0.5F
                );

                MagicData.getPlayerMagicData(serverPlayer).resetCastingState();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new OnCastFinishedPacket(serverPlayer.getUUID(), event.getSpellId(), true));
            }
        }
        if(currentExLvl == 3){
            //5%
            if(player.getRandom().nextDouble() < 0.05){
                event.getEntity().addEffect(new MobEffectInstance(
                        ModEffects.BURNOUT_EFFECT,
                        20*15,
                        0,
                        false,
                        false,
                        true
                ));
                ServerLevel serverLevel = (ServerLevel) player.level();
                serverLevel.playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SHIELD_BREAK,
                        SoundSource.PLAYERS,
                        1F,
                        0.5F
                );

                MagicData.getPlayerMagicData(serverPlayer).resetCastingState();
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(serverPlayer, new OnCastFinishedPacket(serverPlayer.getUUID(), event.getSpellId(), true));
            }
        }
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

//    public static void particleGenElemental(ServerTickEvent event){
//        MinecraftServer minecraftServer =  event.getServer();
//        if(minecraftServer.getTickCount()%20==0) {
//            minecraftServer.getPlayerList().getPlayers().forEach(serverPlayer -> {
//                if (serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
//                    SchoolType schoolType = getElementalSchool(serverPlayer);
//
//                    serverPlayer.level().addParticle(
//                            ParticleRegistry.BLOOD_PARTICLE.get(),
//                            0,
//                            0,
//                            0,
//                            0,
//                            0,
//                            0
//                    );
//                }
//            });
//        }
//    }
    public static SchoolType getElementalSchool(Player player){
        SchoolType maxSchool = null;
        double maxValue = -1;
        for(SchoolType school : SchoolRegistry.REGISTRY){
            double schoolPower = school.getPowerFor(player);
            if(maxSchool == null){
                maxSchool = school;
                maxValue = schoolPower;
            }else if(schoolPower>maxValue) {
                maxSchool = school;
                maxValue = schoolPower;
            }else if(schoolPower==maxValue){
                maxSchool = null;
                maxValue = -1;
            }
        }
        if(maxValue<=0){
            return null;
        }else{
            return maxSchool;
        }
    }

    @SubscribeEvent
    public static void uselessShield(LivingShieldBlockEvent event){
        DamageSource source = event.getDamageSource();
        if (source instanceof SpellDamageSource spellDamageSource){
            event.setBlocked(false);
        }
    }





}
