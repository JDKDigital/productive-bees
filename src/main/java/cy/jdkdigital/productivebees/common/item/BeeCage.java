package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeAttributes;
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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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

            // Delete stack
            context.getPlayer().inventory.deleteStack(stack);
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity targetIn, Hand hand) {
        if (targetIn.getEntityWorld().isRemote() || (!(targetIn instanceof BeeEntity) || !targetIn.isAlive()) || (isFilled(itemStack))) {
            return false;
        }

        BeeEntity target = (BeeEntity) targetIn;

        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("entity", EntityType.getKey(target.getType()).toString());
        if (target.hasCustomName()) {
            nbt.putString("name", target.getCustomName().getFormattedText());
        }
        else {
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

        ItemStack cageStack = new ItemStack(itemStack.getItem());
        cageStack.setTag(nbt);

        if (!player.inventory.addItemStackToInventory(cageStack)) {
            player.dropItem(cageStack, false);
        }

        player.swingArm(hand);

        if (player instanceof ServerPlayerEntity) {
            ModAdvancements.CATCH_BEE.trigger((ServerPlayerEntity) player, cageStack);
        }

        itemStack.shrink(1);
        target.remove(true);

        return true;
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
        if (tag != null) {
            if (Screen.hasShiftDown()) {
                boolean hasStung = tag.getBoolean("HasStung");
                if (hasStung) {
                    list.add(new TranslationTextComponent("productivebees.information.health.dying").applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.ITALIC));
                }

                list.add(new TranslationTextComponent(tag.getInt("Age") < 0 ? "productivebees.information.age.child" : "productivebees.information.age.adult").applyTextStyle(TextFormatting.AQUA).applyTextStyle(TextFormatting.ITALIC));

                if (tag.getBoolean("isProductiveBee")) {
                    String type = tag.getString("bee_type");
                    ITextComponent type_value = new TranslationTextComponent("productivebees.information.attribute.type." + type).applyTextStyle(getColor(type));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.type", type_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    int productivity = tag.getInt("bee_productivity");
                    ITextComponent productivity_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.PRODUCTIVITY).get(productivity)).applyTextStyle(getColor(productivity));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.productivity", productivity_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    int tolerance = tag.getInt("bee_weather_tolerance");
                    ITextComponent tolerance_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.WEATHER_TOLERANCE).get(tolerance)).applyTextStyle(getColor(tolerance));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.weather_tolerance", tolerance_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    int behavior = tag.getInt("bee_behavior");
                    ITextComponent behavior_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.BEHAVIOR).get(behavior)).applyTextStyle(getColor(behavior));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.behavior", behavior_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    int endurance = tag.getInt("bee_endurance");
                    ITextComponent endurance_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.ENDURANCE).get(endurance)).applyTextStyle(getColor(endurance));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.endurance", endurance_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    int temper = tag.getInt("bee_temper");
                    ITextComponent temper_value = new TranslationTextComponent(BeeAttributes.keyMap.get(BeeAttributes.TEMPER).get(temper)).applyTextStyle(getColor(temper));
                    list.add((new TranslationTextComponent("productivebees.information.attribute.temper", temper_value)).applyTextStyle(TextFormatting.DARK_GRAY));

                    if (tag.contains("HivePos")) {
                        BlockPos hivePos = NBTUtil.readBlockPos(tag.getCompound("HivePos"));
                        list.add(new StringTextComponent("Home position: " + hivePos.getX() + ", " + hivePos.getY() + ", " + hivePos.getZ()));
                    }
                } else {
                    list.add((new StringTextComponent("Mod: " + tag.getString("mod"))).applyTextStyle(TextFormatting.DARK_AQUA));
                }
            } else {
                list.add(new TranslationTextComponent("productivebees.information.hold_shift").applyTextStyle(TextFormatting.WHITE));
            }
        }
    }

    public static TextFormatting getColor(String type) {
        switch (type) {
            case "hive":
                return TextFormatting.YELLOW;
            case "solitary":
                return TextFormatting.GRAY;
        }
        return TextFormatting.WHITE;
    }

    public static TextFormatting getColor(int level) {
        switch (level) {
            case -3:
                return TextFormatting.RED;
            case -2:
                return TextFormatting.DARK_RED;
            case -1:
                return TextFormatting.YELLOW;
            case 1:
                return TextFormatting.GREEN;
            case 2:
                return TextFormatting.BLUE;
            case 3:
                return TextFormatting.GOLD;
        }
        return TextFormatting.LIGHT_PURPLE;
    }
}
