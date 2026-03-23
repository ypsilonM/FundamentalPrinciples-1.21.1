package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.Config;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.config.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.item.custom.IExhaustionConsumable;
import com.ypsi.fundamentalism.network.packets.SyncCategoryLevelsPacket;
import com.ypsi.fundamentalism.network.packets.SyncReinforcementPacket;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.attachments.AvailableSpellsAttachment;
import com.ypsi.fundamentalism.attachments.SpellCategoryProgression;
import com.ypsi.fundamentalism.spells.ModSpells;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.*;

import static com.ypsi.fundamentalism.util.Util.getMaxExPerLevel;

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
    public static void registerBrewingRecipeRegister(RegisterBrewingRecipesEvent event){
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addRecipe(
                Ingredient.of(ModItems.TEST_TUBE),  Ingredient.of(Items.PITCHER_PLANT),      ModItems.PITCHER_EXTRACT.toStack(1)
        );
        builder.addRecipe(
                Ingredient.of(ModItems.TEST_TUBE),  Ingredient.of(ModItems.ARCANE_MIXTURE),  ModItems.LUMINAIRE_EXTRACT.toStack(1)
        );

    }




    @SubscribeEvent
    public static void starAlignment(CriticalHitEvent event){
        if(event.getTarget() instanceof LivingEntity enemy) {
            if (!event.isCriticalHit()) {
                return;
            }
            Player player = event.getEntity();
            if (!event.getEntity().level().isClientSide()) {
                double n = 100;
                int amplifier = 0;
                //1st Criteria
                if (player.hasEffect(ModEffects.MINDFUL_EFFECT)) {
                    n -= 20;
                    int level = player.getEffect(ModEffects.MINDFUL_EFFECT).getAmplifier() + 1;
                    amplifier = Math.clamp(level, 0, 2);
                    n -= (5) * (level);
                }
                //2nd Criteria
                if (player.getHealth() <= player.getMaxHealth() * 0.50) {
                    n -= 10;
                }
                //3rd Criteria
                if (enemy instanceof AbstractSpellCastingMob){
                    n -= 10;
                }
                if (enemy instanceof Player target){
                    double targetSP = Util.getElementalMaxValue(target);
                    double playerSP = Util.getElementalMaxValue(player);
                    double delta = Math.abs(targetSP-playerSP);
                    if(delta < playerSP*0.20){
                        n -=20;
                    }
                }

                double prob = 1 / n;
                //if(true){
                if (player.getRandom().nextDouble() < prob) {

                    event.setDamageMultiplier(2f);
                    float knockbackStrength = 2F;
                    float yawRad = player.getYRot() * (float) (Math.PI / 180.0);
                    double knockbackX = Math.sin(yawRad);
                    double knockbackZ = -Math.cos(yawRad);
                    enemy.knockback(knockbackStrength, knockbackX, knockbackZ);

                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, 0);
                    player.setData(YpsAttachments.LEVEL_EXHAUSTION, 0);
                    //SyncExhaustionPacket.sendToPlayer((ServerPlayer) player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                    //SyncExhaustionLevelPacket.sendToPlayer((ServerPlayer) player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));

                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, false, true, true));
                    var effect = player.getEffect(ModEffects.BURNOUT_EFFECT);

                    if (effect != null)
                        player.removeEffect(ModEffects.BURNOUT_EFFECT);

                    ServerLevel serverLevel = (ServerLevel) enemy.level();
                    serverLevel.sendParticles(ParticleTypes.END_ROD, enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 30, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);

                    serverLevel.sendParticles(new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 60, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);
                    serverLevel.sendParticles(ModParticles.CONSTELLATION_PARTICLE.get(), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 1, 0, 0, 0, 0.01);

                    player.addEffect(new MobEffectInstance(ModEffects.MINDFUL_EFFECT, 30 * 20, amplifier, false, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * 20, 1, false, false, true));

                    player.level().playSound(null, enemy.getX(), enemy.getY(), enemy.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.PLAYERS, 2.0F, 0.8F);
                    CameraShakeManager.addCameraShake(new CameraShakeData(player.level(), 15, enemy.position(), 10));

                }
            }
        }
    }

//    @SubscribeEvent
//    public static void mobStarAlignment(LivingDamageEvent.Pre event){
//
//
//    }

    @SubscribeEvent
    public static void moreMobResistances(FinalizeSpawnEvent event){
        var mob = event.getEntity();

        if(mob instanceof ImpEntity){
            setIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
        }
        if(mob instanceof VenemerusEntity){
            setIfNonNull(mob, AttributeRegistry.NATURE_MAGIC_RESIST, 1.5);
        }
    }
    private static void setIfNonNull(LivingEntity mob, Holder<Attribute> attribute, double value) {
        var instance = mob.getAttributes().getInstance(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
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



    @SubscribeEvent
    public static void reinforcementLayerSync(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer targetPlayer && event.getEntity() instanceof ServerPlayer trackingPlayer) {
            if (targetPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                PacketDistributor.sendToPlayer(trackingPlayer, new SyncReinforcementPacket(targetPlayer.getId(), true));
            }
        }
    }

    @SubscribeEvent
    public static void onManaReinforcementDamage(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {

                MagicData magicData = MagicData.getPlayerMagicData(event.getEntity());
                float originalDamage = event.getNewDamage();
                float currentMana = magicData.getMana();

                if(originalDamage==0){ return; }

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
                if(currentMana>=(maxMana*.05)) { //0.05
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

                addExhaustion(serverPlayer, exhaustionAcc);

//                SyncExhaustionPacket.sendToPlayer(serverPlayer,serverPlayer.getData(YpsAttachments.CURRENT_EXHAUSTION));
//                SyncExhaustionLevelPacket.sendToPlayer(serverPlayer, serverPlayer.getData(YpsAttachments.LEVEL_EXHAUSTION));

                event.setNewDamage(modifiedDamage);
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
    public static void PreSpellVerification(SpellPreCastEvent event){ //Dominan, Apparitio and Burnout
        Player player = event.getEntity();
        AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
        MagicData magicData = MagicData.getPlayerMagicData(player);

        int level = event.getSpellLevel();
        String spellId = event.getSpellId();
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);

        if(player instanceof ServerPlayer caster){

            if (caster.hasEffect(ModEffects.BURNOUT_EFFECT) && !caster.level().isClientSide) {
                cancelCast(event, magicData, caster, spell);
                return;
            }

            if(cancelDominanSpells(categories, level, caster, spell) ){
                cancelCast(event, magicData, caster, spell);
                if (caster instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.translatable("Complex spell unable to cast.").withStyle(ChatFormatting.DARK_PURPLE), true);
                }
                return;
            }
            if(teleportCanceled(categories,caster,spell,event,magicData)){
                cancelTeleportSpell(event, magicData, caster, spell);
                return;
            }
        }


    }
    @SubscribeEvent
    public static void onServerTickCancelingCast(ServerTickEvent.Post event){
        if(event.getServer().getTickCount()%5!=0) return;
        event.getServer().getPlayerList().getPlayers().forEach(player -> {
            if(player.hasEffect(ModEffects.BURNOUT_EFFECT)){
                if(MagicData.getPlayerMagicData(player).isCasting()) {
                    Utils.serverSideCancelCast(player, true);
                }
            }
        });

    }
    @SubscribeEvent
    public static void SpellNerfCast(SpellOnCastEvent event){
        Player p = event.getEntity();
        if(p instanceof ServerPlayer player && !player.level().isClientSide) {

            String spellId = event.getSpellId();

            int mana = event.getManaCost();
            int currentExLvl = player.getData(YpsAttachments.LEVEL_EXHAUSTION.get());

            if(SpellCategoriesGenerator.isInCategory(spellId, "immutable")) {
                int certumLevel = SpellCategoryProgression.getCategoryLevel(player, Principles.CERTUM);
                event.setManaCost((int) (mana*(1+Util.manaMultiplier(currentExLvl, certumLevel))));
            }

            CastSource castSource = event.getCastSource();
            int MANA_USED = event.getManaCost();
            int LEVEL = event.getSpellLevel();
            SpellRarity spellRarity = SpellRegistry.getSpell(event.getSpellId()).getRarity(LEVEL);


            AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());

            double entitySchoolPowerModifier = 1;
            entitySchoolPowerModifier = spell.getSchoolType().getPowerFor(player);

            double spellPower = player.getAttributeValue(AttributeRegistry.SPELL_POWER);
            boolean notElementalPower = entitySchoolPowerModifier <= 0 || spellPower <= 0;

            double rarityRatio = switch (spellRarity) {
                case COMMON -> 1.0;
                case UNCOMMON -> 1.10;
                case RARE -> 1.15;
                case EPIC -> 1.20;
                case LEGENDARY -> 1.25;
            };
            boolean castedByStaff = player.getOffhandItem().getItem() instanceof StaffItem || player.getMainHandItem().getItem() instanceof StaffItem;
            double staffReduction = castedByStaff ? 0.8 : 1;
            Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);

            float formulaAdd = 0;
            if (notElementalPower) {
                formulaAdd = 1;
            } else {
                formulaAdd =
                        Math.max(
                                (float) (
                                        (
                                                (Math.pow(MANA_USED, 1f / 1.3f)) /
                                                        (spellPower * entitySchoolPowerModifier) * staffReduction * rarityRatio * principleRatio(categories, player)
                                        ) * Config.fatigueGen)
                                , 1f);

            }

            addExhaustion(player, (int) formulaAdd);

            //->Leveling up
            int levelBonus = calculateXpFromSpell(LEVEL, spell);
            if (castSource != CastSource.SCROLL) {
                for (String category : categories) {
                    SpellCategoryProgression.addCategoryExperience(player, category, levelBonus);
                }
            }
            //-> Search Equipped Spellbook
            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                inv.findCurios(Curios.SPELLBOOK_SLOT).forEach(curio -> {
                    ItemStack spellBook = curio.stack();
                    if (spellBook.getItem() instanceof SpellBook) {
                        int xpGained = calculateXpFromSpell(LEVEL, spell);
                        SpellBookComponentHelper.addXP(spellBook, xpGained, player);
                    }
                });
            });
            //->ServerPlayer to ClientPlayer
//            SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
//            SyncExhaustionLevelPacket.sendToPlayer(player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));

            //--------------------------------------------//

            if (currentExLvl == 4) { //10%
                if (player.getRandom().nextDouble() < 0.10) {
                    addBurnoutEffect(player, 60);
                    burnoutSound(player.serverLevel(), player);
                }
            }
            if (currentExLvl == 3) { //3%
                if (player.getRandom().nextDouble() < 0.03) {
                    addBurnoutEffect(player, 15);
                    burnoutSound(player.serverLevel(), player);
                }
            }


        }

    }

    public static void cancelCast(SpellPreCastEvent event , @NotNull MagicData magicData, ServerPlayer caster, AbstractSpell spell){
        if(!caster.level().isClientSide){
            event.setCanceled(true);
            if (magicData.getAdditionalCastData() != null) {
                magicData.resetAdditionalCastData();
            }
            PacketDistributor.sendToPlayer(caster, new SyncTargetingDataPacket(spell, List.of()));
            vanishCastEffects(caster);
        }
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
    public static boolean teleportCanceled(Set<String> categories, ServerPlayer caster, AbstractSpell spell, SpellPreCastEvent event, MagicData magicData){
        if(categories.contains("usesTeleport")) {
            double probabilityForFail = 0;
            int categoryLevel = SpellCategoryProgression.getCategoryLevel(caster, "usesTeleport");
            int cooldown = MagicManager.getEffectiveSpellCooldown(spell, caster, event.getCastSource()) / 20;
            probabilityForFail = Util.getFailureTPChance(categoryLevel, cooldown) / 100;
            double random = caster.getRandom().nextDouble();
            boolean isCanceled = random < probabilityForFail;
            return isCanceled;
        }
        return false;
    }
    public static void cancelTeleportSpell(SpellPreCastEvent event , @NotNull MagicData magicData, ServerPlayer caster, AbstractSpell spell){
        cancelCast(event, magicData, caster, spell);
        teleportCastEffects(caster);
        MagicHelper.MAGIC_MANAGER.addCooldown(caster,spell,event.getCastSource());
    }

    public static int getLevelRequiredForRARITY(SpellRarity rarity){
        return switch (rarity) {
            case COMMON -> Config.dominanLvls.get(0);
            case UNCOMMON -> Config.dominanLvls.get(1);
            case RARE -> Config.dominanLvls.get(2);
            case EPIC -> Config.dominanLvls.get(3);
            case LEGENDARY -> Config.dominanLvls.get(4);
        };
    }
    public static void vanishCastEffects(ServerPlayer caster){
        ServerLevel serverLevel = (ServerLevel) caster.level();
        serverLevel.playSound(
                caster, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.COPPER_BULB_PLACE, SoundSource.PLAYERS, 1F, 0.5F
        );
        serverLevel.sendParticles(
                new DustParticleOptions(new Vector3f(0.5f,0.5f,0.5f),1.0f), caster.getX(), caster.getY() + caster.getBbHeight() * 0.7, caster.getZ(), 6, 0.2, 0.2, 0, 0.01
        );
    }
    public static void teleportCastEffects(ServerPlayer caster){
        ServerLevel serverLevel = (ServerLevel) caster.level();
        serverLevel.playSound(
                caster, caster.getX(), caster.getY(), caster.getZ(), SoundEvents.TRIAL_SPAWNER_SPAWN_MOB, SoundSource.PLAYERS, 1F, 1F
        );
    }


    @SubscribeEvent
    public static void exhaustionDecrement(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            final String TICK_COUNTER_KEY = "exhaustionTickCounter";
            final String LEVEL_COUNTER_KEY = "exhaustionLevelCounter";

            int tickCounter = player.getPersistentData().getInt(TICK_COUNTER_KEY);
            tickCounter++;

            int TICKS_PER_SECOND = 20;
            int regen = (int) player.getAttributeValue(YpsAttributes.EXHAUSTION_REGEN);
            regen = Math.clamp(regen, 0, 10);

            TICKS_PER_SECOND-= regen*2;

            if (tickCounter >= TICKS_PER_SECOND) {
                int levelEx = player.getData(YpsAttachments.LEVEL_EXHAUSTION);
                int currentEx = player.getData(YpsAttachments.CURRENT_EXHAUSTION);

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
                    int result = currentEx - 1;
                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.max(result, 0));
                    if (levelCounter > 0) {
                        player.getPersistentData().putInt(LEVEL_COUNTER_KEY, 0);
                    }
                }

                tickCounter = 0;
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);
                //SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                //SyncExhaustionLevelPacket.sendToPlayer(player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));

            } else {
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);
            }
            //Refresh every second

        }
    }

    private static void burnoutSound(ServerLevel level, ServerPlayer player){
        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHIELD_BREAK,
                SoundSource.PLAYERS,
                1F,
                0.5F
        );
    }
    private static void addBurnoutEffect(ServerPlayer player, int time){
        final int SECOND = 20;
        player.addEffect(new MobEffectInstance(
                ModEffects.BURNOUT_EFFECT,
                 time*SECOND,
                0,
                false,
                false,
                true
        ));
    }

    public static float principleRatio(Set<String> categories, Player player){
        float ratio = 1f;
        for(String category: categories){
            //5% per category
            int principleLevel = SpellCategoryProgression.getCategoryLevel(player, category);
            ratio+= 0.05f-(0.005f*principleLevel);
        }
        return ratio;
    }

    private static void addExhaustion(ServerPlayer player, int amountToAdd) {
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

        int maxEx = getMaxExPerLevel(currentLevel, player);

        if(currentLevel==4 && currentEx==maxEx && !player.hasEffect(ModEffects.BURNOUT_EFFECT)){
            player.addEffect(new MobEffectInstance(
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
        }
    }

    private static int calculateXpFromSpell(int level, AbstractSpell spell) {
        SpellRarity spellRarity = spell.getRarity(level);
        if(spell.getCastType() != CastType.CONTINUOUS) {
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
        }else{
            switch (spellRarity) {
                case SpellRarity.COMMON, SpellRarity.UNCOMMON:
                    return 1;
                case SpellRarity.RARE, SpellRarity.EPIC:
                    return 2;
                case SpellRarity.LEGENDARY:
                    return 3;
                default:
                    return 0;
            }
        }
    }


//    @SubscribeEvent
//    public static void brainDamage(SpellOnCastEvent event){
//        Player player = event.getEntity();
//        if(player instanceof ServerPlayer serverPlayer) {
//
//        }
//    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);
        }
    }
    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);
        }
    }

    @SubscribeEvent
    public static void uselessShield(LivingShieldBlockEvent event){
        DamageSource source = event.getDamageSource();
        if (source instanceof SpellDamageSource spellDamageSource
                && event.getEntity() instanceof ServerPlayer player && event.getBlocked()){

            final int SHIELD_COOLDOWN_TICKS = 300;
            InteractionHand shieldHand = null;
            ShieldItem shield = null;

            if (player.getMainHandItem().getItem() instanceof ShieldItem shieldItem) {
                shieldHand = InteractionHand.MAIN_HAND;
                shield = shieldItem;
            } else if (player.getOffhandItem().getItem() instanceof ShieldItem shieldItem) {
                shieldHand = InteractionHand.OFF_HAND;
                shield = shieldItem;
            }
            player.getCooldowns().addCooldown(shield, SHIELD_COOLDOWN_TICKS);

            if (player.isUsingItem() && shieldHand != null) {
                ItemStack activeItem = player.getItemInHand(shieldHand);
                if (activeItem.getItem() instanceof ShieldItem) {
                    player.stopUsingItem();
                    player.connection.send(new ClientboundSetEntityLinkPacket(player, null));
                }
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SHIELD_BREAK, player.getSoundSource(), 0.8F, 0.8F + player.level().random.nextFloat() * 0.4F);

        }
    }


    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            player.server.execute(() -> {
                SyncCategoryLevelsPacket.sendToPlayer(player);
                player.setData(YpsAttachments.CURRENT_EXHAUSTION, 0);
                player.setData(YpsAttachments.LEVEL_EXHAUSTION, 0);
                //SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                //SyncExhaustionLevelPacket.sendToPlayer(player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));
            });
        }
    }

    ///FUCKING SHIT
    //Unlock Tonatiu Spell
    @SubscribeEvent
    public static void onFireBossDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getEntity() instanceof FireBossEntity fireBoss)) {
            return;
        }
        var source = event.getSource();
        if (source == null) {
            return;
        }
        var killer = source.getEntity();
        if (!(killer instanceof ServerPlayer player)) {
            return;
        }
        AbstractSpell spell = ModSpells.SOL_SPELL.get();
        var data = MagicData.getPlayerMagicData(player).getSyncedData();

        if (!data.isSpellLearned(spell)) {
            data.learnSpell(spell);
        }
    }

//    @SubscribeEvent
//    public static void inversedHealingSpell(SpellHealEvent event){
//        LivingEntity objective = event.getTargetEntity();
//        LivingEntity caster = event.getEntity();
//        float healAmount = event.getHealAmount();
//
//        var damageType = event.getSchoolType().getDamageType();
//        var damageSource = DamageSources.get(objective.level(), damageType);
//
//        if(!caster.isAlliedTo(objective) &&
//                ((objective.getType().is(EntityTypeTags.UNDEAD))
//                || objective.getType().is(EntityTypeTags.SENSITIVE_TO_SMITE)
//                || objective.getType().is(EntityTypeTags.INVERTED_HEALING_AND_HARM)))
//        {
//            DamageSources.applyDamage(objective, healAmount*2, damageSource);
//
//        }
//    }

    @SubscribeEvent
    public static void maxExhaustionSpellbook(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof SpellBook) {
            if(stack.has(YpsDataComponents.YP_SPELL_SLOTS)){
                AttributeModifier modifier = new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "ex_add"),
                        stack.get(YpsDataComponents.YP_SPELL_SLOTS)*5,
                        AttributeModifier.Operation.ADD_VALUE
                );
                event.addModifier(YpsAttributes.MAX_EXHAUSTION.getDelegate(), modifier);
            }

        }
    }


}
