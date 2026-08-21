package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.recipe.BeeFishingRecipe;
import cy.jdkdigital.productivebees.gen.feature.WoodNestDecorator;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeRegistries;
import cy.jdkdigital.productivebees.common.block.entity.CentrifugeBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.HeatedCentrifugeBlockEntity;
import cy.jdkdigital.productivebees.util.BeeHelper;
import cy.jdkdigital.productivebees.util.GeneAttribute;
import cy.jdkdigital.productivebees.util.GeneValue;
import cy.jdkdigital.productivelib.event.AddEntityToFilterEvent;
import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.*;

@EventBusSubscriber(modid = ProductiveBees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        var registries = event.getServer().registryAccess();
        BeeRegistries.setRegistries(registries);
        BeeRegistries.evaluateRuntimeGates(registries);
        // Warm the ingredient map on the server thread so recipe sync on the IO thread doesn't race.
        BeeIngredientFactory.getOrCreateList();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        BeeRegistries.setRegistries(null);
    }

    @SubscribeEvent
    public static void addEntityToFilter(AddEntityToFilterEvent event) {
        if (event.getEntity() instanceof ConfigurableBee bee) {
            var type = Identifier.parse(BeeIngredientFactory.getIngredientKey(bee));
            event.setKey(type);
        }
    }

    @SubscribeEvent
    public static void addUpgradeTooltip(UpgradeTooltipEvent event) {
        if (event.getEntities() != null) {
            List<Identifier> leftovers = new ArrayList<>();
            event.getEntities().forEach(resourceLocation -> {
                var type = BeeIngredientFactory.getIngredient(resourceLocation.toString());
                if (type.get() != null && type.get().isConfigurable()) {
                    event.getTooltipComponents().accept(Component.translatable("productivelib.information.upgrade.upgrade_entity_filter.list_item", Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(type.get().getBeeType()) + "_bee").getString()).withStyle(ChatFormatting.GOLD));
                } else {
                    leftovers.add(resourceLocation);
                }
            });
            event.setEntities(leftovers);
        } else {
            var upgradeType = BuiltInRegistries.ITEM.getKey(event.getStack().getItem());

            int value = (int)(switch (upgradeType.getPath()) {
                case "upgrade_child" -> ProductiveBeesConfig.UPGRADES.breedingChance.get();
                case "upgrade_time" -> ProductiveBeesConfig.UPGRADES.timeBonus.get();
                case "upgrade_time_2" -> ProductiveBeesConfig.UPGRADES.timeBonus.get() * 2;
                case "upgrade_productivity" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier.get();
                case "upgrade_productivity_2" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier2.get();
                case "upgrade_productivity_3" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier3.get();
                case "upgrade_productivity_4" -> ProductiveBeesConfig.UPGRADES.productivityMultiplier4.get();
                case "upgrade_gene_sampler" -> ProductiveBeesConfig.UPGRADES.samplerChance.get();
                case "upgrade_stability" -> ProductiveBeesConfig.UPGRADES.stabilityChanceIncrease.get();
                default -> 0.0F;
            } * 100);

            String tPrefix = "productivebees.information.upgrade." + upgradeType.getPath() + ".";
            switch (upgradeType.getPath()) {
                case "upgrade_entity_filter" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.catcher"), tPrefix + "catcher", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                }
                case "upgrade_child", "upgrade_range" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.catcher"), tPrefix + "catcher", value);
                }
                case "upgrade_adult" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.catcher"), tPrefix + "catcher", value);
                }
                case "upgrade_productivity_2", "upgrade_productivity_3", "upgrade_productivity_4" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                }
                case "upgrade_gene_sampler", "upgrade_anti_teleport", "upgrade_block", "upgrade_simulator" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                }
                case "upgrade_time" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.breeding_chamber"), tPrefix + "breeding_chamber", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.incubator"), tPrefix + "incubator", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.honey_generator"), tPrefix + "honey_generator", value);
                }
                case "upgrade_time_2" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.breeding_chamber"), tPrefix + "breeding_chamber", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.incubator"), tPrefix + "incubator", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.honey_generator"), tPrefix + "honey_generator", value);
                }
                case "upgrade_productivity" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.advanced_beehive"), tPrefix + "advanced_beehive", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.honey_generator"), tPrefix + "honey_generator", value);
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                }
                case "upgrade_stability" -> {
                    event.addValidBlock(Component.translatable("productivebees.devices.centrifuge"), tPrefix + "centrifuge", value);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
        // Recipe lookups are cached by bee/item id, so drop them whenever the datapacks reload.
        event.addListener(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "recipe_caches"),
                (sharedState, taskExecutor, barrier, reloadExecutor) -> barrier.<Void>wait(null).thenRun(() -> {
                    BeeHelper.clearRecipeCaches();
                    CentrifugeBlockEntity.clearRecipeCache();
                    HeatedCentrifugeBlockEntity.clearBlockRecipeCache();
                }));
    }

    @SubscribeEvent
    public static void onDataSync(OnDatapackSyncEvent event) {
        var registries = event.getPlayerList().getServer().registryAccess();
        BeeRegistries.setRegistries(registries);
        BeeRegistries.evaluateRuntimeGates(registries);
        // Opt our recipe types into the client recipe sync (consumed by JEI compat via RecipesReceivedEvent).
        event.sendRecipes(
                ModRecipeTypes.ADVANCED_BEEHIVE_TYPE.get(),
                ModRecipeTypes.CENTRIFUGE_TYPE.get(),
                ModRecipeTypes.BOTTLER_TYPE.get(),
                ModRecipeTypes.BEE_BREEDING_TYPE.get(),
                ModRecipeTypes.BEE_CONVERSION_TYPE.get(),
                ModRecipeTypes.BEE_FISHING_TYPE.get(),
                ModRecipeTypes.BEE_SPAWNING_TYPE.get(),
                ModRecipeTypes.BLOCK_CONVERSION_TYPE.get(),
                ModRecipeTypes.ITEM_CONVERSION_TYPE.get()
        );
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Bee bee) {
            // Attribute improvement while leashed
            if (!bee.level().isClientSide() && bee.isLeashed() && bee.tickCount % ProductiveBeesConfig.BEE_ATTRIBUTES.leashedTicks.get() == 0) {
                var attributes = bee.getData(ProductiveBees.ATTRIBUTE_HANDLER);
                // Rain tolerance improvements
                GeneValue tolerance = attributes.getAttributeValue(GeneAttribute.WEATHER_TOLERANCE);
                if (tolerance.getValue() < 2 && bee.level().getRandom().nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.toleranceChance.get()) {
                    if (tolerance.equals(GeneValue.WEATHER_TOLERANCE_NONE) && (bee.level().isRaining() || bee.level().isThundering())) {
                        attributes.setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_RAIN);
                    } else if (tolerance.equals(GeneValue.WEATHER_TOLERANCE_RAIN) && bee.level().isThundering()) {
                        attributes.setAttributeValue(GeneAttribute.WEATHER_TOLERANCE, GeneValue.WEATHER_TOLERANCE_ANY);
                    }
                }
                // Behavior improvement
                GeneValue behavior = attributes.getAttributeValue(GeneAttribute.BEHAVIOR);
                if (behavior.getValue() < 2 && bee.level().getRandom().nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.behaviorChance.get()) {
                    // If diurnal, it can change to nocturnal
                    long overworldTimeOfDay = bee.level().getOverworldClockTime() % 24000L;
                    boolean isNight = overworldTimeOfDay >= 12000L;
                    if (behavior.equals(GeneValue.BEHAVIOR_DIURNAL) && isNight) {
                        attributes.setAttributeValue(GeneAttribute.BEHAVIOR, bee.level().getRandom().nextFloat() < 0.85F ? GeneValue.BEHAVIOR_NOCTURNAL : GeneValue.BEHAVIOR_METATURNAL);
                    }
                    // If nocturnal, it can become metaturnal or back to diurnal
                    else if (behavior.equals(GeneValue.BEHAVIOR_NOCTURNAL) && !isNight) {
                        attributes.setAttributeValue(GeneAttribute.BEHAVIOR, bee.level().getRandom().nextFloat() < 0.9F ? GeneValue.BEHAVIOR_METATURNAL : GeneValue.BEHAVIOR_DIURNAL);
                    }
                }

                // It might die when leashed outside
                long overworldTimeOfDay = bee.level().getOverworldClockTime() % 24000L;
                boolean isInDangerFromRain = tolerance.equals(GeneValue.WEATHER_TOLERANCE_NONE) && bee.level().isRaining();
                boolean isInDayCycleDanger = (behavior.equals(GeneValue.BEHAVIOR_DIURNAL) && overworldTimeOfDay >= 12000L) || (behavior.equals(GeneValue.BEHAVIOR_NOCTURNAL) && overworldTimeOfDay < 12000L);
                if ((isInDangerFromRain || isInDayCycleDanger) && bee.level().getRandom().nextFloat() < ProductiveBeesConfig.BEE_ATTRIBUTES.damageChance.get()) {
                    bee.hurt(isInDangerFromRain ? bee.level().damageSources().drown() : bee.level().damageSources().generic(), (bee.getMaxHealth() / 3) - 1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttacked(LivingIncomingDamageEvent event) {
        if (event.getEntity() instanceof ConfigurableBee bee) {
            if (bee.isIrradiated() && event.getSource().getMsgId().equals("mekanism.radiation")) {
                if (bee.breathCollectionCooldown < 0) {
                    bee.breathCollectionCooldown = 600;
                    bee.internalSetHasNectar(true);
                } else {
                    bee.breathCollectionCooldown-= event.getAmount();
                }
                event.setCanceled(true);
                bee.level().broadcastEntityEvent(bee, (byte) 2);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ConfigurableBee bee) {
            if (
                    event.getSource().getMsgId().equals("mekanism.radiation") &&
                            bee.getBeeType().equals(Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "radioactive")) &&
                            ProductiveBeesConfig.BEES.deadBeeConvertChance.get() > event.getEntity().level().getRandom().nextDouble() &&
                            BeeIngredientFactory.getIngredient("productivebees:wasted_radioactive").get() != null
            ) {
                event.setCanceled(true);
                bee.setHealth(bee.getMaxHealth());
                bee.setBeeType("productivebees:wasted_radioactive");
            }
        } else if (event.getEntity() instanceof Bee bee) {
            if (bee.level().getBlockState(bee.blockPosition()).is(Blocks.WHITE_CONCRETE_POWDER)) {
                Entity newBee = ModEntities.CONFIGURABLE_BEE.get().create(bee.level(), EntitySpawnReason.NATURAL);
                if (newBee instanceof ConfigurableBee configurableBee) {
                    configurableBee.setBeeType("productivebees:phil");
                    configurableBee.snapTo(bee.blockPosition().relative(Direction.UP), bee.getYRot(), bee.getXRot());
                    bee.level().addFreshEntity(newBee);
                }
            }
        } else if (ProductiveBeesConfig.GENERAL.enableJokes.get() && event.getEntity() instanceof Villager && Calendar.getInstance().get(Calendar.MONTH) + 1 == 4 && Calendar.getInstance().get(Calendar.DATE) == 1) {
            Entity newBee = ModEntities.CONFIGURABLE_BEE.get().create(event.getEntity().level(), EntitySpawnReason.NATURAL);
            if (newBee instanceof ConfigurableBee configurableBee) {
                configurableBee.setBeeType("productivebees:villager");
                configurableBee.setPos(event.getEntity().position().relative(Direction.UP, 1));
                configurableBee.internalSetHasNectar(true);
                event.getEntity().level().addFreshEntity(configurableBee);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent.Post event) {
        Entity damageSource = event.getSource().getEntity();
        if (damageSource instanceof LivingEntity attacker && event.getEntity() instanceof Player player) {
            boolean isWearingBeeHelmet = BeeHelper.isWearingBeeNestHelmet(player);
            if (isWearingBeeHelmet && player.level().getRandom().nextDouble() < ProductiveBeesConfig.BEES.kamikazBeeChance.get()) {
                Level level = player.level();
                ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(level, EntitySpawnReason.NATURAL);
                BlockPos pos = player.blockPosition();
                if (bee != null) {
                    bee.setBeeType("productivebees:kamikaz");
                    bee.setDefaultAttributes();
                    bee.setTarget(attacker);
                    bee.snapTo(pos.getX(), pos.getY() + 0.5, pos.getZ(), bee.getYRot(), bee.getXRot());

                    level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    level.playSound(player, pos, SoundEvents.BEE_HURT, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    level.addFreshEntity(bee);
                }
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onBeeBeeHurt(LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof ConfigurableBee entity && entity.getBeeType().toString().equals("productivebees:beebee")) {
            if (event.isCanceled()) {
                if (event.getEntity() instanceof Player suitPlayer) {
                    if (suitPlayer.getRandom().nextFloat() < ProductiveBeesConfig.GENERAL.quantumArmorFailureChance.get()) {
                        event.setCanceled(false);
                        suitPlayer.sendSystemMessage(Component.literal("Suit failure"));
                    } else if (ProductiveBeesConfig.GENERAL.quantumArmorFailureChance.get() > 0d) {
                        switch (suitPlayer.getRandom().nextInt(5)) {
                            case 0:
                                suitPlayer.sendSystemMessage(Component.literal("Suit malfunction imminent! Disengage from Combat IMMEDIATELY"));
                                break;
                            case 1:
                                suitPlayer.sendSystemMessage(Component.literal("Suit Integrity Compromised! Further engagement could result in loss of life"));
                                break;
                            case 2:
                                suitPlayer.sendSystemMessage(Component.literal("NOT THE BEES!"));
                                break;
                            case 3:
                                suitPlayer.sendSystemMessage(Component.literal("It's in my suit. IT'S IN MY SUIT!!"));
                                break;
                            default:
                                suitPlayer.sendSystemMessage(Component.literal("RUUUUUUUUUUUUUUUUUUUN!!"));
                                break;
                        }
                    }
                }
            } else if (event.getEntity() instanceof Player player) {
                player.sendSystemMessage(Component.translatable("productivebees.information.beebee_death"));
            }
        }
    }
    
    @SubscribeEvent
    public static void onArmorDamage(final ArmorHurtEvent event) {
        if (event.getDamageSource().getEntity() instanceof ConfigurableBee entity && entity.getBeeType().toString().equals("productivebees:beebee")) {
            if (ProductiveBeesConfig.GENERAL.beeBeeArmorDurabilityPercentageRemainder.get() == -1) {
                event.setCanceled(true);
                return;
            }
            
            event.getArmorMap().forEach((slot, entry) -> { 
                ItemStack armor = entry.armorItemStack;
                float max = armor.getMaxDamage();
                float damage = armor.getDamageValue();
                float remaining = max - damage;
                
                if (remaining > (max / 2)) {
                    event.setNewDamage(slot, Math.max(damage + remaining * ((100 - ProductiveBeesConfig.GENERAL.beeBeeArmorDurabilityPercentageRemainder.get()) / 100f), 1));
                } else {
                    event.setNewDamage(slot, max);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onBlockGrow(BlockGrowFeatureEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && ProductiveBeesConfig.WORLD_GEN.treeGrowNestChance.get() > 0d) {
            WoodNestDecorator decorator = null;
            float growthRoll = serverLevel.getRandom().nextFloat();
            float growthRollNether = serverLevel.getBiome(event.getPos()).is(Tags.Biomes.IS_NETHER) ? growthRoll * 5 : growthRoll;
            boolean canSpawnNest = hasFlowers(serverLevel, event.getPos()) && growthRoll < ProductiveBeesConfig.WORLD_GEN.treeGrowNestChance.get();
            Block grownBlock =  serverLevel.getBlockState(event.getPos()).getBlock();
            if (canSpawnNest && grownBlock.equals(Blocks.OAK_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.OAK_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.BIRCH_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.BIRCH_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.SPRUCE_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.SPRUCE_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.ACACIA_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.ACACIA_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.DARK_OAK_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.DARK_OAK_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.JUNGLE_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.JUNGLE_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.CHERRY_SAPLING)) {
                decorator = new WoodNestDecorator(ModBlocks.CHERRY_WOOD_NEST.get().defaultBlockState());
            } else if (canSpawnNest && grownBlock.equals(Blocks.MANGROVE_PROPAGULE)) {
                decorator = new WoodNestDecorator(ModBlocks.MANGROVE_WOOD_NEST.get().defaultBlockState());
            } else if (growthRollNether < ProductiveBeesConfig.WORLD_GEN.treeGrowNestChance.get() && (grownBlock.equals(Blocks.CRIMSON_FUNGUS) || grownBlock.equals(Blocks.WARPED_FUNGUS))) {
                var featureKey = grownBlock.equals(Blocks.CRIMSON_FUNGUS) ? ModConfiguredFeatures.CRIMSON_FUNGUS_BEES_GROWN : ModConfiguredFeatures.WARPED_FUNGUS_BEES_GROWN;
                var feature = event.getLevel().registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(featureKey).orElse(null);
                event.setFeature(feature);
            }

            if (decorator != null) {
                if (decorator.getNest().getBlock() instanceof WoodNest woodNest) {
                    decorator.setBeeRecipes(SolitaryNest.getSpawningRecipes(woodNest, serverLevel, serverLevel.getBiome(event.getPos()), ItemStack.EMPTY));
                }

                var feature = event.getFeature();
                TreeDecorator finalDecorator = decorator;
                feature.value().getSubFeatures().forEach(holder -> {
                    if (holder.value().config() instanceof TreeConfiguration treeConfig) {
                        List<TreeDecorator> decorators = new ArrayList<>(treeConfig.decorators);
                        decorators.add(finalDecorator);
                        treeConfig.decorators = decorators;
                    }
                });
                event.setFeature(feature);
            }
        }
    }

    private static boolean hasFlowers(LevelAccessor pLevel, BlockPos pPos) {
        for(BlockPos blockpos : BlockPos.MutableBlockPos.betweenClosed(pPos.below().north(2).west(2), pPos.above().south(2).east(2))) {
            var state = pLevel.getBlockState(blockpos);
            if (state.is(BlockTags.FLOWERS) && !state.is(ModTags.NOT_FLOWERS)) {
                return true;
            }
        }
        return false;
    }


    // Beekeeper village trades are bootstrapped into the datapack registry via {@link ModTrades}.

    @SubscribeEvent
    public static void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        Level level = entityInteract.getLevel();
        if (entity instanceof Bee bee) {
            if (!itemStack.isEmpty() && level instanceof ServerLevel serverLevel) {
                Player player = entityInteract.getEntity();
                BlockPos pos = entity.blockPosition();

                Entity newBee = BeeHelper.itemInteract(bee, itemStack, serverLevel, player);

                if (newBee instanceof Bee) {
                    // PLay event with smoke
                    level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    level.addFreshEntity(newBee);
                    if (bee.isLeashed()) {
                        bee.dropLeash();
                    }
                    entity.discard();
                    entityInteract.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void blockBreakSpawn(BreakBlockEvent event) {
        String beeType = "";
        int beeCount = 1;
        boolean angry = false;
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().getValue(CocoaBlock.AGE) == 2) {
            beeType = "productivebees:sugarbag";
        } else if (BuiltInRegistries.BLOCK.getKey(event.getState().getBlock()).toString().equals("undergarden:gloomgourd")) {
            beeType = "productivebees:utheric";
            angry = true;
            beeCount = 3;
        }
        if (!beeType.isEmpty()) {
            Player player = event.getPlayer();
            Level level = player.level();
            if (level instanceof ServerLevel && player instanceof ServerPlayer && level.getRandom().nextFloat() < ProductiveBeesConfig.BEES.sugarbagBeeChance.get()) {
                for (var i = 0; i < beeCount; i++) {
                    ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(level, EntitySpawnReason.NATURAL);
                    BlockPos pos = event.getPos();
                    if (bee != null && BeeRegistries.lookup(Identifier.parse(beeType)) != null) {
                        bee.setBeeType(beeType);
                        bee.setDefaultAttributes();

                        bee.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

                        level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                        level.playSound(player, pos, SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                        level.addFreshEntity(bee);
                        if (angry) {
                            bee.setTarget(player);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void fuelValues(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();
        if (item.equals(ModItems.WAX.get())) {
            event.setBurnTime(100);
        } else if (item.equals(ModBlocks.WAX_BLOCK.get().asItem())) {
            event.setBurnTime(900);
        }
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        Player player = event.getEntity();
        if (!(player instanceof FakePlayer) && player.level() instanceof ServerLevel serverLevel) {
            BlockPos pos = event.getHookEntity().blockPosition();
            var fishingBiome = player.level().getBiome(pos);
            List<BeeFishingRecipe> possibleRecipes = new ArrayList<>();
            var recipes = BeeFishingRecipe.getRecipeList(fishingBiome, player.level());
            if (!recipes.isEmpty()) {
                for (BeeFishingRecipe recipe: recipes) {
                    boolean willSpawn = player.level().getRandom().nextDouble() < recipe.chance;
                    int fishingLuck = EnchantmentHelper.getFishingLuckBonus(serverLevel, player.getMainHandItem(), player);
                    for (int i = 0; i < (1 + fishingLuck); i++) {
                        willSpawn = willSpawn || player.level().getRandom().nextDouble() < recipe.chance;
                    }

                    if (willSpawn) {
                        possibleRecipes.add(recipe);
                    }
                }
            }

            if (!possibleRecipes.isEmpty()) {
                BeeFishingRecipe chosenRecipe = possibleRecipes.get(player.level().getRandom().nextInt(possibleRecipes.size()));
                BeeIngredient beeIngredient = chosenRecipe.output.get();
                Bee bee = (Bee) beeIngredient.getBeeEntity().create(player.level(), EntitySpawnReason.NATURAL);
                if (bee != null) {
                    if (bee instanceof ConfigurableBee configBee) {
                        configBee.setBeeType(beeIngredient.getBeeType().toString());
                        configBee.setDefaultAttributes();
                    }

                    bee.snapTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

                    player.level().addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    player.level().playSound(player, pos, SoundEvents.BEE_HURT, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    player.level().addFreshEntity(bee);
                    bee.setTarget(player);

                    ModAdvancements.FISH_BEE.get().trigger((ServerPlayer) player, bee);
                }
            }
        }
    }


    @SubscribeEvent
    public static void onEntitySpawn(MobSpawnEvent.PositionCheck event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel && event.getEntity() instanceof ConfigurableBee configurableBee) {
            if (event.getSpawnType().equals(EntitySpawnReason.NATURAL) && serverLevel.getBiome(event.getEntity().blockPosition()).is(ModTags.BEEBEE_SPAWN_BIOMES) && BeeIngredientFactory.getIngredient("productivebees:beebee").get() != null) {
                configurableBee.setBeeType("productivebees:beebee");
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Bee entity && !entity.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
            if (entity instanceof ConfigurableBee configurableBee) {
                configurableBee.setDefaultAttributes();
            } else {
                entity.getData(ProductiveBees.ATTRIBUTE_HANDLER);
            }
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild() instanceof Bee bee && bee.level() instanceof ServerLevel serverLevel) {
            if (event.getParentA() instanceof Bee parenA && event.getParentB() instanceof AgeableMob parentB) {
                if (!bee.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
                    BeeHelper.setOffspringAttributes(bee, parenA, parentB);
                }

                var recipe = BeeHelper.getRandomBreedingRecipe(parenA, parentB, serverLevel);
                if (recipe != null) {
                    if (recipe.value().parentDeathChance > serverLevel.getRandom().nextFloat()) {
                        parenA.setHasStung(true);
                    }
                    if (recipe.value().parentDeathChance > serverLevel.getRandom().nextFloat() && parentB instanceof Bee parentBee) {
                        parentBee.setHasStung(true);
                    }
                }
            }
        }
    }
}
