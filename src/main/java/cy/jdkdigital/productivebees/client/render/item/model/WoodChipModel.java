package cy.jdkdigital.productivebees.client.render.item.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WoodChipModel extends Model
{
    public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(ProductiveBees.MODID, "textures/item/wood_chip.png");
    private final ModelRenderer plate;

    public WoodChipModel() {
        super(RenderType::getEntityCutout);
        this.textureWidth = 8;
        this.textureHeight = 14;

        this.plate = new ModelRenderer(this, 0, 0);
        this.plate.addBox(5.0F, 1.0F, 0.0F, 9.0F, 12.0F, 1.0F, 0.0F);
        this.plate.addBox(6.0F, 0.0F, 0.0F, 8.0F, 1.0F, 1.0F, 0.0F);
        this.plate.addBox(4.0F, 2.0F, 0.0F, 5.0F, 8.0F, 1.0F, 0.0F);
        this.plate.addBox(3.0F, 3.0F, 0.0F, 4.0F, 5.0F, 1.0F, 0.0F);
        this.plate.addBox(9.0F, 3.0F, 0.0F, 10.0F, 10.0F, 1.0F, 0.0F);
        this.plate.addBox(10.0F, 4.0F, 0.0F, 11.0F, 7.0F, 1.0F, 0.0F);
        this.plate.addBox(5.0F, 12.0F, 0.0F, 8.0F, 13.0F, 1.0F, 0.0F);
        this.plate.addBox(6.0F, 13.0F, 0.0F, 7.0F, 14.0F, 1.0F, 0.0F);
    }

    public void render(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {
        this.plate.render(p_225598_1_, p_225598_2_, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
    }
}
