package cy.jdkdigital.productivebees.compat.hwyla;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.productivebees.common.block.entity.SolitaryNestBlockEntity;
import cy.jdkdigital.productivebees.common.entity.bee.ProductiveBee;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * Server-side data collector for {@link SolitaryNestProvider}. Jade 26.1 requires
 * data providers and component providers to be separate classes.
 */
public class SolitaryNestDataProvider implements IServerDataProvider<BlockAccessor>
{
    static final SolitaryNestDataProvider INSTANCE = new SolitaryNestDataProvider();

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof SolitaryNestBlockEntity nest) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(nest.problemPath(), LogUtils.getLogger())) {
                TagValueOutput out = TagValueOutput.createWithContext(reporter, blockAccessor.getLevel().registryAccess());
                nest.savePacketNBT(out);
                tag.merge(out.buildResult());
            }
            tag.putBoolean("canRepopulate", nest.canRepopulate());
            tag.putInt("nestTickCooldown", nest.getNestTickCooldown());
            if (!nest.isEmpty()) {
                var occupant = nest.stored.get(0).occupant;
                var data = occupant.entityData().getUnsafe();
                if (data.contains("type")) {
                    tag.putString("inhabitantName", Component.translatable("entity.productivebees." + ProductiveBee.getBeeName(Identifier.parse(data.getString("type").orElse(""))) + "_bee").getString());
                } else {
                    tag.putString("inhabitantName", Component.translatable(occupant.entityData().type().getDescriptionId()).getString());
                }
            }
        }
    }

    @Override
    public Identifier getUid() {
        return SolitaryNestProvider.UID;
    }
}
