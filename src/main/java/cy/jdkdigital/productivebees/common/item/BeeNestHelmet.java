package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.model.BeeNestHelmetModel;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.IItemRenderProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class BeeNestHelmet extends ArmorItem
{
    public BeeNestHelmet(ArmorMaterial material, EquipmentSlot slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, level, list, flag);

        list.add(new TranslatableComponent("productivebees.information.bee_helmet.info1").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(new TranslatableComponent("productivebees.information.bee_helmet.info2").withStyle(ChatFormatting.LIGHT_PURPLE));
        list.add(new TranslatableComponent("productivebees.information.bee_helmet.info3", 100 * ProductiveBeesConfig.BEES.kamikazBeeChance.get()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }

//    @Nullable
//    @Override
//    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
//        return ProductiveBees.MODID + ":textures/armor/bee_nest_diamond.png";
//    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        super.onArmorTick(stack, level, player);

        BlockPos pos = player.blockPosition();
        if (level.getRandom().nextDouble() < 0.005D && !ProductiveBeesConfig.CLIENT.mutedBeeNestHelmet.get()) {
            level.playSound(player, pos.getX(), pos.getY() + 2D, pos.getZ(), level.random.nextBoolean() ? SoundEvents.BEEHIVE_WORK : SoundEvents.BEEHIVE_DRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        if (level.getRandom().nextDouble() < 0.015D) {
            level.addParticle(ParticleTypes.FALLING_NECTAR, Mth.lerp(level.random.nextDouble(), pos.getX() - 0.5D, pos.getX() + 0.5D), pos.getY() + 1.8D, Mth.lerp(level.random.nextDouble(), pos.getZ() - 0.5D, pos.getZ() + 0.5D), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties()
        {
            @Override
            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
            return BeeNestHelmetModel.INSTANCE.get();
            }
        });
    }
}
