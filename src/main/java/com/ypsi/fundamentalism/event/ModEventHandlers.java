package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.YpsAttributes;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.mobs.cherry_bird.CherryBirdEntity;
import com.ypsi.fundamentalism.entity.mobs.hemomancer.HemomancerEntity;
import com.ypsi.fundamentalism.entity.mobs.imp.ImpEntity;
import com.ypsi.fundamentalism.entity.mobs.runear.RunearEntity;
import com.ypsi.fundamentalism.entity.mobs.venemerus.VenemerusEntity;
import com.ypsi.fundamentalism.entity.spells.chains.ChainsEntity;
import com.ypsi.fundamentalism.item.ModItems;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.BrewingRecipeRegistry;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID)
public class ModEventHandlers {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.IMP.get(), ImpEntity.createAttributes().build());
        event.put(ModEntities.CHAINS.get(), ChainsEntity.createLivingAttributes().build());
        event.put(ModEntities.HEMOMANCER.get(), HemomancerEntity.createAttributes().build());
        event.put(ModEntities.VENEMERUS.get(), VenemerusEntity.createAttributes().build());
        event.put(ModEntities.RUNEAR.get(), RunearEntity.createAttributes().build());
        event.put(ModEntities.CHERRY_BIRD.get(), CherryBirdEntity.createAttributes().build());

    }
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, YpsAttributes.MAX_FATIGUE);
        event.add(EntityType.PLAYER, YpsAttributes.FATIGUE_REGEN);

        event.add(EntityType.PLAYER, YpsAttributes.RESONANCE);

        event.add(EntityType.PLAYER, YpsAttributes.FUNDAMENTALISM_SPELL_POWER);
        event.add(EntityType.PLAYER, YpsAttributes.FUNDAMENTALISM_MAGIC_RESIST);
    }
    @SubscribeEvent
    public static void registerSpawnPositions(RegisterSpawnPlacementsEvent event){
        event.register(ModEntities.HEMOMANCER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.VENEMERUS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.IMP.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.RUNEAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(ModEntities.CHERRY_BIRD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE,
                AbstractSpellCastingMob::checkMobSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);

    }

    @SubscribeEvent
    public static void registerBrewingRecipeRegister(RegisterBrewingRecipesEvent event){
        event.getBuilder().addRecipe(
                Ingredient.of(ModItems.TEST_TUBE),  Ingredient.of(Items.PITCHER_PLANT), ModItems.PITCHER_EXTRACT.toStack(1)
        );
        event.getBuilder().addRecipe(
                Ingredient.of(ModItems.TEST_TUBE),  Ingredient.of(ModItems.ARCANE_MIXTURE), ModItems.LUMINAIRE_EXTRACT.toStack(1)
        );
    }



}
