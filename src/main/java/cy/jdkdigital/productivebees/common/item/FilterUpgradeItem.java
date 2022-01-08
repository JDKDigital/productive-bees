package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FilterUpgradeItem extends UpgradeItem
{
    private static final String KEY = "productivebees_allowed_bees";

    public FilterUpgradeItem(Properties properties) {
        super(properties);
    }

    public static void addAllowedBee(ItemStack stack, Bee bee) {
        // Add bee type to filter
        String type = BeeIngredientFactory.getIngredientKey(bee);
        if (type != null) {
            CompoundTag tag = stack.getOrCreateTag();

            Tag list = tag.get(KEY);
            if (!(list instanceof ListTag)) {
                list = new ListTag();
            }

            for (Tag inbtType: (ListTag) list) {
                if (inbtType.getAsString().equals(type)) {
                    return;
                }
            }

            ((ListTag) list).add(StringTag.valueOf(type));

            tag.put(KEY, list);

            stack.setTag(tag);
        }
    }

    public static List<Supplier<BeeIngredient>> getAllowedBees(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        List<Supplier<BeeIngredient>> beeList = new ArrayList<>();
        if (tag != null && tag.contains(KEY)) {
            ListTag list = (ListTag) tag.get(KEY);

            if (list != null) {
                for (Tag type: list) {
                    beeList.add(BeeIngredientFactory.getIngredient(type.getAsString()));
                }
            }
        }
        return beeList;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, world, tooltip, flagIn);
        List<Supplier<BeeIngredient>> beeList = getAllowedBees(stack);

        for (Supplier<BeeIngredient> allowedBee: beeList) {
            tooltip.add(new TranslatableComponent("productivebees.information.upgrade.upgrade_filter_entity", allowedBee.get().getBeeType()).withStyle(ChatFormatting.GOLD));
        }
        
        if (beeList.isEmpty()) {
            tooltip.add(new TranslatableComponent("productivebees.information.upgrade.upgrade_filter_empty").withStyle(ChatFormatting.WHITE));
        } else {
            tooltip.add(new TranslatableComponent("productivebees.information.upgrade.upgrade_filter").withStyle(ChatFormatting.WHITE));
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity targetIn, InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide() || !(targetIn instanceof Bee)) {
            return InteractionResult.PASS;
        }

        addAllowedBee(itemStack, (Bee) targetIn);

        return InteractionResult.SUCCESS;
    }
}
