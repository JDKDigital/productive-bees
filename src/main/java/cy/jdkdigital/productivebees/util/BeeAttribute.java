package cy.jdkdigital.productivebees.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameter;

public class BeeAttribute<T> extends LootParameter<T>
{
    public BeeAttribute(ResourceLocation id) {
        super(id);
    }

    public String toString() {
        return "<bee_attribute " + this.getId() + ">";
    }
}
