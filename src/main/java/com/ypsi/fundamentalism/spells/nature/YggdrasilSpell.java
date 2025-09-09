package com.ypsi.fundamentalism.spells.nature;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.*;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;


@AutoSpellConfig
public class YggdrasilSpell extends AbstractSpell {

    private final ResourceLocation spellId = ResourceLocation.fromNamespaceAndPath(FundamentalPrinciples.MOD_ID, "yggdrasil");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.LEGENDARY)
            .setSchoolResource(SchoolRegistry.NATURE_RESOURCE)
            .setMaxLevel(1)
            .setCooldownSeconds(80)
            .build();


    public YggdrasilSpell(){
        this.manaCostPerLevel = 30;
        this.baseSpellPower = 30;
        this.spellPowerPerLevel = 10;
        this.castTime = 40;
        this.baseManaCost = 100;
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
    public ResourceLocation getSpellResource() {
        return spellId;
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.CHARGE_ANIMATION;
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
//            if (savedTrees.containsKey(entity.getId())) {
//                Map<BlockPos, BlockState> originalBlocks = savedTrees.get(entity.getId());
//
//                for (Map.Entry<BlockPos, BlockState> entry : originalBlocks.entrySet()) {
//                    level.setBlockAndUpdate(entry.getKey(), entry.getValue());
//                }
//                savedTrees.remove(entity.getId());
//            }
//            entity.addEffect(new MobEffectInstance(ModEffects.TREES_BLESSING, 200, 0)); // 10 segundos
//            createTreeBehindPlayer(level, entity);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }


}
