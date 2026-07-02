package com.ypsi.fundamentalism.block;

import com.ypsi.fundamentalism.FundamentalPrinciples;
import com.ypsi.fundamentalism.block.custom.DomainBlockEntity;
import com.ypsi.fundamentalism.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class YpsEntityBlocks {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE ,FundamentalPrinciples.MOD_ID);

    public static final Supplier<BlockEntityType<DomainBlockEntity>> DOMAIN_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
            "domain_block",
            () -> BlockEntityType.Builder.of(
                    DomainBlockEntity::new,
                    YpsBlocks.DOMAIN_BLOCK.get()
            )
                    .build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
