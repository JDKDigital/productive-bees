package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.tileentity.SolitaryNestTileEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.recipe.BeeSpawningBigRecipe;
import cy.jdkdigital.productivebees.recipe.BeeSpawningRecipe;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolitaryNest extends AdvancedBeehiveAbstract
{
    List<BeeSpawningRecipe> recipes = new ArrayList<>();

    public SolitaryNest(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.NORTH));
    }

    public int getMaxHoneyLevel() {
        return 0;
    }

    public Entity getNestingBeeType(World world, Biome biome) {
        List<BeeSpawningRecipe> spawningRecipes = getSpawningRecipes(world, biome);
        if (!spawningRecipes.isEmpty()) {
            BeeSpawningRecipe spawningRecipe = spawningRecipes.get(ProductiveBees.rand.nextInt(spawningRecipes.size()));
            BeeIngredient beeIngredient = spawningRecipe.output.get(world.random.nextInt(spawningRecipe.output.size())).get();
            Entity bee = beeIngredient.getBeeEntity().create(world);
            if (bee instanceof ConfigurableBeeEntity) {
                ((ConfigurableBeeEntity) bee).setBeeType(beeIngredient.getBeeType().toString());
                ((ConfigurableBeeEntity) bee).setAttributes();
            }
            return bee;
        }
        return null;
    }

    public List<BeeSpawningRecipe> getSpawningRecipes(World world, Biome biome) {
        // Get and cache recipes for nest type
        if (recipes.isEmpty()) {
            Map<ResourceLocation, IRecipe<IInventory>> allRecipes = new HashMap<>();
            allRecipes.putAll(world.getRecipeManager().byType(BeeSpawningBigRecipe.BEE_SPAWNING));
            allRecipes.putAll(world.getRecipeManager().byType(BeeSpawningRecipe.BEE_SPAWNING));
            ItemStack nestItem = new ItemStack(ForgeRegistries.ITEMS.getValue(this.getRegistryName()));
            for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : allRecipes.entrySet()) {
                BeeSpawningRecipe recipe = (BeeSpawningRecipe) entry.getValue();
                if (recipe.matches(nestItem)) {
                    recipes.add(recipe);
                }
            }
        }

        List<BeeSpawningRecipe> spawningRecipes = new ArrayList<>();
        if (!recipes.isEmpty()) {
            for (BeeSpawningRecipe recipe : recipes) {
                if (
                        (recipe.biomes.isEmpty() && world.dimension() == World.OVERWORLD) || recipe.biomes.contains(biome.getBiomeCategory().getName())
                ) {
                    spawningRecipes.add(recipe);
                }
            }
        }
        return spawningRecipes;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
        return new SolitaryNestTileEntity();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext itemUseContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, itemUseContext.getNearestLookingDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.FACING, rotation.rotate(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    public boolean canRepopulateIn(World world, Biome biome) {
        return !getSpawningRecipes(world, biome).isEmpty();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide()) {
            SolitaryNestTileEntity tileEntity = (SolitaryNestTileEntity) world.getBlockEntity(pos);

            ItemStack heldItem = player.getItemInHand(hand);
            if (tileEntity != null && heldItem.getItem().equals(ModItems.HONEY_TREAT.get())) {
                boolean itemUse = false;
                int currentCooldown = tileEntity.getNestTickCooldown();
                if (currentCooldown <= 0) {
                    if (tileEntity.canRepopulate()) {
                        tileEntity.setNestCooldown(ProductiveBeesConfig.GENERAL.nestSpawnCooldown.get());
                        itemUse = true;
                    }
                } else {
                    tileEntity.setNestCooldown((int) (currentCooldown * 0.9));
                    itemUse = true;
                }

                if (itemUse) {
                    world.levelEvent(2005, pos, 0);
                    CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayerEntity) player, pos, heldItem);
                }

                if (!player.isCreative()) {
                    heldItem.shrink(1);
                }
            }
        }

        return super.use(state, world, pos, player, hand, hit);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        if (stack.hasTag() && stack.getTag().getInt("spawnCount") >= ProductiveBeesConfig.BEES.cuckooSpawnCount.get()) {
            tooltip.add(new TranslationTextComponent("productivebees.hive.tooltip.nest_inactive").withStyle(TextFormatting.BOLD));
        }
    }
}
