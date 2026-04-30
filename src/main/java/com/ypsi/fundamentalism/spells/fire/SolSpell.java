package com.ypsi.fundamentalism.spells.fire;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.entity.spells.sol.SolProjectile;
import com.ypsi.fundamentalism.spells.Animations;
import com.ypsi.fundamentalism.spells.ModSpells;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.entity.spells.fireball.MagicFireball;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import javax.annotation.Nullable;
import java.util.List;

public class SolSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "sol");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.aoe_damage", Utils.stringTruncation(getDamage(spellLevel, caster), 1)),
                Component.translatable("ui.irons_spellbooks.radius", Utils.stringTruncation(getRadius(spellLevel, caster), 1))
        );
    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(300)
            .build();

    public SolSpell(){
        this.manaCostPerLevel = 120;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 0;
        this.castTime = 20*10;
        this.baseManaCost = 450;
    }


    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float radius = getRadius(spellLevel, entity);
        Vec3 origin = entity.getEyePosition();
        double yOffset = entity.getBbHeight()*0.5;
        Vec3 spawnPos = origin.add(0, yOffset, 0).add(entity.getForward());

        SolProjectile solProjectile = new SolProjectile(world, entity);

        solProjectile.setRadius(radius);
        solProjectile.setDamage(this.getDamage(spellLevel, entity));
        solProjectile.setExplosionRadius((float)this.getRadius(spellLevel, entity)*4);
        solProjectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        solProjectile.shoot(entity.getLookAngle());
        world.addFreshEntity(solProjectile);
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public ResourceLocation getSpellResource() {return spellId;}

    @Override
    public DefaultConfig getDefaultConfig() {return defaultConfig;}

    @Override
    public CastType getCastType() {return CastType.LONG;}

    private float getDamage(int spellLevel, LivingEntity entity) {
        return (float) (getSpellPower(spellLevel, entity)* (getTimeBoost(entity)));
    }
    private float getTimeBoost(LivingEntity entity){
        if (entity == null) {
            return 1.0f;
        }
        Level level = entity.level();
        if (level == null) {
            return 1.0f;
        }
        float buffScale = 1;
        long dayTime = level.getDayTime();
        if (dayTime > 15000 && dayTime < 21000) {
            //totally debuff
            buffScale = 0.25f;
        } else if (dayTime >= 12000 && dayTime <= 15000 || dayTime >= 21000 && dayTime <= 24000) {
            //debuff
            buffScale = 0.75f;
        } else if (dayTime >= 0 && dayTime <= 3000 || dayTime >= 9000 && dayTime < 12000) {
            //medium buff
            buffScale = 1.0f;
        } else if (dayTime >= 4500 && dayTime <= 7500) {
            //SUN
            buffScale = 2.5f;
        } else if (dayTime > 3000 && dayTime < 9000) {
            //good buff
            buffScale = 1.5f;
        }
        return buffScale;
    }
    private float getRadius(int spellLevel, LivingEntity entity) {
        return (float) ((Math.sqrt(spellLevel)) + (.15f * getSpellPower(spellLevel, entity))*(getTimeBoost(entity)));
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return Animations.TONATIUH_START;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return SpellAnimations.FINISH_ANIMATION;
    }

    @Override
    public boolean stopSoundOnCancel() {
        return true;
    }

    @Override
    public boolean requiresLearning() {
        return true;
    }

}
