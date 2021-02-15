package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.tileentity.CombBlockTileEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigurableCombBlock extends CombBlock
{
    public ConfigurableCombBlock(Properties properties, String colorCode) {
        super(properties, colorCode);
    }

    @Override
    public int getColor(IBlockDisplayReader world, BlockPos pos) {
        if (world != null && pos != null) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof CombBlockTileEntity) {
                return ((CombBlockTileEntity) tileEntity).getColor();
            }
        }
        return getColor();
    }

    @Override
    public void fillItemGroup(@Nonnull ItemGroup group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ForgeRegistries.BLOCKS.getValue(this.getRegistryName()))) {
            super.fillItemGroup(group, items);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CombBlockTileEntity();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CombBlockTileEntity) {
            CompoundNBT tag = stack.getChildTag("EntityTag");
            if (tag != null && tag.contains("type")) {
                ((CombBlockTileEntity) tileEntity).setType(tag.getString("type"));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof CombBlockTileEntity) {
            String type = ((CombBlockTileEntity) tileEntity).getCombType();
            if (type != null) {
                BeeCreator.setTag(type, stack);
            }
        }
        return stack;
    }
}
