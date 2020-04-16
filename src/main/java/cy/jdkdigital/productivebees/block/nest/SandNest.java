package cy.jdkdigital.productivebees.block.nest;

import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

public class SandNest extends SolitaryNest {

    public SandNest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return dimension.isSurfaceWorld() && biome.getCategory().equals(Biome.Category.DESERT);
    }

    @Override
    public EntityType<BeeEntity> getNestingBeeType(World world) {
        return world.rand.nextBoolean() ? ModEntities.NOMAD_BEE.get() : ModEntities.DIGGER_BEE.get();
    }
}
