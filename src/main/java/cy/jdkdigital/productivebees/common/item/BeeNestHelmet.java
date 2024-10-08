package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.model.BeeNestHelmetModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

public class BeeNestHelmet extends ArmorItem
{
    public BeeNestHelmet(Holder<ArmorMaterial> pMaterial, Item.Properties pProperties) {
        super(pMaterial, Type.HELMET, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        pTooltipComponents.add(Component.translatable("productivebees.information.bee_helmet.info1").withStyle(ChatFormatting.DARK_PURPLE));
        pTooltipComponents.add(Component.translatable("productivebees.information.bee_helmet.info2").withStyle(ChatFormatting.LIGHT_PURPLE));
        pTooltipComponents.add(Component.translatable("productivebees.information.bee_helmet.info3", 100 * ProductiveBeesConfig.BEES.kamikazBeeChance.get()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        // 36, 37, 38 and 39
        if (pLevel.isClientSide() && pSlotId >= 36 && pSlotId <= 39) {
            BlockPos pos = pEntity.blockPosition();
            if (pLevel.getRandom().nextDouble() < 0.005D && !ProductiveBeesConfig.CLIENT.mutedBeeNestHelmet.get() && pEntity instanceof Player player) {
                pLevel.playSound(player, pos.getX(), pos.getY() + 2D, pos.getZ(), pLevel.random.nextBoolean() ? SoundEvents.BEEHIVE_WORK : SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            if (pLevel.getRandom().nextDouble() < 0.015D) {
                pLevel.addParticle(ParticleTypes.FALLING_NECTAR, Mth.lerp(pLevel.random.nextDouble(), pos.getX() - 0.5D, pos.getX() + 0.5D), pos.getY() + 1.8D, Mth.lerp(pLevel.random.nextDouble(), pos.getZ() - 0.5D, pos.getZ() + 0.5D), 0.0D, 0.0D, 0.0D);
            }
        }
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions()
        {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                return BeeNestHelmetModel.INSTANCE.get();
            }
        });
    }
}
