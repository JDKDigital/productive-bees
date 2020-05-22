package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DragonEggHiveTileEntity extends AdvancedBeehiveTileEntity
{
    private int tickCounter = 0;
    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> ItemHandlerHelper.getInventoryHandler(this, 1));

    public DragonEggHiveTileEntity() {
        super(ModTileEntityTypes.DRACONIC_BEEHIVE.get());
        MAX_BEES = 3;
    }

    @Override
    public void tick() {
        final World world = this.world;
        if (world == null || world.isRemote()) {
            return;
        }

        if (++tickCounter > ProductiveBeesConfig.GENERAL.itemTickRate.get()) {
            tickCounter = 0;

            ListNBT beeList = this.getBeeListAsNBTList();
            if (beeList.size() > 0) {
                for (INBT inbt : beeList) {
                    CompoundNBT inb = (CompoundNBT) ((CompoundNBT) inbt).get("EntityData");
                    String beeId = inb.getString("id");

                    Double productionChance = ProductiveBeeEntity.getProductionChance(beeId, 0.25D);

                    // Generate bee produce
                    if (productionChance != null && productionChance > 0) {
                        if (world.rand.nextDouble() <= productionChance) {
                            final int productivity = inb.contains("bee_productivity") ? inb.getInt("bee_productivity") : 0;
                            inventoryHandler.ifPresent(inv -> {
                                BeeHelper.getBeeProduce(world, beeId).forEach((stack) -> {
                                    if (!stack.isEmpty()) {
                                        if (productivity > 0) {
                                            float f = (float) productivity * stack.getCount() * BeeAttributes.productivityModifier.generateFloat(world.rand);
                                            stack.grow(Math.round(f));
                                        }
                                        ((ItemHandlerHelper.ItemHandler) inv).addOutput(stack);
                                    }
                                });
                            });
                        }
                    }
                }
            }
        }

        if (tickCounter % 23 == 0) {
            BlockState blockState = this.getBlockState();

            if (blockState.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    int finalHoneyLevel = honeyLevel;
                    inventoryHandler.ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(ItemHandlerHelper.BOTTLE_SLOT);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = world.getDimension().getType() == DimensionType.THE_END ? new ItemStack(Items.DRAGON_BREATH) : new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((ItemHandlerHelper.ItemHandler) inv).addOutput(filledBottle);
                            if (addedBottle) {
                                bottles.shrink(1);
                                world.setBlockState(pos, blockState.with(BeehiveBlock.HONEY_LEVEL, finalHoneyLevel - 5));
                            }
                        }
                    });
                }
            }
        }

        super.tick();
    }

    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);
        // Generate item?
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        inventoryHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        inventoryHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        return tag;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
