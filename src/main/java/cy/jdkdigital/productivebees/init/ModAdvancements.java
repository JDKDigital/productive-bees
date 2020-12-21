package cy.jdkdigital.productivebees.init;

import cy.jdkdigital.productivebees.common.advancements.criterion.CalmBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.CatchBeeTrigger;
import cy.jdkdigital.productivebees.common.advancements.criterion.HoneyloggedTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.RightClickBlockWithItemTrigger;
import net.minecraft.util.ResourceLocation;

public class ModAdvancements
{
    public static CatchBeeTrigger CATCH_BEE;
    public static CalmBeeTrigger CALM_BEE;
    public static HoneyloggedTrigger HONEYLOGGED;

    public static RightClickBlockWithItemTrigger RIGHT_CLICK_BLOCK_WITH_ITEM;

    public static void register() {
        CATCH_BEE = CriteriaTriggers.register(new CatchBeeTrigger());
        CALM_BEE = CriteriaTriggers.register(new CalmBeeTrigger());
        HONEYLOGGED = CriteriaTriggers.register(new HoneyloggedTrigger());
        RIGHT_CLICK_BLOCK_WITH_ITEM = CriteriaTriggers.register(new RightClickBlockWithItemTrigger(new ResourceLocation("item_used_on_block")));
    }
}
