package cy.jdkdigital.productivebees.compat.emi;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.render.ingredient.BeeRenderer;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BeeEmiStack extends EmiStack
{
    private final BeeIngredient beeIngredient;

    private BeeEmiStack(BeeIngredient beeIngredient) {
        this.beeIngredient = beeIngredient;
    }

    public static BeeEmiStack of(BeeIngredient beeIngredient) {
        return new BeeEmiStack(beeIngredient);
    }

    @Override
    public EmiStack copy() {
        return new BeeEmiStack(this.beeIngredient);
    }

    @Override
    public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
        var minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            BeeRenderer.render(draw, x, y, this.beeIngredient, Minecraft.getInstance());
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public DataComponentPatch getComponentChanges() {
        return DataComponentPatch.EMPTY;
    }

    @Override
    public Object getKey() {
        return beeIngredient.getBeeType();
    }

    @Override
    public ResourceLocation getId() {
        return beeIngredient.getBeeType().withPath(p -> "/" + p);
    }

    @Override
    public boolean isEqual(EmiStack stack) {
        if (stack instanceof BeeEmiStack beeEmiStack) {
            return beeEmiStack.beeIngredient.getBeeType().equals(this.beeIngredient.getBeeType());
        }
        return super.isEqual(stack);
    }

    @Override
    public List<Component> getTooltipText() {
        List<Component> list = new ArrayList<>();
        CompoundTag data = BeeReloadListener.INSTANCE.getData(this.beeIngredient.getBeeType());
        if (data != null) {
            list.add(Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeIngredient.getBeeType()) + "_bee"));
        }
        list.add(Component.literal(beeIngredient.getBeeType().toString()).withStyle(ChatFormatting.DARK_GRAY));
        list.add(Component.translatable("itemGroup.productivebees").withStyle(ChatFormatting.DARK_BLUE));
        return list;
    }

    @Override
    public List<ClientTooltipComponent> getTooltip() {
        List<ClientTooltipComponent> list = Lists.newArrayList();
        if (!isEmpty()) {
            list.add(ClientTooltipComponent.create(getName().getVisualOrderText()));
            list.add(ClientTooltipComponent.create(Component.literal(beeIngredient.getBeeType().toString()).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText()));
            list.add(ClientTooltipComponent.create(Component.translatable("itemGroup.productivebees").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC).getVisualOrderText()));
            list.addAll(super.getTooltip());
        }
        return list;
    }

    @Override
    public Component getName() {
        return Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeIngredient.getBeeType()) + "_bee");
    }
}
