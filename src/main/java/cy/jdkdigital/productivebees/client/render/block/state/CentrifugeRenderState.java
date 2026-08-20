package cy.jdkdigital.productivebees.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class CentrifugeRenderState extends BlockEntityRenderState
{
    public boolean hasComb;
    public int stackCount;
    public float animationTime;
    public final ItemStackRenderState combStackState = new ItemStackRenderState();
}
