package cy.jdkdigital.productivebees.common.block.entity;

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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

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
            if (slot == AdvancedBeehiveContainer.SLOT_BOTTLE && item.is(Blocks.SPONGE.asItem())) {
                return true;
            }
            return super.isInputSlotItem(slot, item);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return super.isInputSlot(slot) || (slot == AdvancedBeehiveContainer.SLOT_CAGE && blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity && advancedBeehiveBlockEntity.isSim());
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (slot == AdvancedBeehiveContainer.SLOT_BOTTLE) {
                ItemStack itemInBottleSlot = getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                if (!itemInBottleSlot.isEmpty() && itemInBottleSlot.is(Blocks.SPONGE.asItem()) && blockEntity.getLevel() instanceof ServerLevel level) {
                    level.setBlockAndUpdate(blockEntity.getBlockPos(), level.getBlockState(blockEntity.getBlockPos()).setValue(BeehiveBlock.HONEY_LEVEL, 0));
                }
            }
        }
    });
    protected LazyOptional<IItemHandlerModifiable> upgradeHandler = LazyOptional.of(() -> new InventoryHandlerHelper.UpgradeHandler(4, this));

    public AdvancedBeehiveBlockEntity(AdvancedBeehive hiveBlock, BlockPos pos, BlockState state) {
        this(hiveBlock.getBlockEntitySupplier().get(), pos, state);
    }

    public AdvancedBeehiveBlockEntity(BlockEntityType<?> tileEntityType, BlockPos pos, BlockState state) {
        super(tileEntityType, pos, state);
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
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public boolean isSedated() {
        return true;
    }

    public boolean isSim() {
        return ProductiveBeesConfig.BEES.allowBeeSimulation.get() && (
                getUpgradeCount(ModItems.UPGRADE_SIMULATOR.get()) > 0 ||
                getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_3.get()) > 0 ||
                getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_4.get()) > 0
        );
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedBeehiveBlockEntity blockEntity) {
        if (!blockEntity.hasTicked && ++blockEntity.tickCounter > ProductiveBeesConfig.GENERAL.hiveTickRate.get()) {
            blockEntity.tickCounter = 0;

            // Spawn skeletal and zombie bees in empty hives
            BlockPos front = pos.relative(state.getValue(BeehiveBlock.FACING));
            if (
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
                    blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                        if (!bottles.isEmpty() && bottles.getItem() instanceof BottleItem) {
                            final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((InventoryHandlerHelper.ItemHandler) inv).addOutput(filledBottle).getCount() == 0;
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
                if (blockEntity.isSim()) {
                    blockEntity.inventoryHandler.ifPresent(h -> {
                        if (h instanceof InventoryHandlerHelper.ItemHandler invHelper) {
                            ItemStack cageStack = h.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE);
                            if (!cageStack.isEmpty() && cageStack.getItem() instanceof BeeCage) {
                                if (BeeCage.isFilled(cageStack) && (!cageStack.getItem().equals(ModItems.STURDY_BEE_CAGE.get()) || invHelper.canFitStacks(List.of(new ItemStack(cageStack.getItem()))))) {
                                    // insert into hive if space is available
                                    if (!blockEntity.isFull()) {
                                        Bee bee = BeeCage.getEntityFromStack(cageStack, level, true);
                                        if (bee != null && blockEntity.acceptsBee(bee) && (!(bee instanceof ProductiveBee pBee) || pBee.getAttributeValue(BeeAttributes.TYPE).equals("hive"))) {
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
                                        Entity entity = EntityType.loadEntityRecursive(inhabitant.nbt, level, (spawnedEntity) -> spawnedEntity);
                                        if (entity instanceof Bee beeEntity) {
                                            beeEntity.hivePos = blockEntity.worldPosition;
                                            ItemStack filledCage = new ItemStack(cageStack.getItem());
                                            BeeCage.captureEntity(beeEntity, filledCage);
                                            if (invHelper.canFitStacks(List.of(new ItemStack(cageStack.getItem())))) {
                                                cageStack.shrink(1);
                                                invHelper.addOutput(filledCage);
                                                inhabitantIterator.remove();
                                                level.sendBlockUpdated(pos, state, state, 3);
                                            }
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
        double timeUpgradeModifier = Math.max(0, 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get()));
        return (int) (
            super.getTimeInHive(hasNectar, beeEntity) * timeUpgradeModifier + 20
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
                getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
                    BeeHelper.getBeeProduce(level, beeEntity, getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) > 0).forEach((stackIn) -> {
                        ItemStack stack = stackIn.copy();
                        if (!stack.isEmpty()) {
                            if (beeEntity instanceof ProductiveBee) {
                                int productivity = ((ProductiveBee) beeEntity).getAttributeValue(BeeAttributes.PRODUCTIVITY);
                                if (productivity > 0) {
                                    float modifier = (1f / (productivity + 2f) + (productivity + 1f) / 2f) * stack.getCount();
                                    stack.grow(Math.round(modifier));
                                }
                            }

                            // Apply upgrades
                            int ProductivityUpgrades = getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY.get());
                            int productivity2Upgrades = getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_2.get());
                            int productivity3Upgrades = getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_3.get());
                            int productivity4Upgrades = getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_4.get());

                            double upgradeMod = ProductiveBeesConfig.UPGRADES.productivityMultiplier.get() * ProductivityUpgrades;
                            upgradeMod = upgradeMod + ProductiveBeesConfig.UPGRADES.productivityMultiplier2.get() * productivity2Upgrades;
                            upgradeMod = upgradeMod + ProductiveBeesConfig.UPGRADES.productivityMultiplier3.get() * productivity3Upgrades;
                            upgradeMod = upgradeMod + ProductiveBeesConfig.UPGRADES.productivityMultiplier4.get() * productivity4Upgrades;

                            if (upgradeMod >= 1.0) {
                                double newStackSize = stack.getCount() * upgradeMod;
                                stack.setCount(Math.round((float) newStackSize));
                            }

                            ((InventoryHandlerHelper.ItemHandler) inv).addOutput(stack);
                        }
                    });

                    // If there's a sponge in the bottle slot, empty honey level
                    ItemStack itemInBottleSlot = inv.getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                    if (!itemInBottleSlot.isEmpty() && itemInBottleSlot.is(Blocks.SPONGE.asItem())) {
                        level.setBlockAndUpdate(getBlockPos(), state.setValue(BeehiveBlock.HONEY_LEVEL, 0));
                    }
                });
            }

            // Produce offspring if breeding upgrade is installed
            int breedingUpgrades = getUpgradeCount(ModItems.UPGRADE_BREEDING.get());
            if (breedingUpgrades > 0 && !beeEntity.isBaby() && getOccupantCount() > 0 && level.random.nextFloat() <= (ProductiveBeesConfig.UPGRADES.breedingChance.get() * breedingUpgrades)) {
                boolean canBreed = !(beeEntity instanceof ProductiveBee) || ((ProductiveBee) beeEntity).canSelfBreed();
                if (canBreed) {
                    // Count nearby bee entities
                    List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(this.worldPosition).inflate(5.0D, 5.0D, 5.0D)));
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
                                BlockPos frontPos = getBlockPos().relative(state.getValue(BeehiveBlock.FACING));
                                offspring.moveTo(frontPos.getX(), frontPos.getY() + 0.5F, frontPos.getZ(), 0.0F, 0.0F);
                                level.addFreshEntity(offspring);
                            }
                        }
                    }
                }
            }

            // Produce genes
            int samplerUpgrades = getUpgradeCount(ModItems.UPGRADE_BEE_SAMPLER.get()) + getUpgradeCount(ModItems.UPGRADE_PRODUCTIVITY_4.get());
            if (samplerUpgrades > 0 && !beeEntity.isBaby() && beeEntity instanceof ProductiveBee && level.random.nextFloat() <= (ProductiveBeesConfig.UPGRADES.samplerChance.get() * samplerUpgrades)) {
                getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
                    Map<BeeAttribute<?>, Object> attributes = ((ProductiveBee) beeEntity).getBeeAttributes();
                    // Get a random number for which attribute to extract, if we hit the additional 2 it will extract a type gene instead
                    int attr = level.random.nextInt(attributes.size() + 2);
                    if (attr >= BeeAttributes.attributeList().size()) {
                        // Type gene
                        String type = beeEntity instanceof ConfigurableBee ? ((ConfigurableBee) beeEntity).getBeeType() : beeEntity.getEncodeId();
                        ((InventoryHandlerHelper.ItemHandler) inv).addOutput(Gene.getStack(type, level.random.nextInt(4) + 1));
                    } else {
                        BeeAttribute<?> attribute = BeeAttributes.map.get(BeeAttributes.attributeList().get(attr));
                        Object value = ((ProductiveBee) beeEntity).getAttributeValue(attribute);
                        if (value instanceof Integer) {
                            ((InventoryHandlerHelper.ItemHandler) inv).addOutput(Gene.getStack(attribute, (Integer) value, 1, level.random.nextInt(4) + 1));
                        }
                    }
                });
            }
        }

        // Add to the countdown for it's spot to become available in the hive
        // this prevents other bees from moving in straight away
        abandonCountdown += getTimeInHive(true, beeEntity);
    }

    protected int beesOutsideHive() {
        int timeInHive = getTimeInHive(true, null);
        return timeInHive > 0 ? (int) Math.ceil(abandonCountdown % timeInHive) : 0;
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
        this.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(invTag));

        CompoundTag upgradesTag = tag.getCompound("upgrades");
        upgradeHandler.ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(upgradesTag));

        // Reset MAX_BEES
        MAX_BEES = tag.contains("max_bees") ? tag.getInt("max_bees") : MAX_BEES;
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);

        this.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inv -> {
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
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
