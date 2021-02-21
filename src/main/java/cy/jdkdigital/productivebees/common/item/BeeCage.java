package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BeeCage extends Item
{
    public BeeCage(Properties properties) {
        super(properties);

        this.addPropertyOverride(new ResourceLocation("filled"), (itemStack, world, entity) -> isFilled(itemStack) ? 1.0F : 0.0F);
    }

    public static boolean isFilled(ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.getOrCreateTag().contains("entity");
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World playerWorld = context.getPlayer().getEntityWorld();
        ItemStack stack = context.getItem();

        if (playerWorld.isRemote() || !isFilled(stack)) {
            return ActionResultType.FAIL;
        }

        World worldIn = context.getWorld();
        BlockPos pos = context.getPos();

        BeeEntity entity = getEntityFromStack(stack, worldIn, true);

        entity = BeeHelper.convertToConfigurable(entity);

        if (entity != null) {
            if (context.getPlayer() != null && context.getPlayer().isSneaking()) {
                entity.hivePos = null;
            }

            BlockPos blockPos = pos.offset(context.getFace());
            entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
            worldIn.addEntity(entity);

            postItemUse(context);
        }

        return ActionResultType.SUCCESS;
    }

    protected void postItemUse(ItemUseContext context) {
        // Delete stack
        context.getPlayer().inventory.deleteStack(context.getItem());
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return false;
        }

        BeeEntity target = (BeeEntity) targetIn;

        boolean addToInventory = true;
        ItemStack cageStack = new ItemStack(itemStack.getItem());
        if (itemStack.getCount() == 1) {
            cageStack = itemStack;
            addToInventory = false;
        }

        captureEntity(target, cageStack);

        if (addToInventory || player.isCreative()) {
            if (!player.inventory.addItemStackToInventory(cageStack)) {
                player.dropItem(cageStack, false);
            }
            itemStack.shrink(1);
        }

        player.swingArm(hand);

        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.CATCH_BEE.trigger((ServerPlayerEntity) player, cageStack);
        }
        target.remove(true);

        return true;
    }

    public static ItemStack captureEntity(BeeEntity target, ItemStack cageStack) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getFormattedText());
        } else {
            nbt.putString("name", target.getName().getFormattedText());
        }
        target.writeWithoutTypeId(nbt);

        nbt.putBoolean("isProductiveBee", target instanceof ProductiveBeeEntity);

        String modId = target.getType().getRegistryName().getNamespace();
        String modName = ModList.get().getModObjectById(modId).get().getClass().getSimpleName();

        if (modId.equals("minecraft")) {
            modName = "Minecraft";
        }
        nbt.putString("mod", modName);

        cageStack.setTag(nbt);

        return cageStack;
    }

    @Nullable
    public static BeeEntity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        return getEntityFromStack(stack.getOrCreateTag(), world, withInfo);
    }

    @Nullable
    public static BeeEntity getEntityFromStack(CompoundNBT tag, World world, boolean withInfo) {
        EntityType<?> type = EntityType.byKey(tag.getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            if (withInfo) {
                entity.read(tag);
            }
            if (entity instanceof BeeEntity) {
                return (BeeEntity) entity;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (!isFilled(stack)) {
            return new TranslationTextComponent(this.getTranslationKey());
        }

        String entityId = stack.getTag().getString("name");
        return new TranslationTextComponent(this.getTranslationKey()).appendText(" (" + entityId + ")");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);

        CompoundNBT tag = stack.getTag();
        if (tag != null && !tag.equals(new CompoundNBT())) {
            if (Screen.hasShiftDown()) {
                boolean hasStung = tag.getBoolean("HasStung");
                if (hasStung) {
                    list.add(new TranslationTextComponent("productivebees.information.health.dying").applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.ITALIC));
                }
                BeeHelper.populateBeeInfoFromTag(tag, list);
            } else {
                list.add(new TranslationTextComponent("productivebees.information.hold_shift").applyTextStyle(TextFormatting.WHITE));
            }
        }
    }
}
