package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.tileentity.UpgradeableTileEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpgradeItem extends Item
{
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
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

        tooltip.add(new TranslationTextComponent("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).withStyle(TextFormatting.GOLD));
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        if (!world.isClientSide && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (context.getItemInHand().getItem() instanceof UpgradeItem) {
                TileEntity tileEntity = world.getBlockEntity(context.getClickedPos());
                if (tileEntity instanceof UpgradeableTileEntity && ((UpgradeableTileEntity) tileEntity).acceptsUpgrades()) {
                    AtomicBoolean hasInsertedUpgrade = new AtomicBoolean(false);
                    ((UpgradeableTileEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
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
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return super.useOn(context);
    }
}
