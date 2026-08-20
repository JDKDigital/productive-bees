package cy.jdkdigital.productivebees.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

public class EntityHolderRenderState extends BlockEntityRenderState
{
    public EntityRenderState entityRenderState;
    public float entityBbScale = 0.47F;
    public float facingAngle;
}
