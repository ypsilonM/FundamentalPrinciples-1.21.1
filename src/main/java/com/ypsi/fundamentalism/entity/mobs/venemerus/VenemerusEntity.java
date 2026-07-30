package com.ypsi.fundamentalism.entity.mobs.venemerus;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.UniqueAbstractMeleeCastingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.List;

public class VenemerusEntity extends UniqueAbstractMeleeCastingMob implements Enemy {

    public VenemerusEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
        xpReward = 12;
    }

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID;
    private double originalMovementSpeed = -1;
    private boolean wasRooted = false;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WarlockAttackGoal(this, 1, 100, 140)
                .setSpells(
                        List.of(SpellRegistry.ACID_ORB_SPELL.get()),
                        List.of(),
                        List.of(),
                        List.of()
                )
                        .setSpellQuality(.1f, .5f)
                        .setMeleeBias(1f,1f)
                .setSingleUseSpell(SpellRegistry.SPIDER_ASPECT_SPELL.get(), 80, 15*20, 8,8)
                .setSingleUseSpell(SpellRegistry.POISON_BREATH_SPELL.get(), 40, 120, 2, 3)
                .setMeleeAttackInverval(15, 20)
        );
        this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.8F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() &&
                        !(this.mob.getLastHurtByMob() != null &&
                        this.mob.getLastHurtByMob().getType() == this.mob.getType());
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Slime.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Axolotl.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3)
                .add(Attributes.ATTACK_SPEED, 2)
                .add(Attributes.FOLLOW_RANGE, 25.0);
    }


    @Override
    public void tick() {
        super.tick();

        boolean shouldBeRooted = false;
        if (isCasting()) {
            AbstractSpell castingSpell = getMagicData().getCastingSpell() != null
                    ? getMagicData().getCastingSpell().getSpell()
                    : null;
            if (castingSpell == SpellRegistry.ACID_ORB_SPELL.get()) {
                shouldBeRooted = true;
            }
        }

        if (shouldBeRooted && !wasRooted) {
            originalMovementSpeed = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            this.setNoGravity(true);
            wasRooted = true;
        }
        else if (!shouldBeRooted && wasRooted) {
            if (originalMovementSpeed != -1) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(originalMovementSpeed);
            }
            this.setNoGravity(false);
            wasRooted = false;
            originalMovementSpeed = -1;
        }

        if (!this.level().isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if(attacker instanceof VenemerusEntity){
            return false;
        }
        return super.hurt(source, amount);
    }

    public boolean doHurtTarget(Entity entity) {
        if (super.doHurtTarget(entity)) {

            if (entity instanceof LivingEntity) {

                int i = 0;
                if (this.level().getDifficulty() == Difficulty.NORMAL) {
                    i = 7;
                } else if (this.level().getDifficulty() == Difficulty.HARD) {
                    i = 15;
                }
                if (i > 0) {
                    ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0), this);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    static {
        DATA_FLAGS_ID = SynchedEntityData.defineId(VenemerusEntity.class, EntityDataSerializers.BYTE);
    }

    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplier);
        }

    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_FLAGS_ID, (byte)0);
    }

    public void setClimbing(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(DATA_FLAGS_ID);
        if (climbing) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 &= -2;
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }


    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SPELL_ANIM = RawAnimation.begin().thenPlay("spit");
    //private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("melee");

    @Override
    protected PlayState predicate(AnimationState event) {
        if (isAnimating()) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);
        } else {
            event.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void swing(InteractionHand hand) {
        super.swing(hand);
        this.playAnimation("melee");
    }

    @Override
    protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
        if (spell == SpellRegistry.ACID_ORB_SPELL.get()) {
            controller.forceAnimationReset();
            controller.setAnimation(SPELL_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        }

        super.setStartAnimationFromSpell(controller, spell);
    }


    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.5F,1);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playAttackSound() {
        this.playSound(SoundEvents.PHANTOM_BITE, 1, 1.4F);
    }
}
