package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

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

        // TODO 1.21 reimplement with data components
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @Nonnull InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
        if (!level.isClientSide) {
            BeeBombEntity bombEntity = new BeeBombEntity(level, player);
            bombEntity.setItem(item);
            bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(bombEntity);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getInventory().removeItem(item);

        return InteractionResultHolder.sidedSuccess(item, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        ListTag beeList = BeeBomb.getBees(pStack);
        if (!beeList.isEmpty()) {
            if (Screen.hasShiftDown()) {
                pTooltipComponents.add(Component.translatable("productivebees.hive.tooltip.bees").withStyle(ChatFormatting.DARK_AQUA));
                for (Tag bee : beeList) {
                    String beeType = ((CompoundTag) bee).getString("entity");
                    if (beeType.startsWith("productivebees:")) {
                        pTooltipComponents.add(Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(ResourceLocation.parse(beeType)) + "_bee"));
                    } else {
                        pTooltipComponents.add(Component.literal(beeType));
                    }
                }
            } else {
                pTooltipComponents.add(Component.translatable("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
