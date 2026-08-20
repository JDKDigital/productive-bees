package cy.jdkdigital.productivebees.compat.geckolib.client.render.state;

import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import cy.jdkdigital.productivebees.client.render.entity.state.ProductiveBeeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Geckolib 5.x is state-driven — the per-bee model and texture identifiers must be captured during
 * {@code extractRenderState} so the worker thread building the {@code RenderPassInfo} can resolve
 * them without touching the live entity. This subclass also carries the per-layer base textures
 * (primary/abdomen/glow/santahat) so the colored/glow/christmas layers don't have to dip back into
 * {@code BeeBodyLayer.baseTextures} during {@code submitRenderTask}.
 *
 * <p><b>The data-map dance.</b> Geckolib's {@code EntityRenderStateMixin} adds
 * {@code implements GeoRenderState} to {@code EntityRenderState} at runtime AND overrides
 * {@code addGeckolibData} / {@code hasGeckolibData} / {@code getDataMap} as class members backed
 * by the mixin-added field {@code geckolib$data}. Those class-level overrides always beat
 * interface defaults during virtual dispatch. The compiler, however, doesn't see the mixin, so
 * the bound {@code R extends GeoRenderState} on {@code GeoEntityRenderer} requires our subclass
 * to explicitly {@code implements GeoRenderState} and to provide a concrete {@code getDataMap()}.
 *
 * <p>The trap: if our {@code getDataMap()} returns a separate HashMap, reads via the
 * interface default {@code getGeckolibData} go through our map (empty), while writes via the
 * mixin's class-level {@code addGeckolibData} land in {@code geckolib$data}. The
 * {@code ANIMATABLE_MANAGER} ticket then reads null and {@code AnimationProcessor.extractControllerStates}
 * NPEs.
 *
 * <p>The fix: our {@code getDataMap()} returns the parent's {@code geckolib$data} via cached
 * reflection. Writes and reads now share storage.
 */
public class GeckoBeeRenderState extends ProductiveBeeRenderState implements GeoRenderState
{
    public Identifier modelLocation;
    public Identifier textureLocation;
    public Identifier primaryTexture;
    public Identifier abdomenTexture;
    public Identifier glowTexture;
    public Identifier santahatTexture;
    public boolean isBaby;
    public boolean hasStung;

    private static final Field GECKOLIB_DATA;
    static {
        try {
            Field f = EntityRenderState.class.getDeclaredField("geckolib$data");
            f.setAccessible(true);
            GECKOLIB_DATA = f;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "EntityRenderState has no 'geckolib$data' field — geckolib's EntityRenderStateMixin "
                            + "isn't applied. Check geckolib.mixins.json or the dev launcher.", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<DataTicket<?>, Object> getDataMap() {
        try {
            return (Map<DataTicket<?>, Object>) GECKOLIB_DATA.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
