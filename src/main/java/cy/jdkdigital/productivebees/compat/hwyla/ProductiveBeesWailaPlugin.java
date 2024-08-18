package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.block.CanvasBeehive;
import cy.jdkdigital.productivebees.common.block.CanvasExpansionBox;
import cy.jdkdigital.productivebees.common.block.Jar;
import cy.jdkdigital.productivebees.common.block.SolitaryNest;
import cy.jdkdigital.productivebees.common.block.entity.JarBlockEntity;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.init.ModBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(value = ProductiveBees.MODID)
public class ProductiveBeesWailaPlugin implements IWailaPlugin
{
    public static final ResourceLocation BEE_ATTRIBUTES = ResourceLocation.fromNamespaceAndPath(ProductiveBees.MODID, "bee_attributes");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(SolitaryNestProvider.INSTANCE, SolitaryNestBlockEntity.class);
        registration.registerBlockDataProvider(JarProvider.INSTANCE, JarBlockEntity.class);
        registration.registerEntityDataProvider(BeeComponentDataProvider.INSTANCE, Bee.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CanvasBeehiveProvider.INSTANCE, CanvasBeehive.class);
        registration.registerBlockComponent(CanvasExpansionBoxProvider.INSTANCE, CanvasExpansionBox.class);
        registration.registerBlockComponent(SolitaryNestProvider.INSTANCE, SolitaryNest.class);
        registration.registerBlockComponent(JarProvider.INSTANCE, Jar.class);
        registration.registerEntityComponent(BeeComponentDataProvider.INSTANCE, Bee.class);
        registration.addConfig(BEE_ATTRIBUTES, true);
        registration.usePickedResult(ModBlocks.CONFIGURABLE_COMB.get());
    }
}
