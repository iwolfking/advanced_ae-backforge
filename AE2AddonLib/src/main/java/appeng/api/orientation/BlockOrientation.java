package appeng.api.orientation;

import net.minecraft.core.Direction;

public final class BlockOrientation {
    private final Direction forward;

    private BlockOrientation(Direction forward) {
        this.forward = forward;
    }

    public static BlockOrientation get(Direction forward) {
        return new BlockOrientation(forward);
    }

    public int getAngleX() { return 0; }
    public int getAngleY() { return 0; }

    public Direction getSide(RelativeSide side) {
        return switch (side) {
            case FRONT -> forward;
            case BACK -> forward.getOpposite();
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case LEFT -> forward.getCounterClockWise();
            case RIGHT -> forward.getClockWise();
        };
    }
}
