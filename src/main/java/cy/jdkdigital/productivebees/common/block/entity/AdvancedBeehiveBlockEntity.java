package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class AdvancedBeehiveBlockEntity extends AdvancedBeehiveBlockEntityAbstract implements MenuProvider, UpgradeableBlockEntity
{
    protected int tickCounter = 0;
    private int abandonCountdown = 0;
    protected boolean hasTicked = false;

    protected LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.ItemHandler(11, this));
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
            ListTag beeList = getBeeListAsNBTList(blockEntity);
            BlockPos front = pos.relative(state.getValue(BeehiveBlock.FACING));
            if (
                    level.isNight() &&
                    ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                    level.random.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                    beeList.size() + blockEntity.beesOutsideHive() == 0 &&
                    level.getBlockState(front).isAir() &&
                    level.getBrightness(LightLayer.BLOCK, front) <= 8
            ) {
                EntityType<ConfigurableBee> beeType = ModEntities.CONFIGURABLE_BEE.get();
                ConfigurableBee newBee = beeType.create(level);
                if (newBee != null) {
                    if (level.random.nextBoolean()) {
                        newBee.setBeeType("productivebees:skeletal");
                    } else {
                        newBee.setBeeType("productivebees:zombie");
                    }
                    newBee.setAttributes();

                    blockEntity.addOccupant(newBee, false);
                }
                blockEntity.setChanged();
            }
        }

        if (!blockEntity.hasTicked && blockEntity.tickCounter % 23 == 0) {
            if (state.getBlock() instanceof AdvancedBeehive) {
                int honeyLevel = state.getValue(BeehiveBlock.HONEY_LEVEL);

                // Auto harvest if empty bottles are in
                if (honeyLevel >= 5) {
                    int finalHoneyLevel = honeyLevel;
                    blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(InventoryHandlerHelper.BOTTLE_SLOT);
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
        double combBlockUpgradeModifier = getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) * ProductiveBeesConfig.UPGRADES.combBlockTimeModifier.get();
        double timeUpgradeModifier = 1 - (getUpgradeCount(ModItems.UPGRADE_TIME.get()) * ProductiveBeesConfig.UPGRADES.timeBonus.get());
        return (int) (
                super.getTimeInHive(hasNectar, beeEntity) * Math.max(0, timeUpgradeModifier + combBlockUpgradeModifier)
        );
    }

    @Override
    protected void beeReleasePostAction(Level level, Bee beeEntity, BlockState state, BeeReleaseStatus beeState) {
        super.beeReleasePostAction(level, beeEntity, state, beeState);

        // Generate bee produce
        if (this.level != null && beeState == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
            getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
                BeeHelper.getBeeProduce(this.level, beeEntity, getUpgradeCount(ModItems.UPGRADE_COMB_BLOCK.get()) > 0).forEach((stackIn) -> {
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
        if (breedingUpgrades > 0 && !beeEntity.isBaby() && getOccupantCount() > 0 && this.level.random.nextFloat() <= (ProductiveBeesConfig.UPGRADES.breedingChance.get() * breedingUpgrades)) {
            boolean canBreed = !(beeEntity instanceof ProductiveBee) || ((ProductiveBee) beeEntity).canSelfBreed();
            if (canBreed) {
                // Count nearby bee entities
                List<Bee> bees = this.level.getEntitiesOfClass(Bee.class, (new AABB(worldPosition).expandTowards(3.0D, 3.0D, 3.0D)));
                if (bees.size() < ProductiveBeesConfig.UPGRADES.breedingMaxNearbyEntities.get()) {
                    // Breed this bee with a random bee inside
                    Inhabitant otherBeeInhabitant = getBeeList().get(this.level.random.nextInt(getOccupantCount()));
                    Entity otherBee = EntityType.loadEntityRecursive(otherBeeInhabitant.nbt, this.level, (spawnedEntity) -> spawnedEntity);
                    if (otherBee instanceof Bee) {
                        Entity offspring = BeeHelper.getBreedingResult(beeEntity, (Bee) otherBee, (ServerLevel) this.level);
                        if (offspring != null) {
                            if (offspring instanceof ProductiveBee && beeEntity instanceof ProductiveBee) {
                                BeeHelper.setOffspringAttributes((ProductiveBee) offspring, (ProductiveBee) beeEntity, (Bee) otherBee);
                            }
                            if (offspring instanceof Animal) {
                                ((Animal) offspring).setAge(-24000);
                            }
                            offspring.moveTo(beeEntity.getX(), beeEntity.getY(), beeEntity.getZ(), 0.0F, 0.0F);
                            this.level.addFreshEntity(offspring);
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
    public void load(CompoundTag tag) {
        super.load(tag);

        CompoundTag invTag = tag.getCompound("inv");
        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(invTag));

        CompoundTag upgradesTag = tag.getCompound("upgrades");
        upgradeHandler.ifPresent(inv -> ((INBTSerializable<CompoundTag>) inv).deserializeNBT(upgradesTag));

        // Reset MAX_BEES
        MAX_BEES = tag.contains("max_bees") ? tag.getInt("max_bees") : MAX_BEES;
    }

    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);

        this.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        upgradeHandler.ifPresent(inv -> {
            CompoundTag compound = ((INBTSerializable<CompoundTag>) inv).serializeNBT();
            tag.put("upgrades", compound);
        });

        tag.putInt("max_bees", MAX_BEES);

        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();

        tag.put("bees", getBeeListAsNBTList(this));

        return new ClientboundBlockEntityDataPacket(worldPosition, -1, tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();

        if (tag.contains("bees")) {
            getCapability(CapabilityBee.BEE).ifPresent(inhabitantHandler -> {
                inhabitantHandler.setInhabitantsFromListNBT((ListTag) tag.get("bees"));
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
