package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.Supplier;

public class SpawnEgg extends SpawnEggItem
{
    private final Supplier<EntityType<?>> entityType;

    public SpawnEgg(Supplier<EntityType<?>> entityType, int primaryColor, int secondaryColor, Item.Properties properties) {
        super(null, primaryColor, secondaryColor, properties);
        this.entityType = entityType;
    }

    @Nonnull
    @Override
    public EntityType<?> getType(CompoundNBT compound) {
        if (compound != null && compound.contains("EntityTag", 10)) {
            CompoundNBT entityTag = compound.getCompound("EntityTag");

            if (entityTag.contains("id", 8)) {
                return EntityType.byString(entityTag.getString("id")).orElse(this.entityType.get());
            }
        }
        return this.entityType.get();
    }

    public int getColor(int tintIndex, ItemStack stack) {
        CompoundNBT tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                return tintIndex == 0 ? nbt.getInt("primaryColor") : nbt.getInt("secondaryColor");
            }
        }
        return super.getColor(tintIndex);
    }

    @Nonnull
    @Override
    public ITextComponent getName(ItemStack stack) {
        CompoundNBT tag = stack.getTagElement("EntityTag");
        if (tag != null && tag.contains("type")) {
            CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(tag.getString("type"));
            if (nbt != null) {
                String name = new TranslationTextComponent("entity.productivebees." + ProductiveBeeEntity.getBeeName(tag.getString("type")) + "_bee").getString();
                return new TranslationTextComponent("item.productivebees.spawn_egg_configurable", name);
            }
        }
        return super.getName(stack);
    }

    @Override
    public void fillItemCategory(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ModItems.CONFIGURABLE_SPAWN_EGG.get())) {
            super.fillItemCategory(group, items);
        } else if (group == ItemGroup.TAB_SEARCH) {
            for (Map.Entry<String, CompoundNBT> entry : BeeReloadListener.INSTANCE.getData().entrySet()) {
                items.add(BeeCreator.getSpawnEgg(entry.getKey()));
            }
        }
    }
}
