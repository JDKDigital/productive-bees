package cy.jdkdigital.productivebees.entity.bee;

public interface ExpirableBee
{
    int ticksWithoutNest = 16000;

    void setHasHadNest(boolean hadNest);
    boolean getHasHadNest();
}
