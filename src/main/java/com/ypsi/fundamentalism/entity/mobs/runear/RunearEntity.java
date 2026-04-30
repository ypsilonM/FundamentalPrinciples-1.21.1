package com.ypsi.fundamentalism.entity.mobs.runear;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import io.redspace.ironsspellbooks.entity.mobs.goals.PatrolNearLocationGoal;
import io.redspace.ironsspellbooks.entity.mobs.goals.WarlockAttackGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.checkerframework.checker.units.qual.A;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunearEntity extends AbstractSpellCastingMob implements Enemy {

    public RunearEntity(EntityType<? extends AbstractSpellCastingMob> entityType, Level level) {
        super(entityType, level);
        xpReward = 60;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("anim.walking");
    private static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("anim.running");
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("anim.idle");
    private static final RawAnimation SPELL_SMASH = RawAnimation.begin().thenPlay("anim.smash");
    private static final RawAnimation SPELL_SLAM = RawAnimation.begin().thenPlay("anim.slam");
    private static final RawAnimation ATTACK_1 = RawAnimation.begin().thenPlay("anim.attack");

    private int meleeTimer = 0;
    private double originalMovementSpeed = -1;
    private boolean wasRooted = false;


    @Override
    protected void registerGoals() {
//        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WarlockAttackGoal(this, 1.25, 80, 120)
                .setSpells(
                        List.of(SpellRegistry.STOMP_SPELL.get()),
                        List.of(SpellRegistry.EARTHQUAKE_SPELL.get()),
                        List.of(),
                        List.of(SpellRegistry.OAKSKIN_SPELL.get())
                ).setSpellQuality(2,2)
                .setMeleeBias(0.8f, 1f)
                .setMeleeAttackInverval(20, 40)
        );
        this.goalSelector.addGoal(3, new PatrolNearLocationGoal(this, 30, .75f));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ARMOR, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 15.0)
                .add(AttributeRegistry.SPELL_RESIST, 2)
                .add(AttributeRegistry.SPELL_POWER, 1.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.1)
                .add(Attributes.WATER_MOVEMENT_EFFICIENCY, 2)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 3)
                .add(Attributes.BLOCK_INTERACTION_RANGE, 3)
                .add(Attributes.SCALE, 1.5)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public boolean isInvertedHealAndHarm() {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "move_controller", 0, this::movePredicate));
        controllers.add(new AnimationController<>(this, "stomp_controller", 2, this::castPredicate).triggerableAnim("anim.smash", SPELL_SMASH));
        controllers.add(new AnimationController<>(this, "slam_controller", 2, this::castPredicate).triggerableAnim("anim.slam", SPELL_SLAM));
        controllers.add(new AnimationController<>(this, "melee_controller", 0, this::meleePredicate).triggerableAnim("anim.attack", ATTACK_1));

    }

    private <E extends GeoAnimatable> PlayState movePredicate(AnimationState<E> event) {
        boolean isCastingRootSpell = false;
        if (isCasting()) {
            AbstractSpell castingSpell = getMagicData().getCastingSpell() != null
                    ? getMagicData().getCastingSpell().getSpell() : null;

            isCastingRootSpell = (
                        castingSpell == SpellRegistry.STOMP_SPELL.get() ||
                        castingSpell == SpellRegistry.EARTHQUAKE_SPELL.get()
            );
        }

        if (isCastingRootSpell && this.meleeTimer > 0) {
            return PlayState.STOP;
        }
        if (event.isMoving()) {
            boolean isChasing = this.getTarget()!=null && this.getTarget().isAlive();
            event.getController().setAnimation(isChasing ? RUN_ANIM : WALK_ANIM);
        } else {
            event.getController().setAnimation(IDLE_ANIM);
        }
        return PlayState.CONTINUE;
    }

    private <E extends GeoAnimatable> PlayState castPredicate(AnimationState<E> event) {
        return isCasting()? PlayState.CONTINUE: PlayState.STOP;
    }

    private <E extends GeoAnimatable> PlayState meleePredicate(AnimationState<E> event) {
        return this.meleeTimer > 0 ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void initiateCastSpell(AbstractSpell spell, int spellLevel) {
        super.initiateCastSpell(spell, spellLevel);

        if(spell == SpellRegistry.STOMP_SPELL.get()) {
            this.triggerAnim("stomp_controller", "anim.smash");
        }else if(spell == SpellRegistry.EARTHQUAKE_SPELL.get()) {
            this.triggerAnim("slam_controller", "anim.slam");
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.meleeTimer > 0) {
            this.meleeTimer--;
        }

        boolean shouldBeRooted = false;
        if (isCasting()) {
            AbstractSpell castingSpell = getMagicData().getCastingSpell() != null
                    ? getMagicData().getCastingSpell().getSpell()
                    : null;
            if (castingSpell == SpellRegistry.STOMP_SPELL.get()
                    || castingSpell == SpellRegistry.EARTHQUAKE_SPELL.get()) {
                shouldBeRooted = true;
            }
        }

        if (shouldBeRooted && !wasRooted) {
            // Entering rooted state: store original speed and immobilise
            originalMovementSpeed = this.getAttributeBaseValue(Attributes.MOVEMENT_SPEED);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            this.setNoGravity(true);
            wasRooted = true;
        }
        else if (!shouldBeRooted && wasRooted) {
            // Exiting rooted state: restore speed and gravity
            if (originalMovementSpeed != -1) {
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(originalMovementSpeed);
            }
            this.setNoGravity(false);
            wasRooted = false;
            originalMovementSpeed = -1;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.horizontalCollision && EventHooks.canEntityGrief(this.level(), this) && this.getTarget()!=null) {
            boolean flag = false;
            AABB aabb = this.getBoundingBox().inflate(0.4);
            Iterator var8 = BlockPos.betweenClosed(Mth.floor(aabb.minX), Mth.floor(aabb.minY), Mth.floor(aabb.minZ), Mth.floor(aabb.maxX), Mth.floor(aabb.maxY), Mth.floor(aabb.maxZ)).iterator();

            label62:
            while(true) {
                BlockPos blockpos;
                Block block;
                do {
                    if (!var8.hasNext()) {
                        if (!flag && this.onGround()) {
                            this.jumpFromGround();
                        }
                        break label62;
                    }

                    blockpos = (BlockPos)var8.next();
                    BlockState blockstate = this.level().getBlockState(blockpos);
                    block = blockstate.getBlock();
                } while(!(keepDestroying(block)));

                flag = this.level().destroyBlock(blockpos, true, this) || flag;
            }
        }
    }
    public boolean keepDestroying(Block block){
        return block instanceof LeavesBlock ||
                block.getName().toString().contains(("log"));
    }

    public void triggerMeleeAttack(){
        this.meleeTimer = 15;
        this.triggerAnim("melee_controller", "anim.attack");
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
        this.playSound(SoundEvents.POLAR_BEAR_STEP, 2F,0.6F);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.POLAR_BEAR_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }



}
