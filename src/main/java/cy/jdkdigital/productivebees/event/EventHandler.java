package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.entity.bee.ProductiveBeeEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModFeatures;
import cy.jdkdigital.productivebees.init.ModPointOfInterestTypes;
import cy.jdkdigital.productivebees.util.BeeHelper;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ReplaceBlockConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;

public class EventHandler
{
    @SubscribeEvent
    public void entityRightClicked(PlayerInteractEvent.EntityInteract entityInteract) {
        ItemStack itemStack = entityInteract.getItemStack();
        Entity entity = entityInteract.getTarget();

        if (!itemStack.isEmpty() && entity instanceof BeeEntity) {
            World world = entityInteract.getWorld();
            PlayerEntity player = entityInteract.getPlayer();
            BlockPos pos = entity.getPosition();
            Hand hand = entityInteract.getHand();

            BeeEntity newBee = BeeHelper.itemInteract((BeeEntity)entity, itemStack, world, entity.serializeNBT(), player, hand, entity.getHorizontalFacing());

            if (newBee != null) {
                // PLay event with smoke
                world.addParticle(ParticleTypes.POOF, pos.getX(), pos.getY() + 1, pos.getZ(), 0.2D, 0.1D, 0.2D);
                world.playSound(player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

                world.addEntity(newBee);
                entity.remove();
            }
        }
    }
}
