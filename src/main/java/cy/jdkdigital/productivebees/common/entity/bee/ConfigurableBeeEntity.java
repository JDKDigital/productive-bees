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
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.EntityTeleportEvent.EnderEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    public static final DataParameter<String> TYPE = EntityDataManager.defineId(ConfigurableBeeEntity.class, DataSerializers.STRING);

    public ConfigurableBeeEntity(EntityType<? extends BeeEntity> entityType, World world) {
        super(entityType, world);

        beehiveInterests = (poiType) -> poiType == PointOfInterestType.BEEHIVE ||
                (poiType == ModPointOfInterestTypes.SOLITARY_HIVE.get() && isWild()) ||
                (poiType == ModPointOfInterestTypes.SOLITARY_NEST.get() && isWild()) ||
                (poiType == ModPointOfInterestTypes.DRACONIC_NEST.get() && isDraconic()) ||
                (poiType == ModPointOfInterestTypes.SUGARBAG_NEST.get() && getBeeType().equals("productivebees:sugarbag"));
    }

    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable ILivingEntityData livingEntityData, @Nullable CompoundNBT tag) {
        String type = "";
        if (tag != null) {
            type = tag.contains("type") ? tag.getString("type") : tag.contains("EntityTag") ? tag.getCompound("EntityTag").getString("type") : "";
        }

        if (type.equals("productivebees:ghostly") && ProductiveBees.rand.nextFloat() < 0.02f) {
            this.setCustomName(new StringTextComponent("BooBee"));
        } else if (type.equals("productivebees:blitz") && ProductiveBees.rand.nextFloat() < 0.02f) {
            this.setCustomName(new StringTextComponent("King BitzBee"));
        } else if (type.equals("productivebees:basalz") && ProductiveBees.rand.nextFloat() < 0.02f) {
            this.setCustomName(new StringTextComponent("Queen BazBee"));
        } else if (type.equals("productivebees:blizz") && ProductiveBees.rand.nextFloat() < 0.02f) {
            this.setCustomName(new StringTextComponent("Shiny BizBee"));
        } else if (type.equals("productivebees:destabilized_redstone") && ProductiveBees.rand.nextFloat() < 0.10f) {
            this.setCustomName(new StringTextComponent("Destabilized RedaStone Bee"));
        }

        return super.finalizeSpawn(world, difficulty, spawnReason, livingEntityData, tag);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            --teleportCooldown;
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && isAngry() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0D) {
                attackCooldown = getEffectCooldown(getAttributeValue(BeeAttributes.TEMPER));
                attackTarget(this.getTarget());
            }

            // Draconic bees
            if (--breathCollectionCooldown <= 0) {
                breathCollectionCooldown = 600;
                if (isDraconic() && level.dimension() == World.END) {
                    this.setHasNectar(true);
                }
            }

            if (tickCount % 20 == 0 && hasNectar() && isRedstoned()) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos beePosDown = this.blockPosition().below(i);
                    if (level.isEmptyBlock(beePosDown)) {
                        BlockState redstoneState = ModBlocks.INVISIBLE_REDSTONE_BLOCK.get().defaultBlockState();
                        level.setBlockAndUpdate(beePosDown, redstoneState);
                        level.getBlockTicks().scheduleTick(beePosDown, redstoneState.getBlock(), 20);
                    }
                }
            }

            // Kill unconfigured bees
            if (tickCount > 100 && getBeeType().isEmpty() && isAlive()) {
                this.kill();
            }
        }
    }

    @Override
    public void spawnFluidParticle(World worldIn, double xMin, double xMax, double zMin, double zMax, double posY, IParticleData particleData) {
        NectarParticleType particle;
        switch (getParticleType()) {
            case "pop":
                particle = ModParticles.COLORED_POPPING_NECTAR.get();
                break;
            case "lava":
                particle = ModParticles.COLORED_LAVA_NECTAR.get();
                break;
            case "portal":
                particle = ModParticles.COLORED_PORTAL_NECTAR.get();
                break;
            case "drip":
            default:
                particle = ModParticles.COLORED_FALLING_NECTAR.get();
                break;
        }

        if (hasParticleColor()) {
            particle.setColor(getParticleColor());
        } else {
            particle.setColor(new float[]{0.92F, 0.782F, 0.72F});
        }

        worldIn.addParticle(particle, MathHelper.lerp(worldIn.random.nextDouble(), xMin, xMax), posY, MathHelper.lerp(worldIn.random.nextDouble(), zMin, zMax), 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void customServerAiStep() {
        // Teleport to active path
        if (this.teleportCooldown <= 0) {
            if (null != this.navigation.getPath() && isTeleporting()) {
                if (this.hasHive()) {
                    TileEntity te = level.getBlockEntity(this.getHivePos());
                    if (te instanceof AdvancedBeehiveTileEntity) {
                        int antiTeleportUpgrades = ((AdvancedBeehiveTileEntity) te).getUpgradeCount(ModItems.UPGRADE_ANTI_TELEPORT.get());
                        if (antiTeleportUpgrades > 0) {
                            this.teleportCooldown = 10000;
                            super.customServerAiStep();
                            return;
                        }
                    }
                }
                BlockPos pos = this.navigation.getPath().getTarget();
                teleport(pos.getX(), pos.getY(), pos.getZ());
            }
            this.teleportCooldown = 250;
        }

        super.customServerAiStep();
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vector3d motionMultiplierIn) {
        if (!isStringy() || state.getBlock() != Blocks.COBWEB) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }
    }

    @Override
    public void attackTarget(LivingEntity target) {
        if (this.isAlive() && getNBTData().contains("attackResponse")) {
            String attackResponse = getNBTData().getString("attackResponse");
            switch (attackResponse) {
                case "fire":
                    target.setRemainingFireTicks(200);
                case "lava":
                    // Place flowing lava on the targets location
                    level.setBlock(target.blockPosition(), Blocks.LAVA.defaultBlockState(), 11);
            }
        }
    }

    public void setBeeType(String data) {
        this.entityData.set(TYPE, data);
    }

    public String getBeeType() {
        return this.entityData.get(TYPE);
    }

    @Override
    public void setHasStung(boolean hasStung) {
        if (!isStingless()) {
            super.setHasStung(hasStung);
        }
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> param) {
        if (TYPE.equals(param)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(param);
        // /summon productivebees:configurable_bee ~ ~ ~ {"type":"productivebees:diamond", "NoAI":true, "HasNectar": true}
        // /kill @e[type=productivebees:configurable_bee, name="Diamond Bee"]
    }

    public void setAttributes() {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains(("productivity"))) {
            setAttributeValue(BeeAttributes.PRODUCTIVITY, nbt.getInt("productivity"));
        }
        if (nbt.contains(("temper"))) {
            setAttributeValue(BeeAttributes.TEMPER, nbt.getInt("temper"));
        }
        if (nbt.contains(("endurance"))) {
            setAttributeValue(BeeAttributes.ENDURANCE, nbt.getInt("endurance"));
        }
        if (nbt.contains(("behavior"))) {
            setAttributeValue(BeeAttributes.BEHAVIOR, nbt.getInt("behavior"));
        }
        if (nbt.contains(("weather_tolerance"))) {
            setAttributeValue(BeeAttributes.WEATHER_TOLERANCE, nbt.getInt("weather_tolerance"));
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
    protected ITextComponent getTypeName() {
        CompoundNBT nbt = getNBTData();
        if (nbt.contains("name")) {
            return new TranslationTextComponent("entity.productivebees.bee_configurable", nbt.getString("name"));
        }
        return super.getTypeName();
    }

    @Override
    public float getSizeModifier() {
        CompoundNBT nbt = getNBTData();
        return nbt != null ? nbt.getFloat("size") : super.getSizeModifier();
    }

    @Override
    public boolean canSelfBreed() {
        CompoundNBT nbt = getNBTData();
        return nbt.getBoolean("selfbreed");
    }

    @Override
    public boolean isFlowerBlock(Block flowerBlock) {
        CompoundNBT nbt = getNBTData();
        if (nbt != null) {
            if (nbt.contains("flowerTag")) {
                ITag<Block> flowerTag = ModTags.getTag(new ResourceLocation(nbt.getString("flowerTag")));
                return flowerBlock.is(flowerTag);
            } else if (nbt.contains("flowerBlock")) {
                return flowerBlock.getRegistryName().toString().equals(nbt.getString("flowerBlock"));
            }
        }
        return super.isFlowerBlock(flowerBlock);
    }

    @Override
    public ITag<Block> getNestingTag() {
        CompoundNBT nbt = getNBTData();
        if (nbt != null && nbt.contains("nestingPreference")) {
            return ModTags.getTag(new ResourceLocation(nbt.getString("nestingPreference")));
        }
        return super.getNestingTag();
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
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        setBeeType(compound.getString("type"));
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("type", getBeeType());
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

    private boolean isWild() {
        return getNBTData().contains("nestingPreference");
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

    public boolean isStringy() {
        return getNBTData().getBoolean("stringy");
    }

    public boolean isStingless() {
        return getNBTData().getBoolean("stingless");
    }

    public boolean hasMunchies() {
        return getNBTData().getBoolean("munchies");
    }

    public boolean hasGlowingInnards() {
        return getNBTData().getBoolean("glowingInnards");
    }

    public String getParticleType() {
        return getNBTData().getString("particleType");
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

    public float[] getTertiaryColor() {
        CompoundNBT nbt = getNBTData();
        Integer color = nbt.contains("tertiaryColor") ? nbt.getInt("tertiaryColor") : nbt.getInt("primaryColor");
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
        if (isTranslucent() && source.equals(DamageSource.ANVIL)) {
            return true;
        }
        if (isFireproof() && (source.equals(DamageSource.HOT_FLOOR) || source.equals(DamageSource.IN_FIRE) || source.equals(DamageSource.ON_FIRE) || source.equals(DamageSource.LAVA))) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (isWithered()) {
            return effect.getEffect() != Effects.WITHER && super.canBeAffected(effect);
        }
        return super.canBeAffected(effect);
    }

    private void teleport(double x, double y, double z) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);

        while (blockpos$mutable.getY() > 0 && !level.getBlockState(blockpos$mutable).getMaterial().blocksMotion()) {
            blockpos$mutable.move(Direction.DOWN);
        }

        BlockState blockstate = level.getBlockState(blockpos$mutable);
        if (blockstate.getMaterial().blocksMotion()) {
            EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
            if (!event.isCanceled()) {
                boolean hasTeleported = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (hasTeleported && !this.isSilent()) {
                    level.playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 0.3F, 0.3F);
                    this.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.2F, 1.0F);
                }
            }
        }
    }
}
