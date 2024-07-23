package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.client.render.item.JarBlockItemRenderer;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.nest.WoodNest;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.recipe.BeeFishingRecipe;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredient;
import cy.jdkdigital.productivebees.common.crafting.ingredient.BeeIngredientFactory;
import cy.jdkdigital.productivebees.gen.feature.WoodNestDecorator;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.network.packets.BeeDataMessage;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.BlockGrowFeatureEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.event.village.WandererTradesEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@EventBusSubscriber(modid = ProductiveBees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onServerStarting(AddReloadListenerEvent event) {
        BeeReloadListener.INSTANCE.context = event.getConditionContext();
        event.addListener(BeeReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void onDataSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            PacketDistributor.sendToAllPlayers(new BeeDataMessage(BeeReloadListener.INSTANCE.getData()));
        } else {
            PacketDistributor.sendToPlayer(event.getPlayer(), new BeeDataMessage(BeeReloadListener.INSTANCE.getData()));
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
                    bee.getBeeType().equals(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "radioactive")) &&
                    ProductiveBeesConfig.BEES.deadBeeConvertChance.get() > event.getEntity().level().random.nextDouble() &&
                    BeeIngredientFactory.getIngredient("productivebees:wasted_radioactive").get() != null
            ) {
                event.setCanceled(true);
                bee.setHealth(bee.getMaxHealth());
                bee.setBeeType("productivebees:wasted_radioactive");
            }
        }
    }

    @SubscribeEvent
    public static void onEntityHurt(LivingDamageEvent.Post event) {
        Entity damageSource = event.getSource().getEntity();
        if (damageSource instanceof LivingEntity attacker && event.getEntity() instanceof Player player) {
            boolean isWearingBeeHelmet = false;
            ItemStack itemstack = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!itemstack.isEmpty() && itemstack.getItem().equals(ModItems.BEE_NEST_DIAMOND_HELMET.get())) {
                isWearingBeeHelmet = true;
            }

            if (isWearingBeeHelmet && player.level().random.nextDouble() < ProductiveBeesConfig.BEES.kamikazBeeChance.get()) {
                Level level = player.level();
                ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(level);
                BlockPos pos = player.blockPosition();
                if (bee != null) {
                    bee.setBeeType("productivebees:kamikaz");
                    bee.setDefaultAttributes();
                    bee.setTarget(attacker);
                    bee.moveTo(pos.getX(), pos.getY() + 0.5, pos.getZ(), bee.getYRot(), bee.getXRot());

                    level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    level.playSound(player, pos, SoundEvents.BEE_HURT, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    level.addFreshEntity(bee);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockGrow(BlockGrowFeatureEvent event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            WoodNestDecorator decorator = null;
            float r = serverLevel.getRandom().nextFloat();
            boolean canSpawnNest = hasFlowers(event.getLevel(), event.getPos()) && r < ProductiveBeesConfig.WORLD_GEN.treeGrowNestChance.get();
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
            } else if (r < ProductiveBeesConfig.WORLD_GEN.treeGrowNestChance.get() && (grownBlock.equals(Blocks.CRIMSON_FUNGUS) || grownBlock.equals(Blocks.WARPED_FUNGUS))) {
                var featureKey = grownBlock.equals(Blocks.CRIMSON_FUNGUS) ? ModConfiguredFeatures.CRIMSON_FUNGUS_BEES_GROWN : ModConfiguredFeatures.WARPED_FUNGUS_BEES_GROWN;
                var feature = event.getLevel().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).getHolder(featureKey).orElse(null);
                event.setFeature(feature);
            }

            if (decorator != null) {
                if (decorator.getNest().getBlock() instanceof WoodNest woodNest) {
                    decorator.setBeeRecipes(SolitaryNest.getSpawningRecipes(woodNest, serverLevel, serverLevel.getBiome(event.getPos()), ItemStack.EMPTY));
                }

                var feature = event.getFeature();
                TreeDecorator finalDecorator = decorator;
                feature.value().getFeatures().forEach(configuredFeature -> {
                    if (configuredFeature.config() instanceof TreeConfiguration treeConfig) {
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

    @SubscribeEvent
    public static void onWandererTradesEvent(WandererTradesEvent event) {
        event.getGenericTrades().add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 10), Optional.empty(), new ItemStack(ModItems.STURDY_BEE_CAGE.get()), 1, 12, 6, 0.2F));
        event.getGenericTrades().add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 24), Optional.empty(), new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_bumble_bee"))), 3, 12, 6, 0.2F));
    }

    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType().equals(ModProfessions.BEEKEEPER.get())) {
            // Novice
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.CAMPFIRE), Optional.empty(), new ItemStack(Items.EMERALD), 1, 12, 6, 0.2F));
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(Items.GLASS_BOTTLE, 4), 1, 32, 3, 0.2F));
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 3), Optional.empty(), new ItemStack(Items.SHEARS), 3, 12, 3, 0.2F));

            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.HONEY_BOTTLE, 2), Optional.empty(), new ItemStack(Items.EMERALD), 3, 10, 6, 0.2F));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(ModItems.BEE_CAGE.get(), 4), 3, 64, 6, 0.2F));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 2), Optional.empty(), new ItemStack(ModItems.SUGARBAG_HONEYCOMB.get()), 3, 32, 3, 0.2F));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(ModItems.TREAT_ON_A_STICK.get()), 3, 8, 3, 0.2F));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> {
                if (rand.nextBoolean()) {
                    return new MerchantOffer(new ItemCost(ModItems.HONEY_TREAT.get(), 4), Optional.empty(), new ItemStack(Items.EMERALD), 3, 50, 3, 0.2F);
                }
                return new MerchantOffer(new ItemCost(Items.EMERALD), Optional.empty(), new ItemStack(ModItems.HONEY_TREAT.get(), 2), 3, 100, 3, 0.2F);
            });

            List<Block> hiveMap = new ArrayList<>() {{
                add(ModBlocks.HIVES.get("advanced_jungle_beehive").get());
                add(ModBlocks.HIVES.get("advanced_acacia_beehive").get());
                add(ModBlocks.HIVES.get("advanced_birch_beehive").get());
                add(ModBlocks.HIVES.get("advanced_dark_oak_beehive").get());
                add(ModBlocks.HIVES.get("advanced_mangrove_beehive").get());
                add(ModBlocks.HIVES.get("advanced_spruce_beehive").get());
                add(ModBlocks.HIVES.get("advanced_cherry_beehive").get());
                add(ModBlocks.HIVES.get("advanced_oak_beehive").get());
            }};
            List<Block> boxMap = new ArrayList<>() {{
                add(ModBlocks.EXPANSIONS.get("expansion_box_jungle").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_acacia").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_birch").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_dark_oak").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_mangrove").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_spruce").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_cherry").get());
                add(ModBlocks.EXPANSIONS.get("expansion_box_oak").get());
            }};
            AtomicInteger picked = new AtomicInteger();
            event.getTrades().get(ModProfessions.JOURNEYMAN).add((trader, rand) -> {
                picked.set(rand.nextInt(hiveMap.size()));
                return new MerchantOffer(new ItemCost(Items.BEEHIVE, 1), Optional.of(new ItemCost(Items.EMERALD, 6)), new ItemStack(hiveMap.get(picked.get())), 1, 12, 6, 0.2F);
            });
            event.getTrades().get(ModProfessions.JOURNEYMAN).add((trader, rand) -> {
                return new MerchantOffer(new ItemCost(Items.EMERALD, 4), Optional.empty(), new ItemStack(boxMap.get(picked.get())), 1, 12, 6, 0.2F);
            });

            event.getTrades().get(ModProfessions.EXPERT).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 12), Optional.of(new ItemCost(ModItems.BEE_CAGE.get(), 1)), new ItemStack(ModItems.STURDY_BEE_CAGE.get()), 3, 12, 6, 0.2F));
            event.getTrades().get(ModProfessions.EXPERT).add((trader, rand) -> new MerchantOffer(new ItemCost(Items.EMERALD, 6), Optional.empty(), new ItemStack(ModBlocks.JAR.get(), 1), 2, 12, 8, 0.2F));

            event.getTrades().get(ModProfessions.MASTER).add((trader, rand) -> {
                ItemStack nest = new ItemStack(Items.BEE_NEST);

                nest.applyComponents(DataComponentMap.builder().set(DataComponents.BEES, List.of(BeehiveBlockEntity.Occupant.create(0))).build());

                return new MerchantOffer(new ItemCost(Items.EMERALD, 32), Optional.empty(), nest, 1, 3, 16, 0.2F);
            });
            event.getTrades().get(ModProfessions.MASTER).add((trader, rand) -> {
                Item egg = switch (rand.nextInt(7)) {
                    case 0 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_quarry_bee"));
                    case 1 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_rancher_bee"));
                    case 2 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_farmer_bee"));
                    case 3 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_cupid_bee"));
                    case 4 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_collector_bee"));
                    case 5 -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_dye_bee"));
                    default -> BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "spawn_egg_lumber_bee"));
                };
                return new MerchantOffer(new ItemCost(Items.EMERALD, 24), Optional.empty(), new ItemStack(egg), 3, 12, 6, 0.2F);
            });
        }
    }

    @SubscribeEvent
    public static void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        if (!itemStack.isEmpty() && entity instanceof Bee) {
            Level level = entityInteract.getLevel();
            if (level instanceof ServerLevel serverLevel) {
                Player player = entityInteract.getEntity();
                BlockPos pos = entity.blockPosition();

                Entity newBee = BeeHelper.itemInteract((Bee) entity, itemStack, serverLevel, player);

                if (newBee instanceof Bee) {
                    // PLay event with smoke
                    level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    level.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    level.addFreshEntity(newBee);
                    if (((Bee) entity).isLeashed()) {
                        ((Bee) entity).dropLeash(true, true);
                    }
                    entity.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void cocoaBreakSpawn(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().getValue(CocoaBlock.AGE) == 2) {
            Player player = event.getPlayer();
            Level level = player.level();
            if (level instanceof ServerLevel && player instanceof ServerPlayer && level.random.nextFloat() < ProductiveBeesConfig.BEES.sugarbagBeeChance.get()) {
                ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(level);
                BlockPos pos = event.getPos();
                if (bee != null && BeeReloadListener.INSTANCE.getData("productivebees:sugarbag") != null) {
                    bee.setBeeType("productivebees:sugarbag");
                    bee.setDefaultAttributes();

                    bee.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

                    level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    level.playSound(player, pos, SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    level.addFreshEntity(bee);
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
                    boolean willSpawn = player.level().random.nextDouble() < recipe.chance;
                    int fishingLuck = EnchantmentHelper.getFishingLuckBonus(serverLevel, player.getMainHandItem(), player);
                    for (int i = 0; i < (1 + fishingLuck); i++) {
                        willSpawn = willSpawn || player.level().random.nextDouble() < recipe.chance;
                    }

                    if (willSpawn) {
                        possibleRecipes.add(recipe);
                    }
                }
            }

            if (!possibleRecipes.isEmpty()) {
                BeeFishingRecipe chosenRecipe = possibleRecipes.get(player.level().random.nextInt(possibleRecipes.size()));
                BeeIngredient beeIngredient = chosenRecipe.output.get();
                Bee bee = (Bee) beeIngredient.getBeeEntity().create(player.level());
                if (bee != null) {
                    if (bee instanceof ConfigurableBee configBee) {
                        configBee.setBeeType(beeIngredient.getBeeType().toString());
                        configBee.setDefaultAttributes();
                    }

                    bee.moveTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

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
    public static void onClientTick(ClientTickEvent.Post event) {
        Iterator<Entity> iter = JarBlockItemRenderer.beeEntities.values().iterator();

        while (iter.hasNext()) {
            Entity bee = iter.next();

            bee.tickCount++;

            // Every ~2.5 minutes or so remove bee from cache, so it doesn't tick forever
            // tickCount is a multiple of 360, so it should look about seamless
            if (bee.tickCount % (360 * 17) == 0) {
                iter.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel && event.getEntity() instanceof Bee entity && !entity.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
            entity.getData(ProductiveBees.ATTRIBUTE_HANDLER);
        }
    }

    @SubscribeEvent
    public static void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild() instanceof Bee bee && bee.level() instanceof ServerLevel && !bee.hasData(ProductiveBees.ATTRIBUTE_HANDLER)) {
            if (event.getParentA() instanceof Bee parenA && event.getParentB() instanceof AgeableMob parentB) {
                BeeHelper.setOffspringAttributes(bee, parenA, parentB);
            }
        }
    }
}
