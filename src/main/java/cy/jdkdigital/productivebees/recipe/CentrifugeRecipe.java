package cy.jdkdigital.productivebees.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.integrations.jei.ProduciveBeesJeiPlugin;
import cy.jdkdigital.productivebees.tileentity.ItemHandlerHelper;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CentrifugeRecipe implements IRecipe<IInventory>, IProductiveBeesRecipe {

    public static final IRecipeType<CentrifugeRecipe> CENTRIFUGE = IRecipeType.register(ProductiveBees.MODID + ":centrifuge");

    public final ResourceLocation id;
    public final Ingredient ingredient;
    public final List<ItemStack> output;

    public CentrifugeRecipe(ResourceLocation id, Ingredient ingredient, List<ItemStack> output) {
        this.id = id;
        this.ingredient = ingredient;
        this.output = output;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if (this.ingredient.getMatchingStacks().length > 0) {
            return inv.getStackInSlot(ItemHandlerHelper.INPUT_SLOT).getItem().equals(this.ingredient.getMatchingStacks()[0].getItem());
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return new Serializer<>(CentrifugeRecipe::new);
    }

    @Nonnull
    @Override
    public IRecipeType<?> getType() {
        return CENTRIFUGE;
    }

    public static class Serializer<T extends CentrifugeRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<T> {
        final CentrifugeRecipe.Serializer.IRecipeFactory<T> factory;

        public Serializer(CentrifugeRecipe.Serializer.IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Override
        public T read(ResourceLocation id, JsonObject json) {
            Ingredient ingredient;
            if (JSONUtils.isJsonArray(json, "ingredient")) {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonArray(json, "ingredient"));
            } else {
                ingredient = Ingredient.deserialize(JSONUtils.getJsonObject(json, "ingredient"));
            }

            JsonArray jsonArray = JSONUtils.getJsonArray(json, "output");
            List<ItemStack> outputs = new ArrayList<>();
            jsonArray.forEach(el -> {
                JsonObject jsonObject = el.getAsJsonObject();
                int min = JSONUtils.getInt(jsonObject,"min",1);
                int max = JSONUtils.getInt(jsonObject,"max",1);

                if (jsonObject.has("item")) {
                    String registryname = JSONUtils.getString(jsonObject,"item");
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryname));
                    outputs.add(new ItemStack(item, min));
                } else if (jsonObject.has("tag")) {
                    String registryname = JSONUtils.getString(jsonObject,"tag");
                    Tag<Item> tag = ItemTags.getCollection().getOrCreate(new ResourceLocation(registryname));
                    if (!tag.getAllElements().isEmpty()) {
                        outputs.add(new ItemStack(tag.getAllElements().iterator().next(), min));
                    } else {
                        ProductiveBees.LOGGER.info("tag not found: " + tag.getId() + " in:" + registryname);
                        outputs.add(ItemStack.EMPTY);
                    }
                }
            });

            return this.factory.create(id, ingredient, outputs);
        }

        public T read(@Nonnull ResourceLocation id, @Nonnull PacketBuffer buffer) {
            Ingredient ingredient = Ingredient.read(buffer);

            List<ItemStack> output = new ArrayList<>();
            IntStream.range(0, buffer.readInt()).forEach(i -> output.add(buffer.readItemStack()));

            return this.factory.create(id, ingredient, output);
        }

        public void write(@Nonnull PacketBuffer buffer, T recipe) {
            recipe.ingredient.write(buffer);
            buffer.writeInt(recipe.output.size());
            recipe.output.forEach(buffer::writeItemStack);
        }

        public interface IRecipeFactory<T extends CentrifugeRecipe> {
            T create(ResourceLocation id, Ingredient input, List<ItemStack> output);
        }
    }
}