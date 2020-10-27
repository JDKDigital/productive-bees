package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.client.particle.NectarParticleType;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigurableBeeEntity extends ProductiveBeeEntity implements IEffectBeeEntity
{
    private int attackCooldown = 0;
    public int breathCollectionCooldown = 600;
    private int teleportCooldown = 250;

    // Color calc cache
    private static Map<Integer, float[]> colorCache = new HashMap<>();

    public static final DataParameter<String> TYPE = EntityDataManager.createKey(ConfigurableBeeEntity.class, DataSerializers.STRING);

    public ConfigurableBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE ||
                poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() ||
                poiType == ModPointOfInterestTypes.SOLITARY_NEST.get() ||
                (poiType == ModPointOfInterestTypes.DRACONIC_NEST.get() && isDraconic()) ||
                (poiType == ModPointOfInterestTypes.SUGARBAG_NEST.get() && getBeeType().equals("productivebees:sugarbag"));
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (!this.world.isRemote) {
            --teleportCooldown;
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && isAngry() && this.getAttackTarget() != null && this.getAttackTarget().getDistanceSq(this) < 4.0D) {
                attackCooldown = getEffectCooldown(getAttributeValue(BeeAttributes.TEMPER));
                attackTarget(this.getAttackTarget());
            }

            // Draconic bees
            if (--breathCollectionCooldown <= 0) {
                breathCollectionCooldown = 600;
                if (isDraconic() && this.world.dimension.getType() == DimensionType.THE_END) {
                    this.setHasNectar(true);
                }
            }

            if (ticksExisted % 100 == 0 && isSlimy()) {
                int i = 1;
                for (int j = 0; j < i * 8; ++j) {
                    float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
                    float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
                    float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                    float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
                    this.world.addParticle(ParticleTypes.ITEM_SLIME, this.getPosX() + (double) f2, this.getPosY(), this.getPosZ() + (double) f3, 0.0D, 0.0D, 0.0D);
                }
            }

            if (ticksExisted % 20 == 0 && hasNectar() && isRedstoned()) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos beePosDown = this.getPosition().down(i);
                    if (world.isAirBlock(beePosDown)) {
                        BlockState redstoneState = ModBlocks.INVISIBLE_REDSTONE_BLOCK.get().getDefaultState();
                        world.setBlockState(beePosDown, redstoneState, 3);
                        world.getPendingBlockTicks().scheduleTick(beePosDown, redstoneState.getBlock(), 20);
                    }
                }
            }
        }
    }

    @Override
    public void addParticle(World worldIn, double xMin, double xMax, double zMin, double zMax, double posY, IParticleData particleData) {
        NectarParticleType particle = ModParticles.COLORED_FALLING_NECTAR.get();

        if (hasParticleColor()) {
            Integer color = getParticleColor();
            if (!colorCache.containsKey(color)) {
                colorCache.put(color, (new Color(color)).getComponents(null));
            }
            particle.setColor(colorCache.get(color));
        }

        worldIn.addParticle(particle, MathHelper.lerp(worldIn.rand.nextDouble(), xMin, xMax), posY, MathHelper.lerp(worldIn.rand.nextDouble(), zMin, zMax), 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void updateAITasks() {
        // Teleport to active path
        if (this.teleportCooldown <= 0) {
            if (null != this.navigator.getPath() && isTeleporting()) {
                if (this.hasHive()) {
                    TileEntity te = world.getTileEntity(this.getHivePos());
                    if (te instanceof AdvancedBeehiveTileEntity) {
                        int antiTeleportUpgrades = ((AdvancedBeehiveTileEntity) te).getUpgradeCount(ModItems.UPGRADE_ANTI_TELEPORT.get());
                        if (antiTeleportUpgrades > 0) {
                            this.teleportCooldown = 10000;
                            super.updateAITasks();
                            return;
                        }
                    }
                }
                BlockPos pos = this.navigator.getPath().getTarget();
                teleportTo(pos.getX(), pos.getY(), pos.getZ());
            }
            this.teleportCooldown = 250;
        }

        super.updateAITasks();
    }

//    @Override
//    public boolean isBurning() {
//        return this.isAngry();
//    }

    public void attackTarget(LivingEntity target) {
        if (this.isAlive() && getNBTData().contains("attackResponse")) {
            String attackResponse = getNBTData().getString("attackResponse");
            switch (attackResponse) {
                case "fire":
                    target.setFire(200);
                case "lava":
                    // Place flowing lava on the targets location
                    this.world.setBlockState(target.getPosition(), Blocks.LAVA.getDefaultState(), 11);
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
        if (nbt.contains(("nestingPreference"))) {
            beeAttributes.put(BeeAttributes.NESTING_PREFERENCE, ModTags.getTag(new ResourceLocation(nbt.getString("nestingPreference"))));
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

    @Nonnull
    @Override
    public EntitySize getSize(Pose poseIn) {
        CompoundNBT nbt = getNBTData();
        return super.getSize(poseIn).scale(nbt.getFloat("size"));
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

    public String getRenderer() {
        return getNBTData().getString("renderer");
    }

    // Effect bees
    public boolean isWithered() {
        return getNBTData().getBoolean("withered");
    }

    public boolean isTranslucent() {
        return getNBTData().getBoolean("translucent");
    }

    public boolean isBlinding() {
        return getNBTData().getBoolean("blinding");
    }

    public boolean isDraconic() {
        return getNBTData().getBoolean("draconic");
    }

    public boolean isRedstoned() {
        return getNBTData().getBoolean("redstoned");
    }

    public boolean isSlimy() {
        return getNBTData().getBoolean("slimy");
    }

    public boolean isTeleporting() {
        return getNBTData().getBoolean("teleporting");
    }

    public boolean hasMunchies() {
        return getNBTData().getBoolean("munchies");
    }

    public boolean hasParticleColor() {
        return getNBTData().contains("particleColor");
    }

    public Integer getParticleColor() {
        return getNBTData().getInt("particleColor");
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
        if (isBlinding()) {
            return new HashMap<Effect, Integer>()
            {{
                put(Effects.BLINDNESS, 450);
            }};
        }

        return null;
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull DamageSource source) {
        if (isWithered() && source.equals(DamageSource.WITHER)) {
            return true;
        }
        if (isTranslucent() && (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.ANVIL))) {
            return true;
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

    private void teleportTo(double x, double y, double z) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while (blockpos$mutable.getY() > 0 && !this.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = this.world.getBlockState(blockpos$mutable);
        if (blockstate.getMaterial().blocksMovement()) {
            EnderTeleportEvent event = new EnderTeleportEvent(this, x, y, z, 0);
            if (!MinecraftForge.EVENT_BUS.post(event)) {
                boolean teleported = this.attemptTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (teleported) {
                    this.world.playSound(null, this.prevPosX, this.prevPosY, this.prevPosZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 0.3F, 1.0F);
                    this.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 0.2F, 1.0F);
                }
            }
        }
    }
}
