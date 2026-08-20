package cy.jdkdigital.productivebees.compat.hwyla;

import cy.jdkdigital.productivebees.common.block.entity.AdvancedBeehiveBlockEntityAbstract;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.level.storage.TagValueOutput;
import org.slf4j.LoggerFactory;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * Server-side data collector for {@link BeeComponentDataProvider}. Jade 26.1 requires data
 * providers and component providers to be separate classes.
 */
public class BeeServerDataProvider implements IServerDataProvider<EntityAccessor>
{
    public static final BeeServerDataProvider INSTANCE = new BeeServerDataProvider();

    @Override
    public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
        if (entityAccessor.getEntity() instanceof Bee bee) {
            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(bee.problemPath(), LoggerFactory.getLogger(BeeServerDataProvider.class))) {
                TagValueOutput out = TagValueOutput.createWithContext(reporter, bee.registryAccess());
                bee.saveWithoutId(out);
                CompoundTag built = out.buildResult();
                AdvancedBeehiveBlockEntityAbstract.removeIgnoredTags(built);
                compoundTag.merge(built);
            }
        }
    }

    @Override
    public Identifier getUid() {
        return BeeComponentDataProvider.UID;
    }
}
