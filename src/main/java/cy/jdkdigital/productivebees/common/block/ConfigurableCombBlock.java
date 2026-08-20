package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.CombBlockBlockEntity;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CombBlockBlockEntity(pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack pStack) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof CombBlockBlockEntity && pStack.has(ModDataComponents.BEE_TYPE)) {
            ((CombBlockBlockEntity) tileEntity).setCombType(pStack.get(ModDataComponents.BEE_TYPE));
        }
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
        ItemStack stack = new ItemStack(ModItems.CONFIGURABLE_COMB_BLOCK.get());
        if (level.getBlockEntity(pos) instanceof CombBlockBlockEntity combBlockBlockEntity) {
            Identifier type = combBlockBlockEntity.getCombType();
            if (type != null) {
                BeeCreator.setType(type, stack);
            }
        }
        return stack;
    }
}
