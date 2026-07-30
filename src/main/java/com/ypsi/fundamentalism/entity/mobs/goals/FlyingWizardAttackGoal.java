package com.ypsi.fundamentalism.entity.mobs.goals;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.goals.WizardAttackGoal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class FlyingWizardAttackGoal extends WizardAttackGoal {

    public FlyingWizardAttackGoal(IMagicEntity abstractSpellCastingMob, double pSpeedModifier, int pAttackIntervalMin, int pAttackIntervalMax) {
        super(abstractSpellCastingMob, pSpeedModifier, pAttackIntervalMin, pAttackIntervalMax);
    }

    private float orbitAngle = 0f;

    @Override
    protected void doMovement(double distanceSquared) {
        double speed = movementSpeed();
        mob.lookAt(target, 30, 30);

        double rangeSqr = spellcastingRangeSqr;

        if (distanceSquared > rangeSqr * 0.9 * 0.9) {
            Vec3 targetPos = target.position().add(0, target.getEyeHeight() * 0.5, 0);
            this.mob.getMoveControl().setWantedPosition(targetPos.x, targetPos.y, targetPos.z, speed);
            return;
        }
        if (distanceSquared < rangeSqr * 0.3 * 0.3) {
            Vec3 fleePos = DefaultRandomPos.getPosAway(this.mob, 12, 6, target.position());
            if (fleePos != null) {
                this.mob.getMoveControl().setWantedPosition(fleePos.x, fleePos.y, fleePos.z, speed * 2);
            } else {
                Vec3 away = this.mob.position().subtract(target.position()).normalize().scale(5);
                Vec3 pos = this.mob.position().add(away);
                this.mob.getMoveControl().setWantedPosition(pos.x, pos.y, pos.z, speed * 2);
            }
            return;
        }

        double orbitRadius = distanceSquared < rangeSqr * 0.5 ? 2.5 : 5.0;
        orbitAngle += 10f;
        double rad = Math.toRadians(orbitAngle);
        double dx = Math.cos(rad) * orbitRadius;
        double dz = Math.sin(rad) * orbitRadius;
        double dy = Math.sin(orbitAngle * 0.5) * 1.5;
        Vec3 orbitPos = target.position().add(dx, target.getEyeHeight() * 0.8 + dy, dz);

        this.mob.getMoveControl().setWantedPosition(orbitPos.x, orbitPos.y, orbitPos.z, speed);
    }

    @Override
    protected double movementSpeed() {
        return speedModifier * mob.getAttributeValue(Attributes.FLYING_SPEED) * 2;
    }

    @Override
    protected void tryJump() {

    }
}
