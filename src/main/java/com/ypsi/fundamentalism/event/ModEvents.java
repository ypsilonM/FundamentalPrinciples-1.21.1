package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.ModAttributes;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.imp.ImpEntity;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.joml.Vector3f;

import java.util.*;


@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ModEvents {

    @SubscribeEvent
    public static void onManaChange(ChangeManaEvent event){
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
            }
    }

    @SubscribeEvent
    public static void onPlayerReforcedHurt(LivingDamageEvent.Pre event) {
        if(event.getEntity() instanceof ServerPlayer serverPlayer) {
            if(serverPlayer.hasEffect(ModEffects.REINFORCEMENT_EFFECT)) {
                MagicData magicData = MagicData.getPlayerMagicData(event.getEntity());

                float originalDamage = event.getNewDamage();
                float currentMana = magicData.getMana();

                double maxMana = serverPlayer.getAttributeValue(AttributeRegistry.MAX_MANA);
                double spellPower = serverPlayer.getAttributeValue(AttributeRegistry.SPELL_POWER);

                float mitigatedDamage = (float)(Math.sqrt((maxMana/100))*spellPower);
                float modifiedDamage = originalDamage;

                float manaToConsume = (float)(maxMana*0.10);

                if(currentMana>=(maxMana*.1)) {
                    if (originalDamage < mitigatedDamage) {
                        modifiedDamage = 0.0f;
                        manaToConsume/=2;
                        magicData.addMana(-manaToConsume);
                    } else {
                        modifiedDamage = originalDamage - mitigatedDamage;
                        magicData.addMana(-manaToConsume);
                    }
                    serverPlayer.level().playSound(
                            null,
                            serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(),
                            SoundEvents.SHIELD_BLOCK,
                            SoundSource.PLAYERS,
                            0.5F,
                            0.4F
                    );
                }
                event.setNewDamage(modifiedDamage);
            }
        }
    }

    @SubscribeEvent
    public static void starAlignment(LivingDamageEvent.Pre event){
        if(event.getSource().getDirectEntity() instanceof Player player && event.getSource().is(DamageTypes.PLAYER_ATTACK)){
                int n = 50;
                Random random = new Random();
                if (player.getHealth() <= player.getMaxHealth() * 0.50) {
                    n-=5;
                }
                if (player.hasEffect(ModEffects.MINDFUL_EFFECT)){
                    n-=10;
                }
                if (random.nextInt(n) == 0) {

                    LivingEntity enemy = event.getEntity();
                    float originalDamage = event.getNewDamage();
                    float modifiedDamage = (float) Math.pow(originalDamage, 1.5);

                    event.setNewDamage(modifiedDamage);
                    enemy.addEffect(new MobEffectInstance(
                            MobEffects.WEAKNESS,
                            30*20,
                            0,
                            false,
                            true,
                            true));

                    if (!enemy.level().isClientSide()) {
                        ServerLevel serverLevel = (ServerLevel) enemy.level();

                        serverLevel.sendParticles(
                                new DustParticleOptions(
                                        new Vector3f(1.0f, 0.0f, 0.0f),
                                        1.0f
                                ),
                                enemy.getX(),
                                enemy.getY() + enemy.getBbHeight() * 0.5,
                                enemy.getZ(),
                                90,
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

                    }
                    player.addEffect(new MobEffectInstance(
                            ModEffects.MINDFUL_EFFECT,
                            30*20,
                            0,
                            false,
                            true,
                            true));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_RESISTANCE,
                            5*20,
                            1,
                            false,
                            false,
                            true));

                    player.level().playSound(
                            null,
                            enemy.getX(), enemy.getY(), enemy.getZ(),
                            SoundEvents.TRIDENT_THUNDER,
                            SoundSource.PLAYERS,
                            2.0F,
                            0.4F
                    );
                }
            }

    }



    @SubscribeEvent
    public static void nerfSpell(SpellOnCastEvent event){
        Player player = event.getEntity();
            double proportion = 10;
            int maxLevel = SpellRegistry.getSpell(event.getSpellId()).getMaxLevel();

            int level = event.getSpellLevel();
            double modifiedDamage = level - Math.round((maxLevel*(proportion/100))/2);
            int newLevel = (int) modifiedDamage;
            event.setSpellLevel(newLevel);
            player.sendSystemMessage(Component.literal("Lvl Pre nerf: "+level+" lvl" ));
            player.sendSystemMessage(Component.literal("Lvl Post nerf: "+newLevel+" lvl" ));

    }
    @SubscribeEvent
    public static void moreMobResistances(FinalizeSpawnEvent event){
        var mob = event.getEntity();

        if(mob instanceof ImpEntity){
            setIfNonNull(mob, AttributeRegistry.FIRE_SPELL_POWER, 1.2);
            setIfNonNull(mob, AttributeRegistry.ICE_MAGIC_RESIST, 0.5);
        }
        if(mob instanceof EnderMan || mob instanceof Shulker || mob instanceof Endermite){
            setIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 1.8);
            setIfNonNull(mob, AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.5);
        }
        if(mob instanceof EnderDragon){
            setIfNonNull(mob, AttributeRegistry.ENDER_MAGIC_RESIST, 3.0);
            setIfNonNull(mob, AttributeRegistry.ELDRITCH_MAGIC_RESIST, 0.6);
        }
        if(mob instanceof Creeper){
            setIfNonNull(mob, AttributeRegistry.NATURE_MAGIC_RESIST, 1.5);
        }
        if(mob instanceof IronGolem){
            setIfNonNull(mob, AttributeRegistry.EVOCATION_MAGIC_RESIST, 1.5);
        }

    }

    private static void setIfNonNull(LivingEntity mob, Holder<Attribute> attribute, double value) {
        var instance = mob.getAttributes().getInstance(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }
//    @SubscribeEvent
//    public static void showDamage(LivingDamageEvent.Pre event){
//        if(event.getSource().getEntity() instanceof Player player){
//            player.sendSystemMessage(Component.literal("Daño: "+event.getNewDamage()));
//        }
//    }

}
