package com.ypsi.fundamentalism.spells.blood;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.effect.ModEffects;
import com.ypsi.fundamentalism.spells.Animations;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LacerationSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "laceration");

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.damage", getDamageText(spellLevel, caster)),
                Component.translatable("ui.irons_spellbooks.effect_length",getEffectText(spellLevel, caster), 1)
        );

    }

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.BLOOD_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(30)
            .build();

    public LacerationSpell(){
        this.baseManaCost = 40;
        this.manaCostPerLevel = 10;
        this.baseSpellPower = 5;
        this.spellPowerPerLevel = 2;
        this.castTime = 15 ;
    }

    @Override
    public int getEffectiveCastTime(int spellLevel, @Nullable LivingEntity entity) {
        return getCastTime(spellLevel);
    }

    @Override
    public boolean canBeInterrupted(@Nullable Player player) {
        return false;
    }


    @Override
    public void onCast(Level level, int spellLevel, LivingEntity caster, CastSource castSource, MagicData playerMagicData) {

        float x = 1.6f;
        float slashHeight = 3.2f;
        float z = 1.6f;

        float distance = 2f;
        Vec3 forward = caster.getForward();
        Vec3 hitLocation = caster.position()
                .add(0, caster.getBbHeight() * .3f, 0)
                .add(forward.scale(distance));

        for (Entity target : level.getEntities(caster, AABB.ofSize(hitLocation, x, slashHeight, z))) {
            if (target instanceof LivingEntity &&
                target.isAlive() && caster.isPickable() &&
                target.position().subtract(caster.getEyePosition()).dot(forward) >= 0 &&
                Utils.hasLineOfSight(level, caster.getEyePosition(), target.getBoundingBox().getCenter(), true)
            ) {
                if (target.getBoundingBox().getCenter().subtract(caster.getEyePosition()).dot(forward) >= 0) {
                    if (DamageSources.applyDamage(target, getDamage(spellLevel, caster), this.getDamageSource(caster))) {

                        EnchantmentHelper.doPostAttackEffects((ServerLevel) level, target, this.getDamageSource(caster));
                        MagicManager.spawnParticles(level,
                                ParticleRegistry.BLOOD_PARTICLE.get(),
                                hitLocation.x, hitLocation.y + 0.5, hitLocation.z,
                                100, 0.05, 0.5, 0.05, 0.1, true);
                        if(target instanceof LivingEntity livingEntity)
                            livingEntity.addEffect(
                                    new MobEffectInstance(
                                            ModEffects.LACERATED_EFFECT,
                                            getDuration(spellLevel, caster),
                                            1,
                                            true,
                                            true,
                                            true
                                    )
                            );

                    }
                }
            }
        }

        super.onCast(level, spellLevel, caster, castSource, playerMagicData);
    }

    private float getDamage(int spellLevel, LivingEntity entity) {
        return getSpellPower(spellLevel, entity) + getAdditionalDamage(entity);
    }
    public int getDuration(int spellLevel, LivingEntity caster) {
        return (int) ((getSpellPower(spellLevel, caster) * 15) + getAdditionalDamage(caster)*20*2);
    }
    private float getAdditionalDamage(LivingEntity entity) {
        if (entity == null) {
            return 0;
        }
        float weaponDamage = Utils.getWeaponDamage(entity);
        var weaponItem = entity.getWeaponItem();
        if (!weaponItem.isEmpty() && weaponItem.has(DataComponents.ENCHANTMENTS)) {
            weaponDamage += Utils.getEnchantmentLevel(entity.level(), Enchantments.SHARPNESS, weaponItem.get(DataComponents.ENCHANTMENTS));
        }
        return weaponDamage/2;
    }

    private String getDamageText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float weaponDamage = Utils.getWeaponDamage(entity)/2;
            String plus = "";
            if (weaponDamage > 0) {
                plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
            }
            String damage = Utils.stringTruncation(getDamage(spellLevel, entity), 1);
            return damage + plus;
        }
        return "" + getSpellPower(spellLevel, entity);
    }

    private String getEffectText(int spellLevel, LivingEntity entity) {
        if (entity != null) {
            float weaponDamage = Utils.getWeaponDamage(entity);
            String plus = "";
            if (weaponDamage > 0) {
                plus = String.format(" (+%s)", Utils.stringTruncation(weaponDamage, 1));
            }
            String time = Utils.timeFromTicks(getDuration(spellLevel, entity), 1);
            return time + plus;
        }
        return "" + getSpellPower(spellLevel, entity);
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return Animations.LACERATION_CHARGE;
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        return Animations.LACERATION_END;
    }
}
