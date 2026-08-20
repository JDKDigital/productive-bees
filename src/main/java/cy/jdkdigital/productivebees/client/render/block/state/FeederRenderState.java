package cy.jdkdigital.productivebees.client.render.block.state;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.ArrayList;
import java.util.List;

public class FeederRenderState extends BlockEntityRenderState
{
    public SlabType slabType = SlabType.BOTTOM;
    public float facingAngle;
    public BlockState slabState;
    public final BlockModelRenderState slabModelState = new BlockModelRenderState();
    public final List<Slot> slots = new ArrayList<>();

    public static class Slot
    {
        public final ItemStackRenderState stackState = new ItemStackRenderState();
        public Pair<Float, Float> position;
        public boolean isFlower;
        public int slotIndex;
    }
}
