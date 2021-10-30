package cy.jdkdigital.productivebees.common.crafting.conditions;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BeeExistsCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(ProductiveBees.MODID, "bee_exists");
    private final ResourceLocation beeName;

    public BeeExistsCondition(String location) {
        this(new ResourceLocation(location));
    }

    public BeeExistsCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public BeeExistsCondition(ResourceLocation tag) {
        this.beeName = tag;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        JsonObject beeData = BeeReloadListener.INSTANCE.getCondition(beeName.toString());
        return beeData != null && CraftingHelper.processConditions(beeData, "conditions");
    }

    @Override
    public String toString() {
        return "bee_exists(\"" + beeName + "\")";
    }

    public static class Serializer implements IConditionSerializer<BeeExistsCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, BeeExistsCondition value) {
            json.addProperty("bee", value.beeName.toString());
        }

        @Override
        public BeeExistsCondition read(JsonObject json) {
            return new BeeExistsCondition(new ResourceLocation(JSONUtils.getAsString(json, "bee")));
        }

        @Override
        public ResourceLocation getID() {
            return BeeExistsCondition.NAME;
        }
    }
}