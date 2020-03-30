package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.container.AdvancedBeehiveContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ModContainerTypes {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, ProductiveBees.MODID);

	public static final RegistryObject<ContainerType<AdvancedBeehiveContainer>> ADVANCED_BEEHIVE = CONTAINER_TYPES.register("advanced_beehive", () ->
		IForgeContainerType.create(AdvancedBeehiveContainer::new)
	);
}
