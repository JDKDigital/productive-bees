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
import net.minecraftforge.client.model.*;
import net.minecraftforge.client.model.geometry.IModelGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class CombModel implements IModelGeometry<CombModel>
{
    public static final CombModel INSTANCE = new CombModel(ImmutableList.of());

    public ImmutableList<Material> textures;
    public final ImmutableSet<Integer> fullBrightLayers;

    public CombModel(ImmutableList<Material> textures) {
        this(textures, ImmutableSet.of());
    }

    public CombModel(@Nullable ImmutableList<Material> textures, ImmutableSet<Integer> fullBrightLayers) {
        this.textures = textures;
        this.fullBrightLayers = fullBrightLayers;
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform));

        TextureAtlasSprite particle = spriteGetter.apply(
            owner.isTexturePresent("particle") ? owner.resolveTexture("particle") : textures.get(0)
        );

        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, particle, new Overrides(this, overrides, modelTransform, owner, spriteGetter), transformMap);

        return builder.build();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        ImmutableList.Builder<Material> builder = ImmutableList.builder();
        for (int i = 0; owner.isTexturePresent("layer" + i); i++) {
            Material mat = owner.resolveTexture("layer" + i);
            builder.add(mat);
        }
        textures =  builder.build();
        return textures;
    }

    private static BakedModel bakeModel(IModelConfiguration owner, Material texture, Function<Material, TextureAtlasSprite> spriteGetter, ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap, ItemOverrides overrides) {
        TextureAtlasSprite sprite = spriteGetter.apply(texture);
        ImmutableList<BakedQuad> quads = ItemLayerModel.getQuadsForSprite(-1, sprite, Transformation.identity());
        return new BakedItemModel(quads, sprite, transformMap, overrides, true, owner.isSideLit());
    }

    public static class Overrides extends ItemOverrides {
        private final Map<String, BakedModel> modelCache = Maps.newHashMap();
        private final CombModel combModel;
        private final ItemOverrides nested;
        private final ModelState modelTransform;
        private final IModelConfiguration owner;
        private final Function<Material, TextureAtlasSprite> spriteGetter;

        private Overrides(CombModel combModel, ItemOverrides nested, ModelState modelTransform, IModelConfiguration owner, Function<Material, TextureAtlasSprite> spriteGetter)
        {
            this.combModel = combModel;
            this.nested = nested;
            this.modelTransform = modelTransform;
            this.owner = owner;
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
                    ImmutableMap<ItemTransforms.TransformType, Transformation> transformMap = PerspectiveMapWrapper.getTransforms(new CompositeModelState(owner.getCombinedTransform(), modelTransform));

                    if (nbt.contains("combTexture")) {
                        Material texture = ModelLoaderRegistry.blockMaterial(nbt.getString("combTexture"));
                        BakedModel texturedModel = CombModel.bakeModel(owner, texture, spriteGetter, transformMap, nested);
                        modelCache.put(beeType, texturedModel);
                    } else {
                        TextureAtlasSprite baseSprite = spriteGetter.apply(combModel.textures.get(0));

                        ItemMultiLayerBakedModel.Builder builder = ItemMultiLayerBakedModel.builder(owner, baseSprite, nested, transformMap);
                        boolean fullBright = combModel.fullBrightLayers.contains(0);
                        builder.addQuads(ItemLayerModel.getLayerRenderType(fullBright), ItemLayerModel.getQuadsForSprite(0, baseSprite, modelTransform.getRotation(), fullBright));

                        // Crystal bees have glowing bits on the comb texture
                        if (nbt.contains("renderer") && nbt.getString("renderer").equals("default_crystal")) {
                            TextureAtlasSprite crystalSprite = spriteGetter.apply(combModel.textures.get(1));
                            fullBright = combModel.fullBrightLayers.contains(1);
                            builder.addQuads(ItemLayerModel.getLayerRenderType(fullBright), ItemLayerModel.getQuadsForSprite(1, crystalSprite, modelTransform.getRotation(), fullBright));
                        }

                        modelCache.put(beeType, builder.build());
                    }
                }
                return modelCache.getOrDefault(beeType, model);
            }
            return model;
        }
    }

    public static class Loader implements IModelLoader<CombModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Override
        public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {}

        @Nonnull
        @Override
        public CombModel read(@Nonnull JsonDeserializationContext deserializationContext, JsonObject modelContents) {
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
