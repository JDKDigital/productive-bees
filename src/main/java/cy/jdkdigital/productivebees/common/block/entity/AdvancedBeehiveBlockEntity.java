package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.IProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.SolitaryBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.common.item.Gene;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.state.properties.VerticalHive;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.common.block.entity.ICapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.registry.LibItems;
import cy.jdkdigital.productivelib.registry.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdvancedBeehiveBlockEntity extends AdvancedBeehiveBlockEntityAbstract implements MenuProvider, ICapabilityBlockEntity, IUpgradeableBlockEntity, Container
{
    protected int specialTickCounter = 0;
    protected int abandonCountdown = 0;

    public InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(12, this) {
        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            if (slot == AdvancedBeehiveContainer.SLOT_CAGE) {
                return item.getItem() instanceof BeeCage && blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity && advancedBeehiveBlockEntity.isSim();
            }
            if (slot == AdvancedBeehiveContainer.SLOT_BOTTLE && item.is(Blocks.SPONGE.asItem())) {
                return true;
            }
            return super.isInputSlotItem(slot, item);
        }

        @Override
        public boolean isInputSlot(int slot) {
            return (slot == AdvancedBeehiveContainer.SLOT_CAGE && blockEntity instanceof AdvancedBeehiveBlockEntity advancedBeehiveBlockEntity && advancedBeehiveBlockEntity.isSim()) || super.isInputSlot(slot);
        }

        @Override
        protected void onContentsChanged(int slot, ItemStack previousContents) {
            super.onContentsChanged(slot, previousContents);
            if (slot == AdvancedBeehiveContainer.SLOT_BOTTLE) {
                ItemStack itemInBottleSlot = getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                if (!itemInBottleSlot.isEmpty() && itemInBottleSlot.is(Blocks.SPONGE.asItem()) && blockEntity.getLevel() instanceof ServerLevel level) {
                    level.setBlockAndUpdate(blockEntity.getBlockPos(), level.getBlockState(blockEntity.getBlockPos()).setValue(BeehiveBlock.HONEY_LEVEL, 0));
                }
            }
        }
    };

    protected InventoryHandlerHelper.UpgradeHandler upgradeHandler = new InventoryHandlerHelper.UpgradeHandler(4, this, List.of(
            LibItems.UPGRADE_TIME.get(),
            LibItems.UPGRADE_BLOCK.get(),
            LibItems.UPGRADE_GENE_SAMPLER.get(),
            LibItems.UPGRADE_CHILD.get(),
            LibItems.UPGRADE_ENTITY_FILTER.get(),
            LibItems.UPGRADE_RANGE.get(),
            LibItems.UPGRADE_SIMULATOR.get(),
            LibItems.UPGRADE_PRODUCTIVITY.get(),
            LibItems.UPGRADE_PRODUCTIVITY_2.get(),
            LibItems.UPGRADE_PRODUCTIVITY_3.get(),
            LibItems.UPGRADE_PRODUCTIVITY_4.get()
    ));

    public AdvancedBeehiveBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        MAX_BEES = 3;
    }

    @NotNull
    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntityTypes.ADVANCED_HIVE.get();
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

    @Override
    public boolean isFireNearby() {
        return !isSim() && super.isFireNearby();
    }

    public boolean isSim() {
        return ProductiveBeesConfig.BEES.forceBeeSimulation.get() || (ProductiveBeesConfig.BEES.allowBeeSimulation.get() && (
                getUpgradeCount(LibItems.UPGRADE_SIMULATOR.get()) > 0 ||
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get()) > 0 ||
                getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get()) > 0
        ));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedBeehiveBlockEntity blockEntity) {
        if (++blockEntity.specialTickCounter > ProductiveBeesConfig.GENERAL.hiveTickRate.get()) {
            blockEntity.specialTickCounter = 0;

            // Spawn skeletal and zombie bees in empty hives
            BlockPos front = pos.relative(state.getValue(BeehiveBlock.FACING));
            if (
                    level.getRandom().nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                    level.getOverworldClockTime() % 24000 >= 12000 &&
                    level.getBlockState(front).getCollisionShape(level, front).isEmpty() &&
                    blockEntity.getOccupantCount() + blockEntity.beesOutsideHive() == 0 &&
                    level.getBrightness(LightLayer.BLOCK, front) == 0
            ) {
                List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(pos).inflate(3.0D, 3.0D, 3.0D)));
                if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                    EntityType<ConfigurableBee> beeType = ModEntities.CONFIGURABLE_BEE.get();
                    ConfigurableBee newBee = beeType.create(level, EntitySpawnReason.NATURAL);
                    if (newBee != null) {
                        if (level.getRandom().nextBoolean()) {
                            newBee.setBeeType("productivebees:skeletal");
                        } else {
                            newBee.setBeeType("productivebees:zombie");
                        }
                        newBee.setDefaultAttributes();
                        newBee.setHivePos(pos);

                        blockEntity.addOccupant(newBee);
                    }
                }
            }
        }

        if (blockEntity.tickCounter % 23 == 0 && blockEntity.inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemStackHandler) {
            if (state.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    ItemStack bottles = itemStackHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                    if (!bottles.isEmpty() && bottles.is(Items.GLASS_BOTTLE)) {
                        final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                        boolean addedBottle = itemStackHandler.addOutput(filledBottle).getCount() == 0;
                        if (addedBottle) {
                            itemStackHandler.addOutput(new ItemStack(Items.HONEYCOMB));
                            itemStackHandler.extractItem(AdvancedBeehiveContainer.SLOT_BOTTLE, 1, false, false);
                            level.setBlockAndUpdate(pos, state.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel - 5));
                        }
                    }
                }

                // Insert or extract bees for simulated hives
                if (blockEntity.isSim()) {
                    ItemStack cageStack = itemStackHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE);
                    if (!cageStack.isEmpty() && cageStack.getItem() instanceof BeeCage) {
                        if (BeeCage.isFilled(cageStack) && (!cageStack.getItem().equals(ModItems.STURDY_BEE_CAGE.get()) || itemStackHandler.canFitStacks(List.of(new ItemStack(cageStack.getItem()))))) {
                            // insert into hive if space is available
                            if (!blockEntity.isFull()) {
                                Bee bee = BeeCage.getEntityFromStack(cageStack, level, true);
                                if (bee != null && blockEntity.acceptsBee(bee) && !(bee instanceof SolitaryBee)) {
                                    int beforeCount = blockEntity.getOccupantCount();
                                    blockEntity.addOccupant(bee);
                                    if (blockEntity.getOccupantCount() > beforeCount) {
                                        if (cageStack.getItem().equals(ModItems.STURDY_BEE_CAGE.get())) {
                                            itemStackHandler.addOutput(new ItemStack(cageStack.getItem()));
                                        }
                                        ItemStack shrunkCage = cageStack.copy();
                                        shrunkCage.shrink(1);
                                        itemStackHandler.setStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE, shrunkCage);
                                        level.sendBlockUpdated(pos, state, state, 3);
                                    }
                                }
                            }
                        } else if (!blockEntity.isEmpty()) {
                            // grab a bee from the hive and add to the cage
                            if (blockEntity.getOccupantCount() > 0) {
                                final boolean[] hasRemoved = {false};
                                blockEntity.stored.removeIf(beeData -> {
                                    if (!hasRemoved[0]) {
                                        Entity entity = beeData.toOccupant().createEntity(level, pos);
                                        if (entity instanceof Bee beeEntity) {
                                            beeEntity.setHivePos(blockEntity.worldPosition);
                                            ItemStack filledCage = new ItemStack(cageStack.getItem());
                                            BeeCage.captureEntity(beeEntity, filledCage);
                                            if (itemStackHandler.canFitStacks(List.of(new ItemStack(cageStack.getItem())))) {
                                                // Same 26.1 copy-semantics caveat: persist the shrink via setStackInSlot.
                                                ItemStack shrunkCage = cageStack.copy();
                                                shrunkCage.shrink(1);
                                                itemStackHandler.setStackInSlot(AdvancedBeehiveContainer.SLOT_CAGE, shrunkCage);
                                                itemStackHandler.addOutput(filledCage);
                                                level.sendBlockUpdated(pos, state, state, 3);
                                                hasRemoved[0] = true;
                                                return true;
                                            }
                                        }
                                    }
                                    return false;
                                });
                            }
                        }
                    }
                }
            }
        }

        if (--blockEntity.abandonCountdown < 0) {
            blockEntity.abandonCountdown = 0;
        }

        AdvancedBeehiveBlockEntityAbstract.tick(level, pos, state, blockEntity);
    }

    @Override
    public ResourceHandler<ItemResource> getUpgradeHandler() {
        return upgradeHandler;
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level == null) {
            return;
        }
        for (int slot = 0; slot < inventoryHandler.size(); ++slot) {
            ItemStack stack = ItemUtil.getStack(inventoryHandler, slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
        for (int slot = 0; slot < upgradeHandler.size(); ++slot) {
            ItemStack stack = ItemUtil.getStack(upgradeHandler, slot);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(this.level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override
    public EnergyHandler getEnergyHandler() {
        return null;
    }

    @Override
    public ResourceHandler<FluidResource> getFluidHandler() {
        return null;
    }

    @Override
    protected int getTimeInHive(boolean hasNectar, @Nullable Occupant occupant) {
        double timeUpgradeModifier = Math.max(0, 1 - (ProductiveBeesConfig.UPGRADES.timeBonus.get()) * getUpgradeCount(LibItems.UPGRADE_TIME.get()));
        return (int) (
            super.getTimeInHive(hasNectar, occupant) * timeUpgradeModifier + 20
        );
    }

    @Override
    protected void beeReleasePostAction(@Nonnull Level level, Bee beeEntity, BlockState state, BeeReleaseStatus beeState) {
        if (beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
            // Generate bee produce (No produce after converting a block)
            if (!(beeEntity instanceof ProductiveBee productiveBee) || !productiveBee.hasConverted()) {
                // Count productivity modifier
                double upgradeMod = 1 + ProductiveBeesConfig.UPGRADES.productivityMultiplier.get() * getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY.get())
                                    + ProductiveBeesConfig.UPGRADES.productivityMultiplier2.get() * getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_2.get())
                                    + ProductiveBeesConfig.UPGRADES.productivityMultiplier3.get() * getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_3.get())
                                    + ProductiveBeesConfig.UPGRADES.productivityMultiplier4.get() * getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get());

                var hasBlockUpgrade = (getUpgradeCount(LibItems.UPGRADE_BLOCK.get()) + getUpgradeCount(LibItems.UPGRADE_PRODUCTIVITY_4.get())) > 0;
                BeeHelper.getBeeProduce(level, beeEntity, hasBlockUpgrade, upgradeMod).forEach((stackIn) -> {
                    ItemStack stack = stackIn.copy();
                    if (!stack.isEmpty() && inventoryHandler instanceof InventoryHandlerHelper.BlockEntityItemStackHandler itemStackHandler) {
                        if (beeEntity.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
                            var data = beeEntity.getData(ProductiveBees.ATTRIBUTE_HANDLER);
                            GeneValue productivity = data.getAttributeValue(GeneAttribute.PRODUCTIVITY);
                            if (productivity.getValue() > 0) {
                                if(stack.getCount() == 1) {
                                    stack.grow(productivity.getValue());
                                } else {
                                    float modifier = (1f / (productivity.getValue() + 2f) + (productivity.getValue() + 1f) / 2f) * stack.getCount();
                                    stack.grow(Math.round(modifier));
                                }
                            }
                        }

                        itemStackHandler.addOutput(stack);
                    }
                });

                // If there's a sponge in the bottle slot, empty honey level
                ItemStack itemInBottleSlot = inventoryHandler.getStackInSlot(AdvancedBeehiveContainer.SLOT_BOTTLE);
                if (!itemInBottleSlot.isEmpty() && itemInBottleSlot.is(Blocks.SPONGE.asItem())) {
                    level.setBlockAndUpdate(getBlockPos(), state.setValue(BeehiveBlock.HONEY_LEVEL, 0));
                }
            }

            // Produce offspring if breeding upgrade is installed
            int breedingUpgrades = getUpgradeCount(LibItems.UPGRADE_CHILD.get());
            if (breedingUpgrades > 0 && !beeEntity.isBaby() && getOccupantCount() > 0 && level.getRandom().nextFloat() <= (ProductiveBeesConfig.UPGRADES.breedingChance.get() * breedingUpgrades)) {
                boolean canBreed = !(beeEntity instanceof IProductiveBee) || ((IProductiveBee) beeEntity).canSelfBreed();
                // Check that breeding item is in the hive
                if (canBreed && beeEntity instanceof IProductiveBee productiveBee) {
                    var breedingIngredient = productiveBee.getBreedingIngredient();
                    if (!breedingIngredient.test(Items.POPPY.getDefaultInstance())) {
                        canBreed = false;
                    }
                }
                if (canBreed) {
                    // Count nearby bee entities
                    List<Bee> bees = level.getEntitiesOfClass(Bee.class, (new AABB(this.worldPosition).inflate(5.0D, 5.0D, 5.0D)));
                    if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                        // Breed this bee with a random bee inside
                        var otherBeeInhabitant = this.stored.get(level.getRandom().nextInt(getOccupantCount()));
                        Entity otherBee = otherBeeInhabitant.toOccupant().createEntity(level, getBlockPos());
                        if (otherBee instanceof Bee) {
                            Entity offspring = BeeHelper.getBreedingResult(beeEntity, (Bee) otherBee, (ServerLevel) this.level);
                            if (offspring instanceof Bee bee) {
                                BeeHelper.setOffspringAttributes(bee, beeEntity, (Bee) otherBee);
                                bee.setAge(-24000);
                                BlockPos frontPos = getBlockPos().relative(state.getValue(BeehiveBlock.FACING));
                                bee.snapTo(frontPos.getX(), frontPos.getY() + 0.5F, frontPos.getZ(), 0.0F, 0.0F);
                                level.addFreshEntity(bee);
                            }
                        }
                    }
                }
            }

            // Produce genes
            int samplerUpgrades = getUpgradeCount(LibItems.UPGRADE_GENE_SAMPLER.get());
            if (samplerUpgrades > 0 && !beeEntity.isBaby() && level.getRandom().nextFloat() <= (ProductiveBeesConfig.UPGRADES.samplerChance.get() * samplerUpgrades)) {
                var attributes = beeEntity.getData(ProductiveBees.ATTRIBUTE_HANDLER);
                // Get a random number for which attribute to extract, if we hit the additional 2 it will extract a type gene instead
                GeneAttribute[] geneAttributes = GeneAttribute.values();
                GeneAttribute attribute = geneAttributes[level.getRandom().nextInt(geneAttributes.length)];
                int maxPurity = ProductiveBeesConfig.UPGRADES.samplerPurity.get();
                if (attribute.equals(GeneAttribute.TYPE)) {
//                    // Type gene
                    String type = beeEntity instanceof ConfigurableBee ? ((ConfigurableBee) beeEntity).getBeeType().toString() : beeEntity.getEncodeId();
                    ((InventoryHandlerHelper.BlockEntityItemStackHandler) inventoryHandler).addOutput(Gene.getStack(type, level.getRandom().nextInt(maxPurity) + 1));
                } else {
                    GeneValue value = attributes.getAttributeValue(attribute);
                    ((InventoryHandlerHelper.BlockEntityItemStackHandler) inventoryHandler).addOutput(Gene.getStack(attribute, value.getSerializedName(), 1, level.getRandom().nextInt(maxPurity) + 1));
                }
            }
        }
        super.beeReleasePostAction(level, beeEntity, state, beeState);
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

        List<ItemStack> filters = getInstalledUpgrades(LibItems.UPGRADE_ENTITY_FILTER.get());
        for (ItemStack filter : filters) {
            List<Identifier> entities = filter.getOrDefault(ModDataComponents.ENTITY_TYPE_LIST, new ArrayList<>());
            for (Identifier allowedBee : entities) {
                String type = BeeIngredientFactory.getIngredientKey(bee);
                if (allowedBee.toString().equals(type)) {
                    isInFilters = true;
                }
            }
        }

        return filters.isEmpty() || isInFilters;
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        input.readChild("inv", inventoryHandler);
        input.readChild("upgrades", upgradeHandler);
        MAX_BEES = input.getIntOr("max_bees", MAX_BEES);
        specialTickCounter = input.getIntOr("specialTickCounter", 0);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        output.putChild("inv", inventoryHandler);
        output.putChild("upgrades", upgradeHandler);
        output.putInt("max_bees", MAX_BEES);
        output.putInt("specialTickCounter", specialTickCounter);
    }

    @Override
    public int getContainerSize() {
        return inventoryHandler.size();
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return inventoryHandler.getStackInSlot(pSlot);
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return inventoryHandler.extractItem(pSlot, pAmount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return inventoryHandler.extractItem(pSlot, inventoryHandler.getStackInSlot(pSlot).getCount(), false);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        inventoryHandler.setStackInSlot(pSlot, pStack);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventoryHandler.size(); i++) {
            removeItem(i, inventoryHandler.getStackInSlot(i).getCount());
        }
    }
}
