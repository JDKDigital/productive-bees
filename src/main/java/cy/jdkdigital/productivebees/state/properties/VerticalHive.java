package cy.jdkdigital.productivebees.state.properties;

import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum VerticalHive implements StringRepresentable
{
    NONE("none"),
    UP("up"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    VerticalHive(String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getSerializedName() {
        return this.name;
    }
}
