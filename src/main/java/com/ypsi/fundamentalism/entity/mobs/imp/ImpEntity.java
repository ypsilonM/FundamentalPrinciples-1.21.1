package com.ypsi.fundamentalism.entity.mobs.imp;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.UniqueAbstractSpellCastingMob;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animation.*;

import java.util.List;


public class ImpEntity extends UniqueAbstractSpellCastingMob implements Enemy {

    public ImpEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
        xpReward = 8;
    }


    @Override
    protected void registerGoals() {
//        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));

        this.goalSelector.addGoal(2, new WizardAttackGoal(this, 1.25f, 50, 75)
                .setSpells(
                        List.of(SpellRegistry.FIREBOLT_SPELL.get(), SpellRegistry.FIREBOLT_SPELL.get(), SpellRegistry.FIREBOLT_SPELL.get(), SpellRegistry.FIREBALL_SPELL.get()),
                        List.of(),
                        List.of(SpellRegistry.BURNING_DASH_SPELL.get(), SpellRegistry.BURNING_DASH_SPELL.get(), SpellRegistry.BURNING_DASH_SPELL.get(), SpellRegistry.FLAMING_STRIKE_SPELL.get()),
                        List.of()
                ));
        this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 5.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.1)
                ;
    }


    @Override
    public void tick() {
        super.tick();
    }

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.imp.walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.imp.idle");
    private static final RawAnimation FIREBOLT_ANIM = RawAnimation.begin().thenPlay("animation.imp.firebolt");
    private static final RawAnimation FIREBALL_ANIM = RawAnimation.begin().thenPlay("animation.imp.fireball");
    private static final RawAnimation SLASH_ANIM = RawAnimation.begin().thenPlay("animation.imp.slash");



    @Override
    protected PlayState predicate(AnimationState event) {
        if (isAnimating()) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    @Override
    protected void setStartAnimationFromSpell(AnimationController controller, AbstractSpell spell) {

        if(spell == SpellRegistry.FIREBOLT_SPELL.get()){
            controller.forceAnimationReset();
            controller.setAnimation(FIREBOLT_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        }else if(spell == SpellRegistry.FIREBALL_SPELL.get()){
            controller.forceAnimationReset();
            controller.setAnimation(FIREBALL_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        }else if(spell == SpellRegistry.FLAMING_STRIKE_SPELL.get()) {
            controller.forceAnimationReset();
            controller.setAnimation(SLASH_ANIM);
            lastCastSpellType = spell;
            cancelCastAnimation = false;
            animatingLegs = false;
            return;
        }

        super.setStartAnimationFromSpell(controller, spell);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLAZE_DEATH;
    }

}
