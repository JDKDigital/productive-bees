package cy.jdkdigital.productivebees.state.properties;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nonnull;

public enum VerticalHive implements StringRepresentable
{
    NONE("none"),
    UP("up"),
    DOWN("down"),
    LEFT("left"),
    RIGHT("right"),
    BACK("back");

    private final String name;

    VerticalHive(String name) {
        this.name = name;
    }

    @Nonnull
    @Override
    public String getSerializedName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getSerializedName();
    }

    public VerticalHive opposite() {
        if (this.equals(UP)) return DOWN;
        if (this.equals(DOWN)) return UP;
        if (this.equals(LEFT)) return RIGHT;
        if (this.equals(RIGHT)) return LEFT;
        return NONE;
    }

    public Direction getExpandedCardinalDirection(Direction facing) {
        return switch (this) {
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
            case LEFT -> facing.getClockWise();
            case RIGHT -> facing.getCounterClockWise();
            case BACK -> facing.getOpposite();
            default -> facing;
        };
    }
}
