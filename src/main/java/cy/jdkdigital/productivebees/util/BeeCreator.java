package cy.jdkdigital.productivebees.util;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.ComponentIngredient;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.Ingredient;

public class BeeCreator
{
    public static void setType(Identifier type, ItemStack stack) {
        stack.set(ModDataComponents.BEE_TYPE, type);
    }

    public static ItemStack getSpawnEgg(Identifier beeType) {
        return getSpawnEgg(beeType, false);
    }

    public static ItemStack getSpawnEgg(Identifier beeType, boolean forceConfigurable) {
        ItemStack egg;
        if (BeeRegistries.lookup(beeType) != null || forceConfigurable) {
            egg = new ItemStack(ModItems.CONFIGURABLE_SPAWN_EGG.get());
            // TODO 1.22 use ModDataComponents.BEE_TYPE
            var tag = new CompoundTag();
            tag.putString("type", beeType.toString());
            tag.putString("id", ModEntities.CONFIGURABLE_BEE.getId().toString());
            egg.set(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntities.CONFIGURABLE_BEE.get(), tag));
        } else {
            if (beeType.getNamespace().equals(ProductiveBees.MODID)) {
                egg = new ItemStack(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(beeType.getNamespace(), "spawn_egg_" + beeType.getPath())).map(Holder::value).orElse(Items.AIR));
            } else {
                egg = new ItemStack(BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(beeType.getNamespace(), beeType.getPath() + "_spawn_egg")).map(Holder::value).orElse(Items.AIR));
            }
        }
        return egg;
    }

    /** The component patch marking a configurable spawn egg as a specific bee. */
    public static DataComponentPatch getSpawnEggPatch(Identifier beeType) {
        // TODO 1.22 use ModDataComponents.BEE_TYPE
        CompoundTag tag = new CompoundTag();
        tag.putString("type", beeType.toString());
        tag.putString("id", ModEntities.CONFIGURABLE_BEE.getId().toString());
        return DataComponentPatch.builder()
                .set(DataComponents.ENTITY_DATA, TypedEntityData.of(ModEntities.CONFIGURABLE_BEE.get(), tag))
                .build();
    }

    public static Ingredient getSpawnEggIngredient(Identifier beeType, boolean forceConfigurable) {
        if (BeeRegistries.lookup(beeType) != null || forceConfigurable) {
            return ComponentIngredient.of(getSpawnEggPatch(beeType), ModItems.CONFIGURABLE_SPAWN_EGG.get());
        }
        Item item = beeType.getNamespace().equals(ProductiveBees.MODID)
                ? BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(beeType.getNamespace(), "spawn_egg_" + beeType.getPath())).map(Holder::value).orElse(Items.AIR)
                : BuiltInRegistries.ITEM.get(Identifier.fromNamespaceAndPath(beeType.getNamespace(), beeType.getPath() + "_spawn_egg")).map(Holder::value).orElse(Items.AIR);
        return Ingredient.of(item);
    }

    public static Ingredient getSpawnEggIngredient(Identifier beeType) {
        return getSpawnEggIngredient(beeType, false);
    }
}
