package cy.jdkdigital.productivebees.init;

import com.google.common.collect.ImmutableSet;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = ProductiveBees.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModProfessions
{
    public static final int NOVICE = 1;
    public static final int APPRENTICE = 2;
    public static final int JOURNEYMAN = 3;
    public static final int EXPERT = 4;
    public static final int MASTER = 5;

    private static final Predicate<Holder<PoiType>> beeKeeperPoi = (poi) -> poi.is(ModPointOfInterestTypes.ADVANCED_HIVES.getKey());

    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, ProductiveBees.MODID);

    public static final RegistryObject<VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", beeKeeperPoi, beeKeeperPoi, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_LEATHERWORKER));

    public static void register() {
        GiveGiftToHero.GIFTS.put(BEEKEEPER.get(), new ResourceLocation(ProductiveBees.MODID, "gameplay/hero_of_the_village/beekeeper_gifts.json"));
    }
}
