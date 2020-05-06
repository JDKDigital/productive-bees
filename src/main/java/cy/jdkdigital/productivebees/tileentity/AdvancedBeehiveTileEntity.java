package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedBeehiveTileEntity extends AdvancedBeehiveTileEntityAbstract implements INamedContainerProvider {

    private int tickCounter = 0;
    public static final int BOTTLE_SLOT = 0;
    public static final int[] OUTPUT_SLOTS = new int[] {1,2,3,4,5,6,7,8,9};
    public List<String> inhabitantList = new ArrayList<>();

	private LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> new ItemHandler(10));
	private LazyOptional<IItemHandler> bottleHandler = LazyOptional.of(() -> new ItemHandler(1));

	public AdvancedBeehiveTileEntity() {
	    super(ModTileEntityTypes.ADVANCED_BEEHIVE.get());
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
		return new TranslationTextComponent(ModBlocks.ADVANCED_OAK_BEEHIVE.get().getTranslationKey());
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

		if (++tickCounter > ProductiveBeesConfig.GENERAL.itemTickRate.get()) {
            tickCounter = 0;

            ListNBT beeList = this.getBeeListAsNBTList();
            if (beeList.size() > 0) {
                for (INBT inbt : beeList) {
                    CompoundNBT inb = (CompoundNBT)((CompoundNBT) inbt).get("EntityData");
                    String beeId = inb.getString("id");
                    int productivity = 0;
                    if (inb.contains("bee_productivity")) {
                        productivity = inb.getInt("bee_productivity");
                    }

                    Double productionRate = ProductiveBeeEntity.getProductionRate(beeId, 0.25D);

                    // Generate bee produce
                    if (productionRate != null && productionRate > 0) {
                        if (world.rand.nextDouble() < productionRate) {
                            int finalProductivity = productivity;
                            inventoryHandler.ifPresent(inv -> {
                                getBeeProduce(world, beeId).forEach((stack) -> {
                                    if (!stack.isEmpty()) {
                                        if (finalProductivity > 0) {
                                            float f = (float) finalProductivity * stack.getCount() * BeeAttributes.productivityModifier.generateFloat(world.rand);
                                            stack.grow(Math.round(f));
                                        }
                                        ((ItemHandler)inv).addOutput(stack);
                                    }
                                });
                            });
                        }
                    }
                }
            }

            // Spawn skeletal and zombie bees in available hives
            if (world.isNightTime() && beeList.size() < MAX_BEES && world.rand.nextFloat() < 0.01F) {
                EntityType<BeeEntity> beeType = world.rand.nextBoolean() ? ModEntities.SKELETAL_BEE.get() : ModEntities.ZOMBIE_BEE.get();
                BeeEntity newBee = beeType.create(world);
                if (newBee != null) {
                    tryEnterHive(newBee, false);
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
                    bottleHandler.ifPresent(h -> {
                        ItemStack bottles = h.getStackInSlot(BOTTLE_SLOT);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                            inventoryHandler.ifPresent(inv -> {
                                boolean addedBottle = ((ItemHandler) inv).addOutput(filledBottle);
                                if (addedBottle) {
                                    ((ItemHandler) inv).addOutput(new ItemStack(Items.HONEYCOMB));
                                    bottles.shrink(1);
                                    world.setBlockState(pos, blockState.with(BeehiveBlock.HONEY_LEVEL, finalHoneyLevel - 5));
                                }
                            });
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

        super.tick();
	}

	public static List<ItemStack> getBeeProduce(World world, String beeId) {
        for(Map.Entry<ResourceLocation, IRecipe<IInventory>> entry: world.getRecipeManager().getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE).entrySet()) {
            AdvancedBeehiveRecipe recipe = (AdvancedBeehiveRecipe) entry.getValue();
            if (beeId.equals(recipe.ingredient.getBeeType().getRegistryName().toString())) {
                ProductiveBees.LOGGER.info("getting recipe output " + recipe.outputs);
                return recipe.outputs;
            }
        }

        return Lists.newArrayList(ItemStack.EMPTY);
    }

	private int getAvailableOutputSlot(IItemHandler handler, ItemStack insertStack) {
	    int emptySlot = 0;
        for (int slot : OUTPUT_SLOTS) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.getItem() == insertStack.getItem() && (stack.getMaxStackSize() + insertStack.getCount()) != stack.getCount()) {
                return slot;
            }
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            }
        }
	    return emptySlot;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        CompoundNBT invTag = tag.getCompound("inv");
        inventoryHandler.ifPresent(inv -> ((INBTSerializable<CompoundNBT>) inv).deserializeNBT(invTag));

        CompoundNBT bottleTag = tag.getCompound("bottles");
        bottleHandler.ifPresent(bottle -> ((INBTSerializable<CompoundNBT>) bottle).deserializeNBT(bottleTag));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        inventoryHandler.ifPresent(inv -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) inv).serializeNBT();
            tag.put("inv", compound);
        });

        bottleHandler.ifPresent(bottle -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) bottle).serializeNBT();
            tag.put("bottles", compound);
        });

        return tag;
    }

    class ItemHandler extends ItemStackHandler {
        public ItemHandler(int size) {
            super(size);
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            AdvancedBeehiveTileEntity.this.markDirty();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot != BOTTLE_SLOT || stack.getItem() == Items.GLASS_BOTTLE;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        public boolean addOutput(@Nonnull ItemStack stack) {
            int slot = getAvailableOutputSlot(this, stack);
            if (slot > 0) {
                insertItem(slot, stack, false);
                return true;
            }
            return false;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
	    return this.getCapability(cap, side, false);
    }

    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side, @Nullable boolean getBottleHandler) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (getBottleHandler) {
                return bottleHandler.cast();
            }
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
