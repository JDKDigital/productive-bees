package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.BeeBombEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
        ListNBT bees = getBees(stack);

        bees.add(cage.getTag());

        stack.getOrCreateTag().put(BEES_KEY, bees);
    }

    public static ListNBT getBees(ItemStack stack) {
        INBT bees = stack.getOrCreateTag().get(BEES_KEY);
        if (!(bees instanceof ListNBT)) {
            bees = new ListNBT();
        }
        return (ListNBT) bees;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, @Nonnull Hand hand) {
        ItemStack item = player.getHeldItem(hand);
        world.playSound(null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!world.isRemote) {
            BeeBombEntity bombEntity = new BeeBombEntity(world, player);
            bombEntity.setItem(item);
            bombEntity.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, 1.5F, 1.0F);
            world.addEntity(bombEntity);
        }

        player.inventory.deleteStack(item);

        return ActionResult.resultSuccess(item);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);

        ListNBT beeList = BeeBomb.getBees(stack);
        if (!beeList.isEmpty()) {
            if (Screen.hasShiftDown()) {
                list.add(new TranslationTextComponent("productivebees.hive.tooltip.bees").mergeStyle(TextFormatting.DARK_AQUA));
                for (INBT bee : beeList) {
                    String beeType = ((CompoundNBT) bee).getString("entity");
                    list.add(new StringTextComponent(beeType).mergeStyle(TextFormatting.GOLD));
                }
            } else {
                list.add(new TranslationTextComponent("productivebees.information.hold_shift").mergeStyle(TextFormatting.WHITE));
            }
        }
    }
}
