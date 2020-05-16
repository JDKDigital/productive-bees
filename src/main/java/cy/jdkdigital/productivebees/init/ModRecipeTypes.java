package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.recipe.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModRecipeTypes
{
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, ProductiveBees.MODID);

    public static final RegistryObject<IRecipeSerializer<?>> ADVANCED_BEEHIVE = createRecipeType("advanced_beehive", () -> new AdvancedBeehiveRecipe.Serializer<>(AdvancedBeehiveRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> CENTRIFUGE = createRecipeType("centrifuge", () -> new CentrifugeRecipe.Serializer<>(CentrifugeRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> BEE_BREEDING = createRecipeType("bee_breeding", () -> new BeeBreedingRecipe.Serializer<>(BeeBreedingRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> BEE_SPAWNING = createRecipeType("bee_spawning", () -> new BeeSpawningRecipe.Serializer<>(BeeSpawningRecipe::new));
    public static final RegistryObject<IRecipeSerializer<?>> BEE_SPAWNING_BIG = createRecipeType("bee_spawning_big", () -> new BeeSpawningBigRecipe.Serializer<>(BeeSpawningBigRecipe::new));

    public static <B extends IRecipeSerializer<?>> RegistryObject<B> createRecipeType(String name, Supplier<? extends B> supplier) {
        return RECIPE_SERIALIZERS.register(name, supplier);
    }
}
