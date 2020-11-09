package cy.jdkdigital.productivebees.entity.bee;

import com.resourcefulbees.resourcefulbees.api.beedata.*;
import cy.jdkdigital.productivebees.item.Honeycomb;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.entity.AgeableEntity;
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
                new CombatData.Builder(true).create(),
                new CentrifugeData.Builder(false, null).createCentrifugeData(),
                new BreedData.Builder(false).createBreedData(),
                new SpawnData.Builder(false).createSpawnData(),
                new TraitData(false)
        ).createCustomBee();

        if (hasComb) {
            ItemStack finalCombProduce = combProduce;
            data.setCombSupplier(() -> finalCombProduce);
            // Fallback to comb produce for non-configurable bees
            data.setCombBlockItemSupplier(() -> finalCombProduce);
        }

        customBeeData = data;
        return customBeeData;
    }

    @Override
    public AgeableEntity createSelectedChild(CustomBeeData customBeeData) {
        return null;
    }
}
