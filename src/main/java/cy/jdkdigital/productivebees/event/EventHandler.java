package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.init.ModAdvancements;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        if (!itemStack.isEmpty() && entity instanceof BeeEntity) {
            World world = entityInteract.getWorld();
            if (world instanceof ServerWorld) {
                PlayerEntity player = entityInteract.getPlayer();
                BlockPos pos = entity.blockPosition();

                Entity newBee = BeeHelper.itemInteract((BeeEntity) entity, itemStack, (ServerWorld) world, entity.serializeNBT(), player);

                if (newBee != null) {
                    // PLay event with smoke
                    world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BEEHIVE_WORK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                    world.addFreshEntity(newBee);
                    entity.remove();
                }
            }
        }
    }

    @SubscribeEvent
    public static void cocoaBreakSpawn(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().getValue(CocoaBlock.AGE) == 2) {
            PlayerEntity player = event.getPlayer();
            World world = player.level;
            if (world instanceof ServerWorld && player instanceof ServerPlayerEntity && ProductiveBees.rand.nextDouble() < ProductiveBeesConfig.BEES.sugarbagBeeChance.get()) {
                ConfigurableBeeEntity bee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                BlockPos pos = event.getPos();
                if (bee != null && BeeReloadListener.INSTANCE.getData("productivebees:sugarbag") != null) {
                    bee.setBeeType("productivebees:sugarbag");
                    bee.setAttributes();

                    bee.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, bee.yRot, bee.xRot);

                    world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    world.playSound(player, pos, SoundEvents.BEEHIVE_WORK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                    world.addFreshEntity(bee);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLootSetup(LootTableLoadEvent event) {
        if (event.getName().toString().contains("chests/village")) {
            event.getTable().getPool("main").entries.add(
                ItemLootEntry.lootTableItem(ModItems.STURDY_BEE_CAGE.get()).setWeight(4).build()
            );
        }
    }

    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player != null) {
            boolean willSpawn = ProductiveBees.rand.nextDouble() < ProductiveBeesConfig.BEES.fishingBeeChance.get();
            int fishingLuck = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_LUCK, player.getMainHandItem());
            for (int i = 0; i < (1 + fishingLuck); i++) {
                willSpawn = willSpawn || ProductiveBees.rand.nextDouble() < ProductiveBeesConfig.BEES.fishingBeeChance.get();
            }

            if (willSpawn) {
                ConfigurableBeeEntity bee = ModEntities.CONFIGURABLE_BEE.get().create(player.level);
                BlockPos hookPos = event.getHookEntity().blockPosition();
                if (bee != null && BeeReloadListener.INSTANCE.getData("productivebees:prismarine") != null) {
                    Biome fishingBiome = player.level.getBiome(hookPos);
                    if (fishingBiome.getBiomeCategory().equals(Biome.Category.OCEAN)) {

                        bee.setBeeType("productivebees:prismarine");
                        bee.setAttributes();

                        bee.moveTo(hookPos.getX() + 0.5D, hookPos.getY() + 1, hookPos.getZ() + 0.5D, bee.yRot, bee.xRot);

                        player.level.addParticle(ParticleTypes.POOF, hookPos.getX(), hookPos.getY() + 1, hookPos.getZ(), 0.2D, 0.1D, 0.2D);
                        player.level.playSound(player, hookPos, SoundEvents.BEE_HURT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                        player.level.addFreshEntity(bee);

                        bee.setTarget(player);

                        ModAdvancements.FISH_BEE.trigger((ServerPlayerEntity) player, bee);
                    }
                }
            }
        }
    }
}
