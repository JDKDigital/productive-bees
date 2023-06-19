package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.block.CanvasExpansionBox;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CanvasExpansionBoxBlockEntity extends ExpansionBoxBlockEntity implements CanvasBlockEntityInterface
{
    private final CanvasExpansionBox box;
    private int color = 16777215;

    public CanvasExpansionBoxBlockEntity(CanvasExpansionBox box, BlockPos pos, BlockState state) {
        super(box, pos, state);
        this.box = box;
    }

    @Override
    public BlockEntityType<?> getType() {
        return box != null ? box.getBlockEntitySupplier().get() : super.getType();
    }

    public void setColor(int color) {
        this.color = color;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    public int getColor(int tintIndex) {
        return color;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("color", color);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("color")) {
            this.color = tag.getInt("color");
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return saveWithId();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }
}
