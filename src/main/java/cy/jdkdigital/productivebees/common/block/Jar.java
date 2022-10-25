package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Jar extends Block implements EntityBlock
{
    private static final VoxelShape SHAPE = box(3.5D, 0.0D, 3.5D, 12.5D, 12.0D, 12.5D);

    public Jar(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return 1;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new JarBlockEntity(pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("inv")) {
            CompoundTag invTag = tag.getCompound("BlockEntityTag").getCompound("inv");

            ListTag tagList = invTag.getList("Items", 10);
            if (tagList.size() > 0) {
                CompoundTag itemTag = tagList.getCompound(0);

                ItemStack cage = ItemStack.of(itemTag);

                String entityId = cage.getTag().getString("name");
                tooltip.add(new TranslatableComponent("productivebees.information.jar.bee", entityId));
            } else {
                tooltip.add(new TranslatableComponent("productivebees.information.jar.fill_tip"));
            }
        }
        else {
            tooltip.add(new TranslatableComponent("productivebees.information.jar.fill_tip"));
        }
    }
}
