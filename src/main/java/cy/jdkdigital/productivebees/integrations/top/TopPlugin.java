package cy.jdkdigital.productivebees.integrations.top;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class TopPlugin implements Function<ITheOneProbe, Void>
{
    MutableComponent formattedName = Component.literal("Productive Bees").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC);

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            BlockEntity tileEntity = world.getBlockEntity(data.getPos());
            if (tileEntity instanceof SolitaryNestBlockEntity nest) {
                probeInfo.horizontal()
                        .item(new ItemStack(blockState.getBlock().asItem()))
                        .vertical()
                        .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                        .text(formattedName);

                List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> bees = nest.getBeeList();
                if (!bees.isEmpty()) {
                    probeInfo.text(Component.translatable("productivebees.top.solitary.bee", bees.get(0).localizedName));
                    if (bees.get(0).minOccupationTicks - bees.get(0).ticksInHive > 0) {
                        probeInfo.progress(Math.max(0, bees.get(0).minOccupationTicks - bees.get(0).ticksInHive), bees.get(0).minOccupationTicks);
                    }
                } else {
                    if (nest.getNestTickCooldown() > 0) {
                        probeInfo.text(Component.translatable("productivebees.top.solitary.repopulation_countdown"));
                        probeInfo.progress(nest.getNestTickCooldown() / 20, ProductiveBeesConfig.GENERAL.nestSpawnCooldown.get() / 20);
                    }
                    else {
                        probeInfo.text(Component.translatable("productivebees.top.solitary.repopulation_countdown_inactive"));
                        if (nest.canRepopulate()) {
                            probeInfo.text(Component.translatable("productivebees.top.solitary.can_repopulate_true"));
                        }
                        else {
                            probeInfo.text(Component.translatable("productivebees.top.solitary.can_repopulate_false"));
                        }
                    }
                }
                return true;
            }

            // Centrifuge
            if (tileEntity instanceof CentrifugeBlockEntity centrifugeTileEntity) {
                if (centrifugeTileEntity.recipeProgress > 0) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .progress((int) Math.floor(centrifugeTileEntity.recipeProgress), centrifugeTileEntity.getProcessingTime())
                            .text(formattedName);
                    return true;
                }
            }

            ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
            if (registryName != null && registryName.getNamespace().equals(ProductiveBees.MODID)) {
                probeInfo.horizontal()
                        .item(new ItemStack(blockState.getBlock().asItem()))
                        .vertical()
                        .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                        .text(formattedName);
                return true;
            }
            return false;
        });

        return null;
    }
}