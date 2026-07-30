package com.ypsi.fundamentalism.entity.mobs.hemomancer;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.UniqueAbstractMeleeCastingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animation.*;

import java.util.List;

public class HemomancerEntity extends UniqueAbstractMeleeCastingMob implements Enemy {

    public HemomancerEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
        xpReward = 30;
    }

    private double originalMovementSpeed = -1;
    private boolean wasRooted = false;

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WarlockAttackGoal(this, 1.25, 80, 160)
                .setSpells(
                        List.of(SpellRegistry.DEVOUR_SPELL.get() , SpellRegistry.ACUPUNCTURE_SPELL.get()),
                        List.of(),
                        List.of(SpellRegistry.BLOOD_STEP_SPELL.get()),
                        List.of()
                )
                        .setMeleeBias(1, 1)
                .setSingleUseSpell(SpellRegistry.BLOOD_SLASH_SPELL.get(), 200, 300, 1, 3 )
                .setMeleeAttackInverval(30, 40)
        );
        this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 2.5)
                .add(AttributeRegistry.SPELL_POWER, 1.0)
                .add(Attributes.SCALE, 1.2);
    }

    @Override
    public void tick() {
        super.tick();

        boolean shouldBeRooted = false;
        if (isCasting()) {
            AbstractSpell spell = getMagicData().getCastingSpell() != null
                    ? getMagicData().getCastingSpell().getSpell()
                    : null;
            shouldBeRooted = spell == SpellRegistry.DEVOUR_SPELL.get()
                    || spell == SpellRegistry.BLOOD_SLASH_SPELL.get()
                    || spell == SpellRegistry.ACUPUNCTURE_SPELL.get();
        }
        if (shouldBeRooted && !wasRooted) {
            originalMovementSpeed = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            this.setNoGravity(true);
            wasRooted = true;
        } else if (!shouldBeRooted && wasRooted) {
            if (originalMovementSpeed != -1) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(originalMovementSpeed);
            }
            this.setNoGravity(false);
            wasRooted = false;
            originalMovementSpeed = -1;
        }
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }


    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SPELL_ANIM = RawAnimation.begin().thenPlay("cast");

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
        if (this.random.nextBoolean()) {
            this.playAnimation("attack");
        } else {
            this.playAnimation("attack2");
        }
    }

    @Override
    protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {
        if (spell == SpellRegistry.DEVOUR_SPELL.get()) {
            controller.forceAnimationReset();
            controller.setAnimation(SPELL_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        } else if (spell == SpellRegistry.BLOOD_SLASH_SPELL.get()) {
            controller.forceAnimationReset();
            controller.setAnimation(SPELL_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        } else if (spell == SpellRegistry.ACUPUNCTURE_SPELL.get()) {
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
        this.playSound(SoundEvents.WITHER_SKELETON_STEP, 0.5F,1);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }


}
