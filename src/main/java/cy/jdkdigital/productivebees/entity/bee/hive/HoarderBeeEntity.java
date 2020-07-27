package cy.jdkdigital.productivebees.entity.bee.hive;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.EffectHiveBeeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

public class HoarderBeeEntity extends EffectHiveBeeEntity
{
    protected static final DataParameter<Byte> PEEK_TICK = EntityDataManager.createKey(HoarderBeeEntity.class, DataSerializers.BYTE);
    private float prevPeekAmount;
    private float peekAmount;

    public HoarderBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PEEK_TICK, (byte)0);
    }

    @Override
    public void tick() {
        super.tick();

        float f1 = (float)this.getPeekTick() * 0.01F;
        prevPeekAmount = peekAmount;
        if (peekAmount > f1) {
            peekAmount = MathHelper.clamp(peekAmount - 0.05F, f1, 1.0F);
        } else if (peekAmount < f1) {
            peekAmount = MathHelper.clamp(peekAmount + 0.05F, 0.0F, f1);
        }
        ProductiveBees.LOGGER.info("peekAmount:" + f1 + " - " + peekAmount + "/" + prevPeekAmount);

        if (ticksExisted % 200 == 0) {
            this.dataManager.set(PEEK_TICK, (byte)100);
        }
        if (ticksExisted % 300 == 0) {
            this.dataManager.set(PEEK_TICK, (byte)0);
        }
    }

    public int getPeekTick() {
        return this.dataManager.get(PEEK_TICK);
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientPeekAmount(float p_184688_1_) {
        return MathHelper.lerp(p_184688_1_, this.prevPeekAmount, this.peekAmount);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.dataManager.set(PEEK_TICK, compound.getByte("Peek"));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putByte("Peek", this.dataManager.get(PEEK_TICK));
    }
}
