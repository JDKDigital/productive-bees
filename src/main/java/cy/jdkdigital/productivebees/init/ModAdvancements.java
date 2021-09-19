package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.common.advancements.criterion.*;
import net.minecraft.advancements.CriteriaTriggers;

public class ModAdvancements
{
    public static CatchBeeTrigger CATCH_BEE;
    public static CalmBeeTrigger CALM_BEE;
    public static FishBeeTrigger FISH_BEE;
    public static SaddleBeeTrigger SADDLE_BEE;
    public static HoneyloggedTrigger HONEYLOGGED;

    public static void register() {
        CATCH_BEE = CriteriaTriggers.register(new CatchBeeTrigger());
        CALM_BEE = CriteriaTriggers.register(new CalmBeeTrigger());
        FISH_BEE = CriteriaTriggers.register(new FishBeeTrigger());
        SADDLE_BEE = CriteriaTriggers.register(new SaddleBeeTrigger());
        HONEYLOGGED = CriteriaTriggers.register(new HoneyloggedTrigger());
    }
}
