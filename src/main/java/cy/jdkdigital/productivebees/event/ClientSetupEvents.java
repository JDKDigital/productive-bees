package cy.jdkdigital.productivebees.event;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.CombBlock;
import cy.jdkdigital.productivebees.common.item.Honeycomb;
import cy.jdkdigital.productivebees.common.item.SpawnEgg;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.world.GrassColors;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents
{
    @SubscribeEvent
    public static void registerItemColors(final ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();
        BlockColors blockColors = event.getBlockColors();

        for (RegistryObject<Item> eggItem : ModItems.SPAWN_EGGS) {
            if (ObfuscationReflectionHelper.getPrivateValue(RegistryObject.class, eggItem, "value") != null) {
                Item item = eggItem.get();
                if (item instanceof SpawnEgg) {
                    colors.register((stack, tintIndex) -> ((SpawnEgg) item).getColor(tintIndex, stack), item);
                }
            }
        }

        // Honeycomb colors
        for (RegistryObject<Item> registryItem : ModItems.ITEMS.getEntries()) {
            Item item = registryItem.get();
            if (item instanceof Honeycomb) {
                colors.register((stack, tintIndex) -> ((Honeycomb) item).getColor(stack), item);
            }
            else if (item instanceof BlockItem) {
                Block block = ((BlockItem) item).getBlock();
                if (block instanceof CombBlock) {
                    colors.register((stack, tintIndex) -> ((CombBlock) block).getColor(stack), item);
                }
            }
        }

        colors.register((stack, tintIndex) -> {
            BlockState blockstate = ((BlockItem)stack.getItem()).getBlock().getDefaultState();
            return blockColors.getColor(blockstate, null, null, tintIndex);
        }, ModBlocks.BUMBLE_BEE_NEST.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getGrassColor(lightReader, pos) : -1;
        }, ModBlocks.SUGAR_CANE_NEST.get());

        colors.register((blockState, lightReader, pos, tintIndex) -> {
            return lightReader != null && pos != null ? BiomeColors.getGrassColor(lightReader, pos) : GrassColors.get(0.5D, 1.0D);
        }, ModBlocks.BUMBLE_BEE_NEST.get());

        for (RegistryObject<Block> registryBlock : ModBlocks.BLOCKS.getEntries()) {
            Block block = registryBlock.get();
            if (block instanceof CombBlock) {
                colors.register((blockState, lightReader, pos, tintIndex) -> ((CombBlock) block).getColor(lightReader, pos), block);
            }
        }
    }
}
