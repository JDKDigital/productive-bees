package cy.jdkdigital.productivebees.common.item;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.client.gui.screen.Screen;
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
import net.minecraft.util.text.StringTextComponent;
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

    public static void addGene(ItemStack stack, ItemStack gene) {
        ListNBT genes = getGenes(stack);

        boolean addedToExistingGene = false;
        for (INBT inbt: genes) {
            int purity = ((CompoundNBT) inbt).getInt("purity");
            ItemStack insertedGene = ItemStack.read((CompoundNBT) inbt);
            if (Gene.getAttributeName(insertedGene).equals(Gene.getAttributeName(gene)) && Gene.getValue(insertedGene).equals(Gene.getValue(gene))) {
                purity = Math.min(100, purity + Gene.getPurity(gene));
                ((CompoundNBT) inbt).putInt("purity", purity);
                addedToExistingGene = true;
            }
        };

        if (!addedToExistingGene) {
            CompoundNBT serializedGene = gene.serializeNBT();
            serializedGene.putInt("purity", Gene.getPurity(gene));
            genes.add(serializedGene);
        }

        stack.getOrCreateTag().put(GENES_KEY, genes);
    }

    public static ListNBT getGenes(ItemStack stack) {
        INBT genes = stack.getOrCreateTag().get(GENES_KEY);
        if (!(genes instanceof ListNBT)) {
            genes = new ListNBT();
        }
        return (ListNBT) genes;
    }

    @Override
    public ActionResultType itemInteractionForEntity(ItemStack itemStack, PlayerEntity player, LivingEntity target, Hand hand) {
        if (target.getEntityWorld().isRemote() || !(target instanceof BeeEntity) || !target.isAlive()) {
            return ActionResultType.PASS;
        }

        BeeEntity bee = (BeeEntity) target;

        if (player instanceof ServerPlayerEntity && bee.isAngry()) {
            ModAdvancements.CALM_BEE.trigger((ServerPlayerEntity) player, bee);
        }

        // Stop agro
        bee.setAngerTime(0);
        // Allow entering hive
        bee.setStayOutOfHiveCountdown(0);
        // Heal
        bee.heal(bee.getMaxHealth());

        if (bee.isChild()) {
            bee.ageUp((int) ((float) (-bee.getGrowingAge() / 20) * 0.1F), true);
        }

        itemStack.shrink(1);

        BlockPos pos = target.getPosition();
        target.getEntityWorld().addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);

        // Improve temper
        if (bee instanceof ProductiveBeeEntity) {
            ProductiveBeeEntity productiveBee = (ProductiveBeeEntity) target;
            ListNBT genes = getGenes(itemStack);
            if (!genes.isEmpty()) {
                // Apply genes from honey treat
                for (INBT inbt: genes) {
                    int purity = ((CompoundNBT) inbt).getInt("purity");
                    ItemStack insertedGene = ItemStack.read((CompoundNBT) inbt);
                    if (ProductiveBees.rand.nextInt(100) <= purity) {
                        productiveBee.setAttributeValue(Gene.getAttribute(insertedGene), Gene.getValue(insertedGene));
                    }
                };
            } else {
                int temper = productiveBee.getAttributeValue(BeeAttributes.TEMPER);
                if (temper > 0) {
                    if (player.world.rand.nextFloat() < 0.05F) {
                        productiveBee.getBeeAttributes().put(BeeAttributes.TEMPER, --temper);
                    }
                }
            }
        }

        return ActionResultType.CONSUME;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);

        CompoundNBT tag = stack.getTag();
        if (tag != null) {
            INBT genes = stack.getOrCreateTag().get(GENES_KEY);
            if (genes instanceof ListNBT) {
                if (Screen.hasShiftDown()) {
                    ((ListNBT) genes).forEach(inbt -> {
                        int purity = ((CompoundNBT) inbt).getInt("purity");
                        ItemStack insertedGene = ItemStack.read((CompoundNBT) inbt);

                        Integer value = Gene.getValue(insertedGene);

                        ITextComponent translated_value = new TranslationTextComponent(BeeAttributes.keyMap.get(Gene.getAttribute(insertedGene)).get(value)).mergeStyle(BeeCage.getColor(value));
                        list.add(
                            (new TranslationTextComponent("productivebees.information.attribute." + Gene.getAttributeName(insertedGene), translated_value)).mergeStyle(TextFormatting.DARK_GRAY).append(new StringTextComponent(" (" + purity + "%)"))
                        );
                    });
                }
                else {
                    list.add(new TranslationTextComponent("productivebees.information.hold_shift").mergeStyle(TextFormatting.WHITE));
                }
            }
        }
    }
}
