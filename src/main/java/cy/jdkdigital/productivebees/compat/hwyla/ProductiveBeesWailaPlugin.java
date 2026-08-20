package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.AdvancedBeehiveAbstract;
import cy.jdkdigital.productivebees.common.block.CanvasBeehive;
import cy.jdkdigital.productivebees.common.block.CanvasExpansionBox;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.bee.Bee;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesWailaPlugin implements IWailaPlugin
{
    public static final Identifier BEE_ATTRIBUTES = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "bee_attributes");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SolitaryNestDataProvider.INSTANCE, SolitaryNestBlockEntity.class);
        registration.registerBlockDataProvider(AdvancedBeehiveDataProvider.INSTANCE, AdvancedBeehiveBlockEntityAbstract.class);
        registration.registerEntityDataProvider(BeeServerDataProvider.INSTANCE, Bee.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CanvasBeehiveProvider.INSTANCE, CanvasBeehive.class);
        registration.registerBlockComponent(CanvasExpansionBoxProvider.INSTANCE, CanvasExpansionBox.class);
        registration.registerBlockComponent(SolitaryNestProvider.INSTANCE, SolitaryNest.class);
        registration.registerBlockComponent(AdvancedBeehiveProvider.INSTANCE, AdvancedBeehiveAbstract.class);
        registration.registerEntityComponent(BeeComponentDataProvider.INSTANCE, Bee.class);
        registration.addConfig(BEE_ATTRIBUTES, true);
    }
}
