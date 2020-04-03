package cy.jdkdigital.productivebees.entity.bee.solitary;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.SolitaryBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.tags.Tag;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import java.util.function.Predicate;

public class NomadBeeEntity extends SolitaryBeeEntity {
    public NomadBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        this.nestBlockTag = ModTags.getTag(ModTags.ARID_NESTS);
    }

    @Override
    protected Predicate<BlockState> getFlowerPredicate() {
        return (blockState) -> {
            return blockState.isIn(ModTags.getTag(ModTags.DESERT_FLOWERS));
        };
    }
}
