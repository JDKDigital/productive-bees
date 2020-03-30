package cy.jdkdigital.productivebees.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BeeCageItem extends Item {

    public BeeCageItem(Properties properties) {
        super(properties);

        this.addPropertyOverride(new ResourceLocation("filled"), (itemStack, world, entity) -> isFilled(itemStack) ? 1.0F : 0.0F);
    }

    public static boolean isFilled(ItemStack itemStack) {
        return !itemStack.isEmpty() && itemStack.hasTag() && itemStack.getTag().contains("entity");
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

        Entity entity = getEntityFromStack(stack, worldIn, true);
        BlockPos blockPos = pos.offset(context.getFace());
        entity.setPositionAndRotation(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 0, 0);
        worldIn.addEntity(entity);

        // Delete stack
        context.getPlayer().inventory.deleteStack(stack);

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target.getEntityWorld().isRemote() || (!(target instanceof BeeEntity) || !target.isAlive()) || (isFilled(itemStack))) {
            return false;
        }

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getFormattedText());
        } else {
            nbt.putString("name", target.getName().getFormattedText());
        }
        target.writeWithoutTypeId(nbt);

        ItemStack cageStack = new ItemStack(itemStack.getItem());
        cageStack.setTag(nbt);

        itemStack.shrink(1);
        if (itemStack.isEmpty()) {
            player.setHeldItem(hand, cageStack);
        } else if (!player.inventory.addItemStackToInventory(cageStack)) {
            player.dropItem(cageStack, false);
        }

        player.swingArm(hand);

        target.remove(true);

        return true;
    }

    @Nullable
    public Entity getEntityFromStack(ItemStack stack, World world, boolean withInfo) {
        EntityType type = EntityType.byKey(stack.getTag().getString("entity")).orElse(null);
        if (type != null) {
            Entity entity = type.create(world);
            if (withInfo) {
                entity.read(stack.getTag());
            }
            return entity;
        }
        return null;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (!isFilled(stack)) {
            return new TranslationTextComponent(this.getTranslationKey());
        }

        String entityId = stack.getTag().getString("name");
        return new TranslationTextComponent(this.getTranslationKey()).appendText(" (" + entityId + ")");
    }
}
