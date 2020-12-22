package cy.jdkdigital.productivebees.mixin;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IAngerable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeeEntity.class)
public abstract class BeeSpawnerMixin extends AnimalEntity implements IAngerable
{
    private BeeSpawnerMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
        method = "readAdditional(Lnet/minecraft/nbt/CompoundNBT;)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/BeeEntity;readAngerNBT(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/nbt/CompoundNBT;)V", shift = At.Shift.BEFORE),
        cancellable = true
    )
    private void worldReadAngerNBT(CompoundNBT tag, CallbackInfo ci) {
        if (!this.world.isRemote) {
            this.readAngerNBT((ServerWorld) world, tag);
        }

        ci.cancel();
    }
}