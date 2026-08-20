package cy.jdkdigital.productivebees.setup;

/**
 * Empty client proxy — the pre-26.1 implementation captured a {@code MultiBufferSource} during
 * {@code RenderLivingEvent.Pre} so the bee-nest-helmet armor model could draw a 3D beehive on
 * top of the head. 26.1 reshaped the player render pipeline into state extraction +
 * {@code SubmitNodeCollector}, and the helmet overlay was dropped along with the legacy
 * {@code HumanoidModel.renderToBuffer} hook. Rebuild the visual as a proper player render layer
 * (registered via {@code EntityRenderersEvent.AddLayers}) if it's ever wanted back.
 */
public final class ClientProxy
{
    private ClientProxy() {}
}
