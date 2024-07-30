package cy.jdkdigital.productivebees.compat.emi;

import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceLocation;

public class BeeEmiStackSerializer implements EmiStackSerializer<BeeEmiStack>
{
    @Override
    public String getType() {
        return "bee";
    }

    @Override
    public EmiStack create(ResourceLocation id, DataComponentPatch componentChanges, long amount) {
        return BeeEmiStack.of(BeeIngredientFactory.getIngredient(id.toString().replace(":/", ":")).get());
    }
}
