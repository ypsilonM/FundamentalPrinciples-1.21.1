package com.ypsi.fundamentalism.spells.eldritch;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.RaycastBuilder;
import io.redspace.ironsspellbooks.capabilities.magic.*;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StealSummonSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "summon_steal");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.ELDRITCH_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(180)
            .build();

    public StealSummonSpell() {
        this.manaCostPerLevel = 50;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 60;
        this.baseManaCost = 100;
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
        return Optional.of(SoundEvents.EVOKER_PREPARE_SUMMON);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.EVOKER_CAST_SPELL);
    }


    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        float aimAssist = .25f;
        float range = 10f;
        Vec3 start = entity.getEyePosition();
        Vec3 end = entity.getLookAngle().normalize().scale(range).add(start);
        var target = RaycastBuilder.begin(entity.level(), entity)
                .start(start)
                .end(end)
                .checkForBlocks(true)
                .bbInflation(aimAssist)
                .filter(e -> e instanceof IMagicSummon)
                .build();
        if (target instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity livingTarget) {
            playerMagicData.setAdditionalCastData(new TargetEntityCastData(livingTarget));
            if (entity instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new SyncTargetingDataPacket(livingTarget, this));
                serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.irons_spellbooks.spell_target_success", livingTarget.getDisplayName().getString(), this.getDisplayName(serverPlayer)).withStyle(ChatFormatting.GREEN)));
            }
            return true;
        } else if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(Component.translatable("ui.ypfundamentals.summon_target_failure").withStyle(ChatFormatting.RED)));
        }
        return false;
        //return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 10, .35f);
    }

    @Override
    public int getRecastCount(int spellLevel, @Nullable LivingEntity entity) {
        return spellLevel;
    }

    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if(!playerMagicData.getPlayerRecasts().hasRecastForSpell(getSpellId())){
            playerMagicData.getPlayerRecasts().addRecast(
                    new RecastInstance(getSpellId(), spellLevel, getRecastCount(spellLevel, entity), 20*10, castSource, null), playerMagicData);
        }
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData targetingData) {
            var targetEntity = targetingData.getTarget((ServerLevel) world);
            if (targetEntity != null) {
                if(targetEntity instanceof IMagicSummon summon) {
                    if (!SummonManager.getSummons(entity).contains(targetEntity.getUUID())) {
                        SummonManager.setOwner(targetEntity, entity);
                    }
                    if (targetEntity instanceof Mob mob) {
                        mob.setTarget(targetEntity);
                    }

                }
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);

    }
}
