package cy.jdkdigital.productivebees.client.render.item.property;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record NestAngle() implements RangeSelectItemModelProperty
{
    public static final MapCodec<NestAngle> MAP_CODEC = MapCodec.unit(NestAngle::new);

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        BlockPos targetPos = stack.get(ModDataComponents.POSITION.get());
        if (owner == null || targetPos == null) {
            return 0.5F;
        }
        Vec3 ownerPos = owner.position();
        double targetAngle = Math.atan2((double) targetPos.getZ() + 0.5 - ownerPos.z, (double) targetPos.getX() + 0.5 - ownerPos.x);
        double ownerYaw = Mth.positiveModulo((double) owner.getVisualRotationYInDegrees() / 360.0, 1.0);
        double targetAngleFraction = targetAngle / (Math.PI * 2);
        double finalAngle = 0.5 - (ownerYaw - 0.25 - targetAngleFraction);
        return (float) Mth.positiveModulo(finalAngle, 1.0);
    }

    @Override
    public MapCodec<NestAngle> type() {
        return MAP_CODEC;
    }
}
