package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
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
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID)
public class EventHandler
{
    @SubscribeEvent
    public static void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        if (!itemStack.isEmpty() && entity instanceof BeeEntity) {
            World world = entityInteract.getWorld();
            PlayerEntity player = entityInteract.getPlayer();
            BlockPos pos = entity.getPosition();
            Hand hand = entityInteract.getHand();

            BeeEntity newBee = BeeHelper.itemInteract((BeeEntity) entity, itemStack, world, entity.serializeNBT(), player, hand, entity.getHorizontalFacing());

            if (newBee != null) {
                // PLay event with smoke
                world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                world.addEntity(newBee);
                entity.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        ItemStack stack = event.getResultStack();
        if (stack.getItem().equals(Items.HONEY_BOTTLE)) {
            LivingEntity entity = event.getEntityLiving();
            if (!entity.getEntityWorld().isRemote && entity.getEntityWorld().rand.nextBoolean()) {
                entity.curePotionEffects(stack);
            }
        }
    }

    @SubscribeEvent
    public static void cocoaBreakSpawn(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock().equals(Blocks.COCOA) && event.getState().get(CocoaBlock.AGE) == 2) {
            PlayerEntity player = event.getPlayer();
            World world = event.getWorld().getWorld();
            if (player instanceof ServerPlayerEntity && !world.isRemote && ProductiveBees.rand.nextFloat() < 0.05) {
                ConfigurableBeeEntity bee = ModEntities.CONFIGURABLE_BEE.get().create(world);
                BlockPos pos = event.getPos();
                if (bee != null) {
                    bee.setBeeType("productivebees:sugarbag");
                    bee.setAttributes();
                    BeeHelper.prepareBeeSpawn(bee, pos, Direction.random(ProductiveBees.rand), 0);

                    world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                    world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                    world.addEntity(bee);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            PacketHandler.sendToPlayer(new Messages.BeesMessage(BeeReloadListener.INSTANCE.getData()), (ServerPlayerEntity) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ConfigurableBeeEntity) {
            CompoundNBT tag = new CompoundNBT();
            entity.writeUnlessPassenger(tag);

            if (!tag.contains("type") || tag.getString("type").isEmpty()) {
                // Config bees from summon and spawners have no assigned type
                Map<String, CompoundNBT> data = BeeReloadListener.INSTANCE.getData();

                List<String> beeTypes = new ArrayList<>(data.keySet());
                if (!beeTypes.isEmpty()) {
                    ((ConfigurableBeeEntity) entity).setBeeType(beeTypes.get(ProductiveBees.rand.nextInt(beeTypes.size())));
                }
            } else {
                if (tag.getString("type").equals("productivebees:ghostly") && ProductiveBees.rand.nextFloat() < 0.02f) {
                    entity.setCustomName(new StringTextComponent("BooBee"));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLootSetup(LootTableLoadEvent event) {
        if (event.getName().toString().contains("chests/village")) {
            event.getTable().getPool("main").lootEntries.add(
                ItemLootEntry.builder(ModItems.STURDY_BEE_CAGE.get()).weight(4).build()
            );
        }
    }
}
