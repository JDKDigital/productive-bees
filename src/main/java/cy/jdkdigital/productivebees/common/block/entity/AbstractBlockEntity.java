package cy.jdkdigital.productivebees.common.block.entity;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class AbstractBlockEntity extends BlockEntity
{
    public AbstractBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.loadPacketNBT(tag);
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        this.savePacketNBT(tag);
        return tag;
    }

    public void savePacketNBT(CompoundTag tag) {
    }

    public void loadPacketNBT(CompoundTag tag) {
    }

    @Override
    public CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTagCompound = new CompoundTag();
        savePacketNBT(nbtTagCompound);
        return new ClientboundBlockEntityDataPacket(this.worldPosition, 1, nbtTagCompound);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.loadPacketNBT(pkt.getTag());
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }
}
