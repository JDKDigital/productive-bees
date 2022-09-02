package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.handler.bee.CapabilityBee;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredient;
import cy.jdkdigital.productivebees.integrations.jei.ingredients.BeeIngredientFactory;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AdvancedBeehiveBlockEntity extends AdvancedBeehiveBlockEntityAbstract implements MenuProvider, UpgradeableBlockEntity
{
    protected int tickCounter = 0;
    private int abandonCountdown = 0;
    protected boolean hasTicked = false;

    protected LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(12, this) {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            if (slot == AdvancedBeehiveContainer.SLOT_CAGE) {
                return item.getItem() instanceof BeeCage;
            }
            return super.isInputSlotItem(slot, item);
        }
    });
    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public AdvancedBeehiveBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
    }

    public AdvancedBeehiveBlockEntity(BlockPos pos, BlockState state) {
        this(ModTileEntityTypes.ADVANCED_BEEHIVE.get(), pos, state);
        MAX_BEES = 3;
    }

    @Nonnull
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory playerInventory, final Player player) {
        return new AdvancedBeehiveContainer(windowId, playerInventory, this);
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return new TranslatableComponent(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public boolean isSedated() {
        return true;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedBeehiveBlockEntity blockEntity) {
        if (!blockEntity.hasTicked && ++blockEntity.tickCounter > ProductiveBeesConfig.GENERAL.hiveTickRate.get()) {
            blockEntity.tickCounter = 0;

            // Spawn skeletal and zombie bees in empty hives
            BlockPos front = pos.relative(state.getValue(BeehiveBlock.FACING));
            if (
                    ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                    level.random.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                    level.isNight() &&
                    level.getBlockState(front).getCollisionShape(level, front).isEmpty() &&
                    blockEntity.getOccupantCount() + blockEntity.beesOutsideHive() == 0 &&
                    level.getBrightness(LightLayer.BLOCK, front) == 0
            ) {
                List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(pos).inflate(3.0D, 3.0D, 3.0D)));
                if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                    EntityType<ConfigurableBee> beeType = ModEntities.CONFIGURABLE_BEE.get();
                    ConfigurableBee newBee = beeType.create(level);
                    if (newBee != null) {
                        if (level.random.nextBoolean()) {
                            newBee.setBeeType("productivebees:skeletal");
                        } else {
                            newBee.setBeeType("productivebees:zombie");
                        }
                        newBee.setAttributes();
                        newBee.hivePos = pos;

                        blockEntity.addOccupant(newBee, false);
                    }
                }
            }
        }

        if (!blockEntity.hasTicked && blockEntity.tickCounter % 23 == 0) {
            if (state.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    int finalHoneyLevel = honeyLevel;
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((InventoryHandlerHelper.ItemHandler) inv).addOutput(filledBottle);
                            if (addedBottle) {
                                ((InventoryHandlerHelper.ItemHandler) inv).addOutput(new ItemStack(Items.HONEYCOMB));
                                bottles.shrink(1);
                                level.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, finalHoneyLevel - 5));
                            }
                        }
                    });
                    honeyLevel = level.getBlockState(pos).getValue(BeehiveBlock.HONEY_LEVEL);
                }

                // Update any attached expansion box if the honey level reaches max
                if (state.getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE && honeyLevel >= getMaxHoneyLevel(state)) {
                    ((AdvancedBeehive) state.getBlock()).updateState(level, pos, state, false);
                }

                // Insert or extract bees for simulated hives
                if (ProductiveBeesConfig.BEES.allowBeeSimulation.get() && blockEntity.getUpgradeCount(ModItems.UPGRADE_SIMULATOR.get()) > 0) {
                    blockEntity.inventoryHandler.ifPresent(h -> {
                        if (h instanceof InventoryHandlerHelper.ItemHandler invHelper) {
                            ItemStack cageStack = h.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE);
                            if (!cageStack.isEmpty() && cageStack.getItem() instanceof BeeCage) {
                                if (BeeCage.isFilled(cageStack) && invHelper.canFitStacks(List.of(new ItemStack(cageStack.getItem())))) {
                                    // release into hive if space is available
                                    if (!blockEntity.isFull()) {
                                        Bee bee = BeeCage.getEntityFromStack(cageStack, level, true);
                                        if (blockEntity.acceptsBee(bee) && (!(bee instanceof ProductiveBee pBee) || pBee.getAttributeValue(BeeAttributes.TYPE).equals("hive"))) {
                                            blockEntity.addOccupant(bee, bee.hasNectar());
                                            if (cageStack.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
                                                invHelper.addOutput(new ItemStack(cageStack.getItem()));
                                            }
                                            cageStack.shrink(1);
                                            level.sendBlockUpdated(pos, state, state, 3);
                                        }
                                    }
                                } else if (!blockEntity.isEmpty()) {
                                    // grab a bee from the hive and add to the cage
                                    blockEntity.getCapability(CapabilityBee.BEE).ifPresent(inhabitantStorage -> {
                                        Iterator<Inhabitant> inhabitantIterator = inhabitantStorage.getInhabitants().iterator();
                                        Inhabitant inhabitant = inhabitantIterator.next();
                                        Bee beeEntity = (Bee) EntityType.loadEntityRecursive(inhabitant.nbt, level, (spawnedEntity) -> spawnedEntity);
                                        beeEntity.hivePos = blockEntity.worldPosition;
                                        ItemStack filledCage = new ItemStack(cageStack.getItem());
                                        BeeCage.captureEntity(beeEntity, filledCage);
                                        if (invHelper.canFitStacks(List.of(new ItemStack(cageStack.getItem())))) {
                                            cageStack.shrink(1);
                                            invHelper.addOutput(filledCage);
                                            inhabitantIterator.remove();
                                            level.sendBlockUpdated(pos, state, state, 3);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        }

        if (--blockEntity.abandonCountdown < 0) {
            blockEntity.abandonCountdown = 0;
        }

        AdvancedBeehiveBlockEntityAbstract.tick(level, pos, state, blockEntity);
        blockEntity.hasTicked = false;
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    protected int getTimeInHive(boolean hasNectar, @Nullable Bee beeEntity) {
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
        return (int) (
            super.getTimeInHive(hasNectar, beeEntity) * Math.max(0, timeUpgradeModifier)
        );
    }

    @Override
    protected void beeReleasePostAction(@Nonnull Level level, Bee beeEntity, BlockState state, BeeReleaseStatus beeState) {
        super.beeReleasePostAction(level, beeEntity, state, beeState);

        if (beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
            // Generate bee produce
            if (beeEntity instanceof ProductiveBee productiveBee && productiveBee.hasConverted()) {
                // No produce after converting a block
                productiveBee.setHasConverted(false);
            } else {
                getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                    BeeHelper.getBeeProduce(level, beeEntity, getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) > 0).forEach((stackIn) -> {
                        ItemStack stack = stackIn.copy();
                        applyHiveProductionModifier(stack);
                        if (!stack.isEmpty()) {
                            if (beeEntity instanceof ProductiveBee) {
                                int productivity = ((ProductiveBee) beeEntity).getAttributeValue(BeeAttributes.PRODUCTIVITY);
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
                boolean canBreed = !(beeEntity instanceof ProductiveBee) || ((ProductiveBee) beeEntity).canSelfBreed();
                if (canBreed) {
                    // Count nearby bee entities
                    List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(worldPosition).inflate(3.0D, 3.0D, 3.0D)));
                    if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                        // Breed this bee with a random bee inside
                        Inhabitant otherBeeInhabitant = getBeeList().get(level.random.nextInt(getOccupantCount()));
                        Entity otherBee = EntityType.loadEntityRecursive(otherBeeInhabitant.nbt, level, (spawnedEntity) -> spawnedEntity);
                        if (otherBee instanceof Bee) {
                            Entity offspring = BeeHelper.getBreedingResult(beeEntity, (Bee) otherBee, (ServerLevel) this.level);
                            if (offspring != null) {
                                if (offspring instanceof ProductiveBee && beeEntity instanceof ProductiveBee) {
                                    BeeHelper.setOffspringAttributes((ProductiveBee) offspring, (ProductiveBee) beeEntity, (Bee) otherBee);
                                }
                                if (offspring instanceof AgeableMob) {
                                    ((AgeableMob) offspring).setAge(-24000);
                                }
                                offspring.moveTo(beeEntity.getX(), beeEntity.getY(), beeEntity.getZ(), 0.0F, 0.0F);
                                level.addFreshEntity(offspring);
                            }
                        }
                    }
                }
            }

            // Produce genes
            int samplerUpgrades = getUpgradeCount(ModItems.UPGRADE_BEE_SAMPLER.get());
            if (samplerUpgrades > 0 && !beeEntity.isBaby() && beeEntity instanceof ProductiveBee && level.random.nextFloat() <= (ProductiveBeesConfig.UPGRADES.samplerChance.get() * samplerUpgrades)) {
                getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                    Map<BeeAttribute<?>, Object> attributes = ((ProductiveBee) beeEntity).getBeeAttributes();
                    // Get a random number for which attribute to extract, if we hit the additional 2 it will extract a type gene instead
                    int attr = ProductiveBees.rand.nextInt(attributes.size() + 2);
                    if (attr >= BeeAttributes.attributeList().size()) {
                        // Type gene
                        String type = beeEntity instanceof ConfigurableBee ? ((ConfigurableBee) beeEntity).getBeeType() : beeEntity.getEncodeId();
                        ((InventoryHandlerHelper.ItemHandler) inv).addOutput(Gene.getStack(type, ProductiveBees.rand.nextInt(4) + 1));
                    } else {
                        BeeAttribute<?> attribute = BeeAttributes.map.get(BeeAttributes.attributeList().get(attr));
                        Object value = ((ProductiveBee) beeEntity).getAttributeValue(attribute);
                        if (value instanceof Integer) {
                            ((InventoryHandlerHelper.ItemHandler) inv).addOutput(Gene.getStack(attribute, (Integer) value, 1, ProductiveBees.rand.nextInt(4) + 1));
                        }
                    }
                });
            }
        }

        // Add to the countdown for it's spot to become available in the hive
        // this prevents other bees from moving in straight away
        abandonCountdown += getTimeInHive(true, beeEntity);
    }

    protected void applyHiveProductionModifier(ItemStack stack) {
        //
    }

    protected int beesOutsideHive() {
        return (int) Math.ceil(abandonCountdown % getTimeInHive(true, null));
    }

    @Override
    public boolean acceptsUpgrades() {
        return getBlockState().getValue(AdvancedBeehive.EXPANDED) != VerticalHive.NONE;
    }

    @Override
    public boolean acceptsBee(Bee bee) {
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
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);

        CompoundTag invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(invTag));

        CompoundTag upgradesTag = tag.getCompound("upgrades");
        upgradeHandler.ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(upgradesTag));

        // Reset MAX_BEES
        MAX_BEES = tag.contains("max_bees") ? tag.getInt("max_bees") : MAX_BEES;
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        upgradeHandler.ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("upgrades", compound);
        });

        tag.putInt("max_bees", MAX_BEES);
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
