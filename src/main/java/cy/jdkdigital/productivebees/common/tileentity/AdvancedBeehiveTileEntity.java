package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
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
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdvancedBeehiveTileEntity extends AdvancedBeehiveTileEntityAbstract implements INamedContainerProvider, UpgradeableTileEntity
{
    protected int tickCounter = 0;
    private int abandonCountdown = 0;
    protected boolean hasTicked = false;

    protected LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(11, this));
    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public AdvancedBeehiveTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public AdvancedBeehiveTileEntity() {
        this(ModTileEntityTypes.ADVANCED_BEEHIVE.get());
        MAX_BEES = 3;
    }

    @Nonnull
    @Override
    public Container createMenu(final int windowId, final PlayerInventory playerInventory, final PlayerEntity player) {
        return new AdvancedBeehiveContainer(windowId, playerInventory, this);
    }

    @Nonnull
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

            // Spawn skeletal and zombie bees in empty hives
            ListNBT beeList = this.getBeeListAsNBTList();
            if (
                world.isNightTime() &&
                ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                world.rand.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                beeList.size() + beesOutsideHive() == 0 &&
                world.getLight(pos.offset(getBlockState().get(BeehiveBlock.FACING), 1)) <= 8
            ) {
                EntityType<ConfigurableBeeEntity> beeType = ModEntities.CONFIGURABLE_BEE.get();
                ConfigurableBeeEntity newBee = beeType.create(world);
                if (newBee != null) {
                    if (world.rand.nextBoolean()) {
                        newBee.setBeeType("productivebees:skeletal");
                    } else {
                        newBee.setBeeType("productivebees:zombie");
                    }
                    newBee.setAttributes();

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
                if (blockState.get(AdvancedBeehive.EXPANDED) != VerticalHive.NONE && honeyLevel >= getMaxHoneyLevel(blockState)) {
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

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    protected int getTimeInHive(boolean hasNectar, @Nullable BeeEntity beeEntity) {
        double combBlockUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) * ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
        return (int) (
            super.getTimeInHive(hasNectar, beeEntity) *
            Math.max(0, timeUpgradeModifier + combBlockUpgradeModifier)
        );
    }

    @Override
    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        // Generate bee produce
        if (world != null && beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                BeeHelper.getBeeProduce(world, beeEntity, getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) > 0).forEach((stackIn) -> {
                    ItemStack stack = stackIn.copy();
                    if (!stack.isEmpty()) {
                        if (beeEntity instanceof ProductiveBeeEntity) {
                            int productivity = ((ProductiveBeeEntity) beeEntity).getAttributeValue(BeeAttributes.PRODUCTIVITY);
                            if (productivity > 0) {
                                float modifier = (1f / (productivity + 2f) + (productivity + 1f) / 2f) * stack.getCount();
                                stack.grow(Math.round(modifier));
                            }
                        }

                        // Apply upgrades
                        int productivityUpgrades = getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY.get());
                        if (productivityUpgrades > 0) {
                            double upgradeMod = (stack.getCount() * (ProductiveBeesConfig.UPGRADES.productivityMultiplier.get() * (float) productivityUpgrades));
                            stack.setCount(Math.round((float) upgradeMod));
                        }

                        ((InventoryHandlerHelper.ItemHandler) inv).addOutput(stack);
                    }
                });
            });
        }

        // Produce offspring if breeding upgrade is installed
        int breedingUpgrades = getUpgradeCount(ModItems.UPGRADE_BREEDING.get());
        if (breedingUpgrades > 0 && !beeEntity.isChild() && getBeeCount() > 0 && world.rand.nextFloat() <= (ProductiveBeesConfig.UPGRADES.breedingChance.get() * breedingUpgrades)) {
            // Breed this bee with a random bee inside
            Inhabitant otherBeeInhabitant = getBeeList().get(world.rand.nextInt(getBeeCount()));
            BeeEntity otherBee = (BeeEntity) EntityType.loadEntityAndExecute(otherBeeInhabitant.nbt, world, (spawnedEntity) -> spawnedEntity);
            BeeEntity offspring = BeeHelper.getBreedingResult(beeEntity, otherBee, (ServerWorld) world);
            if (offspring instanceof ProductiveBeeEntity && beeEntity instanceof ProductiveBeeEntity) {
                BeeHelper.setOffspringAttributes((ProductiveBeeEntity) offspring, (ProductiveBeeEntity) beeEntity, otherBee);
            }
            offspring.setGrowingAge(-24000);
            offspring.setLocationAndAngles(beeEntity.getPosX(), beeEntity.getPosY(), beeEntity.getPosZ(), 0.0F, 0.0F);
            world.addEntity(offspring);
        }

        // Add to the countdown for it's spot to become available in the hive
        // this prevents other bees from moving in straight away
        abandonCountdown += getTimeInHive(true, beeEntity);
    }

    protected int beesOutsideHive() {
        return (int) Math.ceil(abandonCountdown % getTimeInHive(true, null));
    }

    @Override
    public void read(BlockState blockState, CompoundNBT tag) {
        super.read(blockState, tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT upgradesTag = tag.getCompound("upgrades");
        upgradeHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(upgradesTag));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        upgradeHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("upgrades", compound);
        });

        return tag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();

        tag.put("bees", getBeeListAsNBTList());

        return new SUpdateTileEntityPacket(getPos(), -1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt){
        CompoundNBT tag = pkt.getNbtCompound();

        if (tag.contains("bees")) {
            getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
                inhabitantHandler.setInhabitantsFromListNBT((ListNBT) tag.get("bees"));
            });
        }
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
