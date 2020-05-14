package cy.jdkdigital.productivebees.tileentity;

import com.google.common.collect.Lists;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.block.AdvancedBeehive;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModTileEntityTypes;
import cy.jdkdigital.productivebees.recipe.AdvancedBeehiveRecipe;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import net.minecraft.block.BeehiveBlock;
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
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
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
import java.util.Map;
import java.util.Random;

public class AdvancedBeehiveTileEntity extends AdvancedBeehiveTileEntityAbstract implements INamedContainerProvider
{
    private static final Random rand = new Random();

    private int tickCounter = 0;
    private int abandonCountdown = 0;
    public List<String> inhabitantList = new ArrayList<>();

    private LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> ItemHandlerHelper.getInventoryHandler(this, 1));

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
                    CompoundNBT inb = (CompoundNBT) ((CompoundNBT) inbt).get("EntityData");
                    String beeId = inb.getString("id");
                    int productivity = 0;
                    if (inb.contains("bee_productivity")) {
                        productivity = inb.getInt("bee_productivity");
                    }

                    Double productionRate = ProductiveBeeEntity.getProductionRate(beeId, 0.25D);

                    // Generate bee produce
                    if (productionRate != null && productionRate > 0) {
                        if (world.rand.nextDouble() <= productionRate) {
                            int finalProductivity = productivity;
                            inventoryHandler.ifPresent(inv -> {
                                getBeeProduce(world, beeId).forEach((stack) -> {
                                    if (!stack.isEmpty()) {
                                        if (finalProductivity > 0) {
                                            float f = (float) finalProductivity * stack.getCount() * BeeAttributes.productivityModifier.generateFloat(world.rand);
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

            // Spawn skeletal and zombie bees in available hives
            if (
                    world.isNightTime() &&
                            ProductiveBeesConfig.BEES.spawnUndeadBees.get() &&
                            world.rand.nextDouble() <= ProductiveBeesConfig.BEES.spawnUndeadBeesChance.get() &&
                            beeList.size() < MAX_BEES &&
                            beeList.size() + beesOutsideHive() < MAX_BEES &&
                            world.getLight(pos.offset(getBlockState().get(BlockStateProperties.FACING), 1)) <= 7
            ) {
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
                    inventoryHandler.ifPresent(inv -> {
                        ItemStack bottles = inv.getStackInSlot(ItemHandlerHelper.BOTTLE_SLOT);
                        if (!bottles.isEmpty()) {
                            final ItemStack filledBottle = new ItemStack(Items.HONEY_BOTTLE);
                            boolean addedBottle = ((ItemHandlerHelper.ItemHandler) inv).addOutput(filledBottle);
                            if (addedBottle) {
                                ((ItemHandlerHelper.ItemHandler) inv).addOutput(new ItemStack(Items.HONEYCOMB));
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
    }

    public static List<ItemStack> getBeeProduce(World world, String beeId) {
        for (Map.Entry<ResourceLocation, IRecipe<IInventory>> entry : world.getRecipeManager().getRecipes(AdvancedBeehiveRecipe.ADVANCED_BEEHIVE).entrySet()) {
            AdvancedBeehiveRecipe recipe = (AdvancedBeehiveRecipe) entry.getValue();
            if (beeId.equals(recipe.ingredient.getBeeType().getRegistryName().toString())) {
                List<ItemStack> outputList = new ArrayList<>();
                recipe.outputs.forEach((itemStack, bounds) -> {
                    int count = MathHelper.nextInt(rand, MathHelper.floor(bounds.getLeft()), MathHelper.floor(bounds.getRight()));
                    itemStack.setCount(count);
                    outputList.add(itemStack);
                });
                return outputList;
            }
        }

        return Lists.newArrayList(ItemStack.EMPTY);
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
