package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FilterUpgradeItem extends UpgradeItem
{
    public FilterUpgradeItem(Properties properties) {
        super(properties);
    }

    public static void addAllowedBee(ItemStack stack, Bee bee) {
        // Add bee type to filter
        var type = ResourceLocation.parse(BeeIngredientFactory.getIngredientKey(bee));
        List<ResourceLocation> tag = stack.getOrDefault(ModDataComponents.BEE_TYPE_LIST, new ArrayList<>());

        if (!tag.contains(type)) {
            tag.add(type);

            stack.set(ModDataComponents.BEE_TYPE_LIST, tag);
        }
    }

    public static List<Supplier<BeeIngredient>> getAllowedBees(ItemStack stack) {
        List<ResourceLocation> bees = stack.getOrDefault(ModDataComponents.BEE_TYPE_LIST, new ArrayList<>());

        return bees.stream().map(resourceLocation -> BeeIngredientFactory.getIngredient(resourceLocation.toString())).toList();
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);
        List<Supplier<BeeIngredient>> beeList = getAllowedBees(pStack);

        for (Supplier<BeeIngredient> allowedBee: beeList) {
            pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.upgrade_filter_entity", allowedBee.get().getBeeType().toString()).withStyle(ChatFormatting.GOLD));
        }
        
        if (beeList.isEmpty()) {
            pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.upgrade_filter_empty").withStyle(ChatFormatting.WHITE));
        } else {
            pTooltipComponents.add(Component.translatable("productivebees.information.upgrade.upgrade_filter").withStyle(ChatFormatting.WHITE));
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity targetIn, InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || !(targetIn instanceof Bee)) {
            return InteractionResult.PASS;
        }

        addAllowedBee(itemStack, (Bee) targetIn);

        player.setItemInHand(hand, itemStack);

        return InteractionResult.SUCCESS;
    }
}
