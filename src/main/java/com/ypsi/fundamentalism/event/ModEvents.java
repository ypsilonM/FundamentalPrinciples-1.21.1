package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.ServerConfig;
import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.*;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.component.SpellbookLevel.SpellBookComponentHelper;
import com.ypsi.fundamentalism.component.YpsDataComponents;
import com.ypsi.fundamentalism.entity.mobs.cherry_bird.CherryBirdEntity;
import com.ypsi.fundamentalism.entity.mobs.runear.RunearEntity;
import com.ypsi.fundamentalism.item.custom.SpellbookCover;
import com.ypsi.fundamentalism.principleGen.SpellCategoriesGenerator;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.enchantment.FundEnchantments;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import com.ypsi.fundamentalism.item.ModItems;
import com.ypsi.fundamentalism.item.custom.IExhaustionConsumable;
import com.ypsi.fundamentalism.network.packets.SyncCategoryLevelsPacket;
import com.ypsi.fundamentalism.network.packets.SyncReinforcementPacket;
import com.ypsi.fundamentalism.particle.ModParticles;
import com.ypsi.fundamentalism.spells.ModSpells;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import com.ypsi.fundamentalism.util.Principles;
import com.ypsi.fundamentalism.util.Util;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.magic.MagicHelper;
import io.redspace.ironsspellbooks.api.magic.SpellSelectionManager;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.capabilities.magic.PlayerCooldowns;
import io.redspace.ironsspellbooks.compat.Curios;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.FireBossEntity;
import io.redspace.ironsspellbooks.item.SpellBook;
import io.redspace.ironsspellbooks.item.UniqueSpellBook;
import io.redspace.ironsspellbooks.item.weapons.StaffItem;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.*;
import io.redspace.ironsspellbooks.registries.ComponentRegistry;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.*;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

import java.util.*;
import java.util.List;

import static com.ypsi.fundamentalism.util.Util.getMaxFatigue;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ModEvents {

    //STAR ALIGNMENT
    @SubscribeEvent
    public static void playerStarAlignment(CriticalHitEvent event){
        if(event.getTarget() instanceof LivingEntity enemy) {
            if (!event.isCriticalHit()) {
                return;
            }
            Player player = event.getEntity();

            if (!event.getEntity().level().isClientSide()) {

                double n = 100;
                int amplifier = 0;

                if (player.hasEffect(ModEffects.MINDFUL_EFFECT)) {
                        n -= 20;
                    int level = player.getEffect(ModEffects.MINDFUL_EFFECT).getAmplifier() + 1;
                    amplifier = Math.clamp(level, 0, 2);
                        n -= (5) * (level);
                }
                if (player.getHealth() <= player.getMaxHealth() * 0.50)
                        n -= 10;
                if (enemy instanceof AbstractSpellCastingMob)
                        n -= 10;
                if (enemy instanceof Player target){
                    double targetSP = Util.getElementalMaxValue(target);
                    double playerSP = Util.getElementalMaxValue(player);
                    double delta = Math.abs(targetSP-playerSP);
                    if(delta < playerSP*0.20)
                        n -=20;
                }

                double graceful = player.getAttributeValue(YpsAttributes.RESONANCE);
                double graceMultiplier = 1.0 / (1.0 + graceful * 0.5);
                n = n * graceMultiplier;

                n = Math.clamp(n, 10, 200);

                double prob = 1 / n;

                if (player.getRandom().nextDouble() < prob) {

                    event.setDamageMultiplier( ServerConfig.ALIGNMENT_MULTIPLIER.get().floatValue());
                    float knockbackStrength = 2F;
                    float yawRad = player.getYRot() * (float) (Math.PI / 180.0);
                    double knockbackX = Math.sin(yawRad);
                    double knockbackZ = -Math.cos(yawRad);
                    enemy.knockback(knockbackStrength, knockbackX, knockbackZ);

                    FatigueManager.cleanFatigue(player);

                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, false, true, true));
                    var effect = player.getEffect(ModEffects.BURNOUT_EFFECT);

                    if (effect != null)
                        player.removeEffect(ModEffects.BURNOUT_EFFECT);

                    ServerLevel serverLevel = (ServerLevel) enemy.level();
                    serverLevel.sendParticles(ParticleTypes.END_ROD, enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 30, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);

                    serverLevel.sendParticles(new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 60, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);
                    serverLevel.sendParticles(ModParticles.CONSTELLATION_PARTICLE.get(), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 1, 0, 0, 0, 0.01);

                    //MobEffects
                    player.addEffect(new MobEffectInstance(ModEffects.MINDFUL_EFFECT, 30 * 20, amplifier, false, true, true));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * 20, 1, false, false, true));
                    //Reduce cooldowns
                    PlayerCooldowns playerCooldowns = MagicData.getPlayerMagicData(player).getPlayerCooldowns();
                    playerCooldowns.getSpellCooldowns().forEach((id, crInstance)->{
                        int cdrRemain = crInstance.getCooldownRemaining();
                        crInstance.decrementBy(cdrRemain/2);
                    });
                    playerCooldowns.syncToPlayer((ServerPlayer)player);

                    player.level().playSound(null, enemy.getX(), enemy.getY(), enemy.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.PLAYERS, 2.0F, 0.8F);
                    CameraShakeManager.addCameraShake(new CameraShakeData(player.level(), 15, enemy.position(), 10));


                }
            }
        }
    }
    @SubscribeEvent
    public static void mobStarAlignment(LivingDamageEvent.Pre event){
        if(event.getSource().getEntity() instanceof AbstractSpellCastingMob castingMob
                && (event.getSource().is(Tags.DamageTypes.IS_PHYSICAL))
                && !(event.getSource() instanceof SpellDamageSource)
        ){
            LivingEntity enemy = event.getEntity();
            double prob = ServerConfig.MOB_STAR_ALIGNMENT.getAsDouble();

            if (enemy.getRandom().nextDouble() < prob) {

                event.setNewDamage((float) (event.getNewDamage()* (ServerConfig.ALIGNMENT_MULTIPLIER.get())));

                float knockbackStrength = 2F;
                float yawRad = castingMob.getYRot() * (float) (Math.PI / 180.0);
                double knockbackX = Math.sin(yawRad);
                double knockbackZ = -Math.cos(yawRad);
                enemy.knockback(knockbackStrength, knockbackX, knockbackZ);

                enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30 * 20, 0, false, true, true));

                ServerLevel serverLevel = (ServerLevel) enemy.level();
                serverLevel.sendParticles(ParticleTypes.END_ROD, enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 30, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);
                serverLevel.sendParticles(new DustParticleOptions(new Vector3f(0.0f, 0.0f, 0.0f), 1.0f), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 60, enemy.getBbWidth() * 1, enemy.getBbHeight() * 0.4, enemy.getBbWidth() * 1, 0.01);
                serverLevel.sendParticles(ModParticles.CONSTELLATION_PARTICLE.get(), enemy.getX(), enemy.getY() + enemy.getBbHeight() * 0.5, enemy.getZ(), 1, 0, 0, 0, 0.01);

                castingMob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * 20, 1, false, false, true));
                castingMob.level().playSound(null, enemy.getX(), enemy.getY(), enemy.getZ(), SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.PLAYERS, 2.0F, 0.8F);
                CameraShakeManager.addCameraShake(new CameraShakeData(castingMob.level(), 15, enemy.position(), 10));

            }

        }
    }
    @SubscribeEvent
    public static void advancementStar(AdvancementEvent.AdvancementEarnEvent event){
        if(event.getAdvancement().id().equals
                (ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful_advancement"))){
            Player player = event.getEntity();
            YpsAttributeManager.RESONANCE.applyModifier(player, 1);
        }
    }


    //MANA REINFORCEMENT
    @SubscribeEvent(priority = EventPriority.HIGHEST)
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

                int currentExLvl = FatigueManager.getFatigueLevel(serverPlayer);
                //int currentExLvl = serverPlayer.getData(YpsAttachments.LEVEL_EXHAUSTION.get());
                float fatigueMult = 1;
                if(ServerConfig.FATIGUE_SYSTEM.get()){
                    fatigueMult = switch(currentExLvl){
                        case 1 -> 0.125f;
                        case 2 -> 0.25f;
                        case 3 -> 0.375f;
                        case 4 -> 0.5f;
                        default -> 0;
                    };
                }
                float mitigatedDamage = (float)(Math.sqrt((maxMana/150))*spellPower);
                mitigatedDamage = mitigatedDamage-(mitigatedDamage*fatigueMult);
                float modifiedDamage = originalDamage;

                float manaToConsume = (float)(maxMana*0.05);
                int exhaustionAcc = 0;

                int augereXp = 0;
                if(currentMana>=(maxMana*.05)) { //0.05
                    if (originalDamage < mitigatedDamage) {
                        modifiedDamage = 0.0f;
                        manaToConsume/=2;
                        magicData.addMana(-manaToConsume);
                        exhaustionAcc = 2;
                        augereXp = 2;
                    } else {
                        modifiedDamage = originalDamage - mitigatedDamage;
                        magicData.addMana(-manaToConsume);
                        exhaustionAcc = 5;
                        augereXp = 5;
                    }
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            0.6F,
                            0.4F
                    );

                    PrinciplesProgressionManager.addCategoryExperience(
                            serverPlayer,
                            PrinciplesProgressionManager.getTechnicalName(Principles.AUGERE),
                            augereXp
                    );

                    PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(magicData));
                }

                if(ServerConfig.FATIGUE_SYSTEM.get())
                    addExhaustion(serverPlayer, exhaustionAcc);

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
    public static void reinforcementLayerSync(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer targetPlayer && event.getEntity() instanceof ServerPlayer trackingPlayer) {
            if (targetPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                PacketDistributor.sendToPlayer(trackingPlayer, new SyncReinforcementPacket(targetPlayer.getId(), true));
            }
        }
    }

    //Spell casting Events
    @SubscribeEvent
    public static void PreSpellVerification(SpellPreCastEvent event){ //Dominan, Apparitio and Burnout
        Player player = event.getEntity();
        AbstractSpell spell = SpellRegistry.getSpell(event.getSpellId());
        MagicData magicData = MagicData.getPlayerMagicData(player);

        int level = event.getSpellLevel();
        String spellId = event.getSpellId();
        Set<String> categories = SpellCategoriesGenerator.getCategoriesForSpell(spellId);

        if(player instanceof ServerPlayer caster){
            if ((
                    caster.hasEffect(ModEffects.BURNOUT_EFFECT) || caster.hasEffect(ModEffects.CHAINED_EFFECT)
                    || caster.getPersistentData().getBoolean("boostActive")
            ) && !caster.level().isClientSide) {
                cancelCast(event, magicData, caster, spell);
                return;
            }

            if(cancelDominanSpells(categories, level, caster, spell) ){
                cancelCast(event, magicData, caster, spell);
                if (caster instanceof ServerPlayer serverPlayer) {
                    serverPlayer.displayClientMessage(Component.translatable(
                            "ui.ypfundamentals.dominan_spell_failure").withStyle(ChatFormatting.GOLD), true);
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
            if((player.hasEffect(ModEffects.BURNOUT_EFFECT) || player.hasEffect(ModEffects.CHAINED_EFFECT))){
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
            int currentExLvl = FatigueManager.getFatigueLevel(p);

            int efficiencyLvl = 5;
            if(ServerConfig.EFFICIENCY_ATTRIBUTE.get())
                efficiencyLvl = player.getData(YpsAttachments.CAST_EFFICIENCY.get()).getEfficiencyLevel();

            //Certum Mana Multiplier
            if (ServerConfig.FATIGUE_SYSTEM.get() && ServerConfig.ACTIVE_CERTUM.get() && ServerConfig.PRINCIPLES_SYSTEM.get()) {
                if (SpellCategoriesGenerator.isInPrinciple(spellId, Principles.CERTUM)) {

                    int certumLevel = PrinciplesProgressionManager.getCategoryLevel(player, Principles.CERTUM);
                    event.setManaCost( (int) (
                                (mana * (1 + Util.certumManaMultiplier(currentExLvl, certumLevel))) *
                                ( ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, true) : 1 )
                            )
                    );
                }else{
                    event.setManaCost((int)
                            (mana * ( ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1 )));
                }
            }else{
                event.setManaCost((int)
                        (mana * ( ServerConfig.EFFICIENCY_ATTRIBUTE.get() ? Util.getEfficiencyMultiplier(efficiencyLvl, false) : 1)));
            }


            //ALL Fatigue Multipliers
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
                        Math.max((float) (
                            ((Math.pow(MANA_USED, 1f / 1.3f))
                            / (spellPower * entitySchoolPowerModifier) * staffReduction * rarityRatio * principleFatigueRatio(categories, player)
                            ) * ServerConfig.FATIGUE_MULTIPLIER.get()) , 1f);

            }
            if(MagicData.getPlayerMagicData(player).getPlayerRecasts().hasRecastForSpell(spell)){
                if(categories.contains("usesSummon"))
                    formulaAdd=0;
                if(categories.contains("hasRecasts"))
                    formulaAdd/=4;
            }

            //Fatigue Addition
            if(ServerConfig.FATIGUE_SYSTEM.get())
                addExhaustion(player, (int) formulaAdd);

            // -> Leveling up Principles
            int levelBonus = calculateXpFromSpell(LEVEL, spell);
            if (castSource != CastSource.SCROLL && ServerConfig.PRINCIPLES_SYSTEM.get()) {
                for (String category : categories) {
                    PrinciplesProgressionManager.addCategoryExperience(player, category, levelBonus * ServerConfig.XP_PRINCIPLE_MULTIPLIER.get());
                }
            }

            //BURNOUT CHANCE
            if(ServerConfig.FATIGUE_SYSTEM.get()) {
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

            //Efficiency LvlUp
            if(ServerConfig.EFFICIENCY_ATTRIBUTE.get()) {
               int MANA = event.getOriginalManaCost();
               EfficiencyManager.addXp(MANA, player);
               player.sendSystemMessage(Component.literal(" N: "+EfficiencyManager.getCurrentLvl(player)
                       + " | "+ "X: "+EfficiencyManager.getCurrentXP(player)));
            }


        }

    }


    //FatigueEvents
    @SubscribeEvent
    public static void fatigueDecrement(ServerTickEvent.Post event) {
        if(!ServerConfig.FATIGUE_SYSTEM.get()) return;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            final String TICK_COUNTER_KEY = "exhaustionTickCounter";
            final String LEVEL_COUNTER_KEY = "exhaustionLevelCounter";

            int tickCounter = player.getPersistentData().getInt(TICK_COUNTER_KEY);
            tickCounter++;

            int TICKS_PER_SECOND = 20;
            int regen = (int) player.getAttributeValue(YpsAttributes.FATIGUE_REGEN);
            regen = Math.clamp(regen, 0, 10);

            TICKS_PER_SECOND-= regen*2;

            if (tickCounter >= TICKS_PER_SECOND) {

                int levelEx = FatigueManager.getFatigueLevel(player);
                int currentEx = FatigueManager.getFatigueAmount(player);

                int levelCounter = player.getPersistentData().getInt(LEVEL_COUNTER_KEY);

                if (currentEx == 0) {
                    levelCounter++;
                    player.getPersistentData().putInt(LEVEL_COUNTER_KEY, levelCounter);

                    if (levelEx >= 1 && levelCounter >= 5) {
                        int newLevel = levelEx - 1;
                        FatigueManager.setFatigueLevel(player, newLevel);
                        int newMax = getMaxFatigue(newLevel, player);
                        FatigueManager.setFatigueAmount(player, newMax);
                        player.getPersistentData().putInt(LEVEL_COUNTER_KEY, 0);
                    }
                } else {
                    int result = currentEx - 1;
                    FatigueManager.setFatigueAmount(player,Math.max(result, 0) );
                    if (levelCounter > 0) {
                        player.getPersistentData().putInt(LEVEL_COUNTER_KEY, 0);
                    }
                }

                tickCounter = 0;
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);

            } else {
                player.getPersistentData().putInt(TICK_COUNTER_KEY, tickCounter);
            }
        }
    }

    //Incantations
    @SubscribeEvent
    public static void handleIncantationEffects(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            final String TICK_COUNTER_KEY = "boostTickCounter";
            final String BOOST_ACTIVE_KEY = "boostActive";
            final String LIFE_TIME_KEY = "boostLifetime";

            if (player.getPersistentData().getBoolean(BOOST_ACTIVE_KEY)) {
                int time = Math.clamp(player.getPersistentData().getInt(TICK_COUNTER_KEY) + 1, 0, 20*4);
                player.getPersistentData().putInt(TICK_COUNTER_KEY, time);

                if (time % 20 == 0 && time > 0 && player.getData(YpsAttachments.BOOST.get()) < 4) {
                    int amplifier = time / 20;
                    player.sendSystemMessage(Component.literal("Tier " + amplifier));
                    player.setData(YpsAttachments.BOOST.get(), amplifier);
                    player.getPersistentData().putInt(LIFE_TIME_KEY, 20*15);
                    //player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 20 * 5, amplifier));
                }
            } else {
                if (player.getPersistentData().getInt(TICK_COUNTER_KEY) != 0) {
                    player.getPersistentData().putInt(TICK_COUNTER_KEY, 0);
                    //player.removeEffect(MobEffects.HEALTH_BOOST);
                }
            }

            if(player.getPersistentData().getInt(LIFE_TIME_KEY) != 0 && !MagicData.getPlayerMagicData(player).isCasting()){
                int currentTimer = player.getPersistentData().getInt(LIFE_TIME_KEY);
                player.getPersistentData().putInt(LIFE_TIME_KEY, currentTimer - 1);
                player.sendSystemMessage(Component.literal("CURRENT COUNTER: "+player.getPersistentData().getInt(LIFE_TIME_KEY) ));
            }else{
                //clean
                player.setData(YpsAttachments.BOOST.get(), 0);
                player.level().playSound(player, player.getOnPos() ,SoundEvents.GOAT_HORN_PLAY, player.getSoundSource());
            }
        }
    }

    @SubscribeEvent
    public static void resting(SleepFinishedTimeEvent event){

        if(!ServerConfig.FATIGUE_SYSTEM.get()) return;

        Objects.requireNonNull(event.getLevel().getServer()).getPlayerList().getPlayers().forEach(e->{

            if(e.isSleeping()) {
                int currentLevel = FatigueManager.getFatigueLevel(e);
                int currentEx = FatigueManager.getFatigueAmount(e);

                int newLevel = Math.max(currentLevel - 2, 0);
                int newExh = Math.min(currentEx, getMaxFatigue(newLevel, e));

                FatigueManager.setFatigueLevel(e, newLevel);
                FatigueManager.setFatigueAmount(e, newExh);
            }

        });
    }
    @SubscribeEvent
    public static void onPlayerDeathResetFatigue(PlayerEvent.PlayerRespawnEvent event){
        if(event.getEntity() instanceof ServerPlayer player){
            player.server.execute(() -> {
                SyncCategoryLevelsPacket.sendToPlayer(player);
                if(ServerConfig.FATIGUE_SYSTEM.get()) {
                    FatigueManager.cleanFatigue(player);
                }
//                player.setData(YpsAttachments.CURRENT_EXHAUSTION, 0);
//                player.setData(YpsAttachments.LEVEL_EXHAUSTION, 0);
                //SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
                //SyncExhaustionLevelPacket.sendToPlayer(player, player.getData(YpsAttachments.LEVEL_EXHAUSTION));
            });
        }
    }
    @SubscribeEvent
    public static void onPlayerLoginFatigueSync(PlayerEvent.@NotNull PlayerLoggedInEvent event) {
        if(ServerConfig.FATIGUE_SYSTEM.get()) {
            if (event.getEntity() instanceof ServerPlayer player) {
                if (!player.getPersistentData().contains("exhaustionTickCounter")) {
                    player.getPersistentData().putInt("exhaustionTickCounter", 0);
                }
                if (!player.getPersistentData().contains("exhaustionLevelCounter")) {
                    player.getPersistentData().putInt("exhaustionLevelCounter", 0);
                }
            }
        }else{
            if(event.getEntity() instanceof ServerPlayer player){
                FatigueManager.cleanFatigue(player);
            }
        }
        if(true){
            if(event.getEntity() instanceof ServerPlayer player) {
                if(!player.getPersistentData().contains("boostTickCounter")) {
                    player.getPersistentData().putInt("boostTickCounter", 0);
                }
                if(!player.getPersistentData().contains("boostActive")) {
                    player.getPersistentData().putInt("boostActive", 0);
                }
                if(!player.getPersistentData().contains("boostLifetime")) {
                    player.getPersistentData().putInt("boostLifetime", 0);
                }
            }
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);

            if(player.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mindful_advancement")) != null){
                YpsAttributeManager.RESONANCE.applyModifier(player, 1);
            }

        }
    }


    //Principles Events
    @SubscribeEvent
    public static void spellSelectionManager(SpellSelectionManager.SpellSelectionEvent event) {
        if (event.getEntity() instanceof Player player) {

            //Remedium
            int remediumLvl = PrinciplesProgressionManager.getCategoryLevel(player, Principles.REMEDIUM);
            if (remediumLvl >= 5) {
                switch (remediumLvl) {
                    case 5, 6, 7, 8, 9:
                        event.addSelectionOption(new SpellData(ModSpells.LAW_OF_REGRESSION_SPELL.get(), 1), "remedium_slot", 0);
                        break;
                    case 10, 11, 12, 13:
                        event.addSelectionOption(new SpellData(ModSpells.LAW_OF_REGRESSION_SPELL.get(), 2), "remedium_slot", 0);
                        break;
                    case 14, 15, 16, 18, 19:
                        event.addSelectionOption(new SpellData(ModSpells.LAW_OF_REGRESSION_SPELL.get(), 3), "remedium_slot", 0);
                        break;
                    case 20:
                        event.addSelectionOption(new SpellData(ModSpells.LAW_OF_REGRESSION_SPELL.get(), 4), "remedium_slot", 0);
                }
            }

            //Saeptum
            if (PrinciplesProgressionManager.getCategoryLevel(player, Principles.CONCENTRATIO) >= 12 &&
                    PrinciplesProgressionManager.getCategoryLevel(player, Principles.LOCUS) >= 10 &&
                    PrinciplesProgressionManager.getCategoryLevel(player, Principles.PERCEPTIO) >= 12 &&
                    PrinciplesProgressionManager.getCategoryLevel(player, Principles.APPARITIO) >= 10) {
                event.addSelectionOption(new SpellData(ModSpells.SAEPTUM_PELL.get(), 1), "domain_slot", 0);
            }
        }
    }
    @SubscribeEvent
    public static void vitaleSummonCooldown(SpellCooldownAddedEvent.Pre event){

        if(!ServerConfig.ACTIVE_VITALE.get() || !ServerConfig.PRINCIPLES_SYSTEM.get()) return;

        AbstractSpell spell = event.getSpell();
        if( event.getEntity() instanceof ServerPlayer serverPlayer
                && SpellCategoriesGenerator.isInCategory(spell.getSpellId(), "usesSummon")
                && spell.getRecastCount(1, serverPlayer)==2) {

                int cooldown = event.getEffectiveCooldown();
                int vitaleLevel = PrinciplesProgressionManager.getCategoryLevel(serverPlayer, Principles.VITALE);
                cooldown -= (int) (cooldown*Util.getCDR(vitaleLevel));
                event.setEffectiveCooldown(cooldown);
        }
    }
    @SubscribeEvent
    public static void reverseCurseTechnique(SpellHealEvent event){

        if(!ServerConfig.ACTIVE_REMEDIUM.get() || !ServerConfig.PRINCIPLES_SYSTEM.get()) return;

        if(event.getSchoolType().equals(YpsSchoolRegistry.FUNDAMENTALISM.get())) return;

        if(event.getTargetEntity() instanceof ServerPlayer healedEntity &&
                event.getEntity() instanceof ServerPlayer caster){

            int remediumLevel = PrinciplesProgressionManager.getCategoryLevel(caster, Principles.REMEDIUM);
            float FOOD_CONSUME = Util.getFoodToCONSUME(remediumLevel);

            float healAmount = event.getHealAmount();

            float healed = healedEntity.getHealth() + healAmount - healedEntity.getMaxHealth();
            if (healed > 0) { //sobra
                healAmount -= healed;
            } //No sobra

            FoodData foodData = healedEntity.getFoodData();

            float totalToConsume = healAmount * FOOD_CONSUME;
            float remainingToConsume = totalToConsume;
//            float saturation = foodData.getSaturationLevel();
//            if (saturation > 0) {
//                float saturationConsumed = Math.min(saturation, remainingToConsume);
//                foodData.setSaturation(saturation - saturationConsumed);
//                remainingToConsume -= saturationConsumed;
//            }
            if (remainingToConsume > 0) {

                int currentFood = foodData.getFoodLevel();
                int foodToConsume = (int) Math.ceil(remainingToConsume);
                int food = currentFood - foodToConsume;
                int newFood = Math.max(0, food);
                foodData.setFoodLevel(newFood);

                if (food < 0) {

                    healAmount += (FOOD_CONSUME * food);
                    healedEntity.heal(healAmount);

                    healedEntity.addEffect(new MobEffectInstance(
                            MobEffectRegistry.BLIGHT,
                            5,
                            10,
                            true,
                            false,
                            true
                    ));
                }

            }

        }

    }
    @SubscribeEvent
    public static void onDimensionChangeSyncPrincipleLvls(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            SyncCategoryLevelsPacket.sendToPlayer(player);
        }
    }


    //Mob related Events
    @SubscribeEvent
    public static void unlockTonatiu(LivingDeathEvent event) {
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
    @SubscribeEvent
    public static void moreMobResistances(FinalizeSpawnEvent event){
        var mob = event.getEntity();

        if(mob instanceof ImpEntity){
            setIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
        }
        if(mob instanceof VenemerusEntity){
            setIfNonNull(mob, AttributeRegistry.NATURE_MAGIC_RESIST, 1.5);
        }
        if(mob instanceof RunearEntity){
            setIfNonNull(mob, AttributeRegistry.SPELL_RESIST, 2);
            setIfNonNull(mob, AttributeRegistry.SPELL_POWER, 1.0);
        }
        if(mob instanceof CherryBirdEntity){
            setIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 2);
        }

    }
    private static void setIfNonNull(LivingEntity mob, Holder<Attribute> attribute, double value) {
        var instance = mob.getAttributes().getInstance(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
    @SubscribeEvent
    public static void cherryBirdAgressionEvent(BlockEvent.BreakEvent event){
        List<Block> blocks = List.of(Blocks.CHERRY_LOG, Blocks.CHERRY_LEAVES);
        Block block = event.getState().getBlock();
        if(blocks.contains(block)){
            Player player = event.getPlayer();
            List<CherryBirdEntity> cherryBirdEntities = event.getLevel().getEntitiesOfClass(
                    CherryBirdEntity.class, new AABB(event.getPos()).inflate(20)
            );
            for(CherryBirdEntity bird : cherryBirdEntities){
                if(bird.distanceToSqr(player) <= 20*20){
                    bird.setPersistentAngerTarget(player.getUUID());
                    bird.setRemainingPersistentAngerTime((TimeUtil.rangeOfSeconds(20, 39).sample(bird.getRandom())));
                    bird.setTarget(player);
                }
            }
        }
    }


    //Spellbook Leveling
    @SubscribeEvent
    public static void intializeSpellbooks(CurioAttributeModifierEvent event) {

        if (!ServerConfig.SPELLBOOK_LEVELS.get()) return;

        ItemStack itemStack = event.getItemStack();

        if (!itemStack.has(YpsDataComponents.YP_SPELL_SLOTS)) {
            if (itemStack.getItem() instanceof SpellBook spellBook) {

                if (spellBook instanceof UniqueSpellBook uniqueSpellBook) {
                    if (ServerConfig.SPELLBOOK_LEVELS.get()) {

                        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);

                        var spellContainer = ISpellContainer.create(4, true, true).mutableCopy();
                        uniqueSpellBook.getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), ServerConfig.UNIQUE_SPELLBOOKS.get()));
                        itemStack.set(YpsDataComponents.YP_SPELL_SLOTS, uniqueSpellBook.getMaxSpellSlots());
                        itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());

                    } else {

                        var spellContainer = ISpellContainer.create(uniqueSpellBook.getMaxSpellSlots(), true, true).mutableCopy();
                        uniqueSpellBook.getSpells().forEach(spellSlot -> spellContainer.addSpell(spellSlot.getSpell(), spellSlot.getLevel(), ServerConfig.UNIQUE_SPELLBOOKS.get()));
                        itemStack.set(ComponentRegistry.SPELL_CONTAINER, spellContainer.toImmutable());
                    }
                } else {
                    if (ServerConfig.SPELLBOOK_LEVELS.get()) {

                        SpellBookComponentHelper.ensureSpellBookComponents(itemStack);

                        itemStack.set(ComponentRegistry.SPELL_CONTAINER, ISpellContainer.create(4, true, true));
                        itemStack.set(YpsDataComponents.YP_SPELL_SLOTS.get(), spellBook.getMaxSpellSlots());

                    } else {
                        ISpellContainer.set(itemStack, ISpellContainer.create(spellBook.getMaxSpellSlots(), true, true));
                    }
                    //}
                }

            }
        }else{
            if(ServerConfig.SPELLBOOK_LEVELS.get()) {
                int spellSlots = itemStack.get(YpsDataComponents.YP_SPELL_SLOTS) != null ? itemStack.get(YpsDataComponents.YP_SPELL_SLOTS) : 0;
                int spellLevel = itemStack.get(YpsDataComponents.SPELLBOOK_LEVEL) != null ? itemStack.get(YpsDataComponents.SPELLBOOK_LEVEL).level() : 0;

                AttributeModifier exhaustion_modifier = new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fp_ex_add"),
                        spellSlots * 5,
                        AttributeModifier.Operation.ADD_VALUE
                );
                AttributeModifier mana_modifier = new AttributeModifier(
                        ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "fp_sp_add"),
                        switch (spellLevel) {
                            case 1-> 0; //common
                            case 2-> .02; //uncommon
                            case 3-> .05; //rare
                            case 4-> .08; //epic
                            case 5-> .10; //legendary
                            case 6-> .15; //mythic
                            default -> 0;
                        },
                        AttributeModifier.Operation.ADD_MULTIPLIED_BASE
                );

                event.addModifier(YpsAttributes.MAX_FATIGUE, exhaustion_modifier);
                event.addModifier(AttributeRegistry.SPELL_POWER, mana_modifier);
            }
        }
    }
    @SubscribeEvent
    public static void onCurioTooltipLoad(ItemTooltipEvent event){
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof SpellBook && stack.has(YpsDataComponents.YP_SPELL_SLOTS)) {
            List<Component> tooltip = event.getToolTip();

            int lvl = stack.get(YpsDataComponents.SPELLBOOK_LEVEL.get()).level();

            MutableComponent first = tooltip.getFirst().copy();
            int color = switch (lvl){
                case 1 -> ChatFormatting.GRAY.getColor(); //common
                case 2 -> ChatFormatting.GREEN.getColor(); //uncommon
                case 3 -> ChatFormatting.AQUA.getColor(); //rare
                case 4 -> ChatFormatting.LIGHT_PURPLE.getColor(); //epic
                case 5 -> ChatFormatting.GOLD.getColor(); //legendary
                case 6 -> 0xFF0066;
                default -> ChatFormatting.BLACK.getColor();
            };
            first.append(Component.literal(" ♦ ").withColor(color));
            tooltip.removeFirst();
            tooltip.addFirst(first);
        }
    }

    //Others
    @SubscribeEvent
    public static void changeModsSpellName(CustomizeScrollModNameEvent event) {
        if (event.getModId().equals(FundamentalPrinciples.MOD_ID)) {
            Component name = Component.literal(" Fundamental Principles ✎").withStyle(ChatFormatting.GOLD);
            event.setModName(name);
        }
    }
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
    public static void uselessShield(LivingShieldBlockEvent event){
        DamageSource source = event.getDamageSource();
        float blockedDamage = event.getBlockedDamage();

        if (source instanceof SpellDamageSource && event.getEntity() instanceof ServerPlayer player && event.getBlocked()){

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

            if (player.isUsingItem() && shieldHand != null) {
                ItemStack activeItem = player.getItemInHand(shieldHand);

                if (activeItem.getItem() instanceof ShieldItem) {
                    Holder<Enchantment> aegisHolder = player.level().registryAccess()
                            .lookupOrThrow(Registries.ENCHANTMENT)
                            .get(FundEnchantments.AEGIS)
                            .orElse(null);

                    int level = 1;
                    float blockeableDamage = 0f;

                    if(aegisHolder!=null) {
                        level = activeItem.getEnchantmentLevel(aegisHolder) + 1;
                        blockeableDamage = switch (level) {
                            case 1 -> 0;
                            case 2 -> 10;
                            case 3 -> 15;
                            case 4 -> 30;
                            case 5 -> 45;
                            case 6 -> 60;
                            default -> 0;
                        };
                    }
                    if(blockedDamage >= blockeableDamage) {
                        player.stopUsingItem();
                        player.connection.send(new ClientboundSetEntityLinkPacket(player, null));
                        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                SoundEvents.SHIELD_BREAK, player.getSoundSource(), 0.8F, 0.8F + player.level().random.nextFloat() * 0.4F);
                        player.getCooldowns().addCooldown(shield, SHIELD_COOLDOWN_TICKS / (level));
                    }
                }

            }

        }
    }
    @SubscribeEvent
    public static void putCoverInSpellbok(PlayerInteractEvent.RightClickItem event) {
        if (!ServerConfig.SPELLBOOK_LEVELS.get()) return;

        Player player = event.getEntity();
        ItemStack stackInHand = player.getItemInHand(event.getHand());
        if (!(stackInHand.getItem() instanceof SpellbookCover)) return;
        CuriosApi.getCuriosInventory(player).ifPresent(inv -> {

            var slotResult = inv.findCurios(Curios.SPELLBOOK_SLOT);

            if (slotResult.isEmpty()) return;
            ItemStack curioSpellbook = slotResult.getFirst().stack();

            if (!(curioSpellbook.getItem() instanceof SpellBook)) return;
            if (stackInHand.getItem() instanceof SpellbookCover spellbookCover) {
                boolean used = false;
                int currentLevel = SpellBookComponentHelper.getLevel(curioSpellbook);

                if (ModItems.NOVICE_SPELLBOOK_COVER.get().equals(spellbookCover) && currentLevel == 1) {
                    SpellBookComponentHelper.setLevel(curioSpellbook, 2, player);
                    used = true;
                } else if (ModItems.ADEPT_SPELLBOOK_COVER.get().equals(spellbookCover) && currentLevel == 2) {
                    SpellBookComponentHelper.setLevel(curioSpellbook, 3, player);
                    used = true;
                } else if (ModItems.SORCERER_SPELLBOOK_COVER.get().equals(spellbookCover) && currentLevel == 3) {
                    SpellBookComponentHelper.setLevel(curioSpellbook, 4, player);
                    used = true;
                } else if (ModItems.SCHOLAR_SPELLBOOK_COVER.get().equals(spellbookCover) && currentLevel == 4) {
                    SpellBookComponentHelper.setLevel(curioSpellbook, 5, player);
                    used = true;
                } else if (ModItems.ARCHMAGE_SPELLBOOK_COVER.get().equals(spellbookCover) && currentLevel == 5) {
                    SpellBookComponentHelper.setLevel(curioSpellbook, 6, player);
                    used = true;
                }

                if (used) {
                    stackInHand.shrink(1);
                    event.setCanceled(true);
                }
            }
        });

    }

    //Helper Methods
        //fatigue
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
    public static float principleFatigueRatio(Set<String> categories, Player player){
        float ratio = 1f;
        for(String category: categories){
            //5% per category
            int principleLevel = PrinciplesProgressionManager.getCategoryLevel(player, category);
            ratio+= 0.05f-(0.005f*principleLevel);
        }
        return ratio;
    }
    private static void addExhaustion(ServerPlayer player, int amountToAdd) {

        FatigueManager.addFatigue(player, amountToAdd);

        int currentLevel = FatigueManager.getFatigueLevel(player);
        int maxEx = getMaxFatigue(currentLevel, player);
        int currentEx = FatigueManager.getFatigueAmount(player);

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
                    return 5;
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
                    return 3;
            }
        }
    }
        //casting
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
        if(categories.size() >= ServerConfig.DOMINAN_PRINCIPLES.get()){
            SpellRarity spellRarity = spell.getRarity(level);
            int minLevel = getLevelRequiredForRARITY(spellRarity);
            for (String category : categories){
                int categoryLevel =  PrinciplesProgressionManager.getCategoryLevel(serverPlayer, category);
                if (categoryLevel<minLevel){
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    public static boolean teleportCanceled(Set<String> categories, ServerPlayer caster, AbstractSpell spell, SpellPreCastEvent event, MagicData magicData){
        if(!ServerConfig.ACTIVE_APPARITIO.get() || !ServerConfig.PRINCIPLES_SYSTEM.get()) return false;

        if(categories.contains("usesTeleport")) {
            double probabilityForFail = 0;
            int categoryLevel = PrinciplesProgressionManager.getCategoryLevel(caster, "usesTeleport");
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
            case COMMON -> ServerConfig.DOMINAN_LEVELS.get().get(0);
            case UNCOMMON -> ServerConfig.DOMINAN_LEVELS.get().get(1);
            case RARE -> ServerConfig.DOMINAN_LEVELS.get().get(2);
            case EPIC -> ServerConfig.DOMINAN_LEVELS.get().get(3);
            case LEGENDARY -> ServerConfig.DOMINAN_LEVELS.get().get(4);
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



}
