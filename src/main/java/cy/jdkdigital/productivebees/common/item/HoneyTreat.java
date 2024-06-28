package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.ColorUtil;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneGroup;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.util.LangUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class HoneyTreat extends Item
{
    public HoneyTreat(Properties properties) {
        super(properties);
    }

    public static boolean hasGene(ItemStack itemStack) {
        var geneGroups = itemStack.get(ModDataComponents.GENE_GROUP_LIST);
        return !itemStack.isEmpty() && geneGroups != null && !geneGroups.isEmpty();
    }

    public static ItemStack getTypeStack(String beeType, int value) {
        ItemStack treat = new ItemStack(ModItems.HONEY_TREAT.get());
        addGene(treat, Gene.getStack(beeType, value));
        return treat;
    }

    public static void addGene(ItemStack stack, ItemStack geneStack) {
        var geneGroups = new ArrayList<>(getGenes(stack));
        var addedGeneGroup = Gene.getGene(geneStack);

        if (addedGeneGroup != null) {
            boolean addedToExistingGene = false;
            var iterator = geneGroups.listIterator();
            while (iterator.hasNext()) {
                GeneGroup geneGroup = iterator.next();
                if (geneGroup.attribute().equals(addedGeneGroup.attribute()) && geneGroup.value().equals(addedGeneGroup.value())) {
                    int purity = geneGroup.purity();
                    purity = Math.min(100, purity + Gene.getPurity(geneStack));
                    iterator.set(GeneGroup.increasePurity(geneGroup, purity));
                    addedToExistingGene = true;
                }
            }

            if (!addedToExistingGene) {
                geneGroups.add(addedGeneGroup);
            }

            stack.set(ModDataComponents.GENE_GROUP_LIST, geneGroups);
        }
    }

    public static List<GeneGroup> getGenes(ItemStack stack) {
        var geneGroups = stack.get(ModDataComponents.GENE_GROUP_LIST);
        return geneGroups != null ? geneGroups : new ArrayList<>();
    }

    public static boolean hasBeeType(ItemStack stack) {
        var genes = getGenes(stack);
        for (GeneGroup geneGroup : genes) {
            if (geneGroup.attribute().equals(GeneAttribute.TYPE)) {
                return true;
            }
        }
        return false;
    }

    public static void applyGenesToBee(Level level, ItemStack treat, Bee bee) {
        var data = bee.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        var geneGroups = getGenes(treat);
        for (GeneGroup geneGroup : geneGroups) {
            int purity = geneGroup.purity();

            if (level.random.nextInt(100) <= purity) {
                data.setAttributeValue(geneGroup.attribute(), GeneValue.byName(geneGroup.value()));
                level.levelEvent(2005, bee.blockPosition(), 0);
            }
        }
        bee.setData(ProductiveBees.ATTRIBUTE_HANDLER, data);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = target.getCommandSenderWorld();
        if (level.isClientSide() || !(target instanceof Bee bee) || !target.isAlive()) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer && bee.isAngry()) {
            ModAdvancements.CALM_BEE.get().trigger((ServerPlayer) player, bee);
        }

        // Stop agro
        bee.setRemainingPersistentAngerTime(0);
        // Allow entering hive
        bee.setStayOutOfHiveCountdown(0);
        // Heal
        bee.heal(bee.getMaxHealth());

        if (bee.isBaby()) {
            bee.ageUp((int) ((float) (-bee.getAge() / 20) * 0.1F), true);
        }

        BlockPos pos = target.blockPosition();
        level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
        bee.playAmbientSound();

        if (bee.hasData(ProductiveBees.ATTRIBUTE_HANDLER) && !hasBeeType(itemStack)) {
            if (hasGene(itemStack)) {
                applyGenesToBee(level, itemStack, bee);
            } else {
                // Improve temper
                var data = bee.getData(ProductiveBees.ATTRIBUTE_HANDLER);
                var temper = data.getAttributeValue(GeneAttribute.TEMPER);
                if (temper.getValue() > 0) {
                    if (player.level().random.nextFloat() < 0.05F) {
                        data.setAttributeValue(GeneAttribute.TEMPER, GeneValue.nextTemper(temper));
                        bee.setData(ProductiveBees.ATTRIBUTE_HANDLER, data);
                    }
                }
            }
            player.swing(hand);
        } else if (hasBeeType(itemStack)) {
            player.sendSystemMessage(Component.translatable(ProductiveBees.MODID + ".honey_treat.invalid_use"));
        }

        itemStack.shrink(1);

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        var geneGroups = pStack.get(ModDataComponents.GENE_GROUP_LIST);
        if (geneGroups != null) {
            geneGroups.forEach(geneGroup -> {
                if (!geneGroup.attribute().equals(GeneAttribute.TYPE)) {
                    Component translatedValue = Component.translatable("productivebees.information.attribute." + geneGroup.value()).withStyle(ColorUtil.getAttributeColor(GeneValue.byName(geneGroup.value())));
                    pTooltipComponents.add((Component.translatable("productivebees.information.attribute." + geneGroup.attribute().getSerializedName(), translatedValue)).withStyle(ChatFormatting.DARK_GRAY).append(" (" + geneGroup.purity() + "%)"));
                } else {
                    pTooltipComponents.add((Component.translatable("productivebees.information.attribute.type", LangUtil.capName(ResourceLocation.parse(geneGroup.value()).getPath()))).withStyle(ChatFormatting.GOLD).append(" (" + geneGroup.purity() + "%)"));
                }
            });
        }
    }
}
