package com.ypsi.fundamentalism.entity.mobs.cherry_bird;

import com.ypsi.fundamentalism.entity.mobs.goals.FlyingWizardAttackGoal;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.UniqueAbstractSpellCastingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;


public class CherryBirdEntity extends UniqueAbstractSpellCastingMob implements FlyingAnimal, NeutralMob {

    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;
    private static final UniformInt PERSISTENT_ANGER_TIME;

    public CherryBirdEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new FlyingMoveControl(this, 5, false);
        xpReward = 8;
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this, new Class[0]).setAlertOthers(new Class[0]));
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(2, new FlyingWizardAttackGoal(this, 1.25f, 20, 40)
                .setSpells(
                        List.of(SpellRegistry.MAGIC_MISSILE_SPELL.get()),
                        List.of(),
                        List.of(),
                        List.of()
                ));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomFlyingGoal(this, 2.0));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.FLYING_SPEED, 2)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.SCALE, 0.5)
                .add(Attributes.GRAVITY, 0.02);
    }

    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public boolean isFlying() {
        return !this.onGround();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getEntity();
        if(attacker instanceof CherryBirdEntity){
            return false;
        }
        return super.hurt(source, amount);
    }

    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void tick() {
        super.tick();
    }

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.walk");
    private static final RawAnimation FLY_ANIM = RawAnimation.begin().thenLoop("animation.fly");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.idle");

    private static final RawAnimation FIREBOLT_ANIM = RawAnimation.begin().thenPlay("animation.imp.firebolt");
    private static final RawAnimation FIREBALL_ANIM = RawAnimation.begin().thenPlay("animation.imp.fireball");
    private static final RawAnimation SLASH_ANIM = RawAnimation.begin().thenPlay("animation.imp.slash");



    @Override
    protected PlayState predicate(AnimationState event) {
        if (isAnimating()) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            if(this.isFlying()){
                event.getController().setAnimation(FLY_ANIM);
                return PlayState.CONTINUE;
            }
            event.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    @Override
    protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {

//        if(spell == SpellRegistry.FIREBOLT_SPELL.get()){
//            controller.forceAnimationReset();
//            controller.setAnimation(FIREBOLT_ANIM);
//            lastCastSpellType = spell;
//            cancelCastAnimation = false;
//            animatingLegs = false;
//            return;
//        }else if(spell == SpellRegistry.FIREBALL_SPELL.get()){
//            controller.forceAnimationReset();
//            controller.setAnimation(FIREBALL_ANIM);
//            lastCastSpellType = spell;
//            cancelCastAnimation = false;
//            animatingLegs = false;
//            return;
//        }else if(spell == SpellRegistry.FLAMING_STRIKE_SPELL.get()) {
//            controller.forceAnimationReset();
//            controller.setAnimation(SLASH_ANIM);
//            lastCastSpellType = spell;
//            cancelCastAnimation = false;
//            animatingLegs = false;
//            return;
//        }

        super.setStartAnimationFromSpell(controller, spell);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.PARROT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PARROT_DEATH;
    }


    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.addPersistentAngerSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readPersistentAngerSaveData(this.level(), pCompound);
    }


    @Override
    public int getRemainingPersistentAngerTime() {return this.remainingPersistentAngerTime;}
    @Override
    public void setRemainingPersistentAngerTime(int i) {this.remainingPersistentAngerTime = i;}

    @Override
    public @Nullable UUID getPersistentAngerTarget() {return this.persistentAngerTarget;}
    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {this.persistentAngerTarget = uuid;}

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    static {
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }

}
