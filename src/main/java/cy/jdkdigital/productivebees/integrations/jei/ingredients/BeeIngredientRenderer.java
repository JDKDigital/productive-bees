package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import com.mojang.blaze3d.matrix.MatrixStack;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BeeIngredientRenderer implements IIngredientRenderer<BeeIngredient>
{
    @Override
    public void render(int xPosition, int yPosition, @Nullable BeeIngredient beeIngredient) {
        if (beeIngredient == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.world != null) {
            BeeEntity bee = beeIngredient.getBeeEntity().create(minecraft.world);

            if (bee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity)bee).setBeeType(beeIngredient.getBeeType().toString());
            }

            if (bee instanceof ProductiveBeeEntity) {
                ((ProductiveBeeEntity)bee).setRenderStatic();
            }

            if (minecraft.player != null && bee != null) {
                bee.ticksExisted = minecraft.player.ticksExisted;
                bee.renderYawOffset = -20;

                float scaledSize = 18;

                MatrixStack matrixStack = new MatrixStack();
                matrixStack.push();
                matrixStack.translate(7 + xPosition, 17 + yPosition, 1.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(190.0F));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(20.0F));
                matrixStack.rotate(Vector3f.XP.rotationDegrees(20.0F));
                matrixStack.translate(0.0F, -0.2F, 1);
                matrixStack.scale(scaledSize, scaledSize, scaledSize);

                EntityRendererManager entityrenderermanager = minecraft.getRenderManager();
                IRenderTypeBuffer.Impl buffer = minecraft.getRenderTypeBuffers().getBufferSource();
                entityrenderermanager.renderEntityStatic(bee, 0, 0, 0.0D, minecraft.getRenderPartialTicks(), 1, matrixStack, buffer, 15728880);
                buffer.finish();
                matrixStack.pop();
            }
        }
    }

    @Nonnull
    @Override
    public List<String> getTooltip(BeeIngredient beeIngredient, ITooltipFlag iTooltipFlag) {
        List<String> list = new ArrayList<>();
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
        if (nbt != null) {
            list.add(new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name")).getFormattedText());
        } else {
            list.add(beeIngredient.getBeeEntity().getName().getFormattedText());
        }
        list.add(TextFormatting.DARK_GRAY + "" + beeIngredient.getBeeType());
        return list;
    }
}
