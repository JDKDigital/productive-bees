package cy.jdkdigital.productivebees.block.nest;

import cy.jdkdigital.productivebees.block.SolitaryNest;
import cy.jdkdigital.productivebees.init.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.Dimension;

public class GlowstoneNest extends SolitaryNest
{
    public GlowstoneNest(Properties properties) {
        super(properties);
    }

    @Override
    public EntityType<BeeEntity> getNestingBeeType(World world) {
        return ModEntities.GLOWING_BEE.get();
    }

    @Override
    public boolean canRepopulateIn(Dimension dimension, Biome biome) {
        return dimension.isNether();
    }
}
