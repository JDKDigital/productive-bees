package cy.jdkdigital.productivebees.common.entity.bee;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.client.particle.NectarParticleType;
import cy.jdkdigital.productivebees.common.block.entity.AmberBlockEntity;
import cy.jdkdigital.productivebees.compat.sussy.SussyCompatHandler;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModParticles;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.init.ModTags;
import cy.jdkdigital.productivebees.setup.BeeData;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurableBee extends ProductiveBee implements IEffectBeeEntity
{
    private int attackCooldown = 0;
    public int breathCollectionCooldown = 600;
    private int teleportCooldown = 250;
    public Mob target = null;

    public static final EntityDataAccessor<String> TYPE = SynchedEntityData.defineId(ConfigurableBee.class, EntityDataSerializers.STRING);

    public ConfigurableBee(EntityType<? extends Bee> entityType, Level world) {
        super(entityType, world);

        beehiveInterests = (poi) -> poi.is(PoiTypeTags.BEE_HOME) ||
                    poi.value() == ModPointOfInterestTypes.NETHER_NEST.get() ||
                    (poi.value() == ModPointOfInterestTypes.SOLITARY_HIVE.get() && isWild()) ||
                    (poi.value() == ModPointOfInterestTypes.SOLITARY_NEST.get() && isWild()) ||
                    (poi.value() == ModPointOfInterestTypes.DRACONIC_NEST.get() && isDraconic()) ||
                    (poi.value() == ModPointOfInterestTypes.SUGARBAG_NEST.get() && getBeeType().equals(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "sugarbag")));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            --teleportCooldown;
            if (--attackCooldown < 0) {
                attackCooldown = 0;
            }
            if (attackCooldown == 0 && isAngry() && this.getTarget() != null && this.getTarget().distanceToSqr(this) < 4.0D) {
                attackCooldown = getEffectCooldown(getAttributeValue(GeneAttribute.TEMPER));
                attackTarget(this.getTarget());
            }

            // Draconic bees
            if (!hasNectar() && level().dimension() == Level.END && isDraconic() && --breathCollectionCooldown <= 0) {
                breathCollectionCooldown = 600;
                this.internalSetHasNectar(true);
            }

            // Redstone bees
            if (tickCount % 21 == 0 && hasNectar() && isRedstoned()) {
                for (int i = 1; i <= 2; ++i) {
                    BlockPos beePosDown = this.blockPosition().below(i);
                    if (level().isEmptyBlock(beePosDown)) {
                        BlockState redstoneState = ModBlocks.INVISIBLE_REDSTONE_BLOCK.get().defaultBlockState();
                        level().setBlockAndUpdate(beePosDown, redstoneState);
                        level().scheduleTick(beePosDown, redstoneState.getBlock(), 20);
                    }
                }
            }

            // Entity targeting bees
            if (target != null) {
                if (!hasNectar()) {
                    target.getNavigation().setSpeedModifier(0.01);
                } else {
                    target.setTarget(this);
                    target = null;
                }
            }

            // Kill unconfigured bees
            if (tickCount > 5 && this.entityData.get(TYPE).isEmpty() && isAlive()) {
                this.kill((ServerLevel) this.level());
            }
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        // self healing bees
        if (!this.level().isClientSide() && this.isAlive()) {
            if (tickCount % 120 == 0 && this.canSelfHeal() && this.getHealth() < this.getMaxHealth()) {
                this.addEffect(new MobEffectInstance(MobEffects.INSTANT_HEALTH, 1));
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity entity) {
        AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);

        if (attackDamage != null && getDamage() > 2.0 && !attackDamage.hasModifier(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "extra_damage"))) {
            double damageModifier = getDamage();
            // For hardcode worlds, clamp the damage to something reasonable
            if (entity.level().getLevelData().isHardcore()) {
                damageModifier = Math.min(damageModifier, 1000d);
            }
            attackDamage.addTransientModifier(new AttributeModifier(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "extra_damage"), damageModifier, AttributeModifier.Operation.ADD_VALUE));
        }
        return super.doHurtTarget(level, entity);
    }

    @Override
    public void spawnFluidParticle(Level pLevel, double pStartX, double pEndX, double pStartZ, double pEndZ, double pPosY, ParticleOptions pParticleOption) {
        NectarParticleType particle = switch (getParticleType()) {
            case "pop" -> ModParticles.COLORED_POPPING_NECTAR.get();
            case "lava" -> ModParticles.COLORED_LAVA_NECTAR.get();
            case "portal" -> ModParticles.COLORED_PORTAL_NECTAR.get();
            case "rising" -> ModParticles.COLORED_RISING_NECTAR.get();
            default -> ModParticles.COLORED_FALLING_NECTAR.get();
        };

        if (hasParticleColor()) {
            particle.setColor(ColorUtil.getCacheColor(getParticleColor()));
        } else {
            particle.setColor(new float[]{0.92F, 0.782F, 0.72F});
        }

        pLevel.addParticle(particle, Mth.lerp(pLevel.getRandom().nextDouble(), pStartX, pEndX), pPosY, Mth.lerp(pLevel.getRandom().nextDouble(), pStartZ, pEndZ), 0.0D, 0.0D, 0.0D);
    }

    @Override
    protected void customServerAiStep(ServerLevel serverLevel) {
        // Teleport to active path
        if (this.teleportCooldown <= 0) {
            if (null != this.navigation.getPath() && isTeleporting()) {
                BlockPos pos = this.navigation.getPath().getTarget();
                teleport(pos.getX(), pos.getY(), pos.getZ());
            }
            this.teleportCooldown = 250;
        }

        super.customServerAiStep(serverLevel);
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplierIn) {
        if (!isStringy() || state.getBlock() != Blocks.COBWEB) {
            super.makeStuckInBlock(state, motionMultiplierIn);
        }
    }

    @Override
    public boolean canFreeze() {
        return !isColdResistant() && super.canFreeze();
    }

    @Override
    public void attackTarget(LivingEntity target) {
        BeeData data = getBeeData();
        if (this.isAlive() && data != null && data.attackResponse().isPresent()) {
            switch (data.attackResponse().get()) {
                case "fire":
                    target.setRemainingFireTicks(200);
                case "lava":
                    // Place flowing lava on the targets location
                    level().setBlock(target.blockPosition(), Blocks.LAVA.defaultBlockState(), 11);
            }
        }
    }

    public void setBeeType(String data) {
        this.entityData.set(TYPE, data);
    }

    @Nullable
    public Identifier getBeeType() {
        return Identifier.tryParse(this.entityData.get(TYPE));
    }

    @Override
    public float getSpeed() {
        return super.getSpeed() * this.getSpeedModifier();
    }

    @Override
    public void setHasStung(boolean hasStung) {
        if (!isStingless()) {
            super.setHasStung(hasStung);
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> param) {
        if (TYPE.equals(param)) {
            refreshDimensions();
        }
        super.onSyncedDataUpdated(param);
        // /summon productivebees:configurable_bee ~ ~ ~ {"type":"productivebees:diamond", "NoAI":true, "HasNectar": true}
        // /kill @e[type=productivebees:configurable_bee, name="Diamond Bee"]
        // /data get entity @s SelectedItem
    }

    public void setDefaultAttributes() {
        if (!hasBeeAttributes()) {
            var attributes = this.getData(ProductiveBees.ATTRIBUTE_HANDLER);
            BeeData data = getBeeData();
            if (data != null) {
                BeeData.BeeAttributes attr = data.attributes();
                attr.productivity().ifPresent(s -> attributes.setAttributeValue(GeneAttribute.PRODUCTIVITY, GeneValue.byName(s)));
                attr.temper().ifPresent(s -> attributes.setAttributeValue(GeneAttribute.TEMPER, GeneValue.byName(s)));
                attr.endurance().ifPresent(s -> attributes.setAttributeValue(GeneAttribute.ENDURANCE, GeneValue.byName(s)));
                attr.behavior().ifPresent(s -> attributes.setAttributeValue(GeneAttribute.BEHAVIOR, GeneValue.byName(s)));
                attr.weatherTolerance().ifPresent(s -> attributes.setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.byName(s)));
            }
            this.setData(ProductiveBees.ATTRIBUTE_HANDLER, attributes);

            // Custom name
            var type = this.getBeeType().toString();
            if (type.equals("productivebees:ghostly") && random.nextFloat() < 0.02f) {
                this.setCustomName(Component.literal("BooBee"));
            } else if (type.equals("productivebees:blitz") && random.nextFloat() < 0.02f) {
                this.setCustomName(Component.literal("King BitzBee"));
            } else if (type.equals("productivebees:basalz") && random.nextFloat() < 0.02f) {
                this.setCustomName(Component.literal("Queen BazBee"));
            } else if (type.equals("productivebees:blizz") && random.nextFloat() < 0.02f) {
                this.setCustomName(Component.literal("Shiny BizBee"));
            } else if (type.equals("productivebees:redstone") && random.nextFloat() < 0.01f) {
                this.setCustomName(Component.literal("Redastone Bee"));
            } else if (type.equals("productivebees:destabilized_redstone") && random.nextFloat() < 0.10f) {
                this.setCustomName(Component.literal("Destabilized RedaStone Bee"));
            } else if (type.equals("productivebees:compressed_iron") && random.nextFloat() < 0.05f) {
                this.setCustomName(Component.literal("Depressed Iron Bee"));
            } else if (type.equals("productivebees:sponge") && random.nextFloat() < 0.05f) {
                this.setCustomName(Component.literal("SpongeBee BlockPants"));
            } else if (type.equals("productivebees:infinity") && random.nextFloat() < 0.25f) {
                this.setCustomName(Component.literal("Infinibee"));
            } else if (type.equals("productivebees:allergy") && random.nextFloat() < 0.25f) {
                this.setCustomName(Component.literal("Beenadryl Buzz"));
            } else if (type.equals("productivebees:gregstar") && random.nextFloat() < 0.25f) {
                this.setCustomName(Component.literal("Monsieur Greg"));
            } else if (type.equals("productivebees:water") && random.nextFloat() < 0.05f) {
                switch (random.nextInt(5)) {
                    case 0 -> this.setCustomName(Component.literal("Wet Bee"));
                    case 1 -> this.setCustomName(Component.literal("Splashy Bee"));
                    case 2 -> this.setCustomName(Component.literal("Fishy Bee"));
                    case 3 -> this.setCustomName(Component.literal("Moist Bee"));
                    case 4 -> this.setCustomName(Component.literal("Dripping Bee"));
                }
            } else if (type.equals("productivebees:beebee") && random.nextFloat() < 0.5f) {
                switch (random.nextInt(8)) {
                    case 0 -> this.setCustomName(Component.literal("Dova Bee"));
                    case 1 -> this.setCustomName(Component.literal("Beepocalypse"));
                    case 2 -> this.setCustomName(Component.literal("Jeepers Beepers"));
                    case 3 -> this.setCustomName(Component.literal("Darth Propolis"));
                    case 4 -> this.setCustomName(Component.literal("The Ter-Bee-Nator"));
                    case 5 -> this.setCustomName(Component.literal("BeelzeBeeb"));
                    case 6 -> this.setCustomName(Component.literal("B-1000"));
                    case 7 -> this.setCustomName(Component.literal("Bzz Ro Dah"));
                }
            } else if (type.equals("productivebees:royal")) {
                String[] names = new String[]{
                        "Natalie", "Fiona", "Ysabelle", "Ada", "Alexandra", "Bianca", "Cherry", "Elizabeth", "Jasmine",
                        "Coral", "Candy", "Ariel", "Dawn", "Faye", "Diana", "Hope", "Genevieve", "Grace", "Eleanor",
                        "Helena", "Eve", "Joy", "Lydia", "Marigold", "Karen", "Marilyn", "Rosalyn", "Nadia", "Patty",
                        "Peach", "Stephanie", "Sophia", "Violette", "Willow", "Zoe", "Merida", "Belle", "Stellar"
                };
                this.setCustomName(Component.literal("Princess " + names[random.nextInt(names.length)]));
            }
        }
    }

    @Override
    public int getColor(int tintIndex, float partialTicks) {
        BeeData data = getBeeData();
        if (data != null) {
            if (data.colorCycle() && !data.renderer().contains("crystal")) {
                return ColorUtil.getCycleColor(data.primaryColor(), data.tertiaryColor(), tickCount, partialTicks);
            }
            return tintIndex == 0 ? data.primaryColor() : data.secondaryColor();
        }
        return super.getColor(tintIndex, partialTicks);
    }

    public int getParticleColor() {
        BeeData data = getBeeData();
        return data != null ? data.particleColor().orElse(0) : 0;
    }

    public int getTertiaryColor(float partialTicks) {
        BeeData data = getBeeData();
        if (data == null) return 0;
        if (data.colorCycle() && data.renderer().contains("crystal")) {
            return ColorUtil.getCycleColor(data.primaryColor(), data.tertiaryColor(), tickCount, partialTicks);
        }
        return data.tertiaryColor();
    }

    public boolean isColored() {
        return !hasBeeTexture();
    }

    @Nonnull
    @Override
    protected Component getTypeName() {
        if (getBeeData() != null) {
            return Component.translatable("entity.productivebees." + getBeeName() + "_bee");
        }
        return super.getTypeName();
    }

    @Override
    public float getSizeModifier() {
        BeeData data = getBeeData();
        return data != null ? (hasNectar() ? data.pollinatedSize() : data.size()) : super.getSizeModifier();
    }

    public float getSpeedModifier() {
        BeeData data = getBeeData();
        return data != null ? data.speed() : 1.0F;
    }

    public double getDamage() {
        BeeData data = getBeeData();
        return data != null ? data.attack() : 2.0D;
    }

    @Override
    public boolean canSelfBreed() {
        BeeData data = getBeeData();
        return data != null && data.selfbreed();
    }

    @Override
    public boolean isFlowerValid(BlockPos pos) {
        if (!level().isLoaded(pos)) {
            return false;
        }

        if (this.getFlowerType().equals("entity_types")) {
            BeeData data = this.getBeeData();
            if (data != null && data.flowerTag().isPresent()) {
                TagKey<EntityType<?>> entityTag = ModTags.getEntityTag(Identifier.parse(data.flowerTag().get()));

                if (level().getBlockEntity(pos) instanceof AmberBlockEntity amberBlockEntity) {
                    var entity = amberBlockEntity.getCachedEntity();
                    return entity != null && BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).is(entityTag);
                } else {
                    boolean inverse = data.inverseFlower();
                    List<Entity> entities = level().getEntities(this, (new AABB(pos).inflate(1.0D, 1.0D, 1.0D)), (entity -> inverse != BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(entity.getType()).is(entityTag)));
                    if (!entities.isEmpty() && entities.getFirst() instanceof Mob mob) {
                        target = mob;

                        target.addEffect(new MobEffectInstance(MobEffects.LUCK, 400));

                        return true;
                    }
                }
            }
        }

        return isFlowerValid(pos, ConfigurableBee.this::isFlowerBlock, ConfigurableBee.this::isFlowerItem);
    }

    @Override
    public boolean isFlowerBlock(BlockState flowerBlock) {
        if (flowerBlock.isAir()) {
            return false;
        }

        boolean canConvertBlock = BeeHelper.hasBlockConversionRecipe(this, flowerBlock);
        if (canConvertBlock) {
            return true;
        }
        BeeData data = getBeeData();
        if (data != null && this.getFlowerType().equals("blocks")) {
            if (data.flowerTag().isPresent()) {
                TagKey<Block> flowerTag = ModTags.getBlockTag(Identifier.parse(data.flowerTag().get()));
                return flowerBlock.is(flowerTag);
            } else if (data.flowerBlock().isPresent()) {
                return BuiltInRegistries.BLOCK.getKey(flowerBlock.getBlock()).toString().equals(data.flowerBlock().get());
            } else if (data.flowerFluid().isPresent() && !flowerBlock.getFluidState().isEmpty()) {
                String flowerFluidStr = data.flowerFluid().get();
                if (flowerFluidStr.contains("#")) {
                    TagKey<Fluid> flowerFluid = ModTags.getFluidTag(Identifier.parse(flowerFluidStr.replace("#", "")));
                    return flowerBlock.getFluidState().is(flowerFluid);
                } else {
                    return BuiltInRegistries.FLUID.getKey(flowerBlock.getFluidState().getType()).toString().equals(flowerFluidStr);
                }
            }
        }
        return false;
    }

    public boolean isFlowerItem(ItemStack flowerItem) {
        if (flowerItem.isEmpty()) {
            return false;
        }

        BeeData data = getBeeData();
        if (data != null && this.getFlowerType().equals("blocks")) {
            if (data.flowerTag().isPresent()) {
                TagKey<Item> flowerTag = ModTags.getItemTag(Identifier.parse(data.flowerTag().get()));
                return flowerItem.is(flowerTag);
            }
            if (data.flowerItem().isPresent()) {
                var itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(data.flowerItem().get()));
                return itemHolder.isPresent() && flowerItem.is(itemHolder.get());
            }
            if (data.flowerFluid().isPresent() && flowerItem.getItem() instanceof BucketItem bucketItem) {
                String flowerFluidStr = data.flowerFluid().get();
                if (flowerFluidStr.contains("#")) {
                    TagKey<Fluid> flowerFluid = ModTags.getFluidTag(Identifier.parse(flowerFluidStr.replace("#", "")));
                    return bucketItem.content.is(flowerFluid);
                } else {
                    var fluidHolder = BuiltInRegistries.FLUID.get(Identifier.parse(flowerFluidStr));
                    return fluidHolder.isPresent() && bucketItem.content.isSame(fluidHolder.get().value());
                }
            }
        }
        if (flowerItem.getItem() instanceof BlockItem blockItem && BeeHelper.hasBlockConversionRecipe(this, blockItem.getBlock().defaultBlockState())) {
            return true;
        }

        return BeeHelper.hasItemConversionRecipe(this, flowerItem);
    }

    @Override
    public Ingredient getBreedingIngredient() {
        BeeData data = getBeeData();
        return getBreedingIngredientFromString(data != null ? data.breedingItem() : "");
    }

    public static Ingredient getBreedingIngredientFromString(String id) {
        if (id.isEmpty()) {
            return Ingredient.of(BuiltInRegistries.ITEM.get(ModTags.DEFAULT_BREEDING).orElseThrow());
        }

        if (id.startsWith("#")) {
            return Ingredient.of(BuiltInRegistries.ITEM.get(ModTags.getItemTag(Identifier.parse(id.substring(1)))).orElseThrow());
        }
        return Ingredient.of(BuiltInRegistries.ITEM.get(Identifier.parse(id)).orElseThrow().value());
    }

    @Override
    public Integer getBreedingItemCount() {
        BeeData data = getBeeData();
        return data != null ? data.breedingItemCount() : 0;
    }

    @Override
    public TagKey<Block> getNestingTag() {
        BeeData data = getBeeData();
        if (data != null && data.nestingPreference().isPresent()) {
            return ModTags.getBlockTag(Identifier.parse(data.nestingPreference().get()));
        }
        return super.getNestingTag();
    }


    @Override
    public BeeEffect getBeeEffect() {
        return super.getBeeEffect();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(TYPE, "");
    }

    @Override
    public void readAdditionalSaveData(@Nonnull ValueInput input) {
        super.readAdditionalSaveData(input);
        setBeeType(input.getStringOr("type", ""));
        breathCollectionCooldown = input.getIntOr("breathCollectionCooldown", 0);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putString("type", getBeeType().toString());
        output.putInt("breathCollectionCooldown", breathCollectionCooldown);
    }

    /**
     * @return the typed bee data record from the {@link BeeRegistries#BEE_DATA} registry, or
     * {@code null} if the bee's type isn't loaded.
     */
    @Nullable
    public BeeData getBeeData() {
        return BeeRegistries.lookup(getBeeType());
    }

    public boolean hasBeeTexture() {
        BeeData data = getBeeData();
        return data != null && data.beeTexture().isPresent();
    }

    public String getBeeTexture() {
        BeeData data = getBeeData();
        return data != null ? data.beeTexture().orElse("") : "";
    }

    public String getRenderer() {
        BeeData data = getBeeData();
        return data != null ? data.renderer() : "";
    }

    public String getRenderTransform() {
        BeeData data = getBeeData();
        return data != null ? data.renderTransform() : "";
    }

    public boolean useGlowLayer() {
        BeeData data = getBeeData();
        return (data != null && data.useGlowLayer()) || (isRedstoned() && hasNectar());
    }

    private boolean isWild() {
        BeeData data = getBeeData();
        return data != null && data.nestingPreference().isPresent();
    }

    // Traits
    public boolean isFireproof() {
        BeeData data = getBeeData();
        return data != null && data.fireproof();
    }

    public boolean isWithered() {
        BeeData data = getBeeData();
        return data != null && data.withered();
    }

    public boolean isTranslucent() {
        BeeData data = getBeeData();
        return data != null && data.translucent();
    }

    public boolean isBlinding() {
        BeeData data = getBeeData();
        return data != null && data.blinding();
    }

    public boolean isDraconic() {
        BeeData data = getBeeData();
        return data != null && data.draconic();
    }

    public boolean isRedstoned() {
        BeeData data = getBeeData();
        return data != null && data.redstoned();
    }

    public boolean isSlimy() {
        BeeData data = getBeeData();
        return data != null && data.slimy();
    }

    public boolean isTeleporting() {
        BeeData data = getBeeData();
        return data != null && data.teleporting();
    }

    public boolean isStringy() {
        BeeData data = getBeeData();
        return data != null && data.stringy();
    }

    public boolean isStingless() {
        BeeData data = getBeeData();
        return data != null && data.stingless();
    }

    public boolean hasMunchies() {
        BeeData data = getBeeData();
        return data != null && data.munchies();
    }

    public boolean isWaterproof() {
        BeeData data = getBeeData();
        return data != null && data.waterproof();
    }

    public boolean isColdResistant() {
        BeeData data = getBeeData();
        return data != null && data.coldResistant();
    }

    public boolean isIrradiated() {
        BeeData data = getBeeData();
        return data != null && data.irradiated();
    }

    public String getParticleType() {
        BeeData data = getBeeData();
        return data != null ? data.particleType() : "";
    }

    public boolean hasParticleColor() {
        BeeData data = getBeeData();
        return data != null && data.particleColor().isPresent();
    }

    public boolean canSelfHeal() {
        BeeData data = getBeeData();
        return data != null && data.selfheal();
    }

    @Override
    public String getFlowerType() {
        BeeData data = getBeeData();
        return data != null ? data.flowerType() : "";
    }

    @Override
    public Map<Holder<MobEffect>, Integer> getAggressiveEffects() {
        if (isWithered()) {
            return new HashMap<>()
            {{
                put(MobEffects.WITHER, 350);
            }};
        }
        if (hasMunchies()) {
            return new HashMap<>()
            {{
                put(MobEffects.HUNGER, 530);
            }};
        }
        if (isBlinding()) {
            return new HashMap<>()
            {{
                put(MobEffects.BLINDNESS, 450);
            }};
        }

        return null;
    }

    public List<String> getInvulnerabilities() {
        BeeData data = getBeeData();
        return data != null ? data.invulnerability() : List.of();
    }

    @Override
    public boolean isInvulnerableTo(@Nonnull ServerLevel level, @Nonnull DamageSource source) {
        if (isWithered() && source.equals(this.level().damageSources().wither())) {
            return true;
        }
        if (isDraconic() && source.equals(this.level().damageSources().dragonBreath())) {
            return true;
        }
        if (isTranslucent() && source.equals(this.level().damageSources().anvil(this))) {
            return true;
        }
        if (isWaterproof() && source.equals(this.level().damageSources().drown())) {
            return true;
        }
        if (isColdResistant() && source.equals(this.level().damageSources().freeze())) {
            return true;
        }
        if (isFireproof() && (source.equals(this.level().damageSources().hotFloor()) || source.equals(this.level().damageSources().inFire()) || source.equals(this.level().damageSources().onFire()) || source.equals(this.level().damageSources().lava()))) {
            return true;
        }
        return super.isInvulnerableTo(level, source) || getInvulnerabilities().contains(source.getMsgId());
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (isWithered()) {
            return effect.getEffect() != MobEffects.WITHER && super.canBeAffected(effect);
        }
        return super.canBeAffected(effect);
    }

    private void teleport(double x, double y, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

        while(pos.getY() > this.level().getMinY() && !this.level().getBlockState(pos).blocksMotion()) {
            pos.move(Direction.DOWN);
        }

        BlockState blockstate = this.level().getBlockState(pos);
        boolean flag = blockstate.blocksMotion();
        boolean flag1 = blockstate.getFluidState().is(FluidTags.WATER);
        if (flag && !flag1) {
           EntityTeleportEvent.EnderEntity event = EventHooks.onEnderTeleport(this, x, y, z);
            if (!event.isCanceled()) {
                Vec3 vec3 = this.position();
                boolean flag2 = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
                if (flag2) {
                    this.level().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(this));
                    if (!this.isSilent()) {
                        this.level().playSound(null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                        this.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void postPollinate() {
        super.postPollinate();

        BeeData data = getBeeData();
        if (data != null && data.postPollination().isPresent()) {
            switch (data.postPollination().get()) {
                case "amber_encase":
                    BeeHelper.encaseMob(target, level(), this.getDirection());
                    target = null;
                    break;
                case "bonemeal":
                    if (level() instanceof ServerLevel serverLevel && this.hasSavedFlowerPos()) {
                        BlockState flower = level().getBlockState(this.getSavedFlowerPos());
                        if (flower.getBlock() instanceof CropBlock cropBlock && cropBlock.isValidBonemealTarget(level(), this.getSavedFlowerPos(), flower)) {
                            cropBlock.performBonemeal(serverLevel, level().getRandom(), this.getSavedFlowerPos(), flower);
                        }
                    }
                    break;
                case "force_grow_crop":
                    if (level() instanceof ServerLevel serverLevel && this.hasSavedFlowerPos()) {
                        var pos = this.getSavedFlowerPos();
                        BlockState flower = level().getBlockState(pos);
                        if (flower.getBlock() instanceof CropBlock cropBlock && !cropBlock.isMaxAge(flower)) {
                            if (CommonHooks.canCropGrow(serverLevel, pos, flower, true)) {
                                serverLevel.setBlock(pos, cropBlock.getStateForAge(cropBlock.getAge(flower) + 1), 2);
                                CommonHooks.fireCropGrowPost(serverLevel, pos, flower);
                            }
                        }
                    }
                    break;
                case "sus":
                    if (level() instanceof ServerLevel sl && this.hasSavedFlowerPos()) {
                        var lootTables = SussyCompatHandler.getLootTables(sl, this.getSavedFlowerPos());
                        if (!lootTables.isEmpty()) {
                            ProductiveBees.LOGGER.debug("Sussy bee found {} loot tables at {}", lootTables.size(), this.getSavedFlowerPos());
                        }
                    }
                    break;
            }
        }
    }
}
