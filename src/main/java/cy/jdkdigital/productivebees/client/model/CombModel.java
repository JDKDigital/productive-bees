package cy.jdkdigital.productivebees.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class CombModel implements IModelGeometry<CombModel>
{
    public static final CombModel INSTANCE = new CombModel(ImmutableList.of());

    private ImmutableList<Material> textures;
    private final ImmutableSet<Integer> fullbrightLayers;

    public CombModel(ImmutableList<Material> textures) {
        this(textures, ImmutableSet.of());
    }

    public CombModel(@Nullable ImmutableList<Material> textures, ImmutableSet<Integer> fullbrightLayers) {
        this.textures = textures;
        this.fullbrightLayers = fullbrightLayers;
    }

    private static ImmutableList<Material> getTextures(IModelConfiguration model) {
        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for (int i = 0; model.isTexturePresent("layer" + i); i++) {
            builder.add(model.resolveTexture("layer" + i));
        }
        return builder.build();
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform));

        Transformation transform = modelTransform.getRotation();
        TextureAtlasSprite particle = spriteGetter.apply(
            owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : textures.get(0)
        );

        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particle, new Overrides(overrides, bakery, owner, spriteGetter), transformMap);

        for (int i = 0; i < textures.size(); i++) {
            TextureAtlasSprite tas = spriteGetter.apply(textures.get(i));
            boolean fullBright = fullbrightLayers.contains(i);
            RenderType rt = getLayerRenderType(fullBright);
            builder.addQuads(rt, ItemLayerModel.getQuadsForSprite(i, tas, transform, fullBright));
        }

        return builder.build();
    }

    public static RenderType getLayerRenderType(boolean isFullbright) {
        return isFullbright ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        textures = getTextures(owner);
        return textures;
    }

    public static class Overrides extends ItemOverrides {
        private final Map<String, BakedModel> modelCache = Maps.newHashMap();
        private final ItemOverrides nested;
        private final ModelBakery bakery;
        private final IModelConfiguration owner;
        private Function<Material, TextureAtlasSprite> spriteGetter;

        private Overrides(ItemOverrides nested, ModelBakery bakery, IModelConfiguration owner, Function<Material, TextureAtlasSprite> spriteGetter)
        {
            this.nested = nested;
            this.bakery = bakery;
            this.owner = owner;
            this.spriteGetter = spriteGetter;
        }

        @Nullable
        @Override
        public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int seed) {
            CompoundTag tag = stack.getTagElement("EntityTag");
            if (tag != null && tag.contains("type")) {
                String beeType = tag.getString("type");
                CompoundTag nbt = BeeReloadListener.INSTANCE.getData(beeType);
                if (nbt.contains("beeTexture")) {
                    if (!modelCache.containsKey(beeType)) {
                        Material texture = ModelLoaderRegistry.blockMaterial(nbt.getString("beeTexture"));
                        BakedModel texturedModel = CombModel.bakeModel(owner, texture, spriteGetter, nested);
                        modelCache.put(beeType, texturedModel);
                    }
                    return modelCache.getOrDefault(beeType, model);
                }
            }
            return model;
        }
    }

    private static BakedModel bakeModel(IModelConfiguration owner, Material texture, Function<Material, TextureAtlasSprite> spriteGetter, ItemOverrides overrides) {
        TextureAtlasSprite sprite = spriteGetter.apply(texture);
        ImmutableList<BakedQuad> quads = ItemLayerModel.getQuadsForSprite(-1, sprite, Transformation.identity());
        return new BakedItemModel(quads, sprite, ImmutableMap.of(), overrides, true, owner.isSideLit());
    }

    public static class Loader implements IModelLoader<CombModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            // nothing to do
        }

        @Override
        public CombModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            ImmutableSet.Builder<Integer> fullbrightLayers = ImmutableSet.builder();
            if (modelContents.has("fullbright_layers")) {
                JsonArray arr = GsonHelper.getAsJsonArray(modelContents, "fullbright_layers");
                for (int i = 0; i < arr.size(); i++) {
                    fullbrightLayers.add(arr.get(i).getAsInt());
                }
            }
            return new CombModel(null, fullbrightLayers.build());
        }
    }
}
