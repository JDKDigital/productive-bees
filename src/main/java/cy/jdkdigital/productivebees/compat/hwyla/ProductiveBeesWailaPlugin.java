package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.*;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesWailaPlugin implements IWailaPlugin
{
    public static final ResourceLocation BEE_ATTRIBUTES = new ResourceLocation(ProductiveBees.MODID, "bee_attributes");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SolitaryNestProvider.INSTANCE, SolitaryNestBlockEntity.class);
        registration.registerBlockDataProvider(JarProvider.INSTANCE, JarBlockEntity.class);
        registration.registerBlockDataProvider(BeeNestProvider.INSTANCE, AdvancedBeehiveBlockEntityAbstract.class);
        registration.registerEntityDataProvider(ProductiveBeeProvider.INSTANCE, ProductiveBee.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CanvasBeehiveProvider.INSTANCE, CanvasBeehive.class);
        registration.registerBlockComponent(CanvasExpansionBoxProvider.INSTANCE, CanvasExpansionBox.class);
        registration.registerBlockComponent(SolitaryNestProvider.INSTANCE, SolitaryNest.class);
        registration.registerBlockComponent(JarProvider.INSTANCE, Jar.class);
        registration.registerBlockComponent(BeeNestProvider.INSTANCE, AdvancedBeehive.class);
        registration.registerBlockComponent(ConfigurableCombProvider.INSTANCE, ConfigurableCombBlock.class);
        registration.registerEntityComponent(ProductiveBeeProvider.INSTANCE, ProductiveBee.class);
        registration.addConfig(BEE_ATTRIBUTES, true);
    }
}
