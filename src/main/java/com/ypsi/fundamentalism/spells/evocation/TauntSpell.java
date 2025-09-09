package com.ypsi.fundamentalism.spells.evocation;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.network.packets.LookAtEntityPacket;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

@AutoSpellConfig
public class TauntSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "taunt");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.EPIC)
            .setSchoolResource(SchoolRegistry.EVOCATION_RESOURCE)
            .setMaxLevel(4)
            .setCooldownSeconds(20)
            .build();

    public TauntSpell() {
        this.manaCostPerLevel = 5;
        this.baseSpellPower = 6;
        this.spellPowerPerLevel = 1;
        this.castTime = 60;
        this.baseManaCost = 50;

    }

    @Override
    public int getCastTime(int spellLevel) {
        return this.castTime - ((spellLevel-1)*10);
    }

    @Override
    public boolean checkPreCastConditions(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        return Utils.preCastTargetHelper(level, entity, playerMagicData, this, 64, .35f);
    }
    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return spellId;
    }


    @Override
    public void onCast(Level world, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (playerMagicData.getAdditionalCastData() instanceof TargetEntityCastData spellTargetingData) {
            Entity targetEntity = spellTargetingData.getTarget((ServerLevel) world);
            if (targetEntity instanceof Mob mob) {
                mob.setTarget(entity);
            }else if (targetEntity instanceof ServerPlayer player){
                LookAtEntityPacket.sendToPlayer(player, entity);

                // Efectos de feedback
                world.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0F, 0.8F);

                // Partículas alrededor del jugador
                for (int i = 0; i < 360; i += 45) {
                    double rad = Math.toRadians(i);
                    double x = player.getX() + Math.cos(rad) * 1.5;
                    double z = player.getZ() + Math.sin(rad) * 1.5;
                    ((ServerLevel) world).sendParticles(
                            ParticleTypes.ANGRY_VILLAGER,
                            x, player.getY() + 1.5, z,
                            1, 0, 0, 0, 0
                    );
                }
            }
        }
        super.onCast(world, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public Vector3f getTargetingColor() {
        return new Vector3f(1.0f, 1.0f, 1.0f);
    }

//    @Override
//    public Optional<SoundEvent> getCastFinishSound() {
//        return SoundEvents.RAVAGER_ROAR
//    }
}
