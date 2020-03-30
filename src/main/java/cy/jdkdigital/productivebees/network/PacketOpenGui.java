package cy.jdkdigital.productivebees.network;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.tileentity.AdvancedBeehiveTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGui {
    private ListNBT beeList;
    private BlockPos pos;

    public PacketOpenGui(PacketBuffer buf) {
        CompoundNBT tag = buf.readCompoundTag();
        this.beeList = (ListNBT) tag.get("BeeList");
        this.pos = buf.readBlockPos();
    }

    public PacketOpenGui(ListNBT beeList, BlockPos pos) {
        this.beeList = beeList;
        this.pos = pos;
    }

    public void toBytes(PacketBuffer buf) {
        CompoundNBT tag = new CompoundNBT();
        tag.put("BeeList", this.beeList);

        buf.writeCompoundTag(tag);
        buf.writeBlockPos(this.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ProductiveBees.LOGGER.info("Packet arrived " + this.beeList);
//            TileEntity tilEntity = ctx.get().getSender().world.getTileEntity(this.pos);
//            if (tilEntity instanceof AdvancedBeehiveTileEntity) {
//                AdvancedBeehiveTileEntity beehiveTileEntity = (AdvancedBeehiveTileEntity) tilEntity;
////                beehiveTileEntity.read();
//            }
        });
        ctx.get().setPacketHandled(true);
    }
}
