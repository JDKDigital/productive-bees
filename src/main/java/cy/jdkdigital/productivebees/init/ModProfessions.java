package cy.jdkdigital.productivebees.init;

import com.google.common.collect.ImmutableSet;
import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Predicate;

@EventBusSubscriber(modid = ProductiveBees.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModProfessions
{
    public static final int NOVICE = 1;
    public static final int APPRENTICE = 2;
    public static final int JOURNEYMAN = 3;
    public static final int EXPERT = 4;
    public static final int MASTER = 5;

    private static final Predicate<Holder<PoiType>> beeKeeperPoi = (poi) -> poi.is(ModPointOfInterestTypes.ADVANCED_HIVES.getKey());

    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(Registries.VILLAGER_PROFESSION, ProductiveBees.MODID);

    public static final DeferredHolder<VillagerProfession, VillagerProfession> BEEKEEPER = PROFESSIONS.register("beekeeper", () -> new VillagerProfession("beekeeper", beeKeeperPoi, beeKeeperPoi, ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_LEATHERWORKER));

    public static void register() {
        // TODO move to data see NeoForgeDataMaps.RAID_HERO_GIFTS
//        GiveGiftToHero.GIFTS.put(BEEKEEPER.get(), new ResourceLocation(ProductiveBees.MODID, "gameplay/hero_of_the_village/beekeeper_gifts.json"));
    }
}
