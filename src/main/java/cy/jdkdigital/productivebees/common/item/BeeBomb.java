package cy.jdkdigital.productivebees.common.item;

import com.mojang.blaze3d.platform.InputConstants;
import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class BeeBomb extends Item
{
    private static final String BEES_KEY = "productivebees_beebomb_bees";
    private final boolean isAngry;

    public BeeBomb(Properties properties, boolean isAngry) {
        super(properties);
        this.isAngry = isAngry;
    }

    public boolean isAngry() {
        return isAngry;
    }

    public static boolean isLoaded(ItemStack itemStack) {
        return getBees(itemStack).size() > 0;
    }

    public static void addBee(ItemStack stack, ItemStack cage) {
        ListTag bees = getBees(stack);

        // TODO: store the caged bee on a data component
//        bees.add(cage.getTag());
//
//        stack.getOrCreateTag().put(BEES_KEY, bees);
    }

    public static ListTag getBees(ItemStack stack) {
//        CompoundTag tag = stack.getTag();
        Tag bees = new ListTag();
//        if (tag != null) {
//            if (tag.get(BEES_KEY) instanceof ListTag) {
//                bees = tag.get(BEES_KEY);
//            }
//        }
        return (ListTag) bees;
    }

    @Nonnull
    @Override
    public InteractionResult use(Level level, Player player, @Nonnull InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide()) {
            BeeBombEntity bombEntity = new BeeBombEntity(level, player);
            bombEntity.setItem(item);
            bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(bombEntity);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getInventory().removeItem(item);

        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, TooltipDisplay tooltipDisplay, Consumer<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltipDisplay, pTooltipComponents, pTooltipFlag);

        ListTag beeList = BeeBomb.getBees(pStack);
        if (!beeList.isEmpty()) {
            var window = Minecraft.getInstance().getWindow();
            if (InputConstants.isKeyDown(window, InputConstants.KEY_LSHIFT) || InputConstants.isKeyDown(window, InputConstants.KEY_RSHIFT)) {
                pTooltipComponents.accept(Component.translatable("productivebees.hive.tooltip.bees").withStyle(ChatFormatting.DARK_AQUA));
                for (Tag bee : beeList) {
                    String beeType = ((CompoundTag) bee).getString("entity").orElse("");
                    if (beeType.startsWith("productivebees:")) {
                        pTooltipComponents.accept(Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(Identifier.parse(beeType)) + "_bee"));
                    } else {
                        pTooltipComponents.accept(Component.literal(beeType));
                    }
                }
            } else {
                pTooltipComponents.accept(Component.translatable("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
