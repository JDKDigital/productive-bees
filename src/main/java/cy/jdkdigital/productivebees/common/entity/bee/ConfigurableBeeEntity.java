package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.NectarParticleType;
import cy.jdkdigital.productivebees.common.tileentity.AdvancedBeehiveTileEntity;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeAttributes;
import cy.jdkdigital.productivebees.util.BeeEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
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

            // Kill unconfigured bees
            if (getBeeType().isEmpty() && isAlive()) {
                attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
            }
        }
    }

    @Override
    public void addParticle(World worldIn, double xMin, double xMax, double zMin, double zMax, double posY, IParticleData particleData) {
        NectarParticleType particle = ModParticles.COLORED_FALLING_NECTAR.get();

        if (hasParticleColor()) {
            particle.setColor(getParticleColor());
        } else {
            particle.setColor(new float[]{0.92F, 0.782F, 0.72F});
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

    @Override
    public void notifyDataManagerChange(DataParameter<?> param) {
        if (TYPE.equals(param)) {
            recalculateSize();
        }
        super.notifyDataManagerChange(param);
        // /summon productivebees:configurable_bee ~ ~ ~ {"type":"productivebees:diamond", "NoAI":true, "HasNectar": true}
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
        if (!getBeeType().isEmpty()) {
            return super.getSize(poseIn).scale(getSizeModifier());
        }

        return super.getSize(poseIn);
    }

    public float getSizeModifier() {
        CompoundNBT nbt = getNBTData();
        return nbt.getFloat("size");
    }

    @Override
    public Tag<Block> getFlowerTag() {
        CompoundNBT nbt = getNBTData();
        if (nbt != null && nbt.contains("flowerTag")) {
            return ModTags.getTag(new ResourceLocation(nbt.getString("flowerTag")));
        }
        return super.getFlowerTag();
    }

    @Override
    public Tag<Block> getNestingTag() {
        CompoundNBT nbt = getNBTData();
        if (nbt != null && nbt.contains("nestingPreference")) {
            return ModTags.getTag(new ResourceLocation(nbt.getString("nestingPreference")));
        }
        return super.getFlowerTag();
    }

    @Override
    public BeeEffect getBeeEffect() {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains(("effect"))) {
            return new BeeEffect(nbt.getCompound("effect"));
        }
        return super.getBeeEffect();
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

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        ItemStack egg = super.getPickedResult(target);
        ModItemGroups.ModItemGroup.setTag(getBeeType(), egg);
        return egg;
    }

    protected CompoundNBT getNBTData() {
        CompoundNBT nbt = BeeReloadListener.INSTANCE.getData(getBeeType());

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

    // Traits
    public boolean isFireproof() {
        return getNBTData().getBoolean("fireproof");
    }

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

    public float[] getParticleColor() {
        Integer color = getNBTData().getInt("particleColor");
        if (!colorCache.containsKey(color)) {
            colorCache.put(color, (new Color(color)).getComponents(null));
        }
        return colorCache.get(color);
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
        if (isDraconic() && source.equals(DamageSource.DRAGON_BREATH)) {
            return true;
        }
        if (isTranslucent() && (source.equals(DamageSource.IN_WALL) || source.equals(DamageSource.ANVIL))) {
            return true;
        }
        if (isFireproof() && (source.equals(DamageSource.HOT_FLOOR) || source.equals(DamageSource.IN_FIRE) || source.equals(DamageSource.ON_FIRE) || source.equals(DamageSource.LAVA))) {
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
