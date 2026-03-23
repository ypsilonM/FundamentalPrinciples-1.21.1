package com.ypsi.fundamentalism.entity.spells.chains;

import com.ypsi.fundamentalism.entity.ModEntities;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.entity.spells.root.PreventDismount;
import io.redspace.ironsspellbooks.entity.spells.root.RootEntity;
import io.redspace.ironsspellbooks.registries.EntityRegistry;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.UUID;

public class ChainsEntity extends LivingEntity implements GeoEntity, PreventDismount, AntiMagicSusceptible {

    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;
    private int duration;
    private boolean playSound;
    private LivingEntity target;

    private Vec3 lastTargetPos;
    private int noPassengerTicks = 0;

    private boolean played;
    private final RawAnimation ANIMATION;
    private final AnimationController controller;
    private final AnimatableInstanceCache cache;


    public float getScale() {
        return this.target == null ? 1.0F : this.target.getScale();
    }

    public ChainsEntity(EntityType<? extends ChainsEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.playSound = true;
        this.played = false;
        this.ANIMATION = RawAnimation.begin().thenPlay("emerge");
        this.controller = new AnimationController(this, "chain_controller", 0, this::animationPredicate);
        this.cache = GeckoLibUtil.createInstanceCache(this);
    }

    public ChainsEntity(Level level, LivingEntity owner) {
        this(ModEntities.CHAINS.get(), level);
        this.setOwner(owner);
    }

    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(LivingEntity target) {
        this.target = target;
    }

    public boolean canCollideWith(@NotNull Entity pEntity) {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    protected void doPush(@NotNull Entity pEntity) {
    }

    public void push(@NotNull Entity pEntity) {
    }

    protected void pushEntities() {
    }

    public boolean dismountsUnderwater() {
        return false;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    public @NotNull Vec3 getPassengerRidingPosition(Entity pEntity) {
        return Vec3.ZERO;
    }

    public boolean shouldRiderFaceForward(@NotNull Player player) {
        return false;
    }

    protected @NotNull EntityDimensions getDefaultDimensions(Pose pPose) {
        Entity rooted = this.getFirstPassenger();
        return rooted != null ? EntityDimensions.fixed(rooted.getBbWidth() * 1.25F, 0.75F) : super.getDefaultDimensions(pPose);
    }

    public void tick() {
        super.tick();
        if (this.playSound) {
            this.refreshDimensions();
            this.playSound((SoundEvent) SoundRegistry.ICE_SPIKE_EMERGE.get(), 2.0F, 1.0F);
            this.playSound = false;
        }

        if (!this.level().isClientSide) {
            boolean shouldRemove = false;

            if (this.tickCount > this.duration) {
                shouldRemove = true;
            } else if (this.target != null && this.target.isDeadOrDying()) {
                shouldRemove = true;
            } else if (!this.isVehicle()) {
                noPassengerTicks++;
                if (noPassengerTicks > 20) {
                    shouldRemove = true;
                }
            } else {
                noPassengerTicks = 0;
            }

            if (shouldRemove) {
                this.removeRoot();
            }
        } else if (this.tickCount < 20) {
            this.clientDiggingParticles(this);
        }
    }


    public void teleportWithTarget() {
        if (this.target != null && this.target.isAlive()) {
            this.setPos(this.target.getX(), this.target.getY(), this.target.getZ());
            this.setDeltaMovement(0, 0, 0);

            if (this.target.getVehicle() != this && this.target.isAlive()) {
                this.target.stopRiding();
                this.target.startRiding(this, true);
            }
        }
    }


    protected void clientDiggingParticles(LivingEntity livingEntity) {
        RandomSource randomsource = livingEntity.getRandom();
            for(int i = 0; i < 8; ++i) {
                double d0 = livingEntity.getX() + (double) Mth.randomBetween(randomsource, -0.5F, 0.5F);
                double d1 = livingEntity.getY() + (double) Mth.randomBetween(randomsource, 0F, livingEntity.getBbHeight());
                double d2 = livingEntity.getZ() + (double)Mth.randomBetween(randomsource, -0.5F, 0.5F);
                livingEntity.level().addParticle(ParticleTypes.SNOWFLAKE, d0, d1, d2, 0.0, 0.0, 0.0);
            }
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getDuration() {
        return this.duration;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    public void removeRoot() {
        if (this.level().isClientSide) {
            for(int i = 0; i < 5; ++i) {
                this.level().addParticle(ParticleHelper.ROOT_FOG, this.getX() + Utils.getRandomScaled(0.10000000149011612), this.getY() + Utils.getRandomScaled(0.10000000149011612), this.getZ() + Utils.getRandomScaled(0.10000000149011612), Utils.getRandomScaled(2.0), (double)(-this.random.nextFloat() * 0.5F), Utils.getRandomScaled(2.0));
            }
        }

        this.ejectPassengers();
        this.discard();
    }

    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Age", this.tickCount);
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }

        pCompound.putInt("Duration", this.duration);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.tickCount = pCompound.getInt("Age");
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }

        this.duration = pCompound.getInt("Duration");
    }

    public boolean hasIndirectPassenger(Entity pEntity) {
        return true;
    }

    public void onAntiMagic(MagicData playerMagicData) {
        this.removeRoot();
    }

    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    public boolean isPushable() {
        return false;
    }

    public boolean isPickable() {
        return false;
    }

    public boolean isDamageSourceBlocked(DamageSource pDamageSource) {
        return true;
    }

    public boolean showVehicleHealth() {
        return false;
    }

    public void knockback(double pStrength, double pX, double pZ) {
    }

    public void positionRider(Entity passenger, Entity.MoveFunction p_19958_) {
        passenger.setPos(this.getX(), this.getY(), this.getZ());
    }

    protected boolean isImmobile() {
        return true;
    }

    public boolean isAffectedByPotions() {
        return false;
    }

    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.removeRoot();
            return true;
        } else {
            return false;
        }
    }

    public Iterable<ItemStack> getArmorSlots() {
        return Collections.singleton(ItemStack.EMPTY);
    }

    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        return ItemStack.EMPTY;
    }

    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
    }

    private PlayState animationPredicate(AnimationState event) {
        AnimationController controller = event.getController();
        if (!this.played && controller.getAnimationState() == AnimationController.State.STOPPED) {
            controller.forceAnimationReset();
            controller.setAnimation(this.ANIMATION);
            this.played = true;
        }

        return PlayState.CONTINUE;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(this.controller);
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
