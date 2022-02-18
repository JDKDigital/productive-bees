package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public abstract class AdvancedBeehiveAbstract extends BaseEntityBlock
{
    public AdvancedBeehiveAbstract(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModTileEntityTypes.ADVANCED_BEEHIVE.get(), AdvancedBeehiveBlockEntity::tick);
    }

    public int getMaxHoneyLevel() {
        return 5;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        return te != null ? te.getCapability(CapabilityBee.BEE).map(b -> b.getInhabitants().size()).orElse(0) : 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundTag entityNBT = stack.getTagElement("BlockEntityTag");
        CompoundTag stateNBT = stack.getTagElement("BlockStateTag");
        if (stateNBT != null) {
            if (stateNBT.contains("honey_level")) {
                String honeyLevel = stateNBT.getString("honey_level");
                tooltip.add(new TranslatableComponent("productivebees.hive.tooltip.honey_level", honeyLevel).withStyle(ChatFormatting.GOLD)); // mergeStyle
            }
        }
        if (entityNBT != null) {
            if (entityNBT.contains("Bees")) {
                ListTag beeList = entityNBT.getCompound("Bees").getList("Inhabitants", 10);
                if (beeList.size() > 0) {
                    tooltip.add(new TranslatableComponent("productivebees.hive.tooltip.bees").withStyle(ChatFormatting.BOLD));
                    for (int i = 0; i < beeList.size(); ++i) {
                        CompoundTag tag = beeList.getCompound(i);
                        String name = tag.getString("Name");

                        tooltip.add(new TextComponent(name).withStyle(ChatFormatting.GREEN));
                    }
                } else {
                    tooltip.add(new TranslatableComponent("productivebees.hive.tooltip.empty"));
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (state.hasProperty(BeehiveBlock.HONEY_LEVEL) && state.getValue(BeehiveBlock.HONEY_LEVEL) >= getMaxHoneyLevel()) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                this.dripHoney(world, pos, state);
            }
        }
    }

    private void dripHoney(Level world, BlockPos pos, BlockState state) {
        if (state.getFluidState().isEmpty() && world.random.nextFloat() >= 0.3F) {
            VoxelShape shape = state.getCollisionShape(world, pos);
            double shapeEnd = shape.max(Direction.Axis.Y);
            if (shapeEnd >= 1.0D && !BlockTags.IMPERMEABLE.contains(state.getBlock())) {
                double shapeStart = shape.min(Direction.Axis.Y);
                if (shapeStart > 0.0D) {
                    spawnParticle(world, pos, shape, (double) pos.getY() + shapeStart - 0.05D);
                } else {
                    BlockPos posDown = pos.below();
                    BlockState stateDown = world.getBlockState(posDown);
                    VoxelShape shapeDown = stateDown.getCollisionShape(world, posDown);
                    double shapeDownEnd = shapeDown.max(Direction.Axis.Y);
                    if ((shapeDownEnd < 1.0D || !stateDown.isCollisionShapeFullBlock(world, posDown)) && stateDown.getFluidState().isEmpty()) {
                        spawnParticle(world, pos, shape, (double) pos.getY() - 0.05D);
                    }
                }
            }
        }
    }

    private static void spawnParticle(Level level, BlockPos pos, VoxelShape shape, double p_226880_4_) {
        spawnFluidParticle(level, (double) pos.getX() + shape.min(Direction.Axis.X), (double) pos.getX() + shape.max(Direction.Axis.X), (double) pos.getZ() + shape.min(Direction.Axis.Z), (double) pos.getZ() + shape.max(Direction.Axis.Z), p_226880_4_);
    }

    private static void spawnFluidParticle(Level level, double d1, double d2, double d3, double d4, double d5) {
        level.addParticle(ParticleTypes.DRIPPING_HONEY, Mth.lerp(level.random.nextDouble(), d1, d2), d5, Mth.lerp(level.random.nextDouble(), d3, d4), 0.0D, 0.0D, 0.0D);
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack heldItem = player.getMainHandItem();
        if (level instanceof ServerLevel && heldItem.getItem().equals(Items.STICK)) {
            BlockEntity tileEntity = level.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveBlockEntityAbstract beehiveTileEntity) {
                beehiveTileEntity.emptyAllLivingFromHive(player, state, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            }
        }
        super.attack(state, level, pos, player);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && player.isCreative() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveBlockEntityAbstract beehiveTileEntity) {
                int honeyLevel = 0;
                if (state.hasProperty(BeehiveBlock.HONEY_LEVEL)) {
                    honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);
                }
                boolean hasBees = !beehiveTileEntity.isEmpty();
                if (!hasBees && honeyLevel == 0) {
                    return;
                }

                ItemStack itemStack = new ItemStack(this);
                CompoundTag compoundNBT = new CompoundTag();
                if (hasBees) {
                    CompoundTag nbt = new CompoundTag();
                    nbt.put("Inhabitants", AdvancedBeehiveBlockEntityAbstract.getBeeListAsNBTList(beehiveTileEntity));
                    compoundNBT.put("Bees", nbt);
                }

                compoundNBT.putInt("honey_level", honeyLevel);
                itemStack.addTagElement("BlockStateTag", compoundNBT);
                ItemEntity hiveEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                hiveEntity.setDefaultPickUpDelay();
                world.addFreshEntity(hiveEntity);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity entity = builder.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (entity instanceof PrimedTnt || entity instanceof Creeper || entity instanceof WitherSkull || entity instanceof WitherBoss || entity instanceof MinecartTNT) {
            BlockEntity tileEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (tileEntity instanceof AdvancedBeehiveBlockEntityAbstract beehiveTileEntity) {
                beehiveTileEntity.emptyAllLivingFromHive(null, state, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            }
        }

        return super.getDrops(state, builder);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor world, BlockPos pos, BlockPos fireBlockPos) {
        if (world.getBlockState(fireBlockPos).getBlock() instanceof FireBlock) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof AdvancedBeehiveBlockEntityAbstract beehiveTileEntity) {
                beehiveTileEntity.emptyAllLivingFromHive(null, state, BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
            }
        }

        return super.updateShape(state, direction, state1, world, pos, fireBlockPos);
    }
}
