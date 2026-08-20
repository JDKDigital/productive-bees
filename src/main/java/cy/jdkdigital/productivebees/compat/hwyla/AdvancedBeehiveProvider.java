package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.ProductiveBees;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedBeehiveProvider implements IBlockComponentProvider
{
    public static final Identifier UID = Identifier.fromNamespaceAndPath(ProductiveBees.MODID, "advanced_beehive");
    static final AdvancedBeehiveProvider INSTANCE = new AdvancedBeehiveProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var serverData = accessor.getServerData();
        if (!serverData.contains("bee_names")) return;
        ListTag names = serverData.getList("bee_names").orElse(new ListTag());
        if (names.isEmpty()) return;
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Tag entry : names) {
            counts.merge(entry.asString().orElse(""), 1, Integer::sum);
        }
        counts.forEach((name, count) -> {
            MutableComponent line = Component.literal(count + "x ").withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(name).withStyle(ChatFormatting.GREEN));
            tooltip.add(line);
        });
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
