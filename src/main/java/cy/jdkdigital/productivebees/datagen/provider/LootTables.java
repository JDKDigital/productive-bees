package cy.jdkdigital.productivebees.datagen.provider;

import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

public class LootTables extends BaseLootTableProvider
{
    public LootTables(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected void addTables() {
        for (RegistryObject<Block> registryObject : ModBlocks.BLOCKS.getEntries()) {
            Block block = registryObject.get();
            ResourceLocation name = block.getRegistryName();

            assert name != null;

//            lootTables.put(block, createStandardTable(name.getPath(), block));
        }
    }
}
