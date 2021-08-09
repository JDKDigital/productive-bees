package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpgradeItem extends Item
{
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);

        if (stack.getItem().equals(ModItems.UPGRADE_FILTER.get())) {
            return;
        }

        String upgradeType = stack.getItem().getRegistryName().getPath();

        double value = 0.0F;
        switch (upgradeType) {
            case "upgrade_productivity":
                value = ProductiveBeesConfig.UPGRADES.productivityMultiplier.get() - 1d;
                break;
            case "upgrade_breeding":
                value = ProductiveBeesConfig.UPGRADES.breedingChance.get();
                break;
            case "upgrade_time":
                value = ProductiveBeesConfig.UPGRADES.timeBonus.get();
                break;
            case "upgrade_comb_block":
                value = ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
                break;
        }

        tooltip.add(new TranslatableComponent("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).withStyle(ChatFormatting.GOLD));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (context.getItemInHand().getItem() instanceof UpgradeItem) {
                BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
                if (tileEntity instanceof UpgradeableBlockEntity && ((UpgradeableBlockEntity) tileEntity).acceptsUpgrades()) {
                    AtomicBoolean hasInsertedUpgrade = new AtomicBoolean(false);
                    ((UpgradeableBlockEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
                        for (int slot = 0; slot < handler.getSlots(); ++slot) {
                            if (handler.getStackInSlot(slot).equals(ItemStack.EMPTY)) {
                                handler.insertItem(slot, context.getItemInHand().copy(), false);
                                hasInsertedUpgrade.set(true);
                                break;
                            }
                        }
                    });
                    if (hasInsertedUpgrade.get()) {
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useOn(context);
    }
}
