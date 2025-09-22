package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attachments.YpsAttachments;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.joml.Vector3f;

import java.util.*;


@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
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
    public static void starAlignment(LivingDamageEvent.Pre event){
        if(event.getSource().getDirectEntity() instanceof Player player && event.getSource().is(DamageTypes.PLAYER_ATTACK)){
                int n = 100;
                Random random1 = new Random();
                int amplifier = 0;

                //mindful levels
                if(player.hasEffect(ModEffects.MINDFUL_EFFECT)){
                    int level = player.getEffect(ModEffects.MINDFUL_EFFECT).getAmplifier()+1;
                    amplifier = Math.clamp(level,0,2);
                    n-=(10)*(level);
                }
                //low health
                if (player.getHealth() <= player.getMaxHealth() * 0.50) {
                    n-=10;
                }
                //critic attack
                boolean isCritical = player.getAttackStrengthScale(0.5F)>1.0F;
                if (isCritical){
                    n-=5;
                }

                int r1 = random1.nextInt(n);
                if (r1 == 0) {
                    LivingEntity enemy = event.getEntity();
                    float originalDamage = event.getNewDamage();
                    float modifiedDamage = (float) Math.pow(originalDamage, 1.5);
                    event.setNewDamage(modifiedDamage);
                    int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Math.clamp(currentExhaustion-40, 0, 100));

                    enemy.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 30*20, 0, false, true, true));

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

                    player.addEffect(new MobEffectInstance(ModEffects.MINDFUL_EFFECT, 30*20, amplifier, false, true, true));

                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4*20, 1, false, false, true));

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

//    @SubscribeEvent
//    public static void preSpellVerification(SpellPreCastEvent event){
//        Player player = event.getEntity();
//            double proportion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());
//
//            int level = event.getSpellLevel();
//
//            double modifiedLevel = level -
//                Math.round(((proportion/200)*level));
//
//            int newLevel = (int) modifiedLevel;
//            if(newLevel <= 0){event.setCanceled(true);}
//
//
//    }

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
//

    @SubscribeEvent
    public static void SpellNerf(SpellOnCastEvent event){
        Player player = event.getEntity();
        int currentExhaustion = player.getData(YpsAttachments.CURRENT_EXHAUSTION.get());

        int level = event.getSpellLevel();
        double modifiedLevel = level -
                Math.round((((float) currentExhaustion /200)*level));
        int newLevel = (int) modifiedLevel;

//        player.sendSystemMessage(Component.literal("Fatiga : "+player.getData(YpsAttachments.CURRENT_EXHAUSTION.get())));
        event.setSpellLevel(newLevel);
//        player.sendSystemMessage(Component.literal("Lvl Pre nerf: "+level+" lvl" ));
//        player.sendSystemMessage(Component.literal("Lvl Post nerf: "+newLevel+" lvl" ));

        boolean continuous = SpellRegistry.getSpell(event.getSpellId()).getCastType() == CastType.CONTINUOUS;
        int manaUsed = event.getManaCost();
        double elementalPower = getElementalMaxValue(player);
        double spellPower = player.getAttributeValue(AttributeRegistry.SPELL_POWER);

        //int formula = (int)Math.round(((((float) currentExhaustion /100)*2)+((float) newLevel /2)+((float) manaUsed /10)));
        int formula = (int) (continuous?
                (newLevel+manaUsed)/(10*spellPower*elementalPower):
                ((double) newLevel /2) + (manaUsed/(10*spellPower*elementalPower))
        );

        player.setData(YpsAttachments.CURRENT_EXHAUSTION,
                Mth.clamp(currentExhaustion+formula,0,100));

        SyncExhaustionPacket.sendToPlayer((ServerPlayer) player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));

    }

    public static double getElementalMaxValue(Player player){
        List<Double> list = new ArrayList<>();
        list.add(player.getAttributeValue(AttributeRegistry.BLOOD_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.ENDER_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.ELDRITCH_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.HOLY_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.NATURE_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.EVOCATION_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.LIGHTNING_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.ICE_SPELL_POWER));
        list.add(player.getAttributeValue(AttributeRegistry.FIRE_SPELL_POWER));
        return Collections.max(list);
    }
    //ExhaustionCounter
    @SubscribeEvent
    public static void exhaustionCounter(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            int tickCounter = player.getPersistentData().getInt("exhaustionTickCounter");
            tickCounter++;

            if (tickCounter >= 20) {
                int current = player.getData(YpsAttachments.CURRENT_EXHAUSTION);
                int max = (int) player.getAttributeValue(YpsAttributes.MAX_EXHAUSTION);
                int regen = (int) player.getAttributeValue(YpsAttributes.EXHAUSTION_REGEN);

                if(player.getData(YpsAttachments.CURRENT_EXHAUSTION) > 0){
                    player.setData(YpsAttachments.CURRENT_EXHAUSTION, Mth.clamp(current - regen, 0, max));
                }
                tickCounter = 0;
                SyncExhaustionPacket.sendToPlayer(player, player.getData(YpsAttachments.CURRENT_EXHAUSTION));
            }

            player.getPersistentData().putInt("exhaustionTickCounter", tickCounter);
        }
    }
}
