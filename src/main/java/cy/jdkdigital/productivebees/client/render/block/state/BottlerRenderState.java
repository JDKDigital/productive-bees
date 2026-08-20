package cy.jdkdigital.productivebees.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class BottlerRenderState extends BlockEntityRenderState
{
    public boolean hasBottle;
    public final ItemStackRenderState bottleStackState = new ItemStackRenderState();
}
