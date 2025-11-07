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
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.util.Log;
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
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class ChainsSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "chains");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length", Utils.timeFromTicks(getDuration(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.UNCOMMON)
            .setSchoolResource(SchoolRegistry.ICE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(54)
            .build();

    public ChainsSpell() {
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 1;
        this.castTime = 60;
        this.baseManaCost = 65;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
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

    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        ICastData var7 = playerMagicData.getAdditionalCastData();
        if (var7 instanceof TargetEntityCastData castTargetingData) {
            LivingEntity target = castTargetingData.getTarget((ServerLevel)level);
            IronsSpellbooks.LOGGER.debug("RootSpell.onCast.1 targetEntity:{}", target);
            if (target != null && !target.getType().is(ModTags.CANT_ROOT)) {
                IronsSpellbooks.LOGGER.debug("RootSpell.onCast.2 targetEntity:{}", target);
                Vec3 spawn = target.position();
                ChainsEntity chainEntity = new ChainsEntity(level, entity);
                chainEntity.setDuration(this.getDuration(spellLevel, entity));
                chainEntity.setTarget(target);
                chainEntity.moveTo(spawn);
                level.addFreshEntity(chainEntity);
                target.stopRiding();
                target.startRiding(chainEntity, true);
            }
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
    @Nullable
    private LivingEntity findTarget(LivingEntity caster) {
        HitResult target = Utils.raycastForEntity(caster.level(), caster, 32.0F, true, 0.35F);
        if (target instanceof EntityHitResult entityHit) {
            Entity var5 = entityHit.getEntity();
            if (var5 instanceof LivingEntity livingTarget) {
                return livingTarget;
            }
        }
        return null;
    }

    public int getDuration(int spellLevel, LivingEntity caster) {
        return (int) (getSpellPower(spellLevel, caster) * 40);
    }

}
