package cy.jdkdigital.productivebees.client.render.entity.state;

import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.world.item.DyeColor;

public class ProductiveBeeRenderState extends BeeRenderState
{
    public boolean isStingless;
    public double deltaMovementSqr;
    public float sizeModifier = 1.0F;
    public String beeName = "";
    public String renderType = "";
    public String renderTransform = "";
    public boolean isColored;
    public boolean isTranslucent;
    public boolean hasBeeTexture;
    public String beeTexture = "";
    public boolean isBlehBumbleBee;
    public boolean isBumbleBee;
    public boolean isSaddled;
    public boolean christmasMode;
    public boolean aprilFool;
    public DyeColor dyeColor;
    public int hoarderInventoryItems;
    public float hoarderPeekAmount;
    public boolean rancherEatingItem;
    public boolean useGlowLayer;
    public boolean renderStatic;
    public boolean hasConverted;
    public boolean hasParticleColor;
    public int particleColor = -1;
    public int primaryColor = -1;
    public int secondaryColor = -1;
    public int tertiaryColor = -1;
    public boolean isInvisible;
    public boolean isConfigurable;
    public int entityId;
}
