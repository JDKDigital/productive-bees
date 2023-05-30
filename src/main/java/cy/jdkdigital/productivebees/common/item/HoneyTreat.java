package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;
import java.util.List;

public class HoneyTreat extends Item
{
    private static final String GENES_KEY = "productivebees_gene_stack";

    public HoneyTreat(Properties properties) {
        super(properties);
    }

    public static boolean hasGene(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        return !itemStack.isEmpty() && tag != null && tag.contains(GENES_KEY);
    }

    public static ItemStack getTypeStack(String beeType, int value) {
        ItemStack treat = new ItemStack(ModItems.HONEY_TREAT.get());
        addGene(treat, Gene.getStack(beeType, value));
        return treat;
    }

    public static void addGene(ItemStack stack, ItemStack gene) {
        ListTag genes = getGenes(stack);

        boolean addedToExistingGene = false;
        for (Tag inbt : genes) {
            ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
            int purity = Gene.getPurity(insertedGene);
            if (Gene.getAttributeName(insertedGene).equals(Gene.getAttributeName(gene)) && Gene.getValue(insertedGene).equals(Gene.getValue(gene))) {
                purity = Math.min(100, purity + Gene.getPurity(gene));
                Gene.setPurity(insertedGene, purity);
                addedToExistingGene = true;
            }
        }

        if (!addedToExistingGene) {
            CompoundTag serializedGene = gene.serializeNBT();
            genes.add(serializedGene);
        }

        stack.getOrCreateTag().put(GENES_KEY, genes);
    }

    public static ListTag getGenes(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        Tag genes = new ListTag();
        if (tag != null) {
            if (tag.get(GENES_KEY) instanceof ListTag) {
                genes = tag.get(GENES_KEY);
            }
        }
        return (ListTag) genes;
    }

    public static boolean hasBeeType(ItemStack stack) {
        ListTag genes = getGenes(stack);
        for (Tag inbt : genes) {
            ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
            BeeAttribute<?> existingAttribute = Gene.getAttribute(insertedGene);
            if (existingAttribute == null && !Gene.getAttributeName(insertedGene).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity target, InteractionHand hand) {
        Level level = target.getCommandSenderWorld();
        if (level.isClientSide() || !(target instanceof Bee bee) || !target.isAlive()) {
            return InteractionResult.PASS;
        }

        if (player instanceof ServerPlayer && bee.isAngry()) {
            ModAdvancements.CALM_BEE.trigger((ServerPlayer) player, bee);
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

        itemStack.shrink(1);

        BlockPos pos = target.blockPosition();
        level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
        bee.playAmbientSound();

        if (bee instanceof ProductiveBee) {
            ProductiveBee productiveBee = (ProductiveBee) target;
            ListTag genes = getGenes(itemStack);
            if (!genes.isEmpty()) {
                // Apply genes from honey treat
                for (Tag inbt : genes) {
                    ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
                    int purity = Gene.getPurity(insertedGene);
                    if (((CompoundTag) inbt).contains("purity")) {
                        purity = ((CompoundTag) inbt).getInt("purity");
                    }
                    if (ProductiveBees.random.nextInt(100) <= purity) {
                        productiveBee.setAttributeValue(Gene.getAttribute(insertedGene), Gene.getValue(insertedGene));
                        level.levelEvent(2005, pos, 0);
                    }
                }
            } else {
                // Improve temper
                int temper = productiveBee.getAttributeValue(BeeAttributes.TEMPER);
                if (temper > 0) {
                    if (player.level.random.nextFloat() < 0.05F) {
                        productiveBee.getBeeAttributes().put(BeeAttributes.TEMPER, --temper);
                    }
                }
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundTag tag = stack.getTag();
        if (tag != null) {
            Tag genes = tag.get(GENES_KEY);
            if (genes instanceof ListTag) {
                ((ListTag) genes).forEach(inbt -> {
                    ItemStack insertedGene = ItemStack.of((CompoundTag) inbt);
                    int purity = Gene.getPurity(insertedGene);
                    if (((CompoundTag) inbt).contains("purity")) {
                        purity = ((CompoundTag) inbt).getInt("purity");
                    }

                    Integer value = Gene.getValue(insertedGene);
                    BeeAttribute<?> attribute = Gene.getAttribute(insertedGene);
                    if (BeeAttributes.keyMap.containsKey(attribute)) {
                        Component translatedValue = Component.translatable(BeeAttributes.keyMap.get(attribute).get(value)).withStyle(ColorUtil.getAttributeColor(value));
                        list.add((Component.translatable("productivebees.information.attribute." + Gene.getAttributeName(insertedGene), translatedValue)).withStyle(ChatFormatting.DARK_GRAY).append(" (" + purity + "%)"));
                    } else {
                        list.add((Component.translatable("productivebees.information.attribute.type", Gene.getAttributeName(insertedGene))).withStyle(ChatFormatting.GOLD).append(" (" + purity + "%)"));
                    }
                });
            }
        }
    }
}
