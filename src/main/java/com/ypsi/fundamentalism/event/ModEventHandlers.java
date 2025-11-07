package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerEntity;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsEntity;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ModEventHandlers {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.IMP.get(), ImpEntity.createAttributes().build());
        event.put(ModEntities.CHAINS.get(), ChainsEntity.createLivingAttributes().build());
        event.put(ModEntities.HEMOMANCER.get(), HemomancerEntity.createAttributes().build());
        event.put(ModEntities.VENEMERUS.get(), VenemerusEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, YpsAttributes.MAX_EXHAUSTION);
        event.add(EntityType.PLAYER, YpsAttributes.EXHAUSTION_REGEN);
    }
    @SubscribeEvent
    public static void registerSpawnPositions(RegisterSpawnPlacementsEvent event){
        event.register(ModEntities.HEMOMANCER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.VENEMERUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.IMP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
