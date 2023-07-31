package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Event;

public class BeeReleaseEvent extends Event
{
    private final Level level;
    private final Bee beeEntity;
    private final AdvancedBeehiveBlockEntityAbstract blockEntity;
    private final BlockState state;
    private final BeehiveBlockEntity.BeeReleaseStatus beeState;

    public BeeReleaseEvent(Level level, Bee beeEntity, AdvancedBeehiveBlockEntityAbstract blockEntity, BlockState state, BeehiveBlockEntity.BeeReleaseStatus beeState)
    {
        this.level = level;
        this.beeEntity = beeEntity;
        this.blockEntity = blockEntity;
        this.state = state;
        this.beeState = beeState;
    }

    public Level getLevel() {
        return level;
    }

    public Bee getBee() {
        return beeEntity;
    }

    public AdvancedBeehiveBlockEntityAbstract getBlockEntity() {
        return blockEntity;
    }

    public BlockState getState() {
        return state;
    }

    public BeehiveBlockEntity.BeeReleaseStatus getBeeState() {
        return beeState;
    }
}
