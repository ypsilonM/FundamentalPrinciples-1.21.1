package com.ypsi.fundamentalism.event;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.attributes.ModAttributes;
import com.ypsi.fundamentalism.entity.ModEntities;
import com.ypsi.fundamentalism.entity.imp.ImpEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;

//Inicializacion
@EventBusSubscriber(modid = FundamentalPrinciples.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandlers {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.IMP.get(), ImpEntity.createAttributes().build());
    }
    @SubscribeEvent
    public static void onAttributeCreate(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, ModAttributes.MAX_EXHAUSTION);
        event.add(EntityType.PLAYER, ModAttributes.EXHAUSTION_REGEN);
    }

}
