package cy.jdkdigital.productivebees.common.block;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class Amber extends BaseEntityBlock
{
    public static final MapCodec<Amber> CODEC = simpleCodec(Amber::new);

    public Amber(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModBlockEntityTypes.AMBER.get(), AmberBlockEntity::serverTick);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) {
        state.add(HorizontalDirectionalBlock.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AmberBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity player, @Nonnull ItemStack stack) {
        // Read data from stack
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (!level.isClientSide() && tileEntity instanceof AmberBlockEntity amberBlockEntity && stack.has(DataComponents.ENTITY_DATA)) {
            CompoundTag tag = stack.get(DataComponents.ENTITY_DATA).copyTag();
            amberBlockEntity.loadPacketNBT(tag, level.registryAccess());
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(ModBlocks.AMBER.get());
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AmberBlockEntity) {
            try {
                CompoundTag tag = new CompoundTag();
                tag.put("BlockEntityTag", blockEntity.saveWithoutMetadata(level.registryAccess()));
                stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
            } catch (Exception e) {
                // Crash can happen here if the server is shutting down as the client (WAILA) is trying to read the data
            }
        }
        return stack;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        if (below.is(BlockTags.CAMPFIRES)) {
            for(int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.trySpawnDripParticles(level, pos, state);
            }
        }
    }

    private void trySpawnDripParticles(Level level, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && !(level.random.nextFloat() < 0.3F)) {
            VoxelShape voxelshape = state.getCollisionShape(level, pos);
            double d0 = voxelshape.max(Direction.Axis.Y);
            if (d0 >= 1.0D && !state.is(BlockTags.IMPERMEABLE)) {
                double d1 = voxelshape.min(Direction.Axis.Y);
                if (d1 > 0.0D) {
                    this.spawnParticle(level, pos, voxelshape, (double)pos.getY() + d1 - 0.05D);
                } else {
                    BlockPos blockpos = pos.below();
                    BlockState blockstate = level.getBlockState(blockpos);
                    VoxelShape voxelshape1 = blockstate.getCollisionShape(level, blockpos);
                    double d2 = voxelshape1.max(Direction.Axis.Y);
                    if ((d2 < 1.0D || !blockstate.isCollisionShapeFullBlock(level, blockpos)) && blockstate.getFluidState().isEmpty()) {
                        this.spawnParticle(level, pos, voxelshape, (double)pos.getY() - 0.05D);
                    }
                }
            }
        }
    }
    private void spawnParticle(Level level, BlockPos pos, VoxelShape voxelShape, double y) {
        this.spawnFluidParticle(level, (double)pos.getX() + voxelShape.min(Direction.Axis.X), (double)pos.getX() + voxelShape.max(Direction.Axis.X), (double)pos.getZ() + voxelShape.min(Direction.Axis.Z), (double)pos.getZ() + voxelShape.max(Direction.Axis.Z), y);
    }

    private void spawnFluidParticle(Level level, double xMin, double xMax, double zMin, double zMax, double y) {
        level.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(level.random.nextDouble(), xMin, xMax), y, Mth.lerp(level.random.nextDouble(), zMin, zMax), 0.0D, 0.0D, 0.0D);
    }
}
