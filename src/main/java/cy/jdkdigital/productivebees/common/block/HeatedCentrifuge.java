package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.HeatedCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HeatedCentrifuge extends PoweredCentrifuge
{
    public HeatedCentrifuge(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, ModTileEntityTypes.HEATED_CENTRIFUGE.get(), HeatedCentrifugeBlockEntity::tick);
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeatedCentrifugeBlockEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack pStack, BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);

        pTooltip.add(new TranslatableComponent("productivebees.heated_centrifuge.tooltip").withStyle(ChatFormatting.GOLD));
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }
}