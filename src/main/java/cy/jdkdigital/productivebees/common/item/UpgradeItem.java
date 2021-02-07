package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.tileentity.UpgradeableTileEntity;
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, world, tooltip, flagIn);

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

        tooltip.add(new TranslationTextComponent("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).mergeStyle(TextFormatting.GOLD));
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote && context.getPlayer() != null && context.getPlayer().isSneaking()) {
            if (context.getItem().getItem() instanceof UpgradeItem) {
                TileEntity tileEntity = world.getTileEntity(context.getPos());
                if (tileEntity instanceof UpgradeableTileEntity && ((UpgradeableTileEntity) tileEntity).acceptsUpgrades()) {
                    AtomicBoolean hasInsertedUpgrade = new AtomicBoolean(false);
                    ((UpgradeableTileEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
                        for (int slot = 0; slot < handler.getSlots(); ++slot) {
                            if (handler.getStackInSlot(slot).equals(ItemStack.EMPTY)) {
                                handler.insertItem(slot, context.getItem().copy(), false);
                                hasInsertedUpgrade.set(true);
                                break;
                            }
                        }
                    });
                    if (hasInsertedUpgrade.get()) {
                        if (!context.getPlayer().isCreative()) {
                            context.getItem().shrink(1);
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return super.onItemUse(context);
    }
}
