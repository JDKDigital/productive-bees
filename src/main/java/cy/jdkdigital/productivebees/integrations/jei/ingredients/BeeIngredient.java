package cy.jdkdigital.productivebees.integrations.jei.ingredients;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BeeIngredient {
    private EntityType<BeeEntity> bee;
    private int renderType = 0;

    public BeeIngredient(EntityType<BeeEntity> bee, int renderType) {
        this.bee = bee;
        this.renderType = renderType;
    }

    public EntityType<BeeEntity> getBeeType() {
        return bee;
    }

    public int getRenderType() {
        return renderType;
    }

    public static BeeIngredient read(PacketBuffer buffer) {
        String beeName = buffer.readString();

        return new BeeIngredient((EntityType<BeeEntity>) ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeName)), buffer.readInt());
    }

    public final void write(PacketBuffer buffer) {
        buffer.writeString("" + this.bee.getRegistryName());
        buffer.writeInt(this.renderType);
    }
}
