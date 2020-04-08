package cy.jdkdigital.productivebees.tileentity;

import com.electronwill.nightconfig.core.Config;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedBeehiveTileEntity extends AdvancedBeehiveTileEntityAbstract implements INamedContainerProvider {

    private int tickCounter = 0;
    public static final int BOTTLE_SLOT = 0;
    public static final int[] OUTPUT_SLOTS = new int[] {1,2,3,4,5,6,7,8,9};
    public List<String> inhabitantList = new ArrayList<>();

	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createInventoryHandler);

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
		return new TranslationTextComponent(ModBlocks.ADVANCED_BEEHIVE.get().getTranslationKey());
	}

	@Override
	public void tick() {
		final World world = this.world;
		if (world == null || world.isRemote()) {
			return;
		}

		if (++tickCounter > ProductiveBeesConfig.GENERAL.itemTickRate.get()) {
            tickCounter = 0;

            ListNBT beeList = ((AdvancedBeehiveTileEntity)world.getTileEntity(this.pos)).getBeeListAsNBTList();
            if (beeList.size() > 0) {
                for (INBT inbt : beeList) {
                    CompoundNBT inb = (CompoundNBT) inbt;
                    String beeId = ((CompoundNBT) inb.get("EntityData")).getString("id");

                    EntityType<BeeEntity> entityType = (EntityType<BeeEntity>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(beeId));
                    BeeEntity bee = entityType.create(world);

                    Config productionList = ProductiveBeeEntity.getProductionList(beeId);

                    // Generate bee produce
                    if (productionList != null) {
                        for (Map.Entry<String, Object> entry : productionList.valueMap().entrySet()) {
                            Double value = (Double) entry.getValue();
                            if (world.rand.nextDouble() < value) {
                                LootTable lootTable = ProductiveBeeEntity.getProductionLootTable(world, beeId);
                                this.handler.ifPresent(itemHandler -> {
                                    LootContext ctx =  new LootContext.Builder((ServerWorld) world)
                                        .withRandom(world.rand)
                                        .withParameter(LootParameters.THIS_ENTITY, bee)
                                        .withParameter(LootParameters.POSITION, this.pos)
                                        .withParameter(LootParameters.DAMAGE_SOURCE, DamageSource.GENERIC)
                                        .build(LootParameterSets.ENTITY);

                                    List<ItemStack> stacks = lootTable.generate(ctx);
                                    net.minecraftforge.common.ForgeHooks.modifyLoot(stacks, ctx).forEach((stack) -> {
                                        int slot = getAvailableOutputSlot(itemHandler, stack);
                                        if (slot > 0) {
                                            itemHandler.insertItem(slot, stack, false);
                                        }
                                    });
                                });
                            }
                        }
                    }
                }
            }

            // Update any attached expansion box if the honey level reaches max
            BlockState blockState = this.getBlockState();
            int honeyLevel = blockState.get(AdvancedBeehiveAbstract.HONEY_LEVEL);
            if (blockState.getBlock() instanceof AdvancedBeehive && blockState.get(AdvancedBeehive.EXPANDED)) {
                if (honeyLevel >= getMaxHoneyLevel(blockState)) {
                    ((AdvancedBeehive) blockState.getBlock()).updateState(world, this.getPos(), blockState, false);
                }
            }
        }

        super.tick();
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
    public CompoundNBT getUpdateTag() {
//        LOGGER.info("getUpdateTag " + this.world);
        CompoundNBT tag = super.getUpdateTag();
        if (this.getBeeListAsNBTList().size() > 0) {
            tag.put("bees", this.getBeeListAsNBTList());
        }
        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>) h).deserializeNBT(invTag));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h -> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>) h).serializeNBT();
            tag.put("inv", compound);
        });

        return super.write(tag);
    }
	
    private IItemHandler createInventoryHandler() {
        return new ItemStackHandler(10) {

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
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }
}
