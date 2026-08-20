package cy.jdkdigital.productivebees.common.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.item.BeeCage;
import cy.jdkdigital.productivebees.container.CryoStasisContainer;
import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivelib.common.block.entity.CapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CryoStasisBlockEntity extends CapabilityBlockEntity implements MenuProvider
{
    private static final String CRYO_BEES_KEY = "CryoBees";

    List<BeeEntry> cryoBees = new ArrayList<>();

    public static int SLOT_INPUT = 0;
    public static int SLOT_CAGE = 1;
    public static int SLOT_OUT = 2;
    public final InventoryHandlerHelper.BlockEntityItemStackHandler inventoryHandler = new InventoryHandlerHelper.BlockEntityItemStackHandler(3, this)
    {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
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
    };

    public CryoStasisBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.CRYO_STASIS.get(), pos, state);
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        if (!cryoBees.isEmpty()) {
            ValueOutput.TypedOutputList<BeeEntry> list = output.list(CRYO_BEES_KEY, BeeEntry.CODEC);
            for (BeeEntry entry : cryoBees) {
                list.add(entry);
            }
        }
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        cryoBees = new ArrayList<>();
        input.listOrEmpty(CRYO_BEES_KEY, BeeEntry.CODEC).stream().forEach(cryoBees::add);
    }

    public static <E extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState blockState, CryoStasisBlockEntity blockEntity) {
        var input = blockEntity.inventoryHandler.getStackInSlot(blockEntity.SLOT_INPUT);
        if (!input.isEmpty() && BeeCage.isFilled(input)) {
            var entity = BeeCage.getEntityFromStack(input, level, true);
            if (entity != null) {
                if (entity instanceof ProductiveBee pBee) {
//                    blockEntity.cryoBees.add(new BeeEntry(
//                            new Identifier(pBee.getBeeType()),
//                            true, 1200,
//                            pBee.getAttributeValue(GeneAttribute.PRODUCTIVITY),
//                            pBee.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE),
//                            pBee.getAttributeValue(GeneAttribute.BEHAVIOR),
//                            pBee.getAttributeValue(GeneAttribute.ENDURANCE),
//                            pBee.getAttributeValue(GeneAttribute.TEMPER)
//                    ));
                } else {
                    blockEntity.cryoBees.add(new BeeEntry(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), false, 1200, 0, 0, 0, 0, 0));
                }
//                input.shrink(1);
            }
        }
    }

    @Override
    public Component getName() {
        return Component.translatable(ModBlocks.CRYO_STASIS.get().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int index, Inventory inventory, Player player) {
        return new CryoStasisContainer(index, inventory, this);
    }

    public record BeeEntry(
            Identifier id,
            Boolean isProductive,
            Integer cooldown,
            Integer productivity,
            Integer weatherTolerance,
            Integer behavior,
            Integer endurance,
            Integer temper
    ) {
        public static final Codec<BeeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("id").forGetter(BeeEntry::id),
                Codec.BOOL.fieldOf("isProductive").forGetter(BeeEntry::isProductive),
                Codec.INT.fieldOf("cooldown").forGetter(BeeEntry::cooldown),
                Codec.INT.fieldOf("productivity").forGetter(BeeEntry::productivity),
                Codec.INT.fieldOf("weatherTolerance").forGetter(BeeEntry::weatherTolerance),
                Codec.INT.fieldOf("behavior").forGetter(BeeEntry::behavior),
                Codec.INT.fieldOf("endurance").forGetter(BeeEntry::endurance),
                Codec.INT.fieldOf("temper").forGetter(BeeEntry::temper)
        ).apply(instance, BeeEntry::new));
    }

    @Override
    public ResourceHandler<ItemResource> getItemHandler() {
        return inventoryHandler;
    }
}
