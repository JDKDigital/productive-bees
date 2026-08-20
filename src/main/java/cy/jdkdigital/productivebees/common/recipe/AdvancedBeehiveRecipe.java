package cy.jdkdigital.productivebees.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.BeeCreator;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.resources.Identifier;
import cy.jdkdigital.productivelib.common.recipe.TagOutputRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class AdvancedBeehiveRecipe extends TagOutputRecipe implements Recipe<RecipeInput>
{
    public static final MapCodec<AdvancedBeehiveRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    BeeIngredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
                    Codec.list(ChancedOutput.CODEC).fieldOf("results").forGetter(recipe -> recipe.itemOutput)
            )
            .apply(builder, AdvancedBeehiveRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedBeehiveRecipe> STREAM_CODEC = StreamCodec.of(
            AdvancedBeehiveRecipe::toNetwork, AdvancedBeehiveRecipe::fromNetwork
    );

    public static final RecipeSerializer<AdvancedBeehiveRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    public final Supplier<BeeIngredient> ingredient;

    public AdvancedBeehiveRecipe(Supplier<BeeIngredient> ingredient, List<ChancedOutput> itemOutput) {
        super(itemOutput);
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(RecipeInput inv, Level worldIn) {
        if (inv instanceof BeeHelper.IdentifierInventory && ingredient.get() != null) {
            String beeName = ((BeeHelper.IdentifierInventory) inv).getIdentifier();
            // Resolve so a simple-name id on either side matches the canonical full-path form.
            Identifier invId = BeeRegistries.resolveId(Identifier.parse(beeName));
            Identifier ingId = BeeRegistries.resolveId(ingredient.get().getBeeType());
            return invId.equals(ingId);
        }
        return false;
    }

    @Override
    public Map<ItemStack, ChancedOutput> getRecipeOutputs() {
        Map<ItemStack, ChancedOutput> output = super.getRecipeOutputs();

        for (Map.Entry<ItemStack, ChancedOutput> entry : output.entrySet()) {
            if (ingredient.get().isConfigurable()) {
                if (entry.getKey().getItem().equals(ModItems.CONFIGURABLE_HONEYCOMB.get())) {
                    BeeCreator.setType(ingredient.get().getBeeType(), entry.getKey());
                }
            }
        }

        return output;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nonnull
    @Override
    public ItemStack assemble(RecipeInput inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<AdvancedBeehiveRecipe> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public RecipeType<AdvancedBeehiveRecipe> getType() {
        return ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    public static AdvancedBeehiveRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
        try {
            BeeIngredient ingredient = BeeIngredient.fromNetwork(buffer);
            List<ChancedOutput> itemOutput = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> itemOutput.add(ChancedOutput.read(buffer)));

            return new AdvancedBeehiveRecipe(() -> ingredient, itemOutput);
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error reading beehive produce recipe from packet. ", e);
            throw e;
        }
    }

    public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, AdvancedBeehiveRecipe recipe) {
        try {
            if (recipe.ingredient.get() != null) {
                recipe.ingredient.get().toNetwork(buffer);
            } else {
                throw new RuntimeException("Bee produce recipe ingredient missing");
            }

            buffer.writeInt(recipe.itemOutput.size());
            recipe.itemOutput.forEach(chancedRecipe -> {
                ChancedOutput.write(buffer, chancedRecipe);
            });
        } catch (Exception e) {
            ProductiveBees.LOGGER.error("Error writing beehive produce recipe to packet.", e);
            throw e;
        }
    }
}
