package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.UpgradeableBlockEntity;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

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

        String upgradeType = ForgeRegistries.ITEMS.getKey(stack.getItem()).getPath();

        double value = switch (upgradeType) {
            case "upgrade_productivity" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier.get();
            case "upgrade_breeding" -> ProductiveBeesConfig.UPGRADES.breedingChance.get();
            case "upgrade_time" -> ProductiveBeesConfig.UPGRADES.timeBonus.get();
            default -> 0.0F;
        };

        tooltip.add(Component.translatable("productivebees.information.upgrade." + upgradeType, (int) (value * 100)).withStyle(ChatFormatting.GOLD));
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
