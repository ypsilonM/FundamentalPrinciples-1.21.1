package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.ModAttributes;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.attachments.ModAttachments;
import com.ypsi.fundamentalism.keybind.ModKeyBinds;
import com.ypsi.fundamentalism.network.packets.SyncExhaustionPacket;
import io.redspace.ironsspellbooks.api.events.*;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import java.util.*;
import java.util.logging.Level;


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
                    int currentExhaustion = player.getData(ModAttachments.CURRENT_EXHAUSTION.get());
                    player.setData(ModAttachments.CURRENT_EXHAUSTION, Math.clamp(currentExhaustion-40, 0, 100));

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
//    public static void attackFire(LivingDamageEvent.Pre event) {
//        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker && attacker.hasEffect(ModEffects.FLAME_GRANT_STRENGTH)){
//            LivingEntity target = event.getEntity();
//            int amplifier = attacker.getEffect(ModEffects.FLAME_GRANT_STRENGTH).getAmplifier();
//            target.igniteForSeconds(5 + amplifier);
//        }
//    }

    @SubscribeEvent
    public static void preSpellVerification(SpellPreCastEvent event){
        Player player = event.getEntity();
            double proportion = player.getData(ModAttachments.CURRENT_EXHAUSTION.get());

            int level = event.getSpellLevel();

            double modifiedLevel = level -
                Math.round(((proportion/200)*level));

            int newLevel = (int) modifiedLevel;
            if(newLevel <= 0){event.setCanceled(true);}


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

    @SubscribeEvent
    public static void SpellNerf(SpellOnCastEvent event){
        Player player = event.getEntity();
        double proportion = player.getData(ModAttachments.CURRENT_EXHAUSTION.get());

        int level = event.getSpellLevel();
        double modifiedLevel = level -
                Math.round(((proportion/200)*level));
        int newLevel = (int) modifiedLevel;

//        player.sendSystemMessage(Component.literal("Fatiga : "+player.getData(ModAttachments.CURRENT_EXHAUSTION.get())));
        event.setSpellLevel(newLevel);

//        player.sendSystemMessage(Component.literal("Lvl Pre nerf: "+level+" lvl" ));
//        player.sendSystemMessage(Component.literal("Lvl Post nerf: "+newLevel+" lvl" ));

        int currentExhaustion = player.getData(ModAttachments.CURRENT_EXHAUSTION.get());

        boolean continuous = SpellRegistry.getSpell(event.getSpellId()).getCastType() == CastType.CONTINUOUS;
        int manaUsed = event.getManaCost();

        //int formula = (int)Math.round(((((float) currentExhaustion /100)*2)+((float) newLevel /2)+((float) manaUsed /10)));
        int formula = continuous?(newLevel+manaUsed)/10:(newLevel) + (manaUsed/10);

        player.setData(ModAttachments.CURRENT_EXHAUSTION,
                Mth.clamp(currentExhaustion+formula,0,100));

        SyncExhaustionPacket.sendToPlayer((ServerPlayer) player, player.getData(ModAttachments.CURRENT_EXHAUSTION));

    }

    @SubscribeEvent
    public static void exhaustionCounter(ServerTickEvent.Post event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            int tickCounter = player.getPersistentData().getInt("exhaustionTickCounter");
            tickCounter++;

            if (tickCounter >= 20) {
                int current = player.getData(ModAttachments.CURRENT_EXHAUSTION);
                int max = (int) player.getAttributeValue(ModAttributes.MAX_EXHAUSTION);
                int regen = (int) player.getAttributeValue(ModAttributes.EXHAUSTION_REGEN);

                if(player.getData(ModAttachments.CURRENT_EXHAUSTION) > 0){
                    player.setData(ModAttachments.CURRENT_EXHAUSTION, Mth.clamp(current - regen, 0, max));
                }
                tickCounter = 0;
                SyncExhaustionPacket.sendToPlayer(player, player.getData(ModAttachments.CURRENT_EXHAUSTION));
            }

            player.getPersistentData().putInt("exhaustionTickCounter", tickCounter);
        }
    }



//    @SubscribeEvent
//    public static void onSelection(ClientTickEvent.Post event) {
//        Minecraft mc = Minecraft.getInstance();
//        if (mc.player == null || mc.level == null) return;
//
//        if (ModKeyBinds.SELECTION_KEY.get().consumeClick()) {
//
//        }
//    }

    public static void elementalReaction(SpellDamageEvent spellDamageEvent){
        spellDamageEvent.getSpellDamageSource();

    }

//    @SubscribeEvent
//    public static void showDamage(LivingDamageEvent.Pre event){
//        if(event.getSource().getEntity() instanceof Player player){
//            player.sendSystemMessage(Component.literal("Daño: "+event.getNewDamage()));
//        }
//    }

}
