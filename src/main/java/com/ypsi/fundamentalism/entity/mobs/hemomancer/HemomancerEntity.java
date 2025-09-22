package com.ypsi.fundamentalism.entity.mobs.hemomancer;

import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HemomancerEntity extends AbstractSpellCastingMob implements Enemy {
    public HemomancerEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SPELL_ANIM = RawAnimation.begin().thenPlay("cast");
    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("attack");
    private static final RawAnimation ATTACK_2 = RawAnimation.begin().thenPlay("attack2");

    private int castingTimer = 0;
    private int meleeTimer = 0;

    @Override
    protected void registerGoals() {
//        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WarlockAttackGoal(this, 1.25, 80, 160)
                .setSpells(
                        List.of(SpellRegistry.DEVOUR_SPELL.get() , SpellRegistry.ACUPUNCTURE_SPELL.get()),
                        List.of(),
                        List.of(SpellRegistry.BLOOD_STEP_SPELL.get()),
                        List.of()
                )
                        .setMeleeBias(1, 1)
                .setSingleUseSpell(SpellRegistry.BLOOD_SLASH_SPELL.get(), 200, 300, 1, 1 )
                .setMeleeAttackInverval(30, 40)
        );
        this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ARMOR, 12.0)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 25.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 2.5)
                .add(AttributeRegistry.SPELL_POWER, 1.0);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move_controller", 0, this::movePredicate));
        controllers.add(new AnimationController<>(this, "devour_controller", 0, this::castPredicate).triggerableAnim("cast", SPELL_ANIM));
        controllers.add(new AnimationController<>(this, "slash_controller", 0, this::castPredicate).triggerableAnim("cast", SPELL_ANIM));
        controllers.add(new AnimationController<>(this, "melee_controller", 0, this::meleePredicate)
                .triggerableAnim("attack", ATTACK_1)
                .triggerableAnim("attack2",ATTACK_2));

    }

    private <E extends GeoAnimatable> PlayState movePredicate(AnimationState<E> event) {
        if (this.castingTimer > 0 || this.meleeTimer > 0) {
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

    private <E extends GeoAnimatable> PlayState meleePredicate(AnimationState<E> event) {
        return this.meleeTimer > 0 ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        List<AbstractSpell> castSpells = new ArrayList<>();
        castSpells.add(SpellRegistry.DEVOUR_SPELL.get());
        castSpells.add(SpellRegistry.BLOOD_SLASH_SPELL.get());
        castSpells.add(SpellRegistry.ACUPUNCTURE_SPELL.get());
        if (castSpells.contains(spell)) {
            super.initiateCastSpell(spell, spellLevel);
            this.castingTimer = 10;
            this.triggerAnim("devour_controller", "cast");
        }
    }

    public void triggerMeleeAttack(){
        this.meleeTimer = 20;
        if(this.random.nextBoolean()){
            this.triggerAnim("melee_controller", "attack");
        }else{
            this.triggerAnim("melee_controller", "attack2");
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.castingTimer > 0) {
            this.castingTimer--;
        }

        if (this.meleeTimer > 0) {
            this.meleeTimer--;
        }
    }

    @Override
    public void swing(InteractionHand hand) {
        super.swing(hand);
        this.triggerMeleeAttack();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
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
