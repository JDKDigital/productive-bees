package cy.jdkdigital.productivebees.common.block;

import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
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
    public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTootipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTootipComponents, pTooltipFlag);
        if (pStack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            CompoundTag tag = pStack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
            if (tag.contains("BlockEntityTag") && tag.getCompound("BlockEntityTag").contains("inv")) {
                CompoundTag invTag = tag.getCompound("BlockEntityTag").getCompound("inv");

                ListTag tagList = invTag.getList("Items", 10);
                if (tagList.size() > 0) {
                    CompoundTag itemTag = tagList.getCompound(0);

                    ItemStack cage = ItemStack.parse(pContext.registries(), itemTag).get();
                    if (BeeCage.isFilled(cage)) {
                        String entityId = cage.get(DataComponents.CUSTOM_DATA).copyTag().getString("name");
                        pTootipComponents.add(Component.translatable("productivebees.information.jar.bee", entityId));
                    }
                } else {
                    pTootipComponents.add(Component.translatable("productivebees.information.jar.fill_tip"));
                }
            }
        } else {
            pTootipComponents.add(Component.translatable("productivebees.information.jar.fill_tip"));
        }
    }
}
