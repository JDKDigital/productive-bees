package cy.jdkdigital.productivebees.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AdvancedBeehiveTileEntity extends AdvancedBeehiveTileEntityAbstract implements INamedContainerProvider
{
    protected int tickCounter = 0;
    private int abandonCountdown = 0;
    protected boolean hasTicked = false;

    // Used for displaying bees in gui
    public List<String> inhabitantList = new ArrayList<>();

    protected LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> InventoryHandlerHelper.getInventoryHandler(this, 1));

    public AdvancedBeehiveTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public AdvancedBeehiveTileEntity() {
        this(ModTileEntityTypes.ADVANCED_BEEHIVE.get());
        MAX_BEES = 3;
    }

    /**
     * @return The logical-server-side Container for this TileEntity
     */
    @Nonnull
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new AdvancedBeehiveContainer(windowId, playerInventory, this);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(this.getBlockState().getBlock().getTranslationKey());
    }

    public boolean isSmoked() {
        return true;
    }

    @Override
    public void tick() {
        final World world = this.world;
        if (world == null || world.isRemote()) {
            return;
        }

        if (!hasTicked && ++tickCounter > ProductiveBeesConfig.GENERAL.itemTickRate.get()) {
            tickCounter = 0;

            productionTick();

            // Spawn skeletal and zombie bees in available hives
            ListNBT beeList = this.getBeeListAsNBTList();
            if (
                world.isNightTime() &&
                ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                world.rand.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                beeList.size() + beesOutsideHive() < MAX_BEES &&
                world.getLight(pos.offset(getBlockState().get(BlockStateProperties.FACING), 1)) <= 8
            ) {
                EntityType<BeeEntity> beeType = world.rand.nextBoolean() ? ModEntities.SKELETAL_BEE.get() : ModEntities.ZOMBIE_BEE.get();
                BeeEntity newBee = beeType.create(world);
                if (newBee != null) {
                    tryEnterHive(newBee, false);
                }
            }
        }

        if (!hasTicked && tickCounter % 23 == 0) {
            BlockState blockState = this.getBlockState();

            if (blockState.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = blockState.get(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    int finalHoneyLevel = honeyLevel;
                    this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((InventoryHandlerHelper.ItemHandler) inv).addOutput(filledBottle);
                            if (addedBottle) {
                                ((InventoryHandlerHelper.ItemHandler) inv).addOutput(new ItemStack(Items.HONEYCOMB));
                                bottles.shrink(1);
                                world.setBlockState(pos, blockState.with(BeehiveBlock.HONEY_LEVEL, finalHoneyLevel - 5));
                            }
                        }
                    });
                    honeyLevel = this.world.getBlockState(this.pos).get(BeehiveBlock.HONEY_LEVEL);
                }

                // Update any attached expansion box if the honey level reaches max
                if (blockState.get(AdvancedBeehive.EXPANDED) && honeyLevel >= getMaxHoneyLevel(blockState)) {
                    ((AdvancedBeehive) blockState.getBlock()).updateState(world, this.getPos(), blockState, false);
                }
            }
        }

        if (--abandonCountdown < 0) {
            abandonCountdown = 0;
        }

        super.tick();
        hasTicked = false;
    }

    protected void productionTick() {
        ListNBT beeList = this.getBeeListAsNBTList();
        if (beeList.size() > 0) {
            for (INBT inbt : beeList) {
                CompoundNBT inb = (CompoundNBT) ((CompoundNBT) inbt).get("EntityData");
                String beeId = inb.getString("id");

                Double productionChance = ProductiveBeeEntity.getProductionChance(beeId, 0.65D);

                // Generate bee produce
                if (productionChance != null && productionChance > 0) {
                    if (world.rand.nextDouble() <= productionChance) {
                        final int productivity = inb.contains("bee_productivity") ? inb.getInt("bee_productivity") : 0;
                        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                            BeeHelper.getBeeProduce(world, beeId, this.flowerPos).forEach((stack) -> {
                                if (!stack.isEmpty()) {
                                    if (productivity > 0) {
                                        float f = (float) productivity * stack.getCount() * BeeAttributes.productivityModifier.generateFloat(world.rand);
                                        stack.grow(Math.round(f));
                                    }
                                    ((InventoryHandlerHelper.ItemHandler) inv).addOutput(stack);
                                }
                            });
                        });
                    }
                }
            }
        }
    }

    @Override
    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        // Add to the countdown for it's spot to become available in the hive
        abandonCountdown += getTimeInHive(true);
    }

    protected int beesOutsideHive() {
        return (int) Math.ceil(abandonCountdown % getTimeInHive(true));
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
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
