//package cy.jdkdigital.productivebees.compat.jei.ingredients;
//
//import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
//import cy.jdkdigital.productivebees.compat.jei.ProductiveBeesJeiPlugin;
//import cy.jdkdigital.productivebees.setup.BeeReloadListener;
//import mezz.jei.api.ingredients.IIngredientHelper;
//import mezz.jei.api.ingredients.IIngredientType;
//import mezz.jei.api.ingredients.subtypes.UidContext;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//
//public class BeeIngredientHelper implements IngredientHelper<BeeIngredient>
//{
//    @Nonnull
//    @Override
//    public IIngredientType<BeeIngredient> getIngredientType() {
//        return ProductiveBeesJeiPlugin.BEE_INGREDIENT;
//    }
//
//    @Nonnull
//    @Override
//    public String getDisplayName(BeeIngredient beeIngredient) {
//        String name = beeIngredient.getBeeEntity().getDescription().getString();
//        CompoundTag nbt = BeeReloadListener.INSTANCE.getData(beeIngredient.getBeeType().toString());
//        if (nbt != null) {
//            name = Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(beeIngredient.getBeeType().toString()) + "_bee").toString();
//            if (!nbt.getString("group").isEmpty()) {
//                name = name + " (" + nbt.getString("group") + ")";
//            }
//        }
//        return name;
//    }
//
//    @Nonnull
//    @Override
//    public String getUniqueId(BeeIngredient beeIngredient, UidContext uidContext) {
//        return "beeingredient:" + beeIngredient.getBeeType();
//    }
//
//    @Override
//    public String getDisplayModId(BeeIngredient ingredient) {
//        return ingredient.getBeeType().getNamespace();
//    }
//
//    @Override
//    public ResourceLocation getResourceLocation(BeeIngredient ingredient) {
//        return ingredient.getBeeType();
//    }
//
//    @Nonnull
//    @Override
//    public BeeIngredient copyIngredient(BeeIngredient beeIngredient) {
//        return new BeeIngredient(beeIngredient.getBeeEntity(), beeIngredient.getBeeType());
//    }
//
//    @Nonnull
//    @Override
//    public String getErrorInfo(@Nullable BeeIngredient beeIngredient) {
//        if (beeIngredient == null) {
//            return "beeingredient:null";
//        }
//        if (beeIngredient.getBeeEntity() == null) {
//            return "beeingredient:bee:null";
//        }
//        return "beeingredient:" + beeIngredient.getBeeType();
//    }
//}
