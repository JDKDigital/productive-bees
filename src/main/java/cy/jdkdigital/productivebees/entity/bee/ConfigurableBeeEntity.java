package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;

public class ConfigurableBeeEntity extends ProductiveBeeEntity
{
    public static final DataParameter<String> TYPE = EntityDataManager.createKey(ConfigurableBeeEntity.class, DataSerializers.STRING);

    public ConfigurableBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setBeeType(String data) {
        this.dataManager.set(TYPE, data);
    }

    public String getBeeType() {
        return this.dataManager.get(TYPE);
    }

    @Override
    public Color getColor(int tintIndex) {
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(getBeeType()));
        if (nbt != null && nbt.contains("primaryColor")) {
            return tintIndex == 0 ? new Color(nbt.getInt("primaryColor")) : new Color(nbt.getInt("secondaryColor"));
        }
        return super.getColor(tintIndex);
    }

    @Nonnull
    @Override
    protected ITextComponent getProfessionName() {
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(getBeeType()));
        if (nbt != null && nbt.contains("name")) {
            return new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name"));
        }
        return super.getProfessionName();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(TYPE, "");
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        setBeeType(compound.getString("type"));
    }

    @Override
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("type", getBeeType());
    }
}
