package cy.jdkdigital.productivebees.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public final class CombModel implements IUnbakedGeometry<CombModel>
{
    private final ImmutableList<String> textures;
    private final ImmutableSet<Integer> fullBrightLayers;

    Material base;
    Material crystal;
    Material particle;

    public CombModel(ImmutableList<String> textures, ImmutableSet<Integer> fullBrightLayers) {
        this.textures = textures;
        this.fullBrightLayers = fullBrightLayers;

        base = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(textures.get(0)));
        crystal = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(textures.get(1)));
        particle = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(textures.get(2)));
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particleTexture = spriteGetter.apply(
                context.hasMaterial("particle") ? context.getMaterial("particle") : particle
        );

        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(context, particleTexture, new Overrides(this, overrides, modelTransform, context, spriteGetter), context.getTransforms());

        return builder.build();
    }

    public static class Overrides extends ItemOverrides {
        private final Map<String, BakedModel> modelCache = Maps.newHashMap();
        private final CombModel combModel;
        private final ItemOverrides nested;
        private final ModelState modelState;
        private final IGeometryBakingContext context;
        private final Function<Material, TextureAtlasSprite> spriteGetter;

        private Overrides(CombModel combModel, ItemOverrides nested, ModelState modelState, IGeometryBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter)
        {
            this.combModel = combModel;
            this.nested = nested;
            this.modelState = modelState;
            this.context = context;
            this.spriteGetter = spriteGetter;
        }

        @Nullable
        @Override
        public BakedModel resolve(@Nonnull BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int seed) {
            CompoundTag tag = stack.getTagElement("EntityTag");
            if (tag != null && tag.contains("type")) {
                String beeType = tag.getString("type");

                if (!modelCache.containsKey(beeType)) {
                    CompoundTag nbt = BeeReloadListener.INSTANCE.getData(beeType);
                    if (nbt == null) {
                        // There's a broken honeycomb definition
                        modelCache.put(beeType, model);
                    } else {
                        TextureAtlasSprite sprite;
                        if (nbt.contains("combTexture")) {
                            Material texture = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(nbt.getString("combTexture")));
                            sprite = spriteGetter.apply(texture);
                        } else {
                            sprite = spriteGetter.apply(combModel.base);
                        }

                        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(context, sprite, nested, context.getTransforms());
                        boolean fullBright = combModel.fullBrightLayers.contains(0);

                        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite.contents());
                        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, new ResourceLocation(ProductiveBees.MODID, "base"));

                        builder.addQuads(renderType(fullBright), quads);

                        // Crystal bees have glowing bits on the comb texture
                        if (nbt.contains("renderer") && nbt.getString("renderer").equals("default_crystal")) {
                            TextureAtlasSprite crystalSprite = spriteGetter.apply(combModel.crystal);
                            fullBright = combModel.fullBrightLayers.contains(1);

                            unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, crystalSprite.contents());
                            quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> crystalSprite, modelState, new ResourceLocation(ProductiveBees.MODID, "crystal"));

                            builder.addQuads(renderType(fullBright), quads);
                        }

                        modelCache.put(beeType, builder.build());
                    }
                }
                return modelCache.getOrDefault(beeType, model);
            }
            return model;
        }
    }

    public static class Loader implements IGeometryLoader<CombModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Nonnull
        @Override
        public CombModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            ImmutableSet.Builder<Integer> fullbrightLayers = ImmutableSet.builder();
            if (jsonObject.has("fullbright_layers")) {
                JsonArray arr = GsonHelper.getAsJsonArray(jsonObject, "fullbright_layers");
                for (int i = 0; i < arr.size(); i++) {
                    fullbrightLayers.add(arr.get(i).getAsInt());
                }
            }
            ImmutableList.Builder<String> textures = ImmutableList.builder();
            if (jsonObject.has("textures")) {
                JsonObject arr = GsonHelper.getAsJsonObject(jsonObject, "textures");
                textures.add(arr.get("base").getAsString());
                textures.add(arr.get("crystal").getAsString());
                textures.add(arr.get("particle").getAsString());
            }
            return new CombModel(textures.build(), fullbrightLayers.build());
        }
    }

    private static RenderTypeGroup renderType(boolean isFullbright) {
        return new RenderTypeGroup(RenderType.translucent(), isFullbright ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }
}
