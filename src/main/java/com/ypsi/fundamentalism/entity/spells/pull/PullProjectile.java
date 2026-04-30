package com.ypsi.fundamentalism.entity.spells.pull;

import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class PullProjectile extends AbstractMagicProjectile implements AntiMagicSusceptible {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(PullProjectile.class, EntityDataSerializers.FLOAT);


    public PullProjectile(EntityType<? extends PullProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public PullProjectile(EntityType<? extends PullProjectile> pEntityType, Level pLevel, LivingEntity shooter) {
        this(pEntityType, pLevel);
        setOwner(shooter);
    }

    public PullProjectile(Level levelIn, LivingEntity shooter){
        this(ModEntities.PULL_PROJECTILE.get(), levelIn, shooter);
    }

    List<Entity> trackingEntities = new ArrayList<>();

    @Override
    public void onAntiMagic(MagicData playerMagicData) {

    }

    public void refreshDimensions() {
        double d0 = this.getX();
        double d1 = this.getY();
        double d2 = this.getZ();
        super.refreshDimensions();
        this.setPos(d0, d1, d2);
    }


    private int soundTick;
    private float damage;

    @Override
    public void trailParticles() {
        double radius = this.getRadius()*2;
        double centerX = this.getX();
        double centerY = this.getY() + this.getBbHeight() / 2;
        double centerZ = this.getZ();

        for (int i = 0; i < 30; i++) {
            // Generar ángulos aleatorios para una distribución uniforme en la esfera
            double theta = 2 * Math.PI * Utils.random.nextDouble(); // azimut
            double phi = Math.acos(2 * Utils.random.nextDouble() - 1); // inclinación (para densidad uniforme)

            // Calcular coordenadas en la superficie
            double dx = radius * Math.sin(phi) * Math.cos(theta);
            double dy = radius * Math.sin(phi) * Math.sin(theta);
            double dz = radius * Math.cos(phi);

            // Agregar partícula en la superficie
            level().addParticle(ParticleTypes.PORTAL,
                    centerX + dx, centerY + dy, centerZ + dz,
                    0, 0, 0); // velocidad cero (o la que quieras)

            // Opcional: partículas de estela (si aún quieres arrastre)
            if (tickCount > 1) {

                level().addParticle(ParticleTypes.PORTAL,
                        centerX + dx - getDeltaMovement().x / 2,
                        centerY + dy - getDeltaMovement().y / 2,
                        centerZ + dz - getDeltaMovement().z / 2,
                        0, 0, 0);
            }
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    public float getSpeed() {
        return 0.30f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.empty();
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
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

    @Override
    public void tick() {
        super.tick();
        int update = Math.max((int) (getRadius() / 2), 2);
        if (tickCount % update == 0) {
            updateTrackingEntities();
        }
        var bb = this.getBoundingBox();
        float radius = (float) (bb.getXsize());
        boolean hitTick = this.tickCount % 5 == 0;
        for (Entity entity : trackingEntities) {
            if (entity != getOwner() && !DamageSources.isFriendlyFireBetween(getOwner(), entity) && !entity.isSpectator()) {
                Vec3 center = bb.getCenter();
                float distance = (float) center.distanceTo(entity.position());
                if (distance > radius) {
                    continue;
                }
                float f = 1 - distance / radius;
                float scale = f  * .5f;
                float resistance = entity instanceof LivingEntity livingEntity ? Mth.clamp(1 - (float) livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE), .3f, 1f) : 1f;
                float bossResistance = entity.getType().is(Tags.EntityTypes.BOSSES) ? 0.5f : 1f;


                Vec3 diff = center.subtract(entity.position()).scale(scale * resistance * bossResistance);

                entity.push(diff.x, diff.y, diff.z);
                if (hitTick && distance < 9 && canHitEntity(entity)) {
                    DamageSources.applyDamage(entity, damage, ModSpells.PULL.get().getDamageSource(this, getOwner()));
                }
                entity.fallDistance = 0;
            }
        }
        if (!level().isClientSide) {
            if (tickCount > 20 * 5) {
                this.discard();
                MagicManager.spawnParticles(level(), ParticleHelper.UNSTABLE_ENDER, getX(), getY() + getRadius(), getZ(), 200, 1, 1, 1, 1, true);
            }
        }
    }

    private void updateTrackingEntities() {
        trackingEntities = level().getEntities(this, this.getBoundingBox().inflate(1));
    };

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    public int getAge() {
        return tickCount;
    }
}
