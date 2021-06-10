package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Jar extends Block
{
    private static final VoxelShape SHAPE = box(3.5D, 0.0D, 3.5D, 12.5D, 12.0D, 12.5D);

    public Jar(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.JAR.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("inv")) {
            CompoundNBT invTag = tag.getCompound("inv");

            ListNBT tagList = invTag.getList("Items", Constants.NBT.TAG_COMPOUND);
            if (tagList.size() > 0) {
                CompoundNBT itemTag = tagList.getCompound(0);

                ItemStack cage = ItemStack.of(itemTag);

                String entityId = cage.getTag().getString("name");
                tooltip.add(new TranslationTextComponent("productivebees.information.jar.bee", entityId));
            }
            else {
                tooltip.add(new TranslationTextComponent("productivebees.information.jar.fill_tip"));
            }
        }
        else {
            tooltip.add(new TranslationTextComponent("productivebees.information.jar.fill_tip"));
        }
    }
}
