package com.ypsi.fundamentalism.entity.spells.sol;

import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.config.ServerConfigs;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.network.particles.FieryExplosionParticlesPacket;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SolProjectile extends AbstractMagicProjectile {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(SolProjectile.class, EntityDataSerializers.FLOAT);
    public SolProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public SolProjectile(Level pLevel, LivingEntity pShooter) {
        this(ModEntities.SOL_PROJECTILE.get(), pLevel);
        this.setOwner(pShooter);
    }

    public void trailParticles() {
        Vec3 center = this.getBoundingBox().getCenter();

        AABB aabb = this.getBoundingBox();
        double width = aabb.getXsize();
        double height = aabb.getYsize();
        double depth = aabb.getZsize();

        double speed = this.getDeltaMovement().length();
        int count = Mth.clamp((int)(speed * width * 2.0), 1, 32);

        for(int i = 0; i < count; ++i) {
            double offsetX = (this.random.nextDouble() - 0.5) * width * 0.5;
            double offsetY = (this.random.nextDouble() - 0.5) * height * 0.5;
            double offsetZ = (this.random.nextDouble() - 0.5) * depth * 0.5;

            double motionScale = 0.1 + (width * 0.05);
            double motionX = (this.random.nextDouble() - 0.5) * motionScale;
            double motionY = (this.random.nextDouble() - 0.5) * motionScale;
            double motionZ = (this.random.nextDouble() - 0.5) * motionScale;

            this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    motionX, motionY, motionZ);

            this.level().addParticle(ParticleHelper.EMBERS,
                    center.x + offsetX,
                    center.y + offsetY,
                    center.z + offsetZ,
                    motionX * 0.8, motionY * 0.8, motionZ * 0.8);
        }
    }
    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }

    public void impactParticles(double x, double y, double z) {
    }

    public float getSpeed() {
        return 0.7F;
    }

    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundEvents.GENERIC_EXPLODE);
    }

    protected void onHit(HitResult hitResult) {
        if (!this.level().isClientSide) {
            this.impactParticles(this.xOld, this.yOld, this.zOld);
            float explosionRadius = this.getExplosionRadius();
            float explosionRadiusSqr = explosionRadius * explosionRadius;
            List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().inflate((double)explosionRadius));
            Vec3 losPoint = Utils.raycastForBlock(this.level(), this.position(), this.position().add(0.0, 2.0, 0.0), ClipContext.Fluid.NONE).getLocation();
            Iterator var6 = entities.iterator();

            while(var6.hasNext()) {
                Entity entity = (Entity)var6.next();
                double distanceSqr = entity.distanceToSqr(hitResult.getLocation());
                if (distanceSqr < (double)explosionRadiusSqr && this.canHitEntity(entity) && Utils.hasLineOfSight(this.level(), losPoint, entity.getBoundingBox().getCenter(), true)) {
                    double p = 1.0 - distanceSqr / (double)explosionRadiusSqr;
                    float damage = (float)((double)this.damage * p);
                    DamageSources.applyDamage(entity, damage, ((AbstractSpell) ModSpells.SOL_SPELL.get()).getDamageSource(this, this.getOwner()));
                }
            }

            if (ServerConfigs.SPELL_GREIFING.get()) {
                Explosion explosion = new Explosion(this.level(), null, (ModSpells.SOL_SPELL.get()).getDamageSource(this, this.getOwner()), null, this.getX(), this.getY(), this.getZ(), this.getExplosionRadius()*3, true, Explosion.BlockInteraction.DESTROY, ParticleTypes.EXPLOSION, ParticleTypes.EXPLOSION_EMITTER, SoundEvents.GENERIC_EXPLODE);
                if (!( NeoForge.EVENT_BUS.post(new ExplosionEvent.Start(this.level(), explosion))).isCanceled()) {
                    explosion.explode();
                    explosion.finalizeExplosion(false);
                }
            }

            PacketDistributor.sendToPlayersTrackingEntity(this, new FieryExplosionParticlesPacket(new Vec3(this.getX(), this.getY() + 0.15000000596046448, this.getZ()), this.getExplosionRadius()));
            this.playSound(SoundEvents.GENERIC_EXPLODE.value(), 4.0F, (1.0F + (this.level().random.nextFloat() - this.level().random.nextFloat()) * 0.2F) * 0.7F);
            this.discard();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        return EntityDimensions.scalable(this.getRadius() * 2.0F, this.getRadius() * 2.0F);
    }

    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_RADIUS, 5F);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_RADIUS.equals(pKey)) {
            this.refreshDimensions();
            if (getRadius() < .1f)
                this.discard();
        }

        super.onSyncedDataUpdated(pKey);
    }

    public void setRadius(float pRadius) {
        if (!this.level().isClientSide) {
            this.getEntityData().set(DATA_RADIUS, Math.min(pRadius, 48));
        }
    }

    public float getRadius() {
        return this.getEntityData().get(DATA_RADIUS);
    }

    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Radius", this.getRadius());
        pCompound.putInt("Age", this.tickCount);
        pCompound.putFloat("Damage", this.getDamage());

        super.addAdditionalSaveData(pCompound);
    }

    protected void readAdditionalSaveData(CompoundTag pCompound) {
        this.tickCount = pCompound.getInt("Age");
        this.damage = pCompound.getFloat("Damage");
        if (damage == 0)
            damage = 1;
        if (pCompound.getInt("Radius") > 0)
            this.setRadius(pCompound.getFloat("Radius"));

        super.readAdditionalSaveData(pCompound);

    }

    public int getAge() {
        return tickCount;
    }

    @Override
    public void tick() {
        if(tickCount%20==0 || tickCount==1) {
            CameraShakeManager.addCameraShake(new CameraShakeData(this.level(),20, this.position(), 120));
        }
        super.tick();
    }
}
