package cy.jdkdigital.productivebees.common.block.entity;

import cy.jdkdigital.productivebees.init.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CombBlockBlockEntity extends AbstractBlockEntity
{
    private Identifier combType;

    public CombBlockBlockEntity(BlockPos pos, BlockState state) {
        this(null, pos, state);
    }

    public CombBlockBlockEntity(Identifier combType, BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.COMB_BLOCK.get(), pos, state);
        this.combType = combType;
    }

    public void setCombType(Identifier combType) {
        this.combType = combType;
    }

    public Identifier getCombType() {
        return combType;
    }

    public int getColor() {
        if (combType != null) {
            BeeData beeData = BeeRegistries.lookup(combType);
            if (beeData != null) {
                return beeData.primaryColor();
            }
        }
        return 0;
    }

    @Override
    public void savePacketNBT(ValueOutput output) {
        super.savePacketNBT(output);
        if (combType != null) {
            output.putString("type", combType.toString());
        }
    }

    @Override
    public void loadPacketNBT(ValueInput input) {
        super.loadPacketNBT(input);
        input.getString("type").ifPresent(s -> setCombType(Identifier.parse(s)));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (combType != null) {
            builder.set(ModDataComponents.BEE_TYPE.get(), combType);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter input) {
        super.applyImplicitComponents(input);
        Identifier type = input.get(ModDataComponents.BEE_TYPE.get());
        if (type != null) {
            setCombType(type);
        }
    }
}
