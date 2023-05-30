package cy.jdkdigital.productivebees.common.entity.bee.hive;

import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WannaBee extends ProductiveBee
{
    public static final UUID WANNA_BEE_UUID = UUID.nameUUIDFromBytes("pb_wanna_bee".getBytes(StandardCharsets.UTF_8)); // 4b9dd067-5433-3648-90a3-0d48ac6041f7

    public WannaBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean canSelfBreed() {
        return false;
    }

    @Override
    public String getRenderer() {
        return "thicc";
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        return flowerBlock.is(ModBlocks.AMBER.get());
    }
}
