package com.ypsi.fundamentalism.spells.fire;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.entity.spells.target_area.TargetedAreaEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.spells.TargetAreaCastData;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PyrokinesisSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "pyrokinesis");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.RARE)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(5)
            .build();

    public PyrokinesisSpell() {
        this.manaCostPerLevel = 2;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 1;
        this.castTime = 20;
        this.baseManaCost = 10;

    }

    @Override
    public int getCastTime(int spellLevel) { return castTime + 20 * (spellLevel-1); }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.CONTINUOUS; }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        float radius = 3;
        var area = TargetedAreaEntity.createTargetAreaEntity(level, entity.position(), radius, Utils.packRGB(this.getTargetingColor()), entity);
        playerMagicData.setAdditionalCastData(new TargetAreaCastData(entity.position(), area));
        return true;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onServerCastTick(Level level, int spellLevel, LivingEntity entity, @Nullable MagicData playerMagicData) {
        super.onServerCastTick(level, spellLevel, entity, playerMagicData);

        boolean reflected = false;
        var entities = level.getEntities(entity, entity.getBoundingBox().inflate(3));
        if(entity.isOnFire()){
            entity.clearFire();
        }
        for(Entity projectile : entities){
            reflected = false;
            Vec3 lookAngle = entity.getLookAngle();
            if(projectile instanceof AbstractMagicProjectile amp){
                if(projectile.getClass().toString().toUpperCase().contains("FIRE")) {
                    amp.setOwner(entity);
                    amp.shoot(lookAngle);
                    reflected=true;
                }
            }else if(projectile instanceof AbstractArrow arrow && arrow.isOnFire()){
                arrow.setOwner(entity);
                arrow.shoot(lookAngle.x, lookAngle.y, lookAngle.z,3,0);
                reflected=true;
            }else if(projectile instanceof Fireball fireball){
                fireball.setOwner(entity);
                fireball.shoot(lookAngle.x, lookAngle.y, lookAngle.z,3,0);
                reflected=true;
            }else if(projectile instanceof FireworkRocketEntity fireworkRocketEntity){
                fireworkRocketEntity.setOwner(entity);
                fireworkRocketEntity.shoot(lookAngle.x, lookAngle.y, lookAngle.z,3,0);
                reflected=true;
            }
            if(reflected){
                MagicManager.spawnParticles(level, ParticleHelper.EMBERS, entity.getX(), entity.getY() + 1, entity.getZ(), 8, 0.01, 0.0, 0.01, 0.001, true);
                entity.level().playSound(null, entity.blockPosition(), SoundRegistry.FIRE_BOMB_CAST.get(), SoundSource.PLAYERS, 3, Utils.random.nextIntBetweenInclusive(8, 12) * .1f);
            }
        }
    }

    @Override
    public void onServerCastComplete(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData, boolean cancelled) {
        super.onServerCastComplete(level, spellLevel, entity, playerMagicData, cancelled);
    }

    @Override
    public void playSound(Optional<SoundEvent> sound, Entity entity) {

    }
}
