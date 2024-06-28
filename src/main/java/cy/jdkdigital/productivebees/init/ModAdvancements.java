package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.ProductiveBees;
import cy.jdkdigital.productivebees.common.advancements.criterion.CalmBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.CatchBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.FishBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.SaddleBeeTrigger;
import net.minecraft.advancements.CriterionTrigger;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModAdvancements
{
    public static DeferredHolder<CriterionTrigger<?>, CatchBeeTrigger> CATCH_BEE = ProductiveBees.TRIGGER_TYPES.register("catch_bee", CatchBeeTrigger::new);
    public static DeferredHolder<CriterionTrigger<?>, CalmBeeTrigger> CALM_BEE = ProductiveBees.TRIGGER_TYPES.register("calm_bee", CalmBeeTrigger::new);
    public static DeferredHolder<CriterionTrigger<?>, FishBeeTrigger> FISH_BEE = ProductiveBees.TRIGGER_TYPES.register("fish_bee", FishBeeTrigger::new);
    public static DeferredHolder<CriterionTrigger<?>, SaddleBeeTrigger> SADDLE_BEE = ProductiveBees.TRIGGER_TYPES.register("saddle_bee", SaddleBeeTrigger::new);

    public static void register() {
    }
}
