package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.ColorUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class HoneyTreat extends Item
{
    private static final String GENES_KEY = "productivebees_gene_stack";

    public HoneyTreat(Properties properties) {
        super(properties);
    }

    public static boolean hasGene(ItemStack itemStack) {
        CompoundNBT tag = itemStack.getTag();
        return !itemStack.isEmpty() && tag != null && tag.contains(GENES_KEY);
    }

    public static ItemStack getTypeStack(String beeType, int value) {
        ItemStack treat = new ItemStack(ModItems.HONEY_TREAT.get());
        addGene(treat, Gene.getStack(beeType, value));
        return treat;
    }

    public static void addGene(ItemStack stack, ItemStack gene) {
        ListNBT genes = getGenes(stack);

        boolean addedToExistingGene = false;
        for (INBT inbt : genes) {
            ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
            int purity = Gene.getPurity(insertedGene);
            if (Gene.getAttributeName(insertedGene).equals(Gene.getAttributeName(gene)) && Gene.getValue(insertedGene).equals(Gene.getValue(gene))) {
                purity = Math.min(100, purity + Gene.getPurity(gene));
                Gene.setPurity(insertedGene, purity);
                addedToExistingGene = true;
            }
        }

        if (!addedToExistingGene) {
            CompoundNBT serializedGene = gene.serializeNBT();
            genes.add(serializedGene);
        }

        stack.getOrCreateTag().put(GENES_KEY, genes);
    }

    public static ListNBT getGenes(ItemStack stack) {
        CompoundNBT tag = stack.getTag();
        INBT genes = new ListNBT();
        if (tag != null) {
            if (tag.get(GENES_KEY) instanceof ListNBT) {
                genes = tag.get(GENES_KEY);
            }
        }
        return (ListNBT) genes;
    }

    public static boolean hasBeeType(ItemStack stack) {
        ListNBT genes = getGenes(stack);
        for (INBT inbt : genes) {
            ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
            BeeAttribute<?> existingAttribute = Gene.getAttribute(insertedGene);
            if (existingAttribute == null && !Gene.getAttributeName(insertedGene).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ActionResultType interactLivingEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        World world = target.getCommandSenderWorld();
        if (target.getCommandSenderWorld().isClientSide() || !(target instanceof BeeEntity) || !target.isAlive()) {
            return ActionResultType.PASS;
        }

        BeeEntity bee = (BeeEntity) target;

        if (player instanceof ServerPlayerEntity && bee.isAngry()) {
            ModAdvancements.CALM_BEE.trigger((ServerPlayerEntity) player, bee);
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
        world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);

        if (bee instanceof ProductiveBeeEntity) {
            ProductiveBeeEntity productiveBee = (ProductiveBeeEntity) target;
            ListNBT genes = getGenes(itemStack);
            if (!genes.isEmpty()) {
                // Apply genes from honey treat
                for (INBT inbt : genes) {
                    ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
                    int purity = Gene.getPurity(insertedGene);
                    if (((CompoundNBT) inbt).contains("purity")) {
                        purity = ((CompoundNBT) inbt).getInt("purity");
                    }
                    if (ProductiveBees.rand.nextInt(100) <= purity) {
                        productiveBee.setAttributeValue(Gene.getAttribute(insertedGene), Gene.getValue(insertedGene));
                        world.levelEvent(2005, pos, 0);
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

        return ActionResultType.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.appendHoverText(stack, world, list, flag);

        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            INBT genes = tag.get(GENES_KEY);
            if (genes instanceof ListNBT) {
                ((ListNBT) genes).forEach(inbt -> {
                    ItemStack insertedGene = ItemStack.of((CompoundNBT) inbt);
                    int purity = Gene.getPurity(insertedGene);
                    if (((CompoundNBT) inbt).contains("purity")) {
                        purity = ((CompoundNBT) inbt).getInt("purity");
                    }

                    Integer value = Gene.getValue(insertedGene);
                    BeeAttribute<?> attribute = Gene.getAttribute(insertedGene);
                    if (BeeAttributes.keyMap.containsKey(attribute)) {
                        ITextComponent translatedValue = new TranslationTextComponent(BeeAttributes.keyMap.get(attribute).get(value)).withStyle(ColorUtil.getColor(value));
                        list.add((new TranslationTextComponent("productivebees.information.attribute." + Gene.getAttributeName(insertedGene), translatedValue)).withStyle(TextFormatting.DARK_GRAY).append(" (" + purity + "%)"));
                    } else {
                        list.add((new TranslationTextComponent("productivebees.information.attribute.type", Gene.getAttributeName(insertedGene))).withStyle(TextFormatting.DARK_GRAY).append(" (" + purity + "%)"));
                    }
                });
            }
        }
    }
}
