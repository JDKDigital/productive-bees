package cy.jdkdigital.productivebees.recipe;

import java.util.ArrayList;
import java.util.List;

public class ProductiveBeesRecipeType<T extends IProductiveBeesRecipe> {

    private static final List<ProductiveBeesRecipeType<? extends IProductiveBeesRecipe>> types = new ArrayList<>();

    public static final ProductiveBeesRecipeType<AdvancedBeehiveRecipe> HIVE_PRODUCTION = create("hive_production");
    public static final ProductiveBeesRecipeType<AdvancedBeehiveRecipe> BEE_BREEDING = create("bee_breeding");

    public static <T extends IProductiveBeesRecipe> ProductiveBeesRecipeType<T> create(String name) {
        ProductiveBeesRecipeType<T> type = new ProductiveBeesRecipeType<>(name);
        types.add(type);
        return type;
    }

    private ProductiveBeesRecipeType(String name) {
    }
}
