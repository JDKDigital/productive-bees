package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.CombBlockBlockEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConfigurableCombBlock extends CombBlock implements EntityBlock
{
    public ConfigurableCombBlock(Properties properties, String colorCode) {
        super(properties, colorCode);
    }

    @Override
    public int getColor(BlockAndTintGetter world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof CombBlockBlockEntity) {
                return ((CombBlockBlockEntity) tileEntity).getColor();
            }
        }
        return getColor();
    }

    @Override
    public void fillItemCategory(@Nonnull CreativeModeTab group, @Nonnull NonNullList<ItemStack> items) {
        if (!this.equals(ForgeRegistries.BLOCKS.getValue(this.getRegistryName()))) {
            super.fillItemCategory(group, items);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CombBlockBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CombBlockBlockEntity) {
            CompoundTag tag = stack.getTagElement("EntityTag");
            if (tag != null && tag.contains("type")) {
                ((CombBlockBlockEntity) tileEntity).setType(tag.getString("type"));
            }
        }
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof CombBlockBlockEntity) {
            String type = ((CombBlockBlockEntity) tileEntity).getCombType();
            if (type != null) {
                BeeCreator.setTag(type, stack);
            }
        }
        return stack;
    }
}
