package cy.jdkdigital.productivebees.util;

import net.minecraft.loot.LootParameter;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BeeAttribute<T> extends LootParameter<T>
{
    public BeeAttribute(ResourceLocation id) {
        super(id);
    }

    @Override
    @Nonnull
    public String toString() {
        return this.getId().getPath();
    }
}
