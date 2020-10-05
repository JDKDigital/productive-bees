package cy.jdkdigital.productivebees.entity.bee;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;
import com.resourcefulbees.resourcefulbees.api.beedata.*;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.item.Honeycomb;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public class ResourcefulBeeEntity extends ProductiveBeeEntity implements ResourcefulCompatBee
{
    private CustomBeeData customBeeData = null;

    public ResourcefulBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public CustomBeeData getBeeData() {
        if (customBeeData != null) {
            return customBeeData;
        }
        List<ItemStack> produce = BeeHelper.getBeeProduce(world, this);
        boolean hasComb = false;
        ItemStack combProduce = null;
        for(ItemStack stack: produce) {
            if (stack.getItem() instanceof Honeycomb || stack.getItem().getRegistryName().getPath().contains("honeycomb_")) {
                combProduce = stack;
                hasComb = true;
                break;
            }
        }

        CustomBeeData data = new CustomBeeData.Builder(
                getName().toString(),
                "#minecraft:flowers",
                hasComb,
                new MutationData.Builder(false, null).createMutationData(),
                new ColorData.Builder(true)
                        .setPrimaryColor(String.format("#%06x", getColor(0).getRGB() & 0xFFFFFF))
                        .setSecondaryColor(String.format("#%06x", getColor(1).getRGB() & 0xFFFFFF))
                        .createColorData(),
                new CentrifugeData.Builder(false, null).createCentrifugeData(),
                new BreedData.Builder(false).createBreedData(),
                new SpawnData.Builder(false).createSpawnData(),
                new TraitData(false)
        ).createCustomBee();

        if (hasComb) {
            ItemStack finalCombProduce = combProduce;
            data.setCombSupplier(() -> {
                return finalCombProduce;
            });
            // Fallback to comb produce for non-configurable bees
            data.setCombBlockItemSupplier(() -> {
                return finalCombProduce;
            });
        }

        customBeeData = data;
        return customBeeData;
    }

    @Override
    public ICustomBee createSelectedChild(CustomBeeData customBeeData) {
        BeeIngredient beeIngredient = BeeIngredientFactory.getIngredient(customBeeData.getName()).get();
        if (beeIngredient != null) {
            BeeEntity newBee = beeIngredient.getBeeEntity().create(world);
            if (newBee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity) newBee).setBeeType(beeIngredient.getBeeType().toString());
            }
            return (ICustomBee) newBee;
        }
        return null;
    }
}
