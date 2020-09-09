package cy.jdkdigital.productivebees.entity.bee.nesting;

import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

public class MagmaticBeeEntity extends EffectHiveBeeEntity
{
    private int lavaDuration = 0;
    private BlockPos lavaPosition = null;

    public MagmaticBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
        beeAttributes.put(BeeAttributes.FOOD_SOURCE, ModTags.MAGMATIC_FLOWERS);
        beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.NETHER_BRICK_NESTS);
        beeAttributes.put(BeeAttributes.EFFECTS, new BeeEffect(new HashMap<Effect, Integer>() {{
            put(Effects.FIRE_RESISTANCE, 400);
        }}));
    }

    @Override
    public void tick() {
        super.tick();
        // Remove lava source after a few ticks
        if (this.lavaPosition != null && --this.lavaDuration <= 0) {
            this.world.setBlockState(this.lavaPosition, Blocks.AIR.getDefaultState(), 11);
            lavaPosition = null;
        }
    }

    public float getBrightness() {
        return 1.0F;
    }

    public boolean isBurning() {
        return this.isAngry();
    }

    public void attackTarget(LivingEntity target) {
        if (this.isAlive()) {
            // Place flowing lava on the targets location
            this.lavaPosition = target.getPosition();
            this.lavaDuration = 100;
            this.world.setBlockState(lavaPosition, Blocks.LAVA.getDefaultState().getBlockState(), 11);
        }
    }
}
