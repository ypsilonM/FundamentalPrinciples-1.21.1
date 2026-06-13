package com.ypsi.fundamentalism.item.custom;

import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.network.packets.SyncReinforcementPacket;
import dev.kosmx.playerAnim.core.util.Vec3f;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Random;
import org.joml.Vector3f;

//@EventBusSubscriber
public class NullifierBlade extends SwordItem {

    public NullifierBlade(Tier tier, Properties properties) {
        super(tier, properties);
    }

    @Override
    public int getEnchantmentValue() {
        return 18;
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(entity.tickCount % 10 == 0
            && entity instanceof Player player){
            double maxMana = player.getAttributeValue(AttributeRegistry.MAX_MANA);
            MagicData magicData = MagicData.getPlayerMagicData(player);
            magicData.setMana((float) (magicData.getMana()-(maxMana*0.10)));
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {

            if (entity instanceof AntiMagicSusceptible antiMagicSusceptible) {
                if(antiMagicSusceptible instanceof IMagicSummon summon) {
                    if(player.getRandom().nextDouble() < (0.25)) {
                        summon.onAntiMagic(MagicData.getPlayerMagicData(player));
                        stack.setDamageValue(stack.getDamageValue() + 10);
                        //stack.getItem().setDamage(stack, 10);
                    }
                }else {
                    antiMagicSusceptible.onAntiMagic(MagicData.getPlayerMagicData(player));
                    stack.setDamageValue(stack.getDamageValue() + 2);
                    //stack.getItem().setDamage(stack, 2);
                }
            }else if(entity instanceof IMagicEntity abstractSpellCastingMob){
                if(abstractSpellCastingMob.isCasting()) {
                    abstractSpellCastingMob.cancelCast();
                    stack.setDamageValue(stack.getDamageValue() + 5);
                }
                //stack.getItem().setDamage(stack, 5);
            }else if(entity instanceof Player target){
                if(target.hasEffect(ModEffects.REINFORCEMENT_EFFECT)){
                    target.removeEffect(ModEffects.REINFORCEMENT_EFFECT);
                    if (player.level() instanceof ServerLevel) {
                        PacketDistributor.sendToAllPlayers(new SyncReinforcementPacket(player.getId(), false));
                    }
                }else{
                    MagicData magicData = MagicData.getPlayerMagicData(target);
                    magicData.setMana(magicData.getMana() - 150);
                }
            }
            return super.onLeftClickEntity(stack, player, entity);

    }


    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {

        float radius = 4f;
        float distance = 2f;
        Vec3 forward = entity.getForward();
        Vec3 hitLocation = entity.position().add(0, entity.getBbHeight() * .6f, 0).add(forward.scale(distance));
        var entities = entity.level().getEntities(entity, AABB.ofSize(hitLocation, radius * 2, radius, radius * 2));

        Vec3 right = new Vec3(-forward.z, 0, forward.x).normalize();

        int arcPoints = 60;
        float arcRadius = 1.8f;

        RandomSource random = entity.getRandom();
        float startHeight = 0.2f + random.nextFloat() * 0.5f;  // Entre 0.2 y 0.7
        float endHeight = 0.2f + random.nextFloat() * 0.5f;   // Entre 0.2 y 0.7

        for (int i = 0; i <= arcPoints; i++) {
            double angle = Math.PI * i / arcPoints;

            double lateralOffset = Math.cos(angle) * arcRadius;
            double forwardCurve = Math.sin(angle) * arcRadius;

            double progress = (double)i / arcPoints;

            // Interpolación lineal entre startHeight y endHeight
            float heightVariation = startHeight + (float)(progress * (endHeight - startHeight));

            for (int layer = 0; layer < 3; layer++) {
                double layerOffset = layer * 0.15;

                double px = hitLocation.x + (right.x * lateralOffset);
                double pz = hitLocation.z + (right.z * lateralOffset);

                px += forward.x * forwardCurve * 0.8;
                pz += forward.z * forwardCurve * 0.8;

                double py = hitLocation.y + heightVariation + layerOffset;

                double vx = forward.x * Math.sin(angle) * 0.5;
                double vz = forward.z * Math.sin(angle) * 0.5;
                double vy = 0.1;

                if (entity.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(
                            new DustParticleOptions(new Vector3f(0, 0, 0), 0.6f),
                            px, py, pz,
                            1, vx, vy, vz,
                            0.1
                    );
                }
            }
        }


            entity.level().playSound(
                    null,
                    new BlockPos((int) hitLocation.x, (int) hitLocation.y, (int) hitLocation.z),
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.PLAYERS,
                    1f,
                    1.6f
            );
            for (Entity targetEntity : entities) {
                if (
                        targetEntity instanceof Entity hitedEntity
                                && targetEntity.isAlive()
                                && entity.isPickable()
                                && targetEntity.position().subtract(entity.getEyePosition()).dot(forward) >= 0
                                && entity.distanceToSqr(targetEntity) < radius * radius
                ) {
                    if (hitedEntity instanceof AntiMagicSusceptible antiMagicSusceptible && !(hitedEntity instanceof IMagicSummon)) {
                        if(hitedEntity.level() instanceof ServerLevel) {
                            antiMagicSusceptible.onAntiMagic(MagicData.getPlayerMagicData(entity));
                            stack.setDamageValue(stack.getDamageValue() + 5);
                            //stack.getItem().setDamage(stack, 5);
                        }
                    }
                }
            }


        //entity.level().playSound(entity, entity.getOnPos(), SoundRegistry.RAISE_DEAD_FINISH.get(), SoundSource.PLAYERS, 2, 1);
        return super.onEntitySwing(stack, entity, hand);
    }



}
