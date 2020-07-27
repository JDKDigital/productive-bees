package cy.jdkdigital.productivebees.state.properties;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

public enum VerticalHive implements IStringSerializable
{
    NONE("none"),
    UP("up"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    VerticalHive(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Nonnull
    @Override
    public String getString() {
        return this.name;
    }
}
