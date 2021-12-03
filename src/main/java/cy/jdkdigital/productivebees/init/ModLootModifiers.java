package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.event.loot.SturdyCageModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModLootModifiers
{
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_SERIALIZERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, ProductiveBees.MODID);

    public static final RegistryObject<GlobalLootModifierSerializer<SturdyCageModifier>> VILLAGE_CHEST_STURDY_CAGE = LOOT_SERIALIZERS.register("village_chest_sturdy_cage", SturdyCageModifier.Serializer::new);
}
