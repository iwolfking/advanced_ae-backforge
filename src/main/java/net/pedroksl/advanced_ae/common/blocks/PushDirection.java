package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.core.Direction;

public enum PushDirection implements net.minecraft.util.StringRepresentable {
    ALL,
    FRONT,
    BACK,
    LEFT,
    RIGHT,
    UP,
    DOWN;

    public static PushDirection fromDirection(Direction direction) {
        if (direction == null) {
            return ALL;
        }
        return switch (direction) {
            case NORTH -> FRONT;
            case SOUTH -> BACK;
            case EAST -> RIGHT;
            case WEST -> LEFT;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    public String getSerializedName() {
        return name().toLowerCase();
    }

    public Direction getDirection() {
        return switch (this) {
            case ALL -> null;
            case FRONT -> Direction.NORTH;
            case BACK -> Direction.SOUTH;
            case RIGHT -> Direction.EAST;
            case LEFT -> Direction.WEST;
            case UP -> Direction.UP;
            case DOWN -> Direction.DOWN;
        };
    }
}
