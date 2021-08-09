package cy.jdkdigital.productivebees.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import javax.annotation.Nonnull;

public class BeeAttribute<T> extends LootContextParam<T>
{
    public BeeAttribute(ResourceLocation id) {
        super(id);
    }

    @Override
    @Nonnull
    public String toString() {
        return this.getName().getPath();
    }
}
