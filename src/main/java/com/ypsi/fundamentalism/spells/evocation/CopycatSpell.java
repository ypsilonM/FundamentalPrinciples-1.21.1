package com.ypsi.fundamentalism.spells.evocation;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

@AutoSpellConfig
public class CopycatSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "copycat");
    private static final int NUM_ILLUSIONS = 3;

    public CopycatSpell(){
        this.baseManaCost = 30;
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 20;
    }

    @Override
    public ResourceLocation getSpellResource() {return spellId;}

    @Override
    public DefaultConfig getDefaultConfig() {
        return new DefaultConfig()
                .setMinRarity(SpellRarity.RARE)
                .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
                .setMaxLevel(3)
                .setCooldownSeconds(30)
                .build();
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.ILLUSIONER_PREPARE_MIRROR);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ILLUSIONER_CAST_SPELL);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
//            ServerLevel serverLevel = (ServerLevel) level;
//            // Duración base: 25 segundos + 5 segundos por nivel
//            int duration = 500 + (100 * spellLevel);
//            // Aplicar invisibilidad
//            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration));
//            // Crear ilusiones
//            createIllusions(serverLevel, entity, spellLevel, duration);
//            // Efectos de sonido y partículas
//            playMirrorEffects(serverLevel, entity);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public void onClientCast(Level level, int spellLevel, LivingEntity entity, ICastData castData) {
        if (level.isClientSide) {
            // Efectos visuales solo para el cliente
            for (int i = 0; i < 8; i++) {
                level.addParticle(ParticleTypes.CLOUD,
                        entity.getRandomX(0.5),
                        entity.getRandomY(),
                        entity.getRandomZ(0.5),
                        0, 0, 0);
            }
        }

        super.onClientCast(level, spellLevel, entity, castData);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.effect_length",
                        Component.literal(Utils.timeFromTicks(600 + (200 * spellLevel), 1)))
        );
    }
}
