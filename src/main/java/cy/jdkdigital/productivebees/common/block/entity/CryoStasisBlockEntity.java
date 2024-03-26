package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.container.CryoStasisContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CryoStasisBlockEntity extends CapabilityBlockEntity
{
    List<BeeEntry> cryoBees = new ArrayList<>();

    public static int SLOT_INPUT = 0;
    public static int SLOT_CAGE = 1;
    public static int SLOT_OUT = 2;
    private final LazyOptional<IItemHandlerModifiable> inventoryHandler = LazyOptional.of(() -> new InventoryHandlerHelper.BlockEntityItemStackHandler(3, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return slot < SLOT_OUT && stack.getItem().asItem() instanceof BeeCage && (slot != SLOT_INPUT || BeeCage.isFilled(stack));
        }

        @Override
        public boolean isContainerItem(Item item) {
            return item instanceof BeeCage;
        }

        @Override
        public boolean isInputSlot(int slot) {
            return slot == SLOT_INPUT;
        }
    });

    public CryoStasisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CRYO_STASIS.get(), pos, state);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void savePacketNBT(CompoundTag tag) {
        super.savePacketNBT(tag);
        ListTag listNBT = new ListTag();
        cryoBees.forEach(beeEntry -> {
            listNBT.add(beeEntry.serializeNBT());
        });
        tag.put("BeeList", listNBT);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag) {
        super.loadPacketNBT(tag);
        if (tag.contains("BeeList")) {
            ListTag listNBT = tag.getList("BeeList", 10);
            listNBT.forEach(beeTag -> {
                this.cryoBees.add(BeeEntry.fromNbt((CompoundTag) beeTag));
            });
        }
    }

    public static <E extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, CryoStasisBlockEntity blockEntity) {
        blockEntity.inventoryHandler.ifPresent(inv -> {
            var input = inv.getStackInSlot(blockEntity.SLOT_INPUT);
            if (!input.isEmpty() && BeeCage.isFilled(input)) {
                var entity = BeeCage.getEntityFromStack(input.getTag(), level, true);
                if (entity != null) {
                    if (entity instanceof ProductiveBee pBee) {
                        blockEntity.cryoBees.add(new BeeEntry(
                                new ResourceLocation(pBee.getBeeType()),
                                true, 1200,
                                pBee.getAttributeValue(BeeAttributes.PRODUCTIVITY),
                                pBee.getAttributeValue(BeeAttributes.WEATHER_TOLERANCE),
                                pBee.getAttributeValue(BeeAttributes.BEHAVIOR),
                                pBee.getAttributeValue(BeeAttributes.ENDURANCE),
                                pBee.getAttributeValue(BeeAttributes.TEMPER)
                        ));
                    } else {
                        blockEntity.cryoBees.add(new BeeEntry(ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()), false, 1200, 0, 0, 0, 0, 0));
                    }
                    input.shrink(1);
                }
            }
        });
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CRYO_STASIS.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int index, Inventory inventory, Player player) {
        return new CryoStasisContainer(index, inventory, this);
    }

    static final class BeeEntry implements INBTSerializable<CompoundTag>
    {
        private final ResourceLocation id;
        private final Boolean isProductive;
        private final Integer cooldown;
        private final Integer productivity;
        private final Integer weatherTolerance;
        private final Integer behavior;
        private final Integer endurance;
        private final Integer temper;

        BeeEntry(ResourceLocation id, boolean isProductive, Integer cooldown, Integer productivity, Integer weatherTolerance, Integer behavior, Integer endurance, Integer temper) {
            this.id = id;
            this.isProductive = isProductive;
            this.cooldown = cooldown;
            this.productivity = productivity;
            this.weatherTolerance = weatherTolerance;
            this.behavior = behavior;
            this.endurance = endurance;
            this.temper = temper;
        }

        public static BeeEntry fromNbt(CompoundTag tag) {
            return new BeeEntry(
                    new ResourceLocation(tag.getString("id")),
                    tag.getBoolean("isProductive"),
                    tag.getInt("cooldown"),
                    tag.getInt("productivity"),
                    tag.getInt("weatherTolerance"),
                    tag.getInt("behavior"),
                    tag.getInt("endurance"),
                    tag.getInt("temper")
                    );
        }

        @Override
        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            tag.putString("id", id.toString());
            tag.putBoolean("isProductive", isProductive);
            tag.putInt("cooldown", cooldown);
            tag.putInt("productivity", productivity);
            tag.putInt("weatherTolerance", weatherTolerance);
            tag.putInt("behavior", behavior);
            tag.putInt("endurance", endurance);
            tag.putInt("temper", temper);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
        }

        public ResourceLocation id() {
            return id;
        }

        public Integer cooldown() {
            return cooldown;
        }

        public Integer productivity() {
            return productivity;
        }

        public Integer weatherTolerance() {
            return weatherTolerance;
        }

        public Integer behavior() {
            return behavior;
        }

        public Integer endurance() {
            return endurance;
        }

        public Integer temper() {
            return temper;
        }
    }
}
