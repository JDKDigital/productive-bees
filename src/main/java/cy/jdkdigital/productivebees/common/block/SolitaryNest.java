package cy.jdkdigital.productivebees.common.block;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.item.HoneyTreat;
import cy.jdkdigital.productivebees.common.recipe.BeeSpawningRecipe;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SolitaryNest extends AdvancedBeehiveAbstract
{
    public static final MapCodec<SolitaryNest> CODEC = simpleCodec(SolitaryNest::new);
    public static Supplier<Map<Block, Block>> BLOCK_TO_NEST = Suppliers.memoize(() -> ImmutableMap.<Block, Block>builder()
            .put(Blocks.GRASS_BLOCK, ModBlocks.BUMBLE_BEE_NEST.get())
            .put(Blocks.OAK_LOG, ModBlocks.OAK_WOOD_NEST.get())
            .put(Blocks.BIRCH_LOG, ModBlocks.BIRCH_WOOD_NEST.get())
            .put(Blocks.SPRUCE_LOG, ModBlocks.SPRUCE_WOOD_NEST.get())
            .put(Blocks.ACACIA_LOG, ModBlocks.ACACIA_WOOD_NEST.get())
            .put(Blocks.DARK_OAK_LOG, ModBlocks.DARK_OAK_WOOD_NEST.get())
            .put(Blocks.JUNGLE_LOG, ModBlocks.JUNGLE_WOOD_NEST.get())
            .put(Blocks.MANGROVE_LOG, ModBlocks.MANGROVE_WOOD_NEST.get())
            .put(Blocks.CHERRY_LOG, ModBlocks.CHERRY_WOOD_NEST.get())
            .put(Blocks.GLOWSTONE, ModBlocks.GLOWSTONE_NEST.get())
            .put(Blocks.NETHER_QUARTZ_ORE, ModBlocks.NETHER_QUARTZ_NEST.get())
            .put(Blocks.NETHER_BRICKS, ModBlocks.NETHER_BRICK_NEST.get())
            .put(Blocks.NETHER_GOLD_ORE, ModBlocks.NETHER_GOLD_NEST.get())
            .put(Blocks.SOUL_SAND, ModBlocks.SOUL_SAND_NEST.get())
            .put(Blocks.SOUL_SOIL, ModBlocks.SOUL_SAND_NEST.get())
            .put(Blocks.END_STONE, ModBlocks.END_NEST.get())
            .put(Blocks.OBSIDIAN, ModBlocks.OBSIDIAN_PILLAR_NEST.get())
            .put(Blocks.SLIME_BLOCK, ModBlocks.SLIMY_NEST.get())
            .put(Blocks.SUGAR_CANE, ModBlocks.SUGAR_CANE_NEST.get())
            .put(Blocks.DIRT, ModBlocks.COARSE_DIRT_NEST.get())
            .put(Blocks.STONE, ModBlocks.STONE_NEST.get())
            .put(Blocks.SAND, ModBlocks.SAND_NEST.get())
            .put(Blocks.SNOW_BLOCK, ModBlocks.SNOW_NEST.get())
            .put(Blocks.GRAVEL, ModBlocks.GRAVEL_NEST.get())
        .build());
    static Map<String, List<RecipeHolder<BeeSpawningRecipe>>> recipes = new HashMap<>();

    public SolitaryNest(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.SOLITARY_NEST.get(), SolitaryNestBlockEntity::tick);
    }

    public int getMaxHoneyLevel() {
        return 0;
    }

    public static Entity getNestingBeeType(SolitaryNest block, Level level, Holder<Biome> biome, RandomSource random) {
        List<RecipeHolder<BeeSpawningRecipe>> spawningRecipes = getSpawningRecipes(block, level, biome, ItemStack.EMPTY);
        if (!spawningRecipes.isEmpty()) {
            RecipeHolder<BeeSpawningRecipe> spawningRecipe = spawningRecipes.get(random.nextInt(spawningRecipes.size()));
            BeeIngredient beeIngredient = spawningRecipe.value().output.get(random.nextInt(spawningRecipe.value().output.size())).get();
            if (beeIngredient != null) {
                Entity bee = beeIngredient.getBeeEntity().create(level);
                if (bee instanceof ConfigurableBee) {
                    ((ConfigurableBee) bee).setBeeType(beeIngredient.getBeeType().toString());
                    ((ConfigurableBee) bee).setDefaultAttributes();
                }
                return bee;
            } else {
                ProductiveBees.LOGGER.debug("No bee ingredient found in " + spawningRecipe);
            }
        }
        return null;
    }

    public static List<RecipeHolder<BeeSpawningRecipe>> getSpawningRecipes(SolitaryNest block, Level level, Holder<Biome> biome, ItemStack heldItem) {
        List<RecipeHolder<BeeSpawningRecipe>> spawningRecipes = new ArrayList<>();
        String cacheKey = BuiltInRegistries.ITEM.getKey(heldItem.getItem()) + "_" + BuiltInRegistries.BLOCK.getKey(block) + "_" + level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome.value());
        // Get and cache recipes for nest type
        if (!recipes.containsKey(cacheKey)) {
            List<RecipeHolder<BeeSpawningRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.BEE_SPAWNING_TYPE.get());
            ItemStack nestItem = new ItemStack(block);
            for (RecipeHolder<BeeSpawningRecipe> entry : allRecipes) {
                BeeSpawningRecipe recipe = entry.value();
                if (recipe.matches(nestItem, heldItem, biome)) {
                    spawningRecipes.add(entry);
                }
            }
            recipes.put(cacheKey, spawningRecipes);
        } else {
            spawningRecipes = recipes.get(cacheKey);
        }

        return spawningRecipes;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolitaryNestBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext itemUseContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, itemUseContext.getNearestLookingDirection().getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            SolitaryNestBlockEntity tileEntity = (SolitaryNestBlockEntity) pLevel.getBlockEntity(pPos);

            if (tileEntity != null && !pStack.isEmpty()) {
                if (pStack.getItem() instanceof HoneyTreat && HoneyTreat.hasGene(pStack)) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }

                boolean itemUse = false;
                int currentCooldown = tileEntity.getNestTickCooldown();
                if (tileEntity.canRepopulate(pStack)) {
                    itemUse = true;
                    if (currentCooldown <= 0) {
                        tileEntity.setNestCooldown(ProductiveBeesConfig.GENERAL.nestSpawnCooldown.get());
                    } else {
                        tileEntity.setNestCooldown((int) (currentCooldown * 0.9));
                    }
                }

                if (itemUse) {
                    pLevel.levelEvent(2005, pPos, 0);
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer) pPlayer, pPos, pStack);

                    if (!pPlayer.isCreative()) {
                        pStack.shrink(1);
                    }
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }

        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Override
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
        if (pStack.has(DataComponents.BLOCK_ENTITY_DATA) && pStack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe().getInt("spawnCount") >= ProductiveBeesConfig.BEES.cuckooSpawnCount.get()) {
            pTootipComponents.add(Component.translatable("productivebees.hive.tooltip.nest_inactive").withStyle(ChatFormatting.BOLD));
        }
    }
}
