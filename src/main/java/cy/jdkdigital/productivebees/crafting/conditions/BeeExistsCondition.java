package cy.jdkdigital.productivebees.crafting.conditions;

import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class BeeExistsCondition implements ICondition
{
    private static final ResourceLocation NAME = new ResourceLocation(ProductiveBees.MODID, "bee_exists");
    private final String bee;

    public BeeExistsCondition(String bee)
    {
        this.bee = bee;
    }

    @Override
    public ResourceLocation getID()
    {
        return NAME;
    }

    @Override
    public boolean test()
    {
        return BeeIngredientFactory.getIngredient(bee).get() != null;
    }

    @Override
    public String toString()
    {
        return "bee_exists(\"" + bee + "\")";
    }

    public static class Serializer implements IConditionSerializer<BeeExistsCondition>
    {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, BeeExistsCondition value)
        {
            json.addProperty("bee", value.bee);
        }

        @Override
        public BeeExistsCondition read(JsonObject json)
        {
            return new BeeExistsCondition(JSONUtils.getString(json, "bee"));
        }

        @Override
        public ResourceLocation getID()
        {
            return BeeExistsCondition.NAME;
        }
    }
}