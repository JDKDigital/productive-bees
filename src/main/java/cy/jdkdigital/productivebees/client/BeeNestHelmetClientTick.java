package cy.jdkdigital.productivebees.client;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.ProductiveBeesConfig;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Spawns the falling-nectar particles around the local player when they're wearing the bee-nest
 * helmet. The pre-26.1 implementation did this from {@code Item.inventoryTick}, but that hook now
 * fires server-only, so the visual half lives client-side.
 */
@EventBusSubscriber(modid = ProductiveBees.MODID, value = Dist.CLIENT)
public final class BeeNestHelmetClientTick
{
    private BeeNestHelmetClientTick() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        Level level = mc.level;
        if (player == null || level == null || mc.isPaused()) {
            return;
        }
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() != ModItems.BEE_NEST_DIAMOND_HELMET.get()) {
            return;
        }
        if (level.getRandom().nextDouble() < 0.015D) {
            BlockPos pos = player.blockPosition();
            level.addParticle(ParticleTypes.FALLING_NECTAR,
                    Mth.lerp(level.getRandom().nextDouble(), pos.getX() - 0.5D, pos.getX() + 0.5D),
                    pos.getY() + 1.8D,
                    Mth.lerp(level.getRandom().nextDouble(), pos.getZ() - 0.5D, pos.getZ() + 0.5D),
                    0.0D, 0.0D, 0.0D);
        }
        if (!ProductiveBeesConfig.CLIENT.mutedBeeNestHelmet.get() && level.getRandom().nextDouble() < 0.005D) {
            BlockPos pos = player.blockPosition();
            level.playLocalSound(pos.getX(), pos.getY() + 2D, pos.getZ(),
                    level.getRandom().nextBoolean() ? SoundEvents.BEEHIVE_WORK : SoundEvents.BEEHIVE_DRIP,
                    SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }
    }
}
