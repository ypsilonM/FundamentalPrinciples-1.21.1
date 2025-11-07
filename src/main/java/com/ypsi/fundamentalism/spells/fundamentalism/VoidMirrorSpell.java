package com.ypsi.fundamentalism.spells.fundamentalism;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.spells.YpsSchoolRegistry;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@AutoSpellConfig
public class VoidMirrorSpell extends AbstractSpell {
    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "mirror_spell");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(YpsSchoolRegistry.FUNDAMENTAL_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(5)
            .build();

    public VoidMirrorSpell() {
        this.manaCostPerLevel = 0;
        this.baseSpellPower = 0;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 0;
    }

    @Override
    public ResourceLocation getSpellResource() { return spellId; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public CastType getCastType() { return CastType.INSTANT; }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        entity.sendSystemMessage(Component.literal("Casteo de fundamental spell"));
    }

    @Override
    public boolean allowLooting() {
        return true;
    }

    @Override
    public boolean requiresLearning() {
        return false;
    }
}
