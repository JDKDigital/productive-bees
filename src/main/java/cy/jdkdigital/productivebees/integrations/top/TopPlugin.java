package cy.jdkdigital.productivebees.integrations.top;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.IRecipeProcessingBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.util.BeeHelper;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TopPlugin implements Function<ITheOneProbe, Void>
{
    MutableComponent formattedName = new TextComponent("Productive Bees").withStyle(ChatFormatting.BLUE).withStyle(ChatFormatting.ITALIC);

    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerEntityDisplayOverride((probeMode, probeInfo, player, level, entity, iProbeHitEntityData) -> {
            if (entity instanceof ProductiveBee bee && probeMode.equals(ProbeMode.EXTENDED)) {
                probeInfo.horizontal()
                        .entity(entity)
                        .vertical()
                        .text(CompoundText.create().name(entity.getName()))
                        .text(CompoundText.create().style(TextStyleClass.MODNAME).text("Productive Bees"));

                List<Component> list =  new ArrayList<>();
                BeeHelper.populateBeeInfoFromEntity(bee, list);
                for (Component component: list) {
                    probeInfo.mcText(component);
                }
                probeInfo.text(formattedName);

                return true;
            }
            return false;
        });

        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            BlockEntity tileEntity = world.getBlockEntity(data.getPos());
            if (tileEntity instanceof SolitaryNestBlockEntity nest) {
                probeInfo.horizontal()
                        .item(new ItemStack(blockState.getBlock().asItem()))
                        .vertical()
                        .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                        .text(CompoundText.create().style(TextStyleClass.MODNAME).text("Productive Bees"));

                List<AdvancedBeehiveBlockEntityAbstract.Inhabitant> bees = nest.getBeeList();
                if (!bees.isEmpty()) {
                    probeInfo.text(new TranslatableComponent("productivebees.top.solitary.bee", bees.get(0).localizedName));
                    if (bees.get(0).minOccupationTicks - bees.get(0).ticksInHive > 0) {
                        probeInfo.progress(Math.max(0, bees.get(0).minOccupationTicks - bees.get(0).ticksInHive), bees.get(0).minOccupationTicks);
                    }
                } else {
                    if (nest.getNestTickCooldown() > 0) {
                        probeInfo.text(new TranslatableComponent("productivebees.top.solitary.repopulation_countdown"));
                        probeInfo.progress(nest.getNestTickCooldown() / 20, ProductiveBeesConfig.GENERAL.nestSpawnCooldown.get() / 20);
                    }
                    else {
                        probeInfo.text(new TranslatableComponent("productivebees.top.solitary.repopulation_countdown_inactive"));
                        if (nest.canRepopulate()) {
                            probeInfo.text(new TranslatableComponent("productivebees.top.solitary.can_repopulate_true"));
                        }
                        else {
                            probeInfo.text(new TranslatableComponent("productivebees.top.solitary.can_repopulate_false"));
                        }
                    }
                }
                return true;
            }

            // Centrifuge
            if (tileEntity instanceof IRecipeProcessingBlockEntity recipeProcessingBlockEntity) {
                if (recipeProcessingBlockEntity.getRecipeProgress() > 0) {
                    probeInfo.horizontal()
                            .item(new ItemStack(blockState.getBlock().asItem()))
                            .vertical()
                            .itemLabel(new ItemStack(blockState.getBlock().asItem()))
                            .progress(recipeProcessingBlockEntity.getRecipeProgress() / 20, recipeProcessingBlockEntity.getProcessingTime() / 20)
                            .text(CompoundText.create().style(TextStyleClass.MODNAME).text("Productive Bees"));
                    return true;
                }
            }

            return false;
        });

        return null;
    }
}