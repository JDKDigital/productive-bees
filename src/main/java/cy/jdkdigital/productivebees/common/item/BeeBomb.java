package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeeBomb extends Item
{
    private static final String BEES_KEY = "productivebees_beebomb_bees";

    public BeeBomb(Properties properties) {
        super(properties);
    }

    public static boolean isLoaded(ItemStack itemStack) {
        return getBees(itemStack).size() > 0;
    }

    public static void addBee(ItemStack stack, ItemStack cage) {
        ListTag bees = getBees(stack);

        bees.add(cage.getTag());

        stack.getOrCreateTag().put(BEES_KEY, bees);
    }

    public static ListTag getBees(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        Tag bees = new ListTag();
        if (tag != null) {
            if (tag.get(BEES_KEY) instanceof ListTag) {
                bees = tag.get(BEES_KEY);
            }
        }
        return (ListTag) bees;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
        ItemStack item = player.getItemInHand(hand);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        if (!world.isClientSide) {
            BeeBombEntity bombEntity = new BeeBombEntity(world, player);
            bombEntity.setItem(item);
            bombEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(bombEntity);
        }

        player.getInventory().removeItem(item);

        return InteractionResultHolder.success(item);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        ListTag beeList = BeeBomb.getBees(stack);
        if (!beeList.isEmpty()) {
            if (Screen.hasShiftDown()) {
                list.add(new TranslatableComponent("productivebees.hive.tooltip.bees").withStyle(ChatFormatting.DARK_AQUA));
                for (Tag bee : beeList) {
                    String beeType = ((CompoundTag) bee).getString("entity");
                    list.add(new TextComponent(beeType).withStyle(ChatFormatting.GOLD));
                }
            } else {
                list.add(new TranslatableComponent("productivebees.information.hold_shift").withStyle(ChatFormatting.WHITE));
            }
        }
    }
}
