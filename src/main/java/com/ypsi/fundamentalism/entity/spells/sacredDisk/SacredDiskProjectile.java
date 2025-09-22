package com.ypsi.fundamentalism.entity.spells.sacredDisk;

import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.AbstractMagicProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class SacredDiskProjectile extends AbstractMagicProjectile {
    private boolean isReturning = false;
    private int returnTicks = 0;
    private static final int MAX_RETURN_TICKS = 20*2;

    public SacredDiskProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setNoGravity(true);
    }

    public SacredDiskProjectile(Level levelIn, LivingEntity shooter){
        this(ModEntities.SACRED_DISK.get(), levelIn);
        setOwner(shooter);
    }

    @Override
    public float getSpeed() {
        return 1.2f;
    }

    @Override
    public void trailParticles() {
        Vec3 center = this.position();
        float radius = 0.5f;
        int particlesCount = 8;
        float angleIncrement = (float) (2 * Math.PI / particlesCount);

        float baseAngle = (this.tickCount * 0.2f) % 360;

        for (int i = 0; i < particlesCount; i++) {
            float angle = angleIncrement * i + baseAngle;

            double xOffset = radius * Math.cos(angle);
            double zOffset = radius * Math.sin(angle);

            double yVariation = Math.sin(angle * 2) * 0.1;

            level().addParticle(ParticleHelper.WISP,
                    center.x + xOffset,
                    center.y + yVariation,
                    center.z + zOffset,
                    0, 0.02, 0);
        }
    }

    @Override
    public void impactParticles(double x, double y, double z) {

    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if(!level().isClientSide){
            damageEntity(pResult.getEntity());
        }
    }
    private void damageEntity(Entity entity) {
            DamageSources.applyDamage(entity, damage, ModSpells.SACRED_DISK.get().getDamageSource(this, getOwner()));
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide && !isReturning) {
            this.isReturning = true;
            this.returnTicks = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(!level().isClientSide && isReturning){
            handleReturnLogic();
        }else {
            if (!level().isClientSide && tickCount % MAX_RETURN_TICKS == 0) {
                this.isReturning = true;
                this.returnTicks = 0;
            }
        }

    }

    private void handleReturnLogic() {
        returnTicks++;
        LivingEntity owner = (LivingEntity) getOwner();
        if (owner != null && owner.isAlive()) {
            Vec3 targetPos = owner.getEyePosition().add(0, -0.5, 0);
            Vec3 currentPos = this.position();

            // Calcular dirección hacia la posición ACTUAL del jugador
            Vec3 direction = targetPos.subtract(currentPos).normalize();

            // Aplicar movimiento de retorno
            double returnSpeed = 0.7; // Velocidad constante de retorno
            this.setDeltaMovement(direction.scale(returnSpeed));

            // Rotar el proyectil hacia la dirección de movimiento
            Vec3 motion = this.getDeltaMovement();
                this.setYRot((float) (Mth.atan2(motion.x, motion.z) * (180F / Math.PI)));
                this.setXRot((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * (180F / Math.PI)));

            // Verificar si llegó cerca del owner (posición ACTUAL)
            if (currentPos.distanceTo(targetPos) < 0.5) {
                // Efectos al regresar
                this.impactParticles(getX(), getY(), getZ());
                this.discard();
            }
        } else {
            // Si el owner no existe o se excedió el tiempo, descartar el proyectil
            this.discard();
        }
    }


    @Override
    public Optional<Holder<SoundEvent>> getImpactSound() {
        return Optional.of(SoundRegistry.GUIDING_BOLT_IMPACT);
    }
}
