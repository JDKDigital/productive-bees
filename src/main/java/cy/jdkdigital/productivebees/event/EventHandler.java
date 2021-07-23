package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.common.entity.bee.ConfigurableBeeEntity;
import cy.jdkdigital.productivebees.init.ModEntities;
import cy.jdkdigital.productivebees.init.ModItems;
import cy.jdkdigital.productivebees.network.PacketHandler;
import cy.jdkdigital.productivebees.network.packets.Messages;
import cy.jdkdigital.productivebees.setup.BeeReloadListener;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.Blocks;
import net.minecraft.block.CocoaBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getResultStack();
        if (stack.getItem().equals(Items.HONEY_BOTTLE)) {
            LivingEntity entity = event.getEntityLiving();
            if (!entity.getCommandSenderWorld().isClientSide && entity.getCommandSenderWorld().random.nextBoolean()) {
                entity.curePotionEffects(stack);
            }
        }
    }

    @SubscribeEvent
    public static void cocoaBreakSpawn(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().getValue(CocoaBlock.AGE) == 2) {
            PlayerEntity player = event.getPlayer();
            World world = player.level;
            if (world instanceof ServerWorld && player instanceof ServerPlayerEntity && ProductiveBees.rand.nextFloat() < ProductiveBeesConfig.BEES.sugarbagBeeChance.get()) {
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
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            // Send data
            PacketHandler.sendBeeDataToPlayer(new Messages.BeeDataMessage(BeeReloadListener.INSTANCE.getData()), (ServerPlayerEntity) event.getEntity());

            // Send reindex message
            int delay = ProductiveBeesConfig.GENERAL.beeSyncDelay.get();
            if (delay > 0 ) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(() -> {
                    PacketHandler.sendReindexCommandToPlayer(new Messages.ReindexMessage(), (ServerPlayerEntity) event.getEntity());
                }, delay, TimeUnit.SECONDS);
                executorService.shutdown();
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        // Clean up "Random spawn bonus" modifier added to bees each trip to a hive prior to 0.6.9.6
        if (entity instanceof BeeEntity) {
            ModifiableAttributeInstance attrib = ((BeeEntity) entity).getAttribute(Attributes.FOLLOW_RANGE);
            if (attrib != null && attrib.getModifiers().size() > 1) {
                for (AttributeModifier modifier : attrib.getModifiers()) {
                    attrib.removeModifier(modifier);
                }
                attrib.addPermanentModifier(new AttributeModifier("Random spawn bonus", ProductiveBees.rand.nextGaussian() * 0.05D, AttributeModifier.Operation.MULTIPLY_BASE));
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
}
