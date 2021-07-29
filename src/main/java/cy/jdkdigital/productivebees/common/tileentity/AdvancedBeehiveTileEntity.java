package cy.jdkdigital.productivebees.common.tileentity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

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
        return new TranslationTextComponent(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public boolean isSedated() {
        return true;
    }

    @Override
    public void tick() {
        final World world = level;
        if (world == null || level.isClientSide()) {
            return;
        }

        if (!hasTicked && ++tickCounter > ProductiveBeesConfig.GENERAL.hiveTickRate.get()) {
            tickCounter = 0;

            // Spawn skeletal and zombie bees in empty hives
            ListNBT beeList = this.getBeeListAsNBTList();
            BlockPos front = worldPosition.relative(getBlockState().getValue(BeehiveBlock.FACING));
            if (
                    level.isNight() &&
                    ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                    level.random.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                    beeList.size() + beesOutsideHive() == 0 &&
                    level.getBlockState(front).isAir() &&
                    level.getBrightness(LightType.BLOCK, front) <= 8
            ) {
                EntityType<ConfigurableBeeEntity> beeType = ModEntities.CONFIGURABLE_BEE.get();
                ConfigurableBeeEntity newBee = beeType.create(world);
                if (newBee != null) {
                    if (world.random.nextBoolean()) {
                        newBee.setBeeType("productivebees:skeletal");
                    } else {
                        newBee.setBeeType("productivebees:zombie");
                    }
                    newBee.setAttributes();

                    addOccupant(newBee, false);
                }
                this.setChanged();
            }
        }

        if (!hasTicked && tickCounter % 23 == 0) {
            BlockState blockState = this.getBlockState();

            if (blockState.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = blockState.getValue(BeehiveBlock.HONEY_LEVEL);

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
                                level.setBlockAndUpdate(worldPosition, blockState.setValue(BeehiveBlock.HONEY_LEVEL, finalHoneyLevel - 5));
                            }
                        }
                    });
                    honeyLevel = level.getBlockState(worldPosition).getValue(BeehiveBlock.HONEY_LEVEL);
                }

                // Update any attached expansion box if the honey level reaches max
                if (blockState.getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE && honeyLevel >= getMaxHoneyLevel(blockState)) {
                    ((AdvancedBeehive) blockState.getBlock()).updateState(world, worldPosition, blockState, false);
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
                super.getTimeInHive(hasNectar, beeEntity) * Math.max(0, timeUpgradeModifier + combBlockUpgradeModifier)
        );
    }

    @Override
    protected void beeReleasePostAction(BeeEntity beeEntity, BlockState state, State beeState) {
        super.beeReleasePostAction(beeEntity, state, beeState);

        // Generate bee produce
        if (level != null && beeState == BeehiveTileEntity.State.HONEY_DELIVERED) {
            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                BeeHelper.getBeeProduce(level, beeEntity, getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) > 0).forEach((stackIn) -> {
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
        if (breedingUpgrades > 0 && !beeEntity.isBaby() && getOccupantCount() > 0 && level.random.nextFloat() <= (ProductiveBeesConfig.UPGRADES.breedingChance.get() * breedingUpgrades)) {
            boolean canBreed = !(beeEntity instanceof ProductiveBeeEntity) || ((ProductiveBeeEntity) beeEntity).canSelfBreed();
            if (canBreed) {
                // Count nearby bee entities
                List<BeeEntity> bees = level.getEntitiesOfClass(BeeEntity.class, (new AxisAlignedBB(worldPosition).expandTowards(3.0D, 3.0D, 3.0D)));
                if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                    // Breed this bee with a random bee inside
                    Inhabitant otherBeeInhabitant = getBeeList().get(level.random.nextInt(getOccupantCount()));
                    Entity otherBee = EntityType.loadEntityRecursive(otherBeeInhabitant.nbt, level, (spawnedEntity) -> spawnedEntity);
                    if (otherBee instanceof BeeEntity) {
                        Entity offspring = BeeHelper.getBreedingResult(beeEntity, (BeeEntity) otherBee, (ServerWorld) level);
                        if (offspring != null) {
                            if (offspring instanceof ProductiveBeeEntity && beeEntity instanceof ProductiveBeeEntity) {
                                BeeHelper.setOffspringAttributes((ProductiveBeeEntity) offspring, (ProductiveBeeEntity) beeEntity, (BeeEntity) otherBee);
                            }
                            if (offspring instanceof AnimalEntity) {
                                ((AnimalEntity) offspring).setAge(-24000);
                            }
                            offspring.moveTo(beeEntity.getX(), beeEntity.getY(), beeEntity.getZ(), 0.0F, 0.0F);
                            level.addFreshEntity(offspring);
                        }
                    }
                }
            }
        }

        // Add to the countdown for it's spot to become available in the hive
        // this prevents other bees from moving in straight away
        abandonCountdown += getTimeInHive(true, beeEntity);
    }

    protected int beesOutsideHive() {
        return (int) Math.ceil(abandonCountdown % getTimeInHive(true, null));
    }

    @Override
    public boolean acceptsUpgrades() {
        return getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
    }

    @Override
    public boolean acceptsBee(BeeEntity bee) {
        boolean isInFilters = false;
        List<ItemStack> filters = getInstalledUpgrades(ModItems.UPGRADE_FILTER.get());
        for (ItemStack filter: filters) {
            List<Supplier<BeeIngredient>> allowedBees = FilterUpgradeItem.getAllowedBees(filter);
            for (Supplier<BeeIngredient> allowedBee: allowedBees) {
                String type = BeeIngredientFactory.getIngredientKey(bee);
                if (allowedBee.get().getBeeType().toString().equals(type)) {
                    isInFilters = true;
                }
            }
        }
        return filters.size() == 0 || isInFilters;
    }

    @Override
    public void load(BlockState blockState, CompoundNBT tag) {
        super.load(blockState, tag);

        CompoundNBT invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT upgradesTag = tag.getCompound("upgrades");
        upgradeHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(upgradesTag));

        // Reset MAX_BEES
        MAX_BEES = tag.contains("max_bees") ? tag.getInt("max_bees") : MAX_BEES;
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        upgradeHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("upgrades", compound);
        });

        tag.putInt("max_bees", MAX_BEES);

        return tag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();

        tag.put("bees", getBeeListAsNBTList());

        return new SUpdateTileEntityPacket(worldPosition, -1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();

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
