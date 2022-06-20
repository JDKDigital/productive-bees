package cy.jdkdigital.productivebees.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBee;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import cy.jdkdigital.productivebees.common.entity.bee.solitary.BlueBandedBee;
import cy.jdkdigital.productivebees.handler.bee.IInhabitantStorage;
import cy.jdkdigital.productivebees.init.*;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void onWandererTradesEvent(WandererTradesEvent event) {
        event.getGenericTrades().add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 10), ItemStack.EMPTY, new ItemStack(ModItems.STURDY_BEE_CAGE.get()), 1, 12, 6, 2));
        event.getGenericTrades().add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 24), ItemStack.EMPTY, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_bumble_bee"))), 3, 12, 6, 2));
    }

    @SubscribeEvent
    public static void onVillagerTradesEvent(VillagerTradesEvent event) {
        if (event.getType().equals(ModProfessions.BEEKEEPER.get())) {
            // Novice
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.CAMPFIRE), ItemStack.EMPTY, new ItemStack(Items.EMERALD), 1, 12, 6, 2));
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), ItemStack.EMPTY, new ItemStack(Items.GLASS_BOTTLE, 4), 1, 32, 3, 2));
            event.getTrades().get(ModProfessions.NOVICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 3), ItemStack.EMPTY, new ItemStack(Items.SHEARS), 3, 12, 3, 2));

            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.HONEY_BOTTLE, 2), ItemStack.EMPTY, new ItemStack(Items.EMERALD), 3, 10, 6, 2));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), ItemStack.EMPTY, new ItemStack(ModItems.BEE_CAGE.get(), 4), 3, 64, 6, 2));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 2), ItemStack.EMPTY, new ItemStack(ModItems.SUGARBAG_HONEYCOMB.get()), 3, 32, 3, 2));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), ItemStack.EMPTY, new ItemStack(ModItems.TREAT_ON_A_STICK.get()), 3, 8, 3, 2));
            event.getTrades().get(ModProfessions.APPRENTICE).add((trader, rand) -> {
                if (rand.nextBoolean()) {
                    return new MerchantOffer(new ItemStack(ModItems.HONEY_TREAT.get(), 4), ItemStack.EMPTY, new ItemStack(Items.EMERALD), 3, 50, 3, 2);
                }
                return new MerchantOffer(new ItemStack(Items.EMERALD), ItemStack.EMPTY, new ItemStack(ModItems.HONEY_TREAT.get(), 2), 3, 100, 3, 2);
            });

            event.getTrades().get(ModProfessions.JOURNEYMAN).add((trader, rand) -> {
                Block hive = ModBlocks.ADVANCED_OAK_BEEHIVE.get();
                if (trader instanceof Villager villager) {
                    VillagerType villagertype = villager.getVillagerData().getType();
                    if (villagertype.equals(VillagerType.JUNGLE)) {
                        hive = ModBlocks.ADVANCED_JUNGLE_BEEHIVE.get();
                    } else if (villagertype.equals(VillagerType.SAVANNA)) {
                        hive = ModBlocks.ADVANCED_ACACIA_BEEHIVE.get();
                    } else if (villagertype.equals(VillagerType.DESERT)) {
                        hive = ModBlocks.ADVANCED_BIRCH_BEEHIVE.get();
                    } else if (villagertype.equals(VillagerType.SWAMP)) {
                        hive = ModBlocks.ADVANCED_DARK_OAK_BEEHIVE.get();
                    } else if (villagertype.equals(VillagerType.TAIGA) || villagertype.equals(VillagerType.SNOW)) {
                        hive = ModBlocks.ADVANCED_SPRUCE_BEEHIVE.get();
                    }
                }
                return new MerchantOffer(new ItemStack(Items.BEEHIVE, 1), new ItemStack(Items.EMERALD, 6), new ItemStack(hive), 1, 12, 6, 2);
            });
            event.getTrades().get(ModProfessions.JOURNEYMAN).add((trader, rand) -> {
                Block box = ModBlocks.EXPANSION_BOX_OAK.get();
                if (trader instanceof Villager villager) {
                    VillagerType villagertype = villager.getVillagerData().getType();
                    if (villagertype.equals(VillagerType.JUNGLE)) {
                        box = ModBlocks.EXPANSION_BOX_JUNGLE.get();
                    } else if (villagertype.equals(VillagerType.SAVANNA)) {
                        box = ModBlocks.EXPANSION_BOX_ACACIA.get();
                    } else if (villagertype.equals(VillagerType.DESERT)) {
                        box = ModBlocks.EXPANSION_BOX_BIRCH.get();
                    } else if (villagertype.equals(VillagerType.SWAMP)) {
                        box = ModBlocks.EXPANSION_BOX_DARK_OAK.get();
                    } else if (villagertype.equals(VillagerType.TAIGA) || villagertype.equals(VillagerType.SNOW)) {
                        box = ModBlocks.EXPANSION_BOX_SPRUCE.get();
                    }
                }
                return new MerchantOffer(new ItemStack(Items.EMERALD, 4), ItemStack.EMPTY, new ItemStack(box), 1, 12, 6, 2);
            });

            event.getTrades().get(ModProfessions.EXPERT).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 12), new ItemStack(ModItems.BEE_CAGE.get(), 1), new ItemStack(ModItems.STURDY_BEE_CAGE.get()), 3, 12, 6, 2));
            event.getTrades().get(ModProfessions.EXPERT).add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 6), ItemStack.EMPTY, new ItemStack(ModBlocks.JAR.get(), 1), 2, 12, 8, 2));

            event.getTrades().get(ModProfessions.MASTER).add((trader, rand) -> {
                ItemStack nest = new ItemStack(Items.BEE_NEST);

                try {
                    CompoundTag tag = nest.getOrCreateTag();
                    CompoundTag bees = TagParser.parseTag("{Bees:[{EntityData:{id:\"minecraft:bee\"},TicksInHive:0,MinOccupationTicks:0}]}");
                    tag.put("BlockEntityTag", bees);
                } catch (CommandSyntaxException e) {
                    ProductiveBees.LOGGER.warn("Failed to put bees into the beekeepers nests :(" + e.getMessage());
                }

                return new MerchantOffer(new ItemStack(Items.EMERALD, 32), ItemStack.EMPTY, nest, 1, 3, 16, 2);
            });
            event.getTrades().get(ModProfessions.MASTER).add((trader, rand) -> {
                Item egg = switch (rand.nextInt(7)) {
                    case 0 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_quarry_bee"));
                    case 1 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_rancher_bee"));
                    case 2 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_farmer_bee"));
                    case 3 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_cupid_bee"));
                    case 4 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_hoarder_bee"));
                    case 5 -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_dye_bee"));
                    default -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(ProductiveBees.MODID, "spawn_egg_lumber_bee"));
                };
                return new MerchantOffer(new ItemStack(Items.EMERALD, 24), ItemStack.EMPTY, new ItemStack(egg), 3, 12, 6, 2);
            });
        }
    }

    @SubscribeEvent
    public static void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        if (!itemStack.isEmpty() && entity instanceof Bee) {
            Level world = entityInteract.getWorld();
            if (world instanceof ServerLevel) {
                Player player = entityInteract.getPlayer();
                BlockPos pos = entity.blockPosition();

                Entity newBee = BeeHelper.itemInteract((Bee) entity, itemStack, (ServerLevel) world, entity.serializeNBT(), player);

                if (newBee != null) {
                    // PLay event with smoke
                    world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    world.addFreshEntity(newBee);
                    entity.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void cocoaBreakSpawn(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().getValue(CocoaBlock.AGE) == 2) {
            Player player = event.getPlayer();
            Level world = player.level;
            if (world instanceof ServerLevel && player instanceof ServerPlayer && ProductiveBees.rand.nextFloat() < ProductiveBeesConfig.BEES.sugarbagBeeChance.get()) {
                ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                BlockPos pos = event.getPos();
                if (bee != null && BeeReloadListener.INSTANCE.getData("productivebees:sugarbag") != null) {
                    bee.setBeeType("productivebees:sugarbag");
                    bee.setAttributes();

                    bee.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

                    world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    world.playSound(player, pos, SoundEvents.BEEHIVE_WORK, SoundSource.NEUTRAL, 1.0F, 1.0F);

                    world.addFreshEntity(bee);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
        // Entity attribute assignments
        for (RegistryObject<EntityType<?>> registryObject : ModEntities.HIVE_BEES.getEntries()) {
            EntityType<ProductiveBee> bee = (EntityType<ProductiveBee>) registryObject.get();
            event.put(bee, Bee.createAttributes().build());
        }
        for (RegistryObject<EntityType<?>> registryObject : ModEntities.SOLITARY_BEES.getEntries()) {
            EntityType<ProductiveBee> bee = (EntityType<ProductiveBee>) registryObject.get();
            if (!bee.getDescriptionId().contains("blue_banded_bee")) {
                event.put(bee, Bee.createAttributes().build());
            }
        }
        event.put(ModEntities.BLUE_BANDED_BEE.get(), BlueBandedBee.getDefaultAttributes().build());
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
        Player player = event.getPlayer();
        if (player != null) {
            boolean willSpawn = ProductiveBees.rand.nextDouble() < ProductiveBeesConfig.BEES.fishingBeeChance.get();
            int fishingLuck = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_LUCK, player.getMainHandItem());
            for (int i = 0; i < (1 + fishingLuck); i++) {
                willSpawn = willSpawn || ProductiveBees.rand.nextDouble() < ProductiveBeesConfig.BEES.fishingBeeChance.get();
            }

            if (willSpawn) {
                ConfigurableBee bee = ModEntities.CONFIGURABLE_BEE.get().create(player.level);
                BlockPos pos = event.getHookEntity().blockPosition();
                if (bee != null && BeeReloadListener.INSTANCE.getData("productivebees:prismarine") != null) {
                    Biome fishingBiome = player.level.getBiome(pos).value();
                    if (fishingBiome.getBiomeCategory().equals(Biome.BiomeCategory.OCEAN)) {
                        bee.setBeeType("productivebees:prismarine");
                        if (BeeReloadListener.INSTANCE.getData("productivebees:oily") != null && ProductiveBees.rand.nextBoolean()) {
                            bee.setBeeType("productivebees:oily");
                        }
                        bee.setAttributes();

                        bee.moveTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, bee.getYRot(), bee.getXRot());

                        player.level.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                        player.level.playSound(player, pos, SoundEvents.BEE_HURT, SoundSource.NEUTRAL, 1.0F, 1.0F);

                        player.level.addFreshEntity(bee);

                        bee.setTarget(player);

                        ModAdvancements.FISH_BEE.trigger((ServerPlayer) player, bee);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(IInhabitantStorage.class);
    }
}
