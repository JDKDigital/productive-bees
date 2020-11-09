package cy.jdkdigital.productivebees.entity.bee;

import com.resourcefulbees.resourcefulbees.api.ICustomBee;

public interface ResourcefulCompatBee extends ICustomBee
{
    @Override
    default int getFeedCount() {
        return 0;
    }

    @Override
    default void resetFeedCount() {
        // unused
    }

    @Override
    default void addFeedCount() {
        // unused
    }
}
