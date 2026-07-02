package com.ypsi.fundamentalism.block.custom;

import com.ypsi.fundamentalism.block.YpsEntityBlocks;
import io.redspace.ironsspellbooks.registries.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DomainBlockEntity extends BlockEntity {

    private int color = 0xFFFFFFFF;

    public DomainBlockEntity(BlockPos pos, BlockState blockState) {
        super(YpsEntityBlocks.DOMAIN_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DomainBlockEntity domainBlockEntity) {
//        if(level.isClientSide){
//            level.addParticle(
//                    ParticleRegistry.BLOOD_PARTICLE.get(),
//                    pos.getX(), pos.getY(), pos.getZ(), 0.5, 0.5, 0.5
//            );
//        }
    }

    public int getColor() { return color; }
    public void setColor(int color) {
        this.color = color;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.getChunkSource().getLightEngine().checkBlock(worldPosition);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.color = tag.getInt("Color");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Color", this.color);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("Color", this.color);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
