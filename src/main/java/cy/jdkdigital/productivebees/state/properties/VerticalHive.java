package cy.jdkdigital.productivebees.state.properties;

import net.minecraft.util.IStringSerializable;

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

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
