package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TreatOnAStick extends Item
{
    private final int consumeItemDamage;

    public TreatOnAStick(Item.Properties properties, int consumeItemDamage) {
        super(properties);
        this.consumeItemDamage = consumeItemDamage;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            Entity entity = player.getVehicle();
            if (player.isPassenger() && entity instanceof ItemSteerable itemsteerable && entity.getType().equals(ModEntities.BUMBLE.get())) {
                if (itemsteerable.boost()) {
                    itemstack.hurtAndBreak(this.consumeItemDamage, serverPlayer, LivingEntity.getSlotForHand(hand));
//                    if (itemstack.isEmpty()) {
//                        ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
//                        itemstack1.setTag(itemstack.getTag());
//                        return InteractionResultHolder.success(itemstack1);
//                    }

                    return InteractionResultHolder.success(itemstack);
                }
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResultHolder.pass(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }
}