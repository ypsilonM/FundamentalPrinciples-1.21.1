package com.ypsi.fundamentalism.spells.ice;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsEntity;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AutoSpellConfig
public class ChainsSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "chains");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getCastTime(spellLevel), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(45)
            .build();

    public ChainsSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 300;
        this.baseManaCost = 20;
    }

    @Override
    public int getCastTime(int spellLevel) { return castTime + 100 * (spellLevel-1); }

    @Override
    public CastType getCastType() {
        return CastType.CONTINUOUS;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.EVOKER_PREPARE_ATTACK);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.empty();
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 32, .35f);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData castData = playerMagicData.getAdditionalCastData();
        if (castData instanceof TargetEntityCastData castTargetingData) {
            LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
            if (target != null && !target.getType().is(ModTags.CANT_ROOT)) {
                Vec3 spawn = target.position();
                ChainsEntity chainEntity = new ChainsEntity(level, entity);

                chainEntity.setDuration(2);
                chainEntity.setTarget(target);
                chainEntity.moveTo(spawn);
                level.addFreshEntity(chainEntity);
                target.stopRiding();
                target.startRiding(chainEntity, true);

                playerMagicData.setAdditionalCastData(new SimpleChainsData(chainEntity, target, level));
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        super.onServerCastTick(level, spellLevel, entity, playerMagicData);

        if (playerMagicData != null) {
            ICastData castData = playerMagicData.getAdditionalCastData();
            if (castData instanceof SimpleChainsData chainsData) {
                ChainsEntity chainEntity = chainsData.chainEntity;
                LivingEntity target = chainsData.getTarget(level);

                if (chainEntity != null && chainEntity.isAlive() && target != null && target.isAlive()) {
                    if (chainEntity.level() != target.level()) {
                        IronsSpellbooks.LOGGER.debug("ChainsSpell: Target en diferente dimensión, cancelando");
                        playerMagicData.resetCastingState();
                        return;
                    }

                    double distance = target.distanceTo(chainEntity);
                    boolean needsTeleport = distance > 8.0;

                    if (needsTeleport) {
                        chainEntity.teleportWithTarget();
                        IronsSpellbooks.LOGGER.debug("ChainsSpell: Teletransporte detectado, distancia: {}", distance);
                    } else {
                        chainEntity.setPos(target.getX(), target.getY(), target.getZ());
                    }

                    chainEntity.setDuration(chainEntity.getDuration() + 1);

                    if (target.getVehicle() != chainEntity) {
                        target.stopRiding();
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.getServer().execute(() -> {
                                if (target.isAlive() && chainEntity.isAlive() && target.getVehicle() != chainEntity) {
                                    target.startRiding(chainEntity, true);
                                }
                            });
                        }
                    }
                    chainEntity.setTarget(target);
//                    IronsSpellbooks.LOGGER.debug("ChainsSpell: Tick - Montado: {}, Distancia: {}",
//                            target.getVehicle() == chainEntity, distance);
                } else {
//                    IronsSpellbooks.LOGGER.debug("ChainsSpell: Condiciones fallaron - chainEntity: {}, target: {}",
//                            chainEntity != null && chainEntity.isAlive(), target != null && target.isAlive());
                    playerMagicData.resetCastingState();
                }
            }
        }
    }

    @Override
    public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
        super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);

        if (playerMagicData != null) {
            ICastData castData = playerMagicData.getAdditionalCastData();
            if (castData instanceof SimpleChainsData chainsData) {
                ChainsEntity chainEntity = chainsData.chainEntity;
                LivingEntity target = chainsData.getTarget(level);

                if (chainEntity != null && chainEntity.isAlive()) {
                    if (!cancelled) {
                        chainEntity.setDuration(10);
                        if (target != null && target.isAlive()) {
                            if (!target.isPassenger() || target.getVehicle() != chainEntity) {
                                target.stopRiding();
                                target.startRiding(chainEntity, true);
                            }
                            chainEntity.setTarget(target);
                        }
                        IronsSpellbooks.LOGGER.debug("ChainsSpell: Casteo completado, manteniendo chains");
                    } else {
                        if (target != null && target.isAlive() && target.isPassenger() && target.getVehicle() == chainEntity) {
                            target.stopRiding();
                        }
                        chainEntity.discard();
                        IronsSpellbooks.LOGGER.debug("ChainsSpell: Casteo cancelado, liberando chains");
                    }
                }
            }
            playerMagicData.resetAdditionalCastData();
        }
    }

    public int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 40);
    }

    public static class SimpleChainsData implements ICastData {
        public final ChainsEntity chainEntity;
        public final UUID targetUUID;
        public LivingEntity cachedTarget;

        public SimpleChainsData(ChainsEntity chainEntity, LivingEntity target, Level level) {
            this.chainEntity = chainEntity;
            this.targetUUID = target.getUUID();
            this.cachedTarget = target;
        }

        public LivingEntity getTarget(Level currentLevel) {
            if (cachedTarget != null && cachedTarget.isAlive() && cachedTarget.level() == currentLevel) {
                return cachedTarget;
            }
            if (currentLevel instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(targetUUID);
                if (entity instanceof LivingEntity livingEntity) {
                    cachedTarget = livingEntity;
                    return livingEntity;
                }
            }

            return null;
        }

        @Override
        public void reset() {
            cachedTarget = null;
        }
    }
}
