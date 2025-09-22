package com.ypsi.fundamentalism.entity.mobs.imp;

import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardRecoverGoal;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;


public class ImpEntity extends AbstractSpellCastingMob implements Enemy {

    public ImpEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.imp.walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.imp.idle");
    private static final RawAnimation FIREBOLT_ANIM = RawAnimation.begin().thenPlay("animation.imp.firebolt");
    private static final RawAnimation FIREBALL_ANIM = RawAnimation.begin().thenPlay("animation.imp.fireball");

    private int castingTimer = 0;

    @Override
    protected void registerGoals() {
//        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WizardAttackGoal(this, 1.25f, 50, 75)
                .setSpells(
                        List.of(SpellRegistry.FIREBOLT_SPELL.get(), SpellRegistry.FIREBALL_SPELL.get()),
                        List.of(),
                        List.of(),
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
                .add(Attributes.FOLLOW_RANGE, 25.0);
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        if (spell == SpellRegistry.FIREBOLT_SPELL.get()) {
            super.initiateCastSpell(spell, spellLevel);
            this.castingTimer = 40;
            this.triggerAnim("firebolt_controller","fireboltCast");

        }else if(spell == SpellRegistry.FIREBALL_SPELL.get()){
            super.initiateCastSpell(spell, spellLevel);
            this.castingTimer = 60;
            this.triggerAnim("fireball_controller","fireballCast");

        }else if(spell == ModSpells.TAUNT.get()){
            super.initiateCastSpell(spell, spellLevel);
            this.castingTimer = 10;
            this.triggerAnim("fireball_controller","fireballCast");

        }
    }
    private void forceLookAtTarget(LivingEntity target) {
        if (target != null) {
            double d0 = target.getX() - this.getX();
            double d2 = target.getZ() - this.getZ();
            double d1 = target.getEyeY() - this.getEyeY();

            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
            float f1 = (float) (-(Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
            this.setXRot(f1 % 360);
            this.setYRot(f % 360);
        }
    }
    @Override
    public void tick() {
        super.tick();

        if (this.castingTimer > 0) {
            this.castingTimer--;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
    }

//    @Override
//    protected void customServerAiStep() {
//        super.customServerAiStep();
//
//        LivingEntity target = this.getTarget();
//        if (target != null && this.distanceTo(target) < 20f) {
//            this.setAggressive(true);
//        } else {
//            this.setAggressive(false);
//        }
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move_controller", 5, this::movePredicate));
        controllers.add(new AnimationController<>(this, "firebolt_controller", 1, this::castPredicate).triggerableAnim("fireboltCast", FIREBOLT_ANIM));
        controllers.add(new AnimationController<>(this, "fireball_controller", 2, this::castPredicate).triggerableAnim("fireballCast", FIREBALL_ANIM));
    }

    private <E extends GeoAnimatable> PlayState movePredicate(AnimationState<E> event) {
        if (this.castingTimer > 0) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            event.getController().setAnimation(WALK_ANIM);
        } else {
            event.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState castPredicate(AnimationState<E> event) {
        return this.castingTimer>0?PlayState.CONTINUE:PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.VEX_AMBIENT, 0.5F,1);
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
