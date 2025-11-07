package com.ypsi.fundamentalism.entity.spells.proiectumProjectile;

import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.spells.thorn.ThornProjectile;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.util.CameraShakeData;
import io.redspace.ironsspellbooks.api.util.CameraShakeManager;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ProiectumProjectile extends AbstractMagicProjectile {
    public ProiectumProjectile(EntityType<? extends ProiectumProjectile> entityType, Level level) {
        super(entityType, level);
        this.setNoGravity(true);
    }

    public ProiectumProjectile(Level levelIn, LivingEntity shooter) {
        this(ModEntities.PROIECTUM_PROJECTILE.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    public float getSpeed() {
        return 1f;
    }

    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.GLASS_BREAK));
    }

    @Override
    protected void doImpactSound(Holder<SoundEvent> sound) {
        level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.NEUTRAL, 2, 1.2f + Utils.random.nextFloat() * .2f);
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult) {
        super.onHitBlock(blockHitResult);
        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);

        if (!(entityHitResult.getEntity() instanceof LivingEntity livingEntity)) {
            discard();
            return;
        }
        Level level = livingEntity.level();
        if (level == null || level.isClientSide()) {
            discard();
            return;
        }
//            float explosionRadius = 3f * (1 + .5f * livingEntity.getHealth() / livingEntity.getMaxHealth());
//            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, livingEntity.getX(), livingEntity.getY() + .25f, livingEntity.getZ(), 100, .03, .4, .03, .4, true);
//            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, livingEntity.getX(), livingEntity.getY() + .25f, livingEntity.getZ(), 100, .03, .4, .03, .4, false);
//            MagicManager.spawnParticles(level, new BlastwaveParticleOptions(SchoolRegistry.BLOOD.get().getTargetingColor(), explosionRadius), livingEntity.getX(), livingEntity.getBoundingBox().getCenter().y, livingEntity.getZ(), 1, 0, 0, 0, 0, true);
//            CameraShakeManager.addCameraShake(new CameraShakeData(10, livingEntity.position(), 20));
//            level.playSound(null, livingEntity.blockPosition(), SoundRegistry.BLOOD_EXPLOSION.get(), SoundSource.PLAYERS, 3, Utils.random.nextIntBetweenInclusive(8, 12) * .1f);
            //DamageSources.applyDamage(entityHitResult.getEntity(), getDamage(), ModSpells.PROIECTUM_PRINCIPLE.get().getDamageSource(this, getOwner()));
        discard();
    }

    @Override
    public void impactParticles(double x, double y, double z) {
        MagicManager.spawnParticles(level(), ParticleTypes.CRIT, x, y, z, 10, .1, .1, .1, .25, true);
    }

    @Override
    public void trailParticles() {
        float yHeading = -((float) (Mth.atan2(getDeltaMovement().z, getDeltaMovement().x) * (double) (180F / (float) Math.PI)) + 90.0F);
        float radius = .25f;
        int steps = 2;
        var vec = getDeltaMovement();
        double x2 = getX();
        double x1 = x2 - vec.x;
        double y2 = getY();
        double y1 = y2 - vec.y;
        double z2 = getZ();
        double z1 = z2 - vec.z;
        for (int j = 0; j < steps; j++) {
            float offset = (1f / steps) * j;
            double radians = ((tickCount + offset) / 7.5f) * 360 * Mth.DEG_TO_RAD;
            Vec3 swirl = new Vec3(Math.cos(radians) * radius, Math.sin(radians) * radius, 0).yRot(yHeading * Mth.DEG_TO_RAD);
            double x = Mth.lerp(offset, x1, x2) + swirl.x;
            double y = Mth.lerp(offset, y1, y2) + swirl.y + getBbHeight() / 2;
            double z = Mth.lerp(offset, z1, z2) + swirl.z;
            Vec3 jitter = Vec3.ZERO;//Utils.getRandomVec3(.05f);
            level().addParticle(ParticleTypes.CRIT, x, y, z, jitter.x, jitter.y, jitter.z);
        }
    }
}
