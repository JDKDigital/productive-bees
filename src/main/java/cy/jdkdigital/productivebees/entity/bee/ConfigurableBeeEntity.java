package cy.jdkdigital.productivebees.entity.bee;

import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeAttribute;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurableBeeEntity extends ProductiveBeeEntity implements IEffectBeeEntity
{
    private int attackCooldown = 0;

    public static final DataParameter<String> TYPE = EntityDataManager.createKey(ConfigurableBeeEntity.class, DataSerializers.STRING);

    public ConfigurableBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && func_233678_J__() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.0D) {
                attackCooldown = getEffectCooldown(getAttributeValue(BeeAttributes.TEMPER));
                attackTarget(this.getAttackTarget());
            }
        }
    }

    public void attackTarget(LivingEntity target) {
        if (this.isAlive() && getNBTData().contains("attackResponse")) {
            String attackResponse = getNBTData().getString("attackResponse");
            switch (attackResponse) {
                case "fire":
                    target.setFire(200);
                case "lava":
                    // Place flowing lava on the targets location
                    this.world.setBlockState(target.getPosition(), Blocks.LAVA.getDefaultState().getBlockState(), 11);
            }
        }
    }

    public void setBeeType(String data) {
        this.dataManager.set(TYPE, data);
    }

    public String getBeeType() {
        return this.dataManager.get(TYPE);
    }

    public void setAttributes() {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains(("productivity"))) {
            beeAttributes.put(BeeAttributes.PRODUCTIVITY, nbt.getInt("productivity"));
        }
        if (nbt.contains(("temper"))) {
            beeAttributes.put(BeeAttributes.TEMPER, nbt.getInt("temper"));
        }
        if (nbt.contains(("endurance"))) {
            beeAttributes.put(BeeAttributes.ENDURANCE, nbt.getInt("endurance"));
        }
        if (nbt.contains(("behavior"))) {
            beeAttributes.put(BeeAttributes.BEHAVIOR, nbt.getInt("behavior"));
        }
        if (nbt.contains(("weather_tolerance"))) {
            beeAttributes.put(BeeAttributes.WEATHER_TOLERANCE, nbt.getInt("weather_tolerance"));
        }
        if (nbt.contains(("effect"))) {
            beeAttributes.put(BeeAttributes.EFFECTS, new BeeEffect(nbt.getCompound("effect")));
        }
    }

    @Override
    public Color getColor(int tintIndex) {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains("primaryColor")) {
            return tintIndex == 0 ? new Color(nbt.getInt("primaryColor")) : new Color(nbt.getInt("secondaryColor"));
        }
        return super.getColor(tintIndex);
    }

    @Nonnull
    @Override
    protected ITextComponent getProfessionName() {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains("name")) {
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

    protected CompoundNBT getNBTData() {
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(new ResourceLocation(getBeeType()));

        return nbt != null ? nbt : new CompoundNBT();
    }

    public boolean hasBeeTexture() {
        return getNBTData().contains("beeTexture");
    }

    public String getBeeTexture() {
        return getNBTData().getString("beeTexture");
    }

    // Effect bees
    private boolean isWithered() {
        return getNBTData().contains("withered") && getNBTData().getBoolean("withered");
    }
    private boolean hasMunchies() {
        return getNBTData().contains("munchies") && getNBTData().getBoolean("munchies");
    }

    @Override
    public Map<Effect, Integer> getAggressiveEffects() {
        if (isWithered()) {
            return new HashMap<Effect, Integer>()
            {{
                put(Effects.WITHER, 350);
            }};
        }
        if (hasMunchies()) {
            return new HashMap<Effect, Integer>()
            {{
                put(Effects.HUNGER, 530);
            }};
        }

        return null;
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        if (isWithered()) {
            return source.equals(DamageSource.WITHER) || super.isInvulnerableTo(source);
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPotionApplicable(EffectInstance effect) {
        if (isWithered()) {
            return effect.getPotion() != Effects.WITHER && super.isPotionApplicable(effect);
        }
        return super.isPotionApplicable(effect);
    }
}
