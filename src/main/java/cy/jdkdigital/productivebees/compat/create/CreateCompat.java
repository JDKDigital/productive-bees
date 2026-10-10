package cy.jdkdigital.productivebees.compat.create;

import com.simibubi.create.api.schematic.requirement.SchematicRequirementRegistries;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import cy.jdkdigital.productivebees.common.block.ConfigurableCombBlock;
import cy.jdkdigital.productivebees.init.ModBlocks;
import cy.jdkdigital.productivebees.init.ModDataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CreateCompat
{
    /**
     * Create only puts the plain block item into the requirements it hands to the blueprint cannon and the deployer,
     * so printing a configurable comb block asks for any comb block and ends up pulling a random bee variant out of
     * the attached inventories. Registering the exact variant makes Create match on the bee type as well.
     */
    public static void registerSchematicRequirements() {
        Block combBlock = ModBlocks.CONFIGURABLE_COMB.get();
        SchematicRequirementRegistries.BLOCKS.register(combBlock, (state, blockEntity) -> {
            ItemStack stack = ConfigurableCombBlock.getCombBlockStack(blockEntity);
            if (!stack.has(ModDataComponents.BEE_TYPE)) {
                return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stack);
            }
            return new ItemRequirement(new ItemRequirement.StrictNbtStackRequirement(stack, ItemRequirement.ItemUseType.CONSUME));
        });
    }
}
